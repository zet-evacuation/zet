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

import opengl.framework.abs.AbstractControl;
import opengl.framework.abs.AbstractDrawable;
import opengl.framework.abs.Controlable;


/**
 *
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
