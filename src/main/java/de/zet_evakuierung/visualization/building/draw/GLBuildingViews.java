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
package de.zet_evakuierung.visualization.building.draw;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import de.zet_evakuierung.visualization.building.model.BuildingVisualizationModel;
import de.zet_evakuierung.visualization.building.model.GLBuildingControl;
import de.zet_evakuierung.visualization.building.model.GLBuildingModel;
import de.zet_evakuierung.visualization.building.model.GLWallModel;

/**
 * Container giving access to the created OpenGL building visualization view objects.
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLBuildingViews {

    private final GLBuilding rootView;
    private final Map<GLWallModel, GLWall> wallViews;

    /**
     * Private constructor initializeng the view container from the builder.
     *
     * @param builder the builder instance
     */
    private GLBuildingViews(GLBuildingViewFactory builder) {
        this.rootView = Objects.requireNonNull(builder.rootView);
        this.wallViews = Objects.requireNonNull(builder.wallViews);
    }

    /**
     * Returns the {@link GLBuilding root view} for the {@link GLBuildingControl model root} of the visualization
     * hierarchy.
     *
     * @return the root view object
     */
    public GLBuilding getView() {
        return rootView;
    }

    /**
     * Returns the {@link GLWall wall view} corresponding to a wall visualization model instance.
     *
     * @param model the visualization model instance
     * @return the view instance
     */
    public GLWall getView(GLWallModel model) {
        return wallViews.get(model);
    }

    /**
     * Factory method creating a {@link GLBuildingViews building views container} instance.
     *
     * @param visualizationModel the visualization model with basic visualization settings
     * @param buildingModel the input data for building visualization
     * @return the created instance
     */
    public static GLBuildingViews createInstance(BuildingVisualizationModel visualizationModel,
            GLBuildingModel buildingModel) {
        GLBuildingViewFactory factory = new GLBuildingViewFactory(visualizationModel, buildingModel);
        factory.createViews();
        return new GLBuildingViews(factory);
    }

    /**
     * Iterates all {@link GLWall wall views} instances.
     *
     * @return iterable over the wall view instances
     */
    public Iterable<GLWall> wallViews() {
        return wallViews.values();
    }

    /**
     * Utility factory class creating the view instances.
     */
    private static class GLBuildingViewFactory {

        /**
         * Null object for the non existing building model to pass to the {@code GLBuilding} view class.
         */
        private static final Void MODEL = ((Supplier<Void>) () -> {
            try {
                Constructor<Void> voidConstructor = Void.class.getDeclaredConstructor();
                voidConstructor.setAccessible(true);
                return voidConstructor.newInstance();
            } catch (Exception ex) {
                // Does not happen
                throw new AssertionError(ex);
            }
        }).get();

        private final GLBuildingModel buildingModel;

        /**
         * The created {@link GLBuilding hierarchy root} view instance; valid after views
         * {@link #createViews() have been created}.
         */
        private GLBuilding rootView;
        /**
         * The created {@link GLWall wall view} instances mapped by the respective
         * {@link GLWallModel visualization model} classes; valid after vies {@link #createViews() have been created}.
         */
        private Map<GLWallModel, GLWall> wallViews;

        GLBuildingViewFactory(BuildingVisualizationModel visualizationModel,
                GLBuildingModel buildingModel) {
            this.buildingModel = buildingModel;
        }

        void createViews() {
            long wallCount = IntStream.range(0, buildingModel.getFloorCount())
                    .mapToLong(i -> buildingModel.getWallModels(i).size()).sum();
            this.wallViews = new HashMap<>(Math.toIntExact(wallCount));

            rootView = new GLBuilding(MODEL);
            createWalls();
        }

        private void createWalls() {
            for (int i = 0; i < buildingModel.getFloorCount(); ++i) {
                buildingModel.getWallModels(i).forEach(
                        wall -> wallViews.put(wall, new GLWall(wall)));
            }
        }
    }
}
