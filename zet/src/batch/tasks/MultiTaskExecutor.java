/**
 * Class MultiTaskExecutor
 * Erstellt 21.07.2008, 00:30:27
 */

package batch.tasks;

import gui.components.progress.ProgressTaskEntry;
import java.util.ArrayList;
import tasks.AlgorithmTask;
import util.Helper;

/**
 * A <code>Runnable</code> class that allows to execute several tasks
 * implementing <code>Runnable</code> themselves. The tasks are expected to be
 * in an list of {@link ProgressTaskEntry} objects.
 * @author Jan-Philipp Kappmeier
 */
public class MultiTaskExecutor implements Runnable {
	/** The list of tasks*/
	private ArrayList<ProgressTaskEntry> tasks;
	
	/**
	 * Creates a new instance of the multiple task executor.
	 * @param tasks the list of tasks that is to be executed
	 */
	public MultiTaskExecutor( ArrayList<ProgressTaskEntry> tasks ) {
		this.tasks = tasks;
	}
	
	boolean done = false;

	public boolean isDone() {
		return done;
	}
	
	boolean closed = false;

	public void setClosed( boolean closed ) {
		this.closed = closed;
	}
	
	/**
	 * Starts executing the submitted tasks. Before a task is started, its name
	 * is send using the {@link task.AlgorithmTask} interface.
	 * @see ProgressTaskEntry
	 */
	public void run() {
		for( ProgressTaskEntry task : tasks ) {
			AlgorithmTask.getInstance().publish( task.title );
			task.task.run();
		}
		done = true;
		AlgorithmTask.getInstance().publish( "Fertig" );
		while( !(AlgorithmTask.getInstance().getProgress() == 0) ) {
			Helper.pause( 500 );
		}
		for( int i = 1; i <= 100; i++ ) {
			if( closed )
				return;
			AlgorithmTask.getInstance().publish( i );
			Helper.pause( 500 );
		}		
	}
}
