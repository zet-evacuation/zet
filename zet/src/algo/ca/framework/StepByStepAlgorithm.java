
package algo.ca.framework;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
final class StepByStepAlgorithm extends AbstractEvacuationCellularAutomatonWrapper {
	StepByStepAlgorithm( EvacuationCellularAutomatonAlgorithm eca ) {
		super( eca );
	}

	@Override
	protected void perform() {
		setPaused( true );
		while( !wrapped.isFinished() ) {
			while( isPaused() )
				try {
					Thread.sleep( 500 );
				} catch( InterruptedException ignore ) {
					//setPaused( false );
				}
			wrapped.performStep();
			setPaused( true );
		}
	}
}
