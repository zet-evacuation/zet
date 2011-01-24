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
/*
 * JRasterizeProgressBarDialog.java
 * Created on 24.01.2008, 23:59:59
 */

package gui.components.progress;

import zet.gui.main.JEditor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker.StateValue;
import batch.tasks.AlgorithmTask;
import batch.tasks.RasterizeTask;

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
					// TODO project update (maybe in control class?)
					//JEditor.getInstance().disableProjectUpdate( false );
					//JEditor.getInstance().getEditView().displayProject();
					//JEditor.getInstance().disableProjectUpdate( true );
					if( AlgorithmTask.getInstance().getState() == StateValue.DONE ) {
						//JEditor.getInstance().disableProjectUpdate( false );
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
			// TODO disableproject update during rasterization
			//JEditor.getInstance().disableProjectUpdate( true );
			worker.executeAlgorithm( true );
		} catch( Exception ex ) {
			System.out.println( "Fehler trat auf" );
		} finally { }		
	}
}
