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
package gui.visualization.draw.ca;

import javax.media.opengl.GL2;

import gui.visualization.control.ca.CellularAutomatonVisualizationModel;
import gui.visualization.control.ca.GLCellularAutomatonControl;
import org.zetool.opengl.framework.abs.AbstractDrawable;

public class GLCA extends AbstractDrawable<GLCAFloor, GLCellularAutomatonControl> {

    private final CellularAutomatonVisualizationModel cellularAutomatonVisualizationModel;
    private long lastStep = 0;

    public GLCA(GLCellularAutomatonControl control, CellularAutomatonVisualizationModel cellularAutomatonVisualizationModel) {
        super(control);
        this.cellularAutomatonVisualizationModel = cellularAutomatonVisualizationModel;
    }

    @Override
    public void update() {
    }

    @Override
    public String toString() {
        return "GLCA";
    }

    @Override
    public void performDrawing(GL2 gl) {
        long step = (long) cellularAutomatonVisualizationModel.getStep();
        if (step != lastStep) {
            getControl().stepUpdate();
            lastStep = step;
        }

        super.performDrawing(gl);
    }
}
