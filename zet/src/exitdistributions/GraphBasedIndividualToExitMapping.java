package exitdistributions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import algo.graph.exitassignment.ExitAssignment;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.ExitCell;
import ds.ca.Individual;
import ds.ca.StaticPotential;
import ds.ca.TargetCell;
import ds.ca.Individual.DeathCause;
import ds.graph.Node;
import evacuationplan.BidirectionalNodeCellMapping;

/**
 * The mapping is calculated in the following way: 
 * First, we get the individuals and their starting positions from a given {@link ds.ca.CellularAutomaton} object.
 * Second, we calculate a list of the cells that belong to each
 * node. This can be done using the {@link evacuationplan.BidirectionalNodeCellMapping} that gives
 * the corresponding node for each cell and vice versa. During this we also create a list of all sources.
 * Third, we iterate through all sources. For each source we assign target cells to all 
 * individuals starting there. This is done according to a given {@link algo.graph.exitassignment.ExitAssignment}
 * that tells for each source how many of the individuals shall leave through which exit node. We
 * simply iterate through the individuals and assign the exits one after the other, according to
 * the exit assignment. To get the target cell from the exit node, we can again use the
 * bidirectional mapping between nodes and cells.
 * 
 * After calculating the mapping, it can be used to get the target cell corresponding
 * to an individual.
 */
public class GraphBasedIndividualToExitMapping extends IndividualToExitMapping{

	public class ConversionException extends IllegalArgumentException {

		private static final long serialVersionUID = 2490405914897585627L;

		public ConversionException(){
			this("Unbekannter Fehler bei der Berechnung der Zuweisung Indivuduum<->Ausgang.");
		}
		
		public ConversionException(String msg){
			super(msg);
		}
	}
	
	/** A cellular automaton that includes the individuals at ther start positions. */
	private CellularAutomaton ca;

	/** The bidirectional node cell mapping describes the relation between 
	 * graph and cellular automaton. It consists of a mapping that gives all cells for a 
	 * given node and of a mapping that gives the node for a given cell. */
	private BidirectionalNodeCellMapping nodeCellMapping;

	/** The exit assignment tells how many people shall leave where. It works
	 * like this: For each source node there is a list of exit nodes. This list
	 * contains as many entries as the source node has supply, i.e. there is 
	 * one entry for every individual standing in the source node. */ 
	private ExitAssignment exitAssignment;

	/**
	 * This mapping is the main part of the <code>GraphBasedIndividualToExitMapping</code>.
	 * It maps individuals to {@link ds.ca.TargetCell} objects, i.e. it maps
	 * an individual to the cell it should go to. The target cell represents 
	 * the exit the individual shall go to.
	 */
	private HashMap<Individual, TargetCell> individualToExitMapping; 
	
	/**
	 * Private boolean to make sure that the mapping has been calculated 
	 * when it is requested.
	 */
	private boolean isInitialised; 
	
	/**
	 * Creates a new <code>GraphBasedIndividualToExitMapping</code> object based on a 
	 * given cellular automaton, a bidirectional mapping between nodes and cells and an
	 * exit assignment. The mapping can be used to get the corresponding target cell
	 * for each individual, where the target cell represents the exit it belongs to.
	 * The individual shall get the potential corresponding to this target cell.
	 * @param ca A cellular automaton including the individuals at their starting positions.
	 * @param nodeCellMapping A bidirectional mapping between nodes and cells.
	 * @param exitAssignment An exit assignment that tells how many people shall go to which exit (for each source node).
	 */
	public GraphBasedIndividualToExitMapping(CellularAutomaton ca, BidirectionalNodeCellMapping nodeCellMapping, ExitAssignment exitAssignment){
		this.isInitialised = false;
		this.individualToExitMapping = new HashMap<Individual, TargetCell>();
		
		this.ca = ca;
		this.nodeCellMapping = nodeCellMapping;
		this.exitAssignment = exitAssignment;
		
		System.out.println(this);
	}

	/**
	 * Returns the corresponding target cell for each individual.
	 * The target cell represents the static potential it belongs to.
	 * @param individual An individual that belongs to the cellular automaton that was set in the constructor.
	 * @return The corresponding target cell for this individual, representing the static potential leading to this target cell 
	 * (and those target cells belonging to the same exit).
	 */
	public TargetCell getExit(Individual individual){
		if(!isInitialised){
			initialise();
		}
		
		return individualToExitMapping.get(individual);
	}
	
	public void calculate(){
		initialise();
	}
	
