package algo.ca.rule;

import java.util.ArrayList;
import java.util.Collections;

import ds.ca.Cell;
import ds.ca.Individual;
import ds.ca.StaticPotential;
import util.random.RandomUtils;
import util.random.GeneralRandom;

/**
 *
 * @author Joscha
 */
public class ChangePotentialInsufficientAdvancementRule extends AbstractPotentialChangeRule{

	private static final int CHANGE_THRESHOLD = 3;
	
	/**
	 * 
	 * @param cell
	 * @return
	 */
	@Override
	public boolean executableOn(Cell cell) {
		if (cell.getIndividual() != null && !cell.getIndividual().isSafe()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param cell
	 */
	@Override
	protected void onExecute(Cell cell) {  

		// Get the potential of the individual on the <code>cell</code> as well as some other concerning constants of the individual
		Individual individual = cell.getIndividual();    
		StaticPotential sp = individual.getStaticPotential();                
		int cellPotential = sp.getPotential(cell);                
		int cellCountToChange = individual.getCellCountToChange();
		int memoryIndex = individual.getMemoryIndex();                

		// Update the <code>potentialMemory</code>        
		if (memoryIndex == 0){
			individual.setPotentialMemoryStart(new PotentialValueTuple(cellPotential,sp));                
		}                                

		// Change potential, if not enough advancement to the designated exit cell has been made
		if (memoryIndex == cellCountToChange - 1){

			/**
			 * Calibratingfactor - 
			 * The smaller <code>epsilon</code>, the lower the probability of a potential-change
			 */
			int epsilon = 10;

			individual.setPotentialMemoryEnd(new PotentialValueTuple(cellPotential,sp));                

			int potentialDifference = individual.getPotentialMemoryStart().getLengthOfWay() - individual.getPotentialMemoryEnd().getLengthOfWay();            
			if ((potentialDifference < epsilon) && (sp == individual.getPotentialMemoryStart().getStaticPotential())){

				// Calculate the second best Potential and the associated potential value on the <code>cell</code>
				ArrayList<StaticPotential> staticPotentials = new ArrayList<StaticPotential>();
				staticPotentials.addAll(this.caController().getCA().getPotentialManager().getStaticPotentials());
				StaticPotential minWayLengthPotential = sp;
				int lengthOfWayValue = Integer.MAX_VALUE;
				for (StaticPotential statPot : staticPotentials){            
					if ((statPot.getPotential(cell) < lengthOfWayValue) && (statPot != sp)){
						minWayLengthPotential = statPot;
						lengthOfWayValue = statPot.getPotential(cell);
					}
				}
				
				// Check if the new potential is promising enough to change.
				// This is the case, if at least CHANGE_THRESHOLD cells of
				// the free neighbors have a lower potential (with respect
				// to the new static potential) than the current cell
				
				ArrayList<Cell> freeNeighbours = cell.getFreeNeighbours();
				int i = 0; 
				int promisingNeighbours = 0;
				int curPotential = minWayLengthPotential.getPotential(cell);
				while(i < freeNeighbours.size() && promisingNeighbours <= CHANGE_THRESHOLD){
					if(minWayLengthPotential.getPotential(freeNeighbours.get(i)) < curPotential){
						promisingNeighbours++;
					}					
					i++;
				}
				
				if(promisingNeighbours > CHANGE_THRESHOLD){
					individual.setStaticPotential(minWayLengthPotential);
					caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic(individual, caController().getCA().getTimeStep());
				}
			}
		}

		memoryIndex = (memoryIndex + 1) % cellCountToChange;   
		individual.setMemoryIndex(memoryIndex);    
	}
}