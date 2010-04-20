/**
 * DrawableControlable.java
 * input:
 * output:
 *
 * method:
 *
 * Created: Mar 9, 2010,5:26:22 PM
 */
package opengl.framework.abs;

import opengl.helper.Frustum;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface DrawableControlable extends Drawable, Controlable {
	void setFrustum( Frustum frustum );
	Frustum getFrustum();
}
