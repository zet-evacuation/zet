/**
 * Do3Task.java
 * Created: 16.02.2011, 17:10:16
 */
package tasks;

import batch.CellularAutomatonAlgorithm;
import gui.AlgorithmControl;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Do3Task implements Runnable {



	@Override
	public void run() {
		// Step 1: create a CA
		final CellularAutomatonTaskStepByStep cat = new CellularAutomatonTaskStepByStep();

		cat.setCaAlgo( CellularAutomatonAlgorithm.InOrder );
		//cat.setProblem( project );
//		cat.addAlgorithmListener( listener );

//		final SerialTask st = new SerialTask( cat );
//		st.addPropertyChangeListener( new PropertyChangeListener() {
//			boolean first = true;
//
//			@Override
//			public void propertyChange( PropertyChangeEvent pce ) {
//				if( first ) {
//					while( cat.getCa() == null ) {
//						try {
//							Thread.sleep( 100 );
//						} catch( InterruptedException ex ) {
//							Logger.getLogger( AlgorithmControl.class.getName() ).log( Level.SEVERE, null, ex );
//						}
//					}
//					cellularAutomaton = cat.getCa();
//					mapping = cat.getMapping();
//					container = cat.getContainer();
//					caVisResults = null;
//					first = false;
//				}
//			}
//		});
//		if( propertyChangeListener != null )
//			st.addPropertyChangeListener( propertyChangeListener );

		System.out.println( "Start slow execution..." );

		//st.execute();
		cat.run();

		// Step 2: Set up the floor

		// Step3: Let the CA run
		
	}

}
