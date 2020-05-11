/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package exitdistributions;

import org.zet.cellularautomaton.IndividualToExitMapping;
import algo.graph.exitassignment.ExitAssignment;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.TargetCell;
import org.zetool.graph.Node;
import evacuationplan.BidirectionalNodeCellMapping;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.potential.Potential;

/**
 * The mapping is calculated in the following way: First, we get the individuals and their starting positions from a
 * given {@link ds.ca.EvacuationCellularAutomaton} object. Second, we calculate a list of the cells that belong to each
 * node. This can be done using the {@link evacuationplan.BidirectionalNodeCellMapping} that gives the corresponding
 * node for each cell and vice versa. During this we also create a list of all sources. Third, we iterate through all
 * sources. For each source we assign target cells to all individuals starting there. This is done according to a given
 * {@link algo.graph.exitassignment.ExitAssignment} that tells for each source how many of the individuals shall leave
 * through which exit node. We simply iterate through the individuals and assign the exits one after the other,
 * according to the exit assignment. To get the target cell from the exit node, we can again use the bidirectional
 * mapping between nodes and cells.
 *
 * After calculating the mapping, it can be used to get the target cell corresponding to an individual.
 */
public class GraphBasedIndividualToExitMapping implements IndividualToExitMapping {

    public class ConversionException extends IllegalArgumentException {

        private static final long serialVersionUID = 2490405914897585627L;

        public ConversionException() {
            this("Unbekannter Fehler bei der Berechnung der Zuweisung Indivuduum<->Ausgang.");
        }

        public ConversionException(String msg) {
            super(msg);
        }
    }

    /**
     * A cellular automaton that includes the individuals at ther start positions.
     */
    private EvacuationCellularAutomaton ca;

    private EvacuationState es;

    /**
     * The bidirectional node cell mapping describes the relation between graph and cellular automaton. It consists of a
     * mapping that gives all cells for a given node and of a mapping that gives the node for a given cell.
     */
    private BidirectionalNodeCellMapping nodeCellMapping;

    /**
     * The exit assignment tells how many people shall leave where. It works like this: For each source node there is a
     * list of exit nodes. This list contains as many entries as the source node has supply, i.e. there is one entry for
     * every individual standing in the source node.
     */
    private ExitAssignment exitAssignment;

    /**
     * This mapping is the main part of the {@code GraphBasedIndividualToExitMapping}. It maps individuals to
     * {@link ds.ca.TargetCell} objects, i.e. it maps an individual to the cell it should go to. The target cell
     * represents the exit the individual shall go to.
     */
    private HashMap<Individual, TargetCell> individualToExitMapping;

    /**
     * Private boolean to make sure that the mapping has been calculated when it is requested.
     */
    private boolean isInitialised;

    /**
     * Creates a new {@code GraphBasedIndividualToExitMapping} object based on a given cellular automaton, a
     * bidirectional mapping between nodes and cells and an exit assignment. The mapping can be used to get the
     * corresponding target cell for each individual, where the target cell represents the exit it belongs to. The
     * individual shall get the potential corresponding to this target cell.
     *
     * @param ca A cellular automaton including the individuals at their starting positions.
     * @param nodeCellMapping A bidirectional mapping between nodes and cells.
     * @param exitAssignment An exit assignment that tells how many people shall go to which exit (for each source
     * node).
     */
    public GraphBasedIndividualToExitMapping(EvacuationCellularAutomaton ca,
            BidirectionalNodeCellMapping nodeCellMapping, ExitAssignment exitAssignment) {
        this.isInitialised = false;
        this.individualToExitMapping = new HashMap<>();

        this.ca = ca;
        this.nodeCellMapping = nodeCellMapping;
        this.exitAssignment = exitAssignment;

        System.out.println("this");
        System.out.println(this);
    }

    /**
     * Returns the corresponding target cell for each individual. The target cell represents the static potential it
     * belongs to.
     *
     * @param individual An individual that belongs to the cellular automaton that was set in the constructor.
     * @return The corresponding target cell for this individual, representing the static potential leading to this
     * target cell (and those target cells belonging to the same exit).
     */
    @Override
    public TargetCell getExit(Individual individual) {
        if (!isInitialised) {
            initialise();
        }

        return individualToExitMapping.get(individual);
    }

    public void calculate() {
        initialise();
    }

