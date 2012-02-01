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

/**
 * Class GLVector
 * Created 08.05.2008, 02:30:02
 */

package opengl.drawingutils;

import javax.media.opengl.GL;
import opengl.framework.abs.Drawable;
import de.tu_berlin.math.coga.math.vectormath.Vector3;

/**
 * An extension of the 3-dimensional {@link Vector3}, that has some
 * {@code OpenGL} features. It can be drawn as a point or used for translations.
 * @author Jan-Philipp Kappmeier
 */
public class GLVector extends Vector3 implements Drawable {
	
	/**
	 * Initializes a default vector in the origin.
	 */
	public GLVector() {
		super();
	}

	/**
	 * Initializes the vector by three real coordinates.
	 * @param x the first or {@code x}-coordinate
	 * @param y the second or {@code y}-coordinate
	 * @param z the third or {@code z}-coordinate
	 */
	public GLVector( double x, double y, double z ) {
		super( x, y, z );
	}

	/**
	 * Initializes this vector as a copy of a given vector.
	 * @param vec the given vector
	 */
	public GLVector( Vector3 vec ) {
		super( vec );
	}

	/**
	 * Draws this vector, that means, it is put using {@code glVertex3d}.
	 * @param gl the drawable context
	 */
	public void draw( GL gl ) {
		gl.glVertex3d( x, y, z );
	}

	/**
	 * Translates the world along the vector using {@code glTranslated}.
	 * @param drawable the drawable context
	 */
	public final void translate( GL gl ) {
		gl.glTranslated( (double)x, (double)y, (double)z );
	}
	
	/**
	 * Sets this vector as normal in {@code OpenGL} using the {@code glNormal3d}
	 * method. The vector itself is not normalized! To do this, use
	 * {@link #normalize()}.
	 * @param gl the GL context
	 */
	public final void normal( GL gl ) {
		gl.glNormal3d( x, y, z );
	}

	@Override
	public GLVector add( Vector3 v ) {
		return new GLVector( x + v.x, y + v.y, z + v.z );
	}
	
	public void update() { }

	public void delete() { }
}