	public String toString(){
		String output = "";
		DecimalFormat pformat = new DecimalFormat("###.#");
		DecimalFormat cformat = new DecimalFormat("####.#");

		int[] sinkCount = new int[exitAssignment.getDomainSize()];
		HashMap<String, Integer> exitCount = new HashMap<String, Integer>();
		for(int i=0; i < exitAssignment.getDomainSize(); i++){
			if(exitAssignment.isDefinedFor(new Node(i))){
				for(Node v : exitAssignment.get(new Node(i))){
					sinkCount[v.id()]++;
					
					TargetCell target = findATargetCell(nodeCellMapping.getCells(v));
					String targetName = "<Ohne Namen>";
					if(target != null && target.getName() != null){
						targetName = target.getName();
					}
					
					int n = 0;
					if(exitCount.get(targetName) != null){
						n = exitCount.get(targetName);
					}
					
					n++;
					exitCount.put(targetName, n);
				}
			}
		}
		
		int sum = 0;
		for(int i=0; i < sinkCount.length; i++){
			sum += sinkCount[i];
		}
		
		output += "---------------------------------\n";
		for(int i=0; i < sinkCount.length; i++){
			if(sinkCount[i] > 0){
					output += "Node " + cformat.format(i) + ": " + cformat.format(sinkCount[i]) + " (" + pformat.format(100*sinkCount[i]/(double)sum) + "%)\n";
			}
		}
		
		output += "---------------------------------\n";
		ArrayList<String> exitNames = new ArrayList<String>(exitCount.keySet());
		Collections.sort(exitNames);
		for(String exitName : exitNames){
			output += exitName + ": " + exitCount.get(exitName) + " (" + pformat.format(100*exitCount.get(exitName)/(double)sum) + "%)\n";
		}
		output += "---------------------------------\n";
		output += "Insg.: " + sum + "\n";
		output += "---------------------------------\n";
		return output;
	}
	
	/**
	 * Calculates the main mapping. This has to be done once before the mapping can be used.
	 */
	private void initialise(){
		calculateIndividualExitMapping();
		isInitialised = true;		
	}
	
	/**
	 * Private method to assign a target cell to each individual.
	 */
	private void calculateIndividualExitMapping(){
		// 1. Get individuals from the cellular automaton.
		List<Individual> individualList = new ArrayList<Individual>(ca.getIndividuals());
		// 2. Store the individuals according to their start node.
		HashMap<Node,ArrayList<Individual>> sourcesIndividualMapping;
		sourcesIndividualMapping = new HashMap<Node,ArrayList<Individual>>();
		HashSet<Node> sources = new HashSet<Node>();
		// iterate through all individuals and insert them into the list of their start node.
		for (Individual ind: individualList){
			Node node = nodeCellMapping.getNode(ind.getCell());
			if (!sources.contains(node)){
				sources.add(node);
			}
			ArrayList<Individual> individualsOfThisNode;
			if (sourcesIndividualMapping.containsKey(node)){
				individualsOfThisNode=sourcesIndividualMapping.get(node);
			} else {
				individualsOfThisNode = new ArrayList<Individual>();
				sourcesIndividualMapping.put(node, individualsOfThisNode);
			}
			individualsOfThisNode.add(ind);
		}
		// 3. Iterate through all sources and assign target cells to the individuals starting 
		//    at each source node.
		for (Node source : sources){
			// Get the individuals and the exits they shall go to
			ArrayList<Individual> individualsOfThisNode = sourcesIndividualMapping.get(source);
			List<Node> exits = exitAssignment.get(source);
			
			if(exits == null){ // This happens if the graph deleted an unreachable 
							   // source. In that case, all individuals on that 
							   // source are doomed to die, since they cannot 
							   // reach any exit. 
							   // This also means that they cannot have a potential.
							   // If they have one nonetheless, then something is 
							   // very wrong.
				
				ArrayList<Individual> doomedIndividuals = new ArrayList<Individual>();
				for(Individual individual : individualsOfThisNode){
					boolean hasPotential = false;
					for(StaticPotential pot : ca.getPotentialManager().getStaticPotentials()){
						if(pot.hasValidPotential(individual.getCell())){
							hasPotential = true;
						}						
					}
					
					if(!hasPotential){
						doomedIndividuals.add(individual);
					} else {
						throw new RuntimeException("The individual " + individual+ " has a potential " 
								+ " (and thus can probably reach an exit) but was not found in the exit assignment.");	
					}
				}
				
				for(Individual individual : doomedIndividuals){
					ca.setIndividualDead(individual, DeathCause.EXIT_UNREACHABLE);
				}
				
			} else {		
				if (individualsOfThisNode.size() != exits.size()){
					throw new ConversionException("The number of individuals in the node "+source+" is "+individualsOfThisNode.size()+" but the number of assigned exits is "+exits.size()
							+ ". Exitlist:" + exits + " Individuals: " + individualsOfThisNode);
				}
				// Assign individuals to target cells by taking the next exit node and looking up its cells
				// (the first cell is chosen as a representative).
				for (Individual individual : individualsOfThisNode){
					Node exit = exits.remove(0);
					TargetCell targetCell = findATargetCell(nodeCellMapping.getCells(exit));
					
					if(targetCell == null){ 
						throw new ConversionException("The node<->cell-mapping yielded a sink that is only mapped to non-exitcells. " +
								"This may happen, if all of your ExitCells are also DoorCells (which is not supported)" +
								"Sink id: " + exit.id());
					} 
					
					individualToExitMapping.put(individual, targetCell);	
				}
			}
		}
	}

	private TargetCell findATargetCell(Iterable<Cell> cellList){
		Cell targetCell = null;
		boolean targetCellFound = false;
		Iterator<Cell> it = cellList.iterator();
		while(!targetCellFound && it.hasNext()){
			Cell possibleTargetCell = it.next();
			if(possibleTargetCell instanceof TargetCell){
				targetCellFound = true;
				targetCell = possibleTargetCell;
			}
		}
		
		if(targetCellFound){
			return (TargetCell)targetCell;
		} else {
			return null;
		}
	}
}
