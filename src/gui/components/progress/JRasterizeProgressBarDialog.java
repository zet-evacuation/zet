/*
 * JRasterizeProgressBarDialog.java
 * Created on 24.01.2008, 23:59:59
 */

package gui.components.progress;

import gui.JEditor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker.StateValue;
import tasks.AlgorithmTask;
import tasks.RasterizeTask;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JRasterizeProgressBarDialog extends JProgressBarDialog {

	/** Creates new form JProgressBarDialog
	 * @param parent
	 * @param title 
	 * @param modal
	 * @param task 
	 */
	public JRasterizeProgressBarDialog(java.awt.Frame parent, String title, boolean modal, RasterizeTask task ) {
		super( parent, title, modal, task );
		
		// Create new progress listener for rasterization
		pcl = new PropertyChangeListener() {
			public void propertyChange( PropertyChangeEvent evt ) {
				if( evt.getPropertyName().equals( "progress" ) ) {
					int progress = (Integer)evt.getNewValue();
					handleProgressEvent( progress );
					JEditor.getInstance().disableProjectUpdate( false );
					JEditor.getInstance().getEditView().displayProject();
					JEditor.getInstance().disableProjectUpdate( true );
					if( AlgorithmTask.getInstance().getState() == StateValue.DONE ) {
						JEditor.getInstance().disableProjectUpdate( false );
					}
				}
			}
		};
	}
	
	@Override
	public void executeTask() {
		// Execute task
		AlgorithmTask worker = AlgorithmTask.getNewInstance();
		worker.setTask( getTask() );
		worker.addPropertyChangeListener( pcl );
		try {
			JEditor.getInstance().disableProjectUpdate( true );
			worker.executeAlgorithm( true );
		} catch( Exception ex ) {
			System.out.println( "Fehler trat auf" );
		} finally { }		
	}
}
