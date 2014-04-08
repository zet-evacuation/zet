/**
 * SerialTask.java
 * Created: Jul 28, 2010,5:45:06 PM
 */
package zet.tasks;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.coga.common.algorithm.AlgorithmStartedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SerialTask extends SwingWorker<Void, AlgorithmEvent> implements AlgorithmListener {
	private static Logger log = Logger.getGlobal();

	ArrayList<Algorithm<?,?>> algorithms;

	public SerialTask() {
		algorithms = new ArrayList<>();
	}
	private RuntimeException error = null;

	/**
	 *
	 * @return
	 */
	public RuntimeException getError() {
		return error;
	}

	public boolean isError() {
		return error != null;
	}

	public SerialTask( Algorithm<?,?> algorithm ) {
		algorithms = new ArrayList<>();
		algorithms.add( algorithm );
	}

	public void add( Algorithm<?,?> algorithm ) {
		algorithms.add( algorithm );
	}

	@Override
	protected Void doInBackground() throws Exception {
		try {
			for( Algorithm<?,?> algorithm : algorithms ) {
				algorithm.addAlgorithmListener( this );
				algorithm.run();
			}
		} catch( RuntimeException ex ) {
			this.error = ex;
		}

		return null;
	}

	public void test() throws Exception {
		doInBackground();
	}

	/**
	 * Takes events thrown by the algorithms and forwards it to the swing workers
	 * publish method. Thus, a listener to the swing worker can also get progress.
	 * @param event
	 */
	@Override
	public void eventOccurred( AlgorithmEvent event ) {
		publish( event );
	}

	@Override
	protected void process( List<AlgorithmEvent> chunks ) {
		for( AlgorithmEvent event : chunks ) {
			if( event instanceof AlgorithmStartedEvent ) {
				Logger.getGlobal().fine( "Gestartet: " + ((AlgorithmStartedEvent)event).getAlgorithm().getName() );
			}
		}
	}
}
