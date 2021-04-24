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
package de.zet_evakuierung.visualization.building.model;

import java.util.List;
import java.util.Objects;

import de.zet_evakuierung.visualization.building.draw.GLBuilding;
import de.zet_evakuierung.visualization.building.draw.GLBuildingViews;
import gui.visualization.control.AbstractZETVisualizationControl;
import org.zetool.opengl.framework.abs.HierarchyNode;

/**
 * A control class that allows hiding and showing of walls on different floors.
 *
 * @author Jan-Philipp Kappmeier
 * @author Daniel R. Schmidt
 */
public class GLBuildingControl
        extends AbstractZETVisualizationControl<GLWallModel, GLBuilding, BuildingVisualizationModel>
        implements HierarchyNode<GLWallModel> {

    /**
     * Gives access to the model objects used by visualization views.
     */
    private final GLBuildingModel buildingModel;
    /**
     * Gives access to all view objects drawing the OpenGL scene.
     */
    private final GLBuildingViews views;

    /**
     * Creates a new object of this control class.The wall objects (a control and the corresponding view object) are
     * created and stored in data structures to easily assign them by their floor id.Note that no default floor is
     * enabled!
     *
     * @param buildingVisualizationModel
     * @param buildingModel
     * @param views
     */
    public GLBuildingControl(BuildingVisualizationModel buildingVisualizationModel, GLBuildingModel buildingModel,
            GLBuildingViews views) {
        super(buildingVisualizationModel);
        this.buildingModel = Objects.requireNonNull(buildingModel);
        this.views = Objects.requireNonNull(views);
        setView(views.getView());

    }

    /**
     * Enables the walls on a specified floor only.
     *
     * @param floorID the specified floor as its id in the visual results.
     */
    public void showOnlyFloor(int floorID) {
        childControls.clear();
        List<GLWallModel> floor = buildingModel.getWallModels(floorID);
        childControls.addAll(floor);
        view.clear();
        for (GLWallModel wall : this) {
            view.addChild(views.getView(wall));
        }

        getView().update();
    }

    /**
     * Enables the walls on all floors.
     */
    public void showAllFloors() {
        childControls.clear();
        for (int i = 0; i < buildingModel.getFloorCount(); ++i) {
            childControls.addAll(buildingModel.getWallModels(i));
        }
        view.clear();
        for (GLWallModel wall : this) {
            view.addChild(views.getView(wall));
        }
        getView().update();
    }

    /**
     * Hides all walls.
     */
    public void hideAll() {
        childControls.clear();
        getView().update();
    }

    /**
     * Prepares this object for deletion, removes all pointers and calls this method on all child elements.
     */
    @Override
    public void delete() {
        view.delete();
        view = null;
    }
}