    @Override
    public String toString() {
        String output = "";
        DecimalFormat pformat = new DecimalFormat("###.#");
        DecimalFormat cformat = new DecimalFormat("####.#");

        int[] sinkCount = new int[exitAssignment.getDomainSize()];
        HashMap<String, Integer> exitCount = new HashMap<>();
        for (int i = 0; i < exitAssignment.getDomainSize(); i++) {
            if (exitAssignment.isDefinedFor(new Node(i))) {
                for (Node v : exitAssignment.get(new Node(i))) {
                    sinkCount[v.id()]++;

                    TargetCell target = findATargetCell(nodeCellMapping.getCells(v));
                    String targetName = "<Ohne Namen>";
                    if (target != null && target.getName() != null) {
                        targetName = target.getName();
                    }

                    int n = 0;
                    if (exitCount.get(targetName) != null) {
                        n = exitCount.get(targetName);
                    }

                    n++;
                    exitCount.put(targetName, n);
                }
            }
        }

        int sum = 0;
        for (int i = 0; i < sinkCount.length; i++) {
            sum += sinkCount[i];
        }

        output += "---------------------------------\n";
        for (int i = 0; i < sinkCount.length; i++) {
            if (sinkCount[i] > 0) {
                output += "Node " + cformat.format(i) + ": " + cformat.format(sinkCount[i])
                        + " (" + pformat.format(100 * sinkCount[i] / (double) sum) + "%)\n";
            }
        }

        output += "---------------------------------\n";
        ArrayList<String> exitNames = new ArrayList<>(exitCount.keySet());
        Collections.sort(exitNames);
        for (String exitName : exitNames) {
            output += exitName + ": " + exitCount.get(exitName)
                    + " (" + pformat.format(100 * exitCount.get(exitName) / (double) sum) + "%)\n";
        }
        output += "---------------------------------\n";
        output += "Insg.: " + sum + "\n";
        output += "---------------------------------\n";
        return output;
    }

    /**
     * Calculates the main mapping. This has to be done once before the mapping can be used.
     */
    private void initialise() {
        calculateIndividualExitMapping();
        isInitialised = true;
    }

