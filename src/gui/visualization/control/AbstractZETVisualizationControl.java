/**
 * AbstractZETVisualizationControl.java
 * input:
 * output:
 *
 * method:
 *
 * Created: Mar 5, 2010,10:05:08 AM
 */
package gui.visualization.control;

import org.zetool.opengl.framework.abs.AbstractControl;
import org.zetool.opengl.framework.abs.AbstractDrawable;
import org.zetool.opengl.framework.abs.Controlable;


/**
 *
 * @param <U>
 * @param <V> 
 * @param <W>
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractZETVisualizationControl<U extends AbstractControl<?, ?>, V extends AbstractDrawable<?, ?>, W extends Controlable> extends AbstractControl<U,V> {
	protected W mainControl;

	public AbstractZETVisualizationControl( V controlled, W mainControl ) {
		super( controlled );
		this.mainControl = mainControl;
	}

	public AbstractZETVisualizationControl( W mainControl ) {
		this.mainControl = mainControl;
	}

	/**
	 * Does not set main control. Need to set it manually!
	 */
	public AbstractZETVisualizationControl() {

	}

}
