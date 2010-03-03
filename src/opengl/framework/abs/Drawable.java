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
package opengl.framework.abs;

import javax.media.opengl.GL;

/**
 * <p>A class should implement the {@code Drawable} interface if the class can
 * draw itself somehow in {@code OpenGL}. In that case, the {@link #draw(GL)}-method
 * can be called by some framework or collection of drawable objects.</p>
 * <p>The {@link #update()}-method is called, whenever the general setting is
 * changed, so that the representation of the {@code Drabwale} could have
 * changed. This may be the case, if the color of the object is defined outside
 * of the object itself. This may also happen, if the object creates a display
 * list that has to be recreated.</p>
 * @author Jan-Philipp Kappmeier
 */
public interface Drawable {
  /**
	 * This method draws the object on an {@code OpenGL} graphcis context.
	 * @param drawable the graphics context
	 */
	void draw( GL gl );

	/**
	 * Is called, whenever the environment has changed and the graphical
	 * representation of the object needs to be changed.
	 */
	void update();
}

