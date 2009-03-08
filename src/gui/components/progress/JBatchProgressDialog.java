/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class JBatchProgressDialog
 * Erstellt 19.07.2008, 17:42:07
 */

package gui.components.progress;

import batch.tasks.MultiTaskExecutor;
import info.clearthought.layout.TableLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import tasks.AlgorithmTask;
import util.Helper;

/**
 * <p>A window containing a <code>JProgressBar</code> and some <code>JLabel</code>
 * objects. The lables display the status of a task and its name.</p>
 * <p>The window takes some tasks as {@link ProgressTaskEntry} and allows
 * executing them all at once.</p>
 * @see AlgorithmTask
 * @see MultiTaskExecutor
 * @author Jan-Philipp Kappmeier
 */
public class JBatchProgressDialog extends javax.swing.JDialog {
	/** A list of all tasks */
	private ArrayList<ProgressTaskEntry> tasks;
	/** A label for detailed information. */
  private JLabel lblDetailedInformation;
	/** A label for the current tasks status. */
  private JLabel lblInformation;
	/** The progress bar. */
  private JProgressBar progressBar;
	/** A label for the current tasks name. */
	private JLabel lblBatchStatus;

	/**
	 * Creates new form JBatchProgressDialog. Initializes all components and sets
	 * up the position and size of the window.
	 * @param parent
	 * @param title 
	 * @param modal
	 */
	public JBatchProgressDialog(java.awt.Frame parent, String title, boolean modal ) {
		super( parent, title, true );
		addComponents();
		pack();
		setSize( 500, 150 );
		setLocation( parent.getX() + ( parent.getWidth() - this.getWidth() ) / 2, parent.getY() + ( parent.getHeight() - this.getHeight() ) / 2 );
		tasks = new ArrayList<ProgressTaskEntry>();
	}
	
	MultiTaskExecutor mte = null;
	
	/**
	 * Starts the execution of the tasks in the task list. A new {@link AlgorithmTask}
	 * instance is created and started in a new thread.
	 * @see MultiTaskExecutor
	 */
	public void start() {
		// Execute task
		AlgorithmTask worker = AlgorithmTask.getNewInstance();
		mte = new MultiTaskExecutor( tasks );
		worker.setTask( mte );
		worker.addPropertyChangeListener( pcl );
		try {
			worker.executeAlgorithm( true );
		} catch( Exception ex ) {
			System.out.println( "Fehler trat auf" );
		} finally {
		}
	}

	/**
	 * Initializes the components of the window.
	 */
	private void addComponents() {
		final int space = 16;
		double size[][] = // Columns
						{
			{ space, TableLayout.FILL, space },
			//Rows
			{ space,
				TableLayout.PREFERRED, // Label
				space,
				TableLayout.PREFERRED, // ProgressBar
				space/2,
				TableLayout.PREFERRED, // Label
				TableLayout.PREFERRED, // Label
				space
			}
		};

		this.setLayout( new TableLayout( size ) );
		int row = 1;

    lblBatchStatus = new JLabel();
    progressBar = new JProgressBar();
    lblInformation = new JLabel();
    lblDetailedInformation = new JLabel();
		
		add( lblBatchStatus, "1, " + row++ );
		row++;
		add( progressBar, "1, " + row++ );
		add( lblInformation, "1, " + row++ );
		add( lblDetailedInformation, "1, " + row++ );
	}
	
	/**
	 * Adds a task to the task list.
	 * @param title the title of the task. it is displayed on a label during execution.
	 * @param task the task
	 */
	public void addTask( String title, Runnable task ) {
		ProgressTaskEntry entry = new ProgressTaskEntry();
		entry.title = title;
		entry.task = task;
		tasks.add( entry );
	}
	
	/**
	 * Checks whether the task execution is finished, or not. Note that the method
	 * may give a wrong result if {@link start()} is not called or another
	 * task has started in the meantime!
	 * @return true if the task is finished.
	 */
	public boolean isFinished() {
		if( AlgorithmTask.getInstance().isDone() )
			return true;
		else
			return false;
	}

	/** The listener for progress updates. */
	protected PropertyChangeListener pcl = new PropertyChangeListener() {
		public void propertyChange( PropertyChangeEvent evt ) {
			if( evt.getPropertyName().equals( "progress" ) ) {
				int progress = (Integer)evt.getNewValue();
				handleProgressEvent( progress );
			}
			if( mte != null && mte.isDone() ) {
				mte.setClosed( true );
				setVisible( false );
				System.err.println( "WIRD GESCHLOSSEN" );
			}
		}
	};

	/**
	 * Updates the window if a progress occured.
	 * @param progress
	 */
	protected void handleProgressEvent( int progress ) {
		progressBar.setValue( progress );
		lblInformation.setText( AlgorithmTask.getInstance().getProgressInformation() );
		lblDetailedInformation.setText( AlgorithmTask.getInstance().getDetailedProgressInformation() );
		lblBatchStatus.setText( AlgorithmTask.getInstance().getName() );
	}
}
