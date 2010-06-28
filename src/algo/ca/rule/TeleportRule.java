/**
 * TeleportRule.java
 * Created: Jun 16, 2010,6:30:54 PM
 */
package algo.ca.rule;

import ds.ca.Cell;
import ds.ca.Individual;
import ds.ca.TeleportCell;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportRule extends AbstractRule {

	@Override
	public boolean executableOn( Cell cell ) {
		boolean res = cell instanceof TeleportCell && super.executableOn( cell );
		if( res )
			res = res && canMove( cell.getIndividual() );
		return res;
	}

	static int counter = 0;

	@Override
	protected void onExecute( Cell cell ) {
		final TeleportCell tc = (TeleportCell)cell;

		if( tc.targetCount() > 0 && tc.getTarget( 0 ) != null ) {

//						double beginTime = Math.max( i.getCell().getOccupiedUntil(), i.getStepEndTime() );
			double targetFreeAt = tc.getTarget( 0 ).getOccupiedUntil();


			if( tc.getTarget( 0 ).getIndividual() == null && tc.getTarget( 0 ).getUsedInTimeStep() < caController().getCA().getTimeStep() ) {
				double moveTime = Math.max( targetFreeAt, cell.getIndividual().getStepEndTime() );
				//cell.getIndividual().setStepStartTime( cell.getIndividual().getStepEndTime() );
				cell.getIndividual().setStepStartTime( moveTime );
				caController().getCA().moveIndividual( cell, tc.getTarget( 0 ) );
				tc.setTeleportFailed( false );
				counter++;
				System.out.println( "Teleportiert: " + counter );
				if( caController().getCA().getTimeStep() > tc.getTarget( 0 ).getUsedInTimeStep() )
					tc.getTarget( 0 ).setUsedInTimeStep( caController().getCA().getTimeStep() );
			} else
				tc.setTeleportFailed( true );
		}
	}

	/**
	 * Decides randomly if an individual moves. (falsch)
	 * @param i An individual with a given parameterSet
	 * @return <code>true</code> if the individual moves or
	 * <code>false</code> otherwise.
	 */
	//gibt true wieder, wenn geschwindigkeit von zelle und individuel (wkeit darueber) bewegung bedeuten
	protected boolean canMove( Individual i ) {
		if( this.caController().getCA().getTimeStep() > i.getStepEndTime() ) {
			return true;
		}
		return false;
	}

}
