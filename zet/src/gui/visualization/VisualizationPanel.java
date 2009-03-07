/**
 * Class AbstractVisualizationPanel
 * Erstellt 08.05.2008, 23:38:40
 */

package gui.visualization;

import java.awt.BorderLayout;
import javax.media.opengl.GLCanvas;
import javax.swing.JPanel;

/**
 *
 * @param <T> 
 * @author Jan-Philipp Kappmeier
 */
public class VisualizationPanel<T extends GLCanvas> extends JPanel {
	T glContainer;
	
	public VisualizationPanel( T glContainer ) {
		this.glContainer = glContainer;
		setLayout( new BorderLayout() );
		this.add( glContainer, BorderLayout.CENTER );
	}
	
	public T getGLContainer() {
		return glContainer;
	}
	
	public void setGLContainer( T glContainer ) {
		this.glContainer = glContainer;
	}
}
