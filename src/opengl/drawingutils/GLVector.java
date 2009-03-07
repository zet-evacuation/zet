/**
 * Class GLVector
 * Erstellt 08.05.2008, 02:30:02
 */

package opengl.drawingutils;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import opengl.framework.abs.drawable;
import util.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLVector extends Vector3 implements drawable {
	public GLVector() {
		super();
	}
	
	public GLVector( double x, double y, double z ) {
		super( x, y, z );
	}
	
	public GLVector( Vector3 vec ) {
		super( vec.x, vec.y, vec.z );
	}
	
	public void draw( GLAutoDrawable drawable ) {
		drawable.getGL().glVertex3d( x, y, z );
	}
	
	public final void translate( GLAutoDrawable drawable ) {
		drawable.getGL().glTranslated(+1.0d*x, +1.0d*y, +1.0d*z);
	}
	
	/**
	 * Sets this vector as normal in OpenGL using the <code>glNormal3d()</code>
	 * method. The vector is not normalized!
	 * @param gl the GL context
	 */
	public final void normal( GL gl ) {
		gl.glNormal3d( x, y, z );
	}
	
	public void update(){ }
}
