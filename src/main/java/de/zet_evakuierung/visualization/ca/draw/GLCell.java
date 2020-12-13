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

    public GLCell(GLCellControl model) {
        //this( model, VisualizationOptionManager.getCellFloorColor() );
        this(model, VisualizationOptionManager.getCellWallColor());
    }

    public GLCell(GLCellControl control, GLColor color) {
        super(control, new GLVector(control.getXPosition(), control.getYPosition(), 0));
        if (topLeft == null) {
            topLeft = new GLVector(control.getOffset(), -control.getOffset(), 0);
            topRight = new GLVector(control.getWidth(), -control.getOffset(), 0);
            bottomLeft = new GLVector(control.getOffset(), -control.getWidth(), 0);
            bottomRight = new GLVector(control.getWidth(), -control.getWidth(), 0);
        }
        this.color = color;
        this.defaultColor = color;
    }

    @Override
    public void performDynamicDrawing(GL2 gl) {
        if (VisualizationOptionManager.smoothCellVisualization()) {
            boolean lighting = gl.glIsEnabled(GL2.GL_LIGHTING);
            gl.glBegin(GL2.GL_QUADS);
            gl.glNormal3d(0, 0, 1);
            getModel().mixColorWithNeighbours(Direction8.TopLeft).draw(gl, lighting);
            topLeft.draw(gl);
            getModel().mixColorWithNeighbours(Direction8.TopRight).draw(gl, lighting);
            topRight.draw(gl);
            getModel().mixColorWithNeighbours(Direction8.DownLeft).draw(gl, lighting);
            bottomRight.draw(gl);
            getModel().mixColorWithNeighbours(Direction8.DownRight).draw(gl, lighting);
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
