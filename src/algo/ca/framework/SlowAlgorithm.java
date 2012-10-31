/**
 * SlowAlgorithm.java
 * Created: 31.10.2012, 14:51:21
 */
package algo.ca.framework;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
final class SlowAlgorithm extends AbstractEvacuationCellularAutomatonWrapper {
	SlowAlgorithm( EvacuationCellularAutomatonAlgorithm eca ) {
		super( eca );
	}

	@Override
	protected void perform() {
		while( !wrapped.isFinished() ) {
			try { // wait anyway
				Thread.sleep( 1000 );
			} catch( InterruptedException ignore ) {
				//setPaused( false );
			}
			while( isPaused() ) // wait longer if paused
				try {
					Thread.sleep( 1000 );
				} catch( InterruptedException ignore ) {
					//setPaused( false );
				}
			wrapped.performStep();
		}
	}
}
