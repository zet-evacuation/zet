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


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractZETVisualizationControl<U extends AbstractControl<?, ?>, V extends AbstractDrawable<?, ?>> extends AbstractControl<U,V> {
	protected GLControl mainControl;

	public AbstractZETVisualizationControl( V controlled, GLControl mainControl ) {
		super( controlled );
		this.mainControl = mainControl;
	}

	public AbstractZETVisualizationControl( GLControl mainControl ) {
		this.mainControl = mainControl;
	}

}
