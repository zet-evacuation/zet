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
package de.zet_evakuierung.visualization.ca.draw;

import javax.media.opengl.GL2;

import com.google.common.annotations.VisibleForTesting;
import de.zet_evakuierung.visualization.ca.model.GLRoomModel;
import gui.visualization.VisualizationOptionManager;
import org.zetool.opengl.drawingutils.GLVector;
import org.zetool.opengl.framework.abs.AbstractDrawable;

/**
 * Draws a room. That is, it draws a ground rectangle for the room.
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLRoom extends AbstractDrawable<GLCell, GLRoomModel> {

    /** Top left coordinate of the room bounding box. */
    private GLVector topLeft;
    /** Top right coordinate of the room bounding box. */
    private GLVector topRight;
    /** Bottom right coordinate of the room bounding box. */
    private GLVector bottomRight;
    /** Bottom left coordinate of the room bounding box. */
    private GLVector bottomLeft;

    /**
     * Draws the room. The top left corner of the room will be located at {@code  (0, 0, z)}.
     *
     * @param model
     */
    public GLRoom(GLRoomModel model) {
        super(model, computePosition(model));
        System.out.println("GRID Property is: " + VisualizationOptionManager.showSpaceBetweenCells());
        if (VisualizationOptionManager.showSpaceBetweenCells()) {
            topLeft = new GLVector(0, 0, -0.1);
            topRight = new GLVector(model.getWidth(), 0, -0.1);
            bottomLeft = new GLVector(0, model.getHeight(), -0.1);
            bottomRight = new GLVector(model.getWidth(), model.getHeight(), -0.1);
        }
    }

    private static GLVector computePosition(GLRoomModel model) {
        double xPosition = model.getXPosition();
        double yPosition = model.getYPosition();
        return new GLVector(xPosition, yPosition, 0);
    }

    @Override
    public void update() {
    }

    /**
     * Draws the underlying floor of the room.
     *
     * @param gl the Java OpenGL object
     */
    @Override
    public void performDynamicDrawing(GL2 gl) {
        if (VisualizationOptionManager.showSpaceBetweenCells()) {
            // draw a floor
            VisualizationOptionManager.getCellSeperationColor().draw(gl);
            gl.glBegin(GL2.GL_QUADS);
            gl.glNormal3d(0, 0, 1);
            topLeft.draw(gl);
            topRight.draw(gl);
            bottomRight.draw(gl);
            bottomLeft.draw(gl);
            gl.glEnd();
        }
    }

    @VisibleForTesting
    GLVector getTopLeft() {
        return topLeft;
    }

    @VisibleForTesting
    GLVector getTopRight() {
        return topRight;
    }

    @VisibleForTesting
    GLVector getBottomRight() {
        return bottomRight;
    }

    @VisibleForTesting
    GLVector getBottomLeft() {
        return bottomLeft;
    }

    @Override
    public String toString() {
        return "GLRoom";
    }
}
