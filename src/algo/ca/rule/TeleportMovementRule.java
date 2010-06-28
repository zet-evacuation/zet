/**
 * TeleportMovementRule.java
 * Created: Jun 17, 2010,4:36:48 PM
 */
package algo.ca.rule;

import ds.ca.Cell;
import ds.ca.TeleportCell;
import java.util.ArrayList;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportMovementRule extends WaitingMovementRule {

	@Override
	public boolean executableOn( Cell cell ) {
		if( cell instanceof TeleportCell ) {
			return super.executableOn( cell ) && !((TeleportCell)cell).isTeleportFailed();
		} else
			return super.executableOn( cell );
	}

	/**
	 * Selects the possible targets including the current cell.
	 * @param fromCell the current sell
	 * @param onlyFreeNeighbours indicates whether only free neighbours or all neighbours are included
	 * @return a list containing all neighbours and the from cell
	 */
	@Override
	protected ArrayList<Cell> selectPossibleTargets( Cell fromCell, boolean onlyFreeNeighbours ) {
		ArrayList<Cell> targets = super.selectPossibleTargets( fromCell, onlyFreeNeighbours );

//		if( true )
//			return targets;

		ArrayList<Cell> returned = new ArrayList<Cell>();
		//targets.add( fromCell );
		double time = caController().getCA().getTimeStep();
		for( Cell cell : targets ) {
			if( !cell.isOccupied( time ) )
				//targets.remove( cell );
				returned.add( cell );
		}
		if( !returned.contains( fromCell) )
			returned.add( fromCell );

		return returned;
	}
}