    /**
     * Private method to assign a target cell to each individual.
     */
    private void calculateIndividualExitMapping() {
        // 1. Get individuals from the cellular automaton.
        List<Individual> individualList = new ArrayList<>(es.getInitialIndividualCount());
        for (Individual i : es) {
            individualList.add(i);
        }

        // 2. Store the individuals according to their start node.
        HashMap<Node, ArrayList<Individual>> sourcesIndividualMapping = new HashMap<>();
        HashSet<Node> sources = new HashSet<>();
        // iterate through all individuals and insert them into the list of their start node.
        for (Individual ind : individualList) {
            Node source = nodeCellMapping.getNode(es.propertyFor(ind).getCell());
            if (!sources.contains(source)) {
                sources.add(source);
            }
            ArrayList<Individual> individualsOfThisNode;
            if (sourcesIndividualMapping.containsKey(source)) {
                individualsOfThisNode = sourcesIndividualMapping.get(source);
            } else {
                individualsOfThisNode = new ArrayList<>();
                sourcesIndividualMapping.put(source, individualsOfThisNode);
            }
            individualsOfThisNode.add(ind);
        }

        // 3. Iterate through all sources and assign target cells to the individuals starting
        //    at each source node.
        for (Node source : sources) {
            // Get the individuals and the exits they shall go to
            ArrayList<Individual> individualsOfThisNode = sourcesIndividualMapping.get(source);
            List<Node> exits = exitAssignment.get(source);

            if (exits == null) { // This happens if the graph deleted an unreachable
                // source. In that case, all individuals on that
                // source are doomed to die, since they cannot
                // reach any exit.
                // This also means that they cannot have a potential.
                // If they have one nonetheless, then something is
                // very wrong.

                ArrayList<Individual> doomedIndividuals = new ArrayList<>();
                for (Individual individual : individualsOfThisNode) {
                    boolean hasPotential = false;
                    for (Exit exit : ca.getExits()) {
                        Potential pot = ca.getPotentialFor(exit);
                        if (pot.hasValidPotential(es.propertyFor(individual).getCell())) {
                            hasPotential = true;
                        }
                    }

                    if (!hasPotential) {
                        doomedIndividuals.add(individual);
                    } else {
                        throw new AssertionError("The individual " + individual + " has a potential "
                                + " (and thus can probably reach an exit) but was not found in the exit assignment.");
                    }
                }

                for (Individual individual : doomedIndividuals) {
                    //ca.setIndividualDead(individual, DeathCause.EXIT_UNREACHABLE);
                    throw new UnsupportedOperationException("Individual is dead!");
                }

            } else {
                HashMap<TargetCell, Potential> potentialMapping;
                potentialMapping = new HashMap<>();
                for (Exit exit : ca.getExits()) {
                    Potential potential = ca.getPotentialFor(exit);
                    for (TargetCell target : exit.getExitCluster()) {
                        if (potentialMapping.put(target, potential) != null) {
                            throw new UnsupportedOperationException("There were two potentials leading to the same exit. "
                                    + "This method can currently not deal with this.");
                        }
                    }
                }

                if (individualsOfThisNode.size() != exits.size()) {
                    throw new ConversionException("The number of individuals in the node " + source + " is "
                            + individualsOfThisNode.size() + " but the number of assigned exits is " + exits.size()
                            + ". Exitlist:" + exits + " Individuals: " + individualsOfThisNode);
                }

                HashMap<Node, Integer> exitOccurences = new HashMap<>();

                for (Node exit : exits) {
                    if (exitOccurences.containsKey(exit)) {
                        exitOccurences.put(exit, exitOccurences.get(exit) + 1);
                    } else {
                        exitOccurences.put(exit, 1);
                    }
                }

                Comparator<Individual> individualPotentialComparator = new Comparator<Individual>() {

                    @Override
                    public int compare(Individual o1, Individual o2) {
                        // compute min distance von o1
                        int minPotential1 = Integer.MAX_VALUE;
                        for (Exit exit : ca.getExits()) {
                            Potential pot = ca.getPotentialFor(exit);
                            if (pot.hasValidPotential(es.propertyFor(o1).getCell())) {
                                minPotential1 = Math.min(pot.getPotential(es.propertyFor(o1).getCell()), minPotential1);
                            }
                        }

                        int minPotential2 = Integer.MAX_VALUE;
                        for (Exit exit : ca.getExits()) {
                            Potential pot = ca.getPotentialFor(exit);
                            if (pot.hasValidPotential(es.propertyFor(o2).getCell())) {
                                minPotential2 = Math.min(pot.getPotential(es.propertyFor(o2).getCell()), minPotential2);
                            }
                        }

                        return minPotential1 - minPotential2;
                        // compute min distance von o2

                    }

                };

                while (!individualsOfThisNode.isEmpty()) {
                    PriorityQueue<Individual> individualRanking = new PriorityQueue<>(individualPotentialComparator);
                    for (Individual i : individualsOfThisNode) {
                        individualRanking.add(i);
                    }

                    Individual i = individualRanking.poll();

                    // Assign individual to the best exit
                    Node currentExit = null;
                    int currentPotential = Integer.MAX_VALUE;
                    TargetCell currentTargetCell = null;

                    for (Node exit : exitOccurences.keySet()) {
                        List<EvacCellInterface> cells = nodeCellMapping.getCells(exit);
                        TargetCell targetCell = findATargetCell(cells);
                        Potential pot = potentialMapping.get(targetCell);
                        if (pot.getPotential(es.propertyFor(i).getCell()) < currentPotential) {
                            currentExit = exit;
                            currentPotential = pot.getPotential(es.propertyFor(i).getCell());
                            currentTargetCell = targetCell;
                        }
                    }
                    individualToExitMapping.put(i, currentTargetCell);
                    if (exitOccurences.get(currentExit) > 1) {
                        exitOccurences.put(currentExit, exitOccurences.get(currentExit) - 1);
                    } else {
                        exitOccurences.remove(currentExit);
                    }
                    individualsOfThisNode.remove(i);
                }

                // Assign individuals to target cells by taking the next exit node and looking up its cells
                // (the first cell is chosen as a representative).
//        for( Individual individual : individualsOfThisNode ) {
//          Node exit = exits.remove( 0 );
//          TargetCell targetCell = findATargetCell( nodeCellMapping.getCells( exit ) );
//
//          if( targetCell == null ) {
//            throw new ConversionException( "The node<->cell-mapping yielded a sink that is only mapped to"
//                    + " non-exitcells. This may happen, if all of your ExitCells are also DoorCells"
//                    + " (which is not supported) Sink id: " + exit.id() );
//          }
//
//          individualToExitMapping.put( individual, targetCell );
//        }
            }
        }
    }

    private TargetCell findATargetCell(Iterable<EvacCellInterface> cellList) {
        EvacCell targetCell = null;
        boolean targetCellFound = false;
        Iterator<EvacCellInterface> it = cellList.iterator();
        while (!targetCellFound && it.hasNext()) {
            EvacCellInterface possibleTargetCell = it.next();
            if (possibleTargetCell instanceof TargetCell) {
                targetCellFound = true;
                targetCell = (TargetCell) possibleTargetCell;
            }
        }

        if (targetCellFound) {
            return (TargetCell) targetCell;
        } else {
            return null;
        }
    }
}
