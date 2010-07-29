/**
 * SerialTask.java
 * Created: Jul 28, 2010,5:45:06 PM
 */
package tasks;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmStartedEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SerialTask extends SwingWorker<Void, AlgorithmEvent> implements AlgorithmListener {

	ArrayList<Algorithm> algorithms;

	public SerialTask() {
		algorithms = new ArrayList<Algorithm>();
	}

	public SerialTask( Algorithm algorithm ) {
		algorithms = new ArrayList<Algorithm>();
		algorithms.add( algorithm );
	}

	public void add( Algorithm algorithm ) {
		algorithms.add( algorithm );
	}

	@Override
	protected Void doInBackground() throws Exception {
		for( Algorithm algorithm : algorithms ) {
			algorithm.addAlgorithmListener( this );
			algorithm.run();
		}

		return null;
	}

	public void eventOccurred( AlgorithmEvent event ) {
		publish( event );
	}

	@Override
	protected void process( List<AlgorithmEvent> chunks ) {
		for( AlgorithmEvent event : chunks ) {
			if( event instanceof AlgorithmStartedEvent ) {
				System.out.println( "Gestartet: " + ((AlgorithmStartedEvent)event).getAlgorithm() );
			}
		}
	}
}
