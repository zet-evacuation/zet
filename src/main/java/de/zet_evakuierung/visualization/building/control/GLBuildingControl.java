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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.zet_evakuierung.visualization.building.draw.GLBuilding;
import gui.visualization.control.AbstractZETVisualizationControl;
import io.visualization.BuildingResults;
import io.visualization.BuildingResults.Floor;
import io.visualization.BuildingResults.Wall;
import org.zetool.opengl.framework.abs.HierarchyNode;

/**
 * A control class that allows hiding and showing of walls on different floors.
 *
 * @author Jan-Philipp Kappmeier
 * @author Daniel R. Schmidt
 */
public class GLBuildingControl extends AbstractZETVisualizationControl<GLWallControl, GLBuilding, BuildingVisualizationModel> implements HierarchyNode {

    private List<ArrayList<GLWallControl>> allFloorsByID;
    private BuildingResults visResult;

    /**
     * Creates a new object of this control class. The wall objects (a control and the corresponding view object) are
     * created and stored in data structures to easily assign them by their floor id. Note that no default floor is
     * enabled!
     *
     * @param visResult
     */
    public GLBuildingControl(BuildingResults visResult, BuildingVisualizationModel visualizationModel) {
        super(visualizationModel);
        this.visResult = visResult;
    }

    public void build() {
        //AlgorithmTask.getInstance().setProgress( 1, DefaultLoc.getSingleton().getStringWithoutPrefix( "batch.tasks.progress.createBuildingVisualizationDataStructure" ), "" );
        visualizationModel.init(visResult.getWalls().size());

        allFloorsByID = new ArrayList<>(visResult.getFloors().size());
        for (int i = 0; i < visResult.getFloors().size(); ++i) {
            allFloorsByID.add(new ArrayList<>());
        }
        for (Wall wall : visResult.getWalls()) {
            final GLWallControl child = new GLWallControl(wall, visualizationModel);
            add(child);
            allFloorsByID.get(wall.getFloor().id()).add(child);
        }
        setView(new GLBuilding(this));
        for (GLWallControl wall : this) {
            view.addChild(wall.getView());
        }
    }

    /**
     * Enables the walls on a specified floor only.
     *
     * @param floorID the specified floor as its id in the visual results.
     */
    public void showOnlyFloor(Integer floorID) {
        childControls.clear();
        ArrayList<GLWallControl> floor = allFloorsByID.get(floorID);
        childControls.addAll(floor);
        view.clear();
        for (GLWallControl wall : this) {
            view.addChild(wall.getView());
        }

        getView().update();
    }

    /**
     * Enables the walls on all floors.
     */
    public void showAllFloors() {
        childControls.clear();
        for (ArrayList<GLWallControl> floor : allFloorsByID) {
            childControls.addAll(floor);
        }
        view.clear();
        for (GLWallControl wall : this) {
            view.addChild(wall.getView());
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
        for (GLWallControl wall : this) {
            wall.delete();
        }
        view.delete();
        view = null;
    }

    public Collection<Floor> getFloors() {
        return visResult.getFloors();
    }
}
