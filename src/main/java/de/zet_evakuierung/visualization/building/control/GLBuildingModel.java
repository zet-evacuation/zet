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
package de.zet_evakuierung.visualization.building.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.visualization.BuildingResults;

/**
 * Container class for all model elements of a building visualization.
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLBuildingModel {

    private final List<ArrayList<GLWallControl>> floors;

    private GLBuildingModel(Builder builder) {
        this.floors = builder.floors;
    }

    /**
     * Returns the wall models for a floor.
     *
     * @param floor the floor index
     * @return a view of all the walls on the floor
     * @throws IndexOutOfBoundsException if the floor does not exist
     */
    public List<GLWallControl> getWallModels(int floor) throws IndexOutOfBoundsException {
        return Collections.unmodifiableList(floors.get(floor));
    }

    /**
     * Returns the number of floors.
     *
     * @see #getWallModels(int)
     * @return the number of floors, at least {@code 0}
     */
    public int getFloorCount() {
        return floors.size();
    }

    /**
     * Creates instances of the {@link GLBuildingModel}. The builder is not thread safe.
     */
    public static class Builder {

        /**
         * Required visualization results used to {@link #build() build} the visualization model.
         */
        private final BuildingResults buildingResults;

        /**
         * Optional visualization model used during the {@link #build() building process}.
         */
        private BuildingVisualizationModel visualizationModel = new BuildingVisualizationModel();

        private List<ArrayList<GLWallControl>> floors;

        public Builder(BuildingResults buildingResults) {
            this.buildingResults = Objects.requireNonNull(buildingResults);
        }

        public Builder withVisualizationModel(BuildingVisualizationModel visualizationModel) {
            this.visualizationModel = visualizationModel;
            return this;
        }

        /**
         * Builds the complete model instances for the {@link GLBuildingControl building root}, and the
         * {@link GLWallControl walls}.
         *
         * @return the container object instance for all the built visualization model instances
         */
        public GLBuildingModel build() {
            visualizationModel.init(buildingResults.getWalls().size());

            this.floors = buildFloorModels();
            createWalls();

            return new GLBuildingModel(this);
        }

        private List<ArrayList<GLWallControl>> buildFloorModels() {
            List<ArrayList<GLWallControl>> allFloorsByID = new ArrayList<>(buildingResults.getFloors().size());
            for (int i = 0; i < buildingResults.getFloors().size(); ++i) {
                allFloorsByID.add(new ArrayList<>());
            }
            return allFloorsByID;
        }

        /**
         * Builds a map of all rooms to their respective GL model. The rooms in the cellular automaton must be unique.
         *
         * @return a mapping of all rooms
         */
        private void createWalls() {
            buildingResults.getWalls().forEach(wall -> floors.get(wall.getFloor().id()).add(wall));
        }
    }

}
