/*
 * zet evacuation tool copyright © 2007-20 zet evacuation team
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

import java.util.function.Function;

import javax.media.opengl.GL2;

import de.zet_evakuierung.visualization.ca.control.GLCellControl;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ZETGLControl.CellInformationDisplay;
import org.zetool.common.util.Direction8;
import org.zetool.opengl.drawingutils.GLColor;
import org.zetool.opengl.drawingutils.GLVector;
import org.zetool.opengl.framework.abs.AbstractDrawable;

public class GLCell extends AbstractDrawable<GLCell, GLCellControl> {

    /** Top left coordinate of the squre cell. */
    private static GLVector topLeft;
    /** Top right coordinate of the squre cell. */
    private static GLVector topRight;
    /** Bottom left coordinate of the squre cell. */
    private static GLVector bottomLeft;
    /** Bottom right coordinate of the squre cell. */
    private static GLVector bottomRight;
    /** Current color of the cell floor. */
    protected GLColor color;
    /** Default color of the cell floor. */
    private final GLColor defaultColor;
    /** Function that allows to query for a neighbor color. */
    private final Function<Direction8, GLColor> neighborColor;

    public GLCell(GLCellControl model, Function<Direction8, GLColor> neighbourColour) {
        //this( model, VisualizationOptionManager.getCellFloorColor() );
        this(model, VisualizationOptionManager.getCellWallColor(), neighbourColour);
    }

    public GLCell(GLCellControl control, GLColor color, Function<Direction8, GLColor> neighbourColour) {
        super(control, new GLVector(control.getXPosition(), control.getYPosition(), 0));
        if (topLeft == null) {
            topLeft = new GLVector(control.getOffset(), -control.getOffset(), 0);
            topRight = new GLVector(control.getWidth(), -control.getOffset(), 0);
            bottomLeft = new GLVector(control.getOffset(), -control.getWidth(), 0);
            bottomRight = new GLVector(control.getWidth(), -control.getWidth(), 0);
        }
        this.color = color;
        this.defaultColor = color;
        this.neighborColor = neighbourColour;
    }

    @Override
    public void performDynamicDrawing(GL2 gl) {
        if (VisualizationOptionManager.smoothCellVisualization()) {
            boolean lighting = gl.glIsEnabled(GL2.GL_LIGHTING);
            gl.glBegin(GL2.GL_QUADS);
            gl.glNormal3d(0, 0, 1);
            mixColorWithNeighbours(Direction8.TopLeft).draw(gl, lighting);
            topLeft.draw(gl);
            mixColorWithNeighbours(Direction8.TopRight).draw(gl, lighting);
            topRight.draw(gl);
            mixColorWithNeighbours(Direction8.DownLeft).draw(gl, lighting);
            bottomRight.draw(gl);
            mixColorWithNeighbours(Direction8.DownRight).draw(gl, lighting);
            bottomLeft.draw(gl);
            gl.glEnd();
        } else {
            color.draw(gl);
            gl.glBegin(GL2.GL_QUADS);
            gl.glNormal3d(0, 0, 1);
            topLeft.draw(gl);
            topRight.draw(gl);
            bottomRight.draw(gl);
            bottomLeft.draw(gl);
            gl.glEnd();
        }
    }
    
    /**
     * Creates a mixed colour for the cell. The direction indicates for which edge of the cell the colour is calculated.
     *
     * @param direction the edge of the cell
     * @return the mixed color for that edge
     */
    public GLColor mixColorWithNeighbours(Direction8 direction) {
        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();

        Direction8[] adjacentNeighbours = new Direction8[3];
        switch (direction) {
            case TopLeft:
                adjacentNeighbours[0] = Direction8.Top;
                adjacentNeighbours[1] = Direction8.TopLeft;
                adjacentNeighbours[2] = Direction8.Left;
                break;
            case TopRight:
                adjacentNeighbours[0] = Direction8.Top;
                adjacentNeighbours[1] = Direction8.TopRight;
                adjacentNeighbours[2] = Direction8.Right;
                break;
            case DownRight:
                adjacentNeighbours[0] = Direction8.Down;
                adjacentNeighbours[1] = Direction8.DownLeft;
                adjacentNeighbours[2] = Direction8.Left;
                break;
            case DownLeft:
                adjacentNeighbours[0] = Direction8.Down;
                adjacentNeighbours[1] = Direction8.DownRight;
                adjacentNeighbours[2] = Direction8.Right;
                break;
            default:
                return new GLColor(1, 1, 1);
        }
        int count = 1;
        for (int i = 0; i < 3; i++) {
            Direction8 currentNeighbor = adjacentNeighbours[i];
            if (model.isNeighborPresent(currentNeighbor)) {
                count++;
                GLColor neighbourColor = neighborColor.apply(currentNeighbor);
                r += neighbourColor.getRed();
                g += neighbourColor.getGreen();
                b += neighbourColor.getBlue();
            }
        }
        r /= count;
        g /= count;
        b /= count;
        return new GLColor(r, g, b, 1);
    }    

    /**
     * Updates the graphical representation of the cell. The current floor color is calculated.
     */
    protected void updateFloorColor() {
        if (model.getDisplayMode() == CellInformationDisplay.NoPotential) {
            color = getDefaultColor();
        } else {
            color = potentialToColor(model.getCellInformation(model.getDisplayMode()),
                    model.getMaxCellInformation(model.getDisplayMode()),
                    VisualizationOptionManager.getCellInformationLowColor(model.getDisplayMode()),
                    VisualizationOptionManager.getCellInformationHighColor(model.getDisplayMode()));
        }
    }

    protected final GLColor potentialToColor(long potential, long maxPotential, GLColor lowColor, GLColor highColor) {
        if (model.isPotentialValid()) {
            return lowColor.blend(highColor, potential / (double) maxPotential);
        } else {
            return VisualizationOptionManager.getInvalidPotentialColor();
        }
    }

    public final GLColor getDefaultColor() {
        return defaultColor;
    }

    @Override
    public void update() {
        updateFloorColor();
    }

    @Override
    public String toString() {
        return "GLCell";
    }

    public GLColor getColor() {
        return color;
    }
}
