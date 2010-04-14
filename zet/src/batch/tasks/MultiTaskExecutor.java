/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
/**
 * Class MultiTaskExecutor
 * Erstellt 21.07.2008, 00:30:27
 */
package batch.tasks;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import gui.components.progress.ProgressTaskEntry;
import java.util.ArrayList;
import de.tu_berlin.math.coga.common.util.Helper;

/**
 * A <code>Runnable</code> class that allows to execute several tasks
 * implementing <code>Runnable</code> themselves. The tasks are expected to be
 * in an list of {@link ProgressTaskEntry} objects.
 * @author Jan-Philipp Kappmeier
 */
public class MultiTaskExecutor implements Runnable {

    private AlgorithmListener algorithmListener;

    private boolean closed = false;

    private boolean done = false;

    /** The list of tasks*/
    private ArrayList<ProgressTaskEntry> tasks;

    /**
     * Creates a new instance of the multiple task executor.
     * @param tasks the list of tasks that is to be executed
     */
    public MultiTaskExecutor(ArrayList<ProgressTaskEntry> tasks) {
        this.tasks = tasks;
    }

    public boolean isDone() {
        return done;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void setAlgorithmListener(AlgorithmListener algorithmListener) {
        this.algorithmListener = algorithmListener;
    }

    /**
     * Starts executing the submitted tasks. Before a task is started, its name
     * is send using the {@link batch.tasks.AlgorithmTask} interface.
     * @see ProgressTaskEntry
     */
	public void run() {
		System.out.println( "Multi Task-Executor starts." );
		for( ProgressTaskEntry task : tasks ) {
			System.out.println( "Execute task: " + task.title );
			AlgorithmTask.getInstance().publish( task.title );
			if( task.task instanceof BatchGraphTask && algorithmListener != null )
				((BatchGraphTask) task.task).listener = algorithmListener;
			task.task.run();
		}
		done = true;
		AlgorithmTask.getInstance().publish( "Fertig" );
		while(!(AlgorithmTask.getInstance().getProgress() == 0))
			Helper.pause( 500 );
		for( int i = 1; i <= 100; i++ ) {
			if( closed )
				return;
			AlgorithmTask.getInstance().publish( i );
			Helper.pause( 500 );
		}
	}
}
