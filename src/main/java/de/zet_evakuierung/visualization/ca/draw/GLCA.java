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

import de.zet_evakuierung.visualization.ca.model.CellularAutomatonVisualizationModel;
import de.zet_evakuierung.visualization.ca.model.GLRootModel;
import org.zetool.opengl.framework.abs.HierarchyRoot;

public class GLCA extends HierarchyRoot<GLCAFloor> {

    private final CellularAutomatonVisualizationModel cellularAutomatonVisualizationModel;
    private long lastStep = 0;
    private final GLRootModel model;

    public GLCA(GLRootModel model, CellularAutomatonVisualizationModel cellularAutomatonVisualizationModel) {
        super();
        this.model = model;
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
    public void performDynamicDrawing(GL2 gl) {
        long step = (long) cellularAutomatonVisualizationModel.getStep();
        if (step != lastStep) {
            model.stepUpdate();
            lastStep = step;
        }
    }
}
