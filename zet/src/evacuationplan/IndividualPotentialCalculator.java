package evacuationplan;

import java.util.ArrayList;

import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import algo.ca.SPPotentialController;

/**
 * This class provides the possibility to calculate a individual specific potential.
 * It extends the <code>SPPotentialController</code> and only changes
 * the <code>getNeighbour()</code> method to forbid transitions that
 * the individual may not do according to the <code>checker</code>. 
 */
public class IndividualPotentialCalculator extends SPPotentialController {

	Individual i;
	CAPathPassabilityChecker checker;
	
	/**
	 * Create a new calculator that can calculate a individual specific potential for 
	 * the cellular automaton <code>ca</code> and a given individual <code>i</code>.
	 * The <code>checker</code> is used to check which transitions are forbidden for 
	 * the individual. 
	 * @param ca A cellular automaton.
	 * @param i A certain individual that is already placed in the cellular automaton.
	 * @param checker A checker to say which transitions are forbidden for the individual.
	 */
	public IndividualPotentialCalculator(CellularAutomaton ca, Individual i, CAPathPassabilityChecker checker){
		super(ca);
		this.i = i;
		this.checker = checker;
	}
	
	@Override
	/**
	 * Overwritten method that obtains all original neighbors of a cell and sorts
	 * those out that the individual may not reach from <code>cell</code> 
	 * because the transition is forbidden for the individual (according
	 * to the <code>checker</code> object).
	 */
	public ArrayList<Cell> getNeighbours(Cell cell){
            ArrayList<Cell> evacPlanNeighbours = new ArrayList<Cell>();
            for (Cell target: cell.getNeighbours()) {
                if (checker.canPass(i, cell, target)) {
                    evacPlanNeighbours.add(target);
                }
            }
            return evacPlanNeighbours;
	}
}
