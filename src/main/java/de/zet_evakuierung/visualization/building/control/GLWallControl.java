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
package de.zet_evakuierung.visualization.building.control;

import de.zet_evakuierung.visualization.building.draw.GLWall;
import gui.visualization.control.AbstractZETVisualizationControl;
import io.visualization.BuildingResults.Wall;
import org.zetool.opengl.framework.abs.HierarchyNode;

/**
 * @author Jan-Philipp Kappmeier
 * @author Daniel R. Schmidt
 */
public class GLWallControl extends AbstractZETVisualizationControl<GLWallControl, GLWall, BuildingVisualizationModel> implements HierarchyNode {

    /**
     * @param controlled
     * @param visualizationModel
     */
    public GLWallControl(Wall controlled, BuildingVisualizationModel visualizationModel) {
        super(visualizationModel);

        this.setView(new GLWall(controlled, visualizationModel));
        visualizationModel.wallProgress();
    }

    @Override
    public void delete() {
        view.delete();
    }
}
