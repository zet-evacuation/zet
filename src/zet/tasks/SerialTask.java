/**
 * SerialTask.java
 * Created: Jul 28, 2010,5:45:06 PM
 */
package zet.tasks;

import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.common.algorithm.AlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.algorithm.AlgorithmStartedEvent;
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

	ArrayList<AbstractAlgorithm<?,?>> algorithms;

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

	public SerialTask( AbstractAlgorithm<?,?> algorithm ) {
		algorithms = new ArrayList<>();
		algorithms.add( algorithm );
	}

	public void add( AbstractAlgorithm<?,?> algorithm ) {
		algorithms.add( algorithm );
	}

	@Override
	protected Void doInBackground() throws Exception {
		try {
			for( AbstractAlgorithm<?,?> algorithm : algorithms ) {
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
