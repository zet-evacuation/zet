/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *//**
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
	 * Sets this vector as normal in {@code OpenGL} using the <code>glNormal3d()</code>
	 * method. The vector is not normalized!
	 * @param gl the GL context
	 */
	public final void normal( GL gl ) {
		gl.glNormal3d( x, y, z );
	}
	
	public void update(){ }
}
