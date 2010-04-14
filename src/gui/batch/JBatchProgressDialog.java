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
 * Class JBatchProgressDialog
 * Erstellt 19.07.2008, 17:42:07
 */
package gui.batch;

import batch.tasks.AlgorithmTask;
import batch.tasks.MultiTaskExecutor;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmProgressEvent;
import de.tu_berlin.math.coga.common.debug.DebugEnum;
import ds.graph.Localization;
import info.clearthought.layout.TableLayout;
import gui.JEditor;
import gui.components.progress.ProgressTaskEntry;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * <p>A window containing a <code>JProgressBar</code> and some <code>JLabel</code>
 * objects. The lables display the status of a task and its name.</p>
 * <p>The window takes some tasks as {@link ProgressTaskEntry} and allows
 * executing them all at once.</p>
 * @see AlgorithmTask
 * @see MultiTaskExecutor
 * @author Jan-Philipp Kappmeier
 */
public class JBatchProgressDialog extends JDialog implements AlgorithmListener, PropertyChangeListener {

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

    private MultiTaskExecutor mte;

    /**
     * Creates new form JBatchProgressDialog. Initializes all components and sets
     * up the position and size of the window.
     */
    public JBatchProgressDialog() {
        super(JEditor.getInstance(), Localization.getInstance().getString( "batch.ProgressTitle" ), true);
        addComponents();
        pack();
        setSize(500, 150);
        setLocationRelativeTo(JEditor.getInstance());
        tasks = new ArrayList<ProgressTaskEntry>();
    }    

    /**
     * Starts the execution of the tasks in the task list. A new {@link AlgorithmTask}
     * instance is created and started in a new thread.
     * @see MultiTaskExecutor
     */
    public void start() {
        // Execute task
        AlgorithmTask worker = AlgorithmTask.getNewInstance();
        mte = new MultiTaskExecutor(tasks);
        mte.setAlgorithmListener(this);
        worker.setTask(mte);

        worker.addPropertyChangeListener(this);
        try {
            //mte.run();
            worker.executeAlgorithm(true);
        } catch (Exception ex) {
            System.out.println("Fehler trat auf");
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
            {space, TableLayout.FILL, space},
            //Rows
            {space,
                TableLayout.PREFERRED, // Label
                space,
                TableLayout.PREFERRED, // ProgressBar
                space / 2,
                TableLayout.PREFERRED, // Label
                TableLayout.PREFERRED, // Label
                space
            }
        };

        this.setLayout(new TableLayout(size));
        int row = 1;

        lblBatchStatus = new JLabel();
        progressBar = new JProgressBar();
        lblInformation = new JLabel();
        lblDetailedInformation = new JLabel();

        add(lblBatchStatus, "1, " + row++);
        row++;
        add(progressBar, "1, " + row++);
        add(lblInformation, "1, " + row++);
        add(lblDetailedInformation, "1, " + row++);
    }

    /**
     * Adds a task to the task list.
     * @param title the title of the task. it is displayed on a label during execution.
     * @param task the task
     */
    public void addTask(String title, Runnable task) {
        ProgressTaskEntry entry = new ProgressTaskEntry();
        entry.title = title;
        entry.task = task;
        tasks.add(entry);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        DebugEnum.println("Progress: " + evt.getPropertyName() + ": " + evt.getOldValue() + " -> " + evt.getNewValue(), DebugEnum.DebugLevel.DebugOut );
        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            lblInformation.setText(AlgorithmTask.getInstance().getProgressInformation());
            lblDetailedInformation.setText(AlgorithmTask.getInstance().getDetailedProgressInformation());
            lblBatchStatus.setText(AlgorithmTask.getInstance().getName());
        }
        if (mte != null && mte.isDone()) {
            mte.setClosed( true );
            setVisible( false );
            //System.err.println("WIRD GESCHLOSSEN");
        }
    }

    public void eventOccurred(AlgorithmEvent event) {
        if (event instanceof AlgorithmProgressEvent) {
            progressBar.setValue(((AlgorithmProgressEvent) event).getProgressAsInteger());
        }
    }
}
