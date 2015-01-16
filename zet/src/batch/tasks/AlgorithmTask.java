/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * GraphCreate.java
 * Created on 23.01.2008, 23:13:35
 */

package batch.tasks;

import org.zetool.common.algorithm.AlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmProgressEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import event.EventServer;
import event.ProgressEvent;
import java.util.List;
import javax.swing.SwingWorker;

// this system is not optimal, but should work well for our system of different
// algorithms in a lot of classes. Otherwise we need to create objects for each
// algorithm, probably like a factory. This would lead to more or less grave
// changes to the algorithm classes as they have to know their SwingWorker.
/**
 * A singleton task starting class. As a {@code SwingWorker} class can only
 * be executed once, this singleton has the method {@code #getNewInstance()}
 * that returns a new instance. It is recommended to use this method before
 * starting the execution of a task, otherwise it can happen that the task is
 * not executed! <p>
 * Be aware, that during process runtime {@code getNewInstance()} should
 * not be called.
 * @author Jan-Philipp Kappmeier
 */
public class AlgorithmTask extends SwingWorker<Integer, ProcessUpdateMessage> implements AlgorithmListener {
	private static AlgorithmTask instance;
	private boolean useUpdates;
	private Runnable algorithmTask;
	private String taskName;
	private String taskProgressInformation;
	private String taskDetailedProgressInformation;
	private boolean oldUpdateStatus;

	@Override
	public void eventOccurred( AlgorithmEvent event ) {
		if( event instanceof AlgorithmProgressEvent ) {
			System.out.print( "Here;" );
			publish( new ProcessUpdateMessage( ((AlgorithmProgressEvent) event).getProgressAsInteger(), "taskName", "taskProgressInformation", "taskDetailedProgressInformation" ) );
		}
	}

	/**
	 *
	 */
	private AlgorithmTask() {
		useUpdates = false;
		oldUpdateStatus = false;
	}

	/**
	 * Returns this singleton object
	 * @return object of RandomUtils
	 */
	public static AlgorithmTask getInstance() {
		if( instance == null )
			instance = new AlgorithmTask();
		return instance;
	}

	/**
	 * Returns a new instance of this singleton object.
	 * @return a new instance of this singleton object
	 */
	public static AlgorithmTask getNewInstance() {
		if( instance != null )
			instance.cancel( true );
		instance = new AlgorithmTask();
		return instance;
	}

	@Override
	public Integer doInBackground() throws Exception {
		if( algorithmTask != null )
			try {
				algorithmTask.run();
			} catch( Exception ex ) {
				ex.printStackTrace( System.err );
			}
		return 1;
	}

	@Override
	protected void process( List<ProcessUpdateMessage> chunks ) {
		if( chunks.size() > 0 ) {
			ProcessUpdateMessage pum = chunks.get( chunks.size() - 1 );
			EventServer.getInstance().dispatchEvent( new ProgressEvent( this, pum ) );
			
		}
	}

	//	public void publish( String taskProgressInformation, String taskDetailedProgressInformation ) {
//		this.taskProgressInformation = taskProgressInformation;
//		this.taskDetailedProgressInformation = taskDetailedProgressInformation;
//		int progress = getProgress();
//		progress = (progress + 1) % 2;
//		setProgress( progress );
//		publish( new ProcessUpdateMessage( -1, taskName, taskProgressInformation, taskDetailedProgressInformation ) );
//	}
//
//	public void publish( int progress, String taskProgressInformation, String taskDetailedProgressInformation ) {
//		this.taskProgressInformation = taskProgressInformation;
//		this.taskDetailedProgressInformation = taskDetailedProgressInformation;
//		setProgress( progress );
//		publish( new ProcessUpdateMessage( progress, taskName, taskProgressInformation, taskDetailedProgressInformation ) );
//	}
//
	/**
	 * This method publishes a new task that is started. It sets the task name
	 * to the submitted name and resets the progress information and sets the
	 * progress to 0.
	 * @param taskName the name of the new task.
	 */
//	public void publish( String taskName ) {
//		this.taskName = taskName;
//		this.taskProgressInformation = "";
//		this.taskDetailedProgressInformation = "";
//		setProgress( 0 );
//		publish( new ProcessUpdateMessage( 0, taskName, taskProgressInformation, taskDetailedProgressInformation ) );
//	}

	/**
	 * Publishes a new progress without changing either the task name or the
	 * progress information status.
	 * @param progress the name of the new task.
	 */
//	public void publish( int progress ) {
//		setProgress( progress );
//		publish( new ProcessUpdateMessage( progress, taskName, taskProgressInformation, taskDetailedProgressInformation ) );
//	}

	/**
	 * Sets a new progress status and new status information texts.
	 * @param progress the progress in the interval 0 to 100
	 * @param taskProgressInformation an information text
	 * @param taskDetailedProgressInformation some detailed information text
	 */
	public void setProgress( int progress, String taskProgressInformation, String taskDetailedProgressInformation ) {
		this.taskProgressInformation = taskProgressInformation;
		this.taskDetailedProgressInformation = taskDetailedProgressInformation;
		setProgress( progress );
	}

	/**
	 * Sets a new progress status, a new task name and new status information
	 * texts.
	 * @param progress the progress in the interval 0 to 100
	 * @param taskName the new name of the task
	 * @param taskProgressInformation an information text
	 * @param taskDetailedProgressInformation some detailed information text
	 */
	public void setProgress( int progress, String taskName, String taskProgressInformation, String taskDetailedProgressInformation ) {
		this.taskName = taskName;
		this.taskProgressInformation = taskProgressInformation;
		this.taskDetailedProgressInformation = taskDetailedProgressInformation;
		setProgress( progress );
	}

	/**
	 * Sets the task that is executed.
	 * @param task the task that is executed
	 */
	public void setTask( Runnable task ) {
		algorithmTask = task;
	}

	/**
	 * Is called when the task has finished.
	 */
	@Override
	public void done() {
		useUpdates = oldUpdateStatus;
	}

	// the use useUpdates value that is set is only used for one execution!
	/**
	 *
	 * @param useUpdates
	 */
	public void executeAlgorithm( boolean useUpdates ) {
		oldUpdateStatus = this.useUpdates;
		this.useUpdates = useUpdates;
		execute();
	}

	public void useUpdates( boolean useUpdates ) {
		this.useUpdates = useUpdates;
		oldUpdateStatus = useUpdates;
	}

	public boolean useUpdates() {
		return useUpdates;
	}
}
