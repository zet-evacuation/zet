/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
package evacuationplan;

import org.zet.cellularautomaton.CellularAutomatonDirectionChecker;
import org.zet.cellularautomaton.EvacPotential;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.generators.GeneralRandom;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.ds.structure.FlowOverTimePath;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.graph.Node;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.PotentialManager;
import org.zet.cellularautomaton.StaticPotential;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class controls the behavior of a cellular automaton so that the individuals (approximately) follow the paths of
 * a given dynamic flow. First, every individual is assigned to one path of the flow (of course the given flow must use
 * the same concrete assignment, else this assignment isn't possible). A {@code EvacPotential} is then calculated for
 * each individual. This is a special type of potential that does not allow the individuals to leave the path or to cut
 * short. It uses the {@code canPass()} method of the {@code CAPathPassabilityChecker} object to test whether two cells
 * lie on neighbour nodes of the path.
 */
public class CAPathPassabilityChecker implements CellularAutomatonDirectionChecker {

    /**
     * A cellular automaton with start individuals. The checker manages the potentials of the individuals in this ca.
     */
    EvacuationCellularAutomaton ca;
    /**
     * The potential manager of the cellular automation {@code ca</code.>
     */
    PotentialManager pm;
    /**
     * The bidirectional node cell mapping describes the relation between graph and ca. It consists of a mapping that
     * gives all cells for a given node and of a mapping that gives the node for a given cell.
     */
    BidirectionalNodeCellMapping nodeCellMapping;
    /**
     * The transshipment describes an evacuation flow based. It's calculated by a graph algorithm and shall be used to
     * calculate the potentials of the individuals.
     */
    PathBasedFlowOverTime transshipment;
    /**
     * A mapping that gives a {@code SuccessorNodeMapping} for each individual. the {@code SuccessorNodeMapping}
     * represents the path that has been chosen for the individual.
     */
    IdentifiableObjectMapping<Individual, SuccessorNodeMapping> individualSuccessorNodeMapping;
    /**
     * A mapping that gives a potential for each individual. The potential is calculated according to the path that has
     * been chosen for the individual.
     */
    IdentifiableObjectMapping<Individual, EvacPotential> individualPotentialMapping;

    /**
     * Constructs an CAPathPassabilityChecker instance for a given cellular automaton, a bidirectional mapping between
     * nodes and cells and a transshipment.
     *
     * @param ca EvacuationCellularAutomaton instance to work with
     * @param transshipment a {@code PathBasedFlowOverTime} object containing a transshipment.
     * @param nodeCellMapping an object containing a mapping from nodes to lists of cells and containing a mapping from
     * cells to nodes.
     */
    public CAPathPassabilityChecker(EvacuationCellularAutomaton ca, BidirectionalNodeCellMapping nodeCellMapping, PathBasedFlowOverTime transshipment) {
        this.ca = ca;
        this.pm = ca.getPotentialManager();
        this.nodeCellMapping = nodeCellMapping;
        this.transshipment = transshipment;
        calculateIndivualPathMapping();
    }

    /**
     * Checks whether it is possible for the individual to pass from the cell "from" to the cell "to" without leaving
     * its evacuationPlan.
     *
     * @param i an individual
     * @param from a cell
     * @param to another cell
     * @return whether it is possible for the individual to pass from the cell "from" to the cell "to" without leaving
     * its evacuationPlan.
     * @throws java.lang.IllegalArgumentException if there is no path defined for the individual
     */
    @Override
    public boolean canPass(Individual i, EvacCell from, EvacCell to) {
        if (individualSuccessorNodeMapping.isDefinedFor(i)) {
            Node fromNode = nodeCellMapping.getNode(from);
            Node toNode = nodeCellMapping.getNode(to);
            if (fromNode.equals(toNode)) {
                return true;
            }
            if (toNode.equals(individualSuccessorNodeMapping.get(i).getSuccessor(fromNode))) {
                return true;
            }
            // If the toNode is outside the path, the reverce direction may not be tested.
            if (individualSuccessorNodeMapping.get(i).isDefinedFor(toNode)) {
                if (fromNode.equals(individualSuccessorNodeMapping.get(i).getSuccessor(toNode))) {
                    return true;
                }
            }
            return false;
        } else {
            throw new IllegalArgumentException(
                    "There is no path defined for this individual");
        }
    }

    /**
     * Returns the potential of the given individual.
     *
     * @param i The individual for which the potential shall be returned.
     * @return the potential of the given individual.
     */
    public EvacPotential getPotential(Individual i) {
        if (!individualPotentialMapping.isDefinedFor(i)) {
            throw new AssertionError("There is no potential for the individual " + i.id() + ".");
        }
        return individualPotentialMapping.get(i);
    }

    /**
     * Private method to assign a path to each individual. The paths are not stored explicitly but as {@code CAPath}
     * object.
     */
    private void calculateIndivualPathMapping() {
        // get individuals from ca
        int numberOfIndividuals = ca.getIndividualCount();
        List<Individual> individualList = ca.getIndividuals();
        // get all individuals and store them according to their start node
        HashMap<Node, ArrayList<Individual>> sourcesIndividualMapping;
        sourcesIndividualMapping = new HashMap<Node, ArrayList<Individual>>();
        for (Individual ind : individualList) {
            Node node = nodeCellMapping.getNode(ind.getCell());
            ArrayList<Individual> individualsOfThisNode;
            if (sourcesIndividualMapping.containsKey(node)) {
                individualsOfThisNode = sourcesIndividualMapping.get(node);
            } else {
                individualsOfThisNode = new ArrayList<Individual>();
                sourcesIndividualMapping.put(node, individualsOfThisNode);
            }
            individualsOfThisNode.add(ind);
        }
        // map individuals and dynamic path flows and calculate potential and successor mapping
        individualSuccessorNodeMapping = new IdentifiableObjectMapping<Individual, SuccessorNodeMapping>(numberOfIndividuals + 1);
        individualPotentialMapping = new IdentifiableObjectMapping<Individual, EvacPotential>(numberOfIndividuals + 1);
        for (FlowOverTimePath dynamicPathFlow : transshipment) {
            Individual chosenIndividual = null;
            // get all individuals that stand in the start node of the path
            if (!sourcesIndividualMapping.containsKey(dynamicPathFlow.firstEdge().start())) {
                throw new AssertionError("There is a path flow from a node that does not contain any individuals.");
            }
            ArrayList<Individual> possibleIndividuals = sourcesIndividualMapping.get(dynamicPathFlow.firstEdge().start());
            // choose as many individuals for this path as flow units go over the path
            for (int c = 0; c < dynamicPathFlow.getAmount(); c++) {
				// choose one of the possible individuals randomly, 
                // associate it to the path and delete it from the list of individuals remaining in this node
                GeneralRandom randGen = RandomUtils.getInstance().getRandomGenerator();
                int randomNumber = randGen.nextInt(possibleIndividuals.size());
                chosenIndividual = possibleIndividuals.remove(randomNumber);
                // calculate successor mapping from path
                SuccessorNodeMapping succNodeMapping = new SuccessorNodeMapping(dynamicPathFlow);
                individualSuccessorNodeMapping.set(chosenIndividual, succNodeMapping);
                // calculate potential for this individual according to its path
                EvacPotential ep = calculateIndividualPotential(chosenIndividual, dynamicPathFlow.lastEdge().start());
                individualPotentialMapping.set(chosenIndividual, ep);
            }
        }
    }

    /**
     * Private method to calculate the {@code EvacPotential} for a given individual. The last node of this individuals
     * path is also needed.
     *
     * @param i The individual for which the potential shall be calculated.
     * @param exitNode The last node on the path of this individual.
     * @return An {@code EvacPotential} object for the given individual, calculated with the cells of the exitNode as
     * destination.
     */
    private EvacPotential calculateIndividualPotential(Individual i, Node exitNode) {
        IndividualPotentialCalculator indPotCal = new IndividualPotentialCalculator(ca, i, this);
        ArrayList<EvacCell> cellList = nodeCellMapping.getCells(exitNode);
        ArrayList<ExitCell> exitCellList = new ArrayList<ExitCell>(cellList.size());
        for (EvacCell cell : cellList) {
            if (cell instanceof ExitCell) {
                exitCellList.add((ExitCell) cell);
            }
        }
        StaticPotential pot = indPotCal.createStaticPotential(exitCellList);
        return pot.getAsEvacPotential(i, this);
    }

}
