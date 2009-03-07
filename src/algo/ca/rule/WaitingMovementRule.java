/*
 * Created on 30.01.2008
 *
 */
package algo.ca.rule;

import java.util.ArrayList;

import ds.ca.Cell;

/**
 *
 * @author Daniel Pluempe
 */
public class WaitingMovementRule extends NonWaitingMovementRule {

	/**
	 * Selects the possible targets including the current cell.
	 * @param fromCell the current sell
	 * @param onlyFreeNeighbours indicates whether only free neighbours or all neighbours are included
	 * @return a list containing all neighbours and the from cell
	 */
	@Override
	protected ArrayList<Cell> selectPossibleTargets( Cell fromCell, boolean onlyFreeNeighbours ) {
		ArrayList<Cell> targets = super.selectPossibleTargets( fromCell, onlyFreeNeighbours );
		targets.add( fromCell );
		return targets;
	}
}
