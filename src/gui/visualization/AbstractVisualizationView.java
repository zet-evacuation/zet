/**
 * Class JVisualizationView
 * Erstellt 08.06.2008, 00:12:49
 */

package gui.visualization;

import gui.components.AbstractSplitPropertyWindow;
import javax.media.opengl.GLCanvas;

/**
 *
 * @param <T> 
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractVisualizationView<T extends GLCanvas> extends AbstractSplitPropertyWindow<VisualizationPanel<T>> {
	private T canvas;
	
	/**
	 * 
	 * @param vis
	 */
	public AbstractVisualizationView( VisualizationPanel<T> vis ) {
		super( vis );
		canvas = vis.getGLContainer();
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	protected String getTitleBarText() {
		return "";
	}
	
	/**
	 * 
	 * @return
	 */
	public final T getGLContainer() {
		return canvas;
	}

	/**
	 * 
	 */
	 public void localize() {

	}
}
