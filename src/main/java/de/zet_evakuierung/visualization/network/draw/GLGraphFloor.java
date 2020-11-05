/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.zet_evakuierung.visualization.network.draw;

import de.zet_evakuierung.visualization.network.control.GLGraphFloorControl;
import org.zetool.opengl.drawingutils.GLVector;
import org.zetool.opengl.framework.abs.AbstractDrawable;

/**
 * <p>
 * This class draws a floor in the graph (which does not explicitly exist in the graph itself). It consists of the nodes
 * belonging to rooms on one floor in a {@link ds.Project}.</p>
 * <p>
 * The nodes are stored in a display list to speed up the visualization, the display list is created if
 * {@link #performStaticDrawing(javax.media.opengl.GLAutoDrawable)} is called. During normal visualization (when
 * {@link #performStaticDrawing(javax.media.opengl.GLAutoDrawable)} is called) the display list is executed. The display
 * list is rebuilt when some settings are updated.</p>
 *
 * @see AbstractDrawable
 * @author Jan-Philipp Kappmeier
 */
public class GLGraphFloor extends AbstractDrawable<GLNode, GLGraphFloorControl> {

    public GLGraphFloor(GLGraphFloorControl model) {
        super(model, new GLVector(model.getXPosition(), model.getYPosition(), 0));
    }

}
