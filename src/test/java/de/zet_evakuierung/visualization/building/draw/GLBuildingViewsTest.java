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

import static de.zet_evakuierung.visualization.ModelContainerTestUtils.createMockList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.zetool.opengl.framework.util.GLContextAwareThread.createWithGLContext;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.zet_evakuierung.visualization.building.BuildingVisualizationProperties;
import de.zet_evakuierung.visualization.building.model.BuildingVisualizationModel;
import de.zet_evakuierung.visualization.building.model.GLBuildingModel;
import de.zet_evakuierung.visualization.building.model.GLWallModel;
import ds.PropertyContainer;
import io.visualization.BuildingResults.Floor;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLBuildingViewsTest {

    /**
     * Some properties that have to be defined when view classes are created by the factory.
     */
    @BeforeClass
    public static void initializeProperty() {
        PropertyContainer.getGlobal().define("options.visualization.appearance.wallHeight", Double.class, 1.5);
        PropertyContainer.getGlobal().define("options.visualization.appearance.floorDistance", Double.class, 10.0);
        PropertyContainer.getGlobal().define("options.visualization.appeareance.colors.floorColor", Color.class,
                Color.RED);
    }

    @Test
    public void testEmpty() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        setUpFloors(baseMocks, 0);
        GLBuildingViews result = baseMocks.createResult();

        assertThat(result.getView(), is(not(nullValue())));
        assertCounts(result, Collections.emptyList(), 0);
    }

    /**
     * Tests creation of some empty floors.
     */
    @Test
    public void testEmptyFloors() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        setUpFloors(baseMocks, 3);

        GLBuildingViews result = createWithGLContext(() -> baseMocks.createResult());

        assertCounts(result, Collections.emptyList(), 0);
    }

    /**
     * Tests creation of some floors with few walls.
     */
    @Test
    public void testWalls() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        List<Floor> floors = setUpFloors(baseMocks, 3);
        List<GLWallModel> walls = setUpWalls(baseMocks, floors, 1, 4, 2);

        GLBuildingViews result = createWithGLContext(() -> baseMocks.createResult());

        assertCounts(result, walls, 7);
    }

    /**
     * Creates mock floors. The floors are only used internally and are not represented as views. The {@code baseMocks}
     * return the correct {@code floorCount}, and the floors their respecitve id.
     *
     * @param baseMocks the base mocks that are set up
     * @param floorCount the number of floors to be mocked
     * @return a list of the created mocks
     */
    private static List<Floor> setUpFloors(FactoryBaseMocks baseMocks, int floorCount) {
        when(baseMocks.buildingModel.getFloorCount()).thenReturn(floorCount);
        List<Floor> floors = createMockList(Floor.class, floorCount);
        for (int i = 0; i < floorCount; ++i) {
            when(floors.get(i).id()).thenReturn(i);
        }
        return floors;
    }

    /**
     * Sets up the mocks for wall model objects. The same amount of {@code wallsOnFloor} as {@code floors} have to be
     * passed.
     * <p>
     * Initializes the mocks also to be prepared for {@link GLWall view} object creation. The wall model mocks return
     * their respective floor. They provide an empty list of points.</p>
     *
     * @param baseMocks the base mocks that are set up
     * @param floors the floors on which the walls are set up
     * @param wallsOnFloor the number of floor mocks to be created
     * @return the mocks
     */
    private static List<GLWallModel> setUpWalls(FactoryBaseMocks baseMocks, final List<Floor> floors,
            int... wallsOnFloor) {
        assertThat(floors.size(), is(equalTo(wallsOnFloor.length)));
        List<GLWallModel> wallMocks = new ArrayList<>(Arrays.stream(wallsOnFloor).sum());
        for (int i = 0; i < wallsOnFloor.length; ++i) {
            final List<GLWallModel> modelMocksForParent = createMockList(GLWallModel.class, wallsOnFloor[i]);
            wallMocks.addAll(modelMocksForParent);
            when(baseMocks.buildingModel.getWallModels(i)).thenReturn(modelMocksForParent);
            for (GLWallModel mock : modelMocksForParent) {
                when(mock.iterator()).thenReturn(Collections.emptyIterator());
            }
        }
        return wallMocks;
    }

    /**
     * Asserts the numbers of wall view objects in a created model object.
     *
     * @param views that is asserted
     * @param wallCount the expected number of walls per floor
     * @param total the expected number of wall view objects
     */
    private static void assertCounts(GLBuildingViews views, List<GLWallModel> walls, int total) {
        assertThat(views.wallViews(), is(iterableWithSize(total)));
        walls.forEach(wall -> assertThat(views.getView(wall), is(not(nullValue()))));
    }

    /**
     * Structure containing two important mocks to initialize the factory with.
     */
    private static class FactoryBaseMocks {

        final BuildingVisualizationModel visualizationModel;
        final GLBuildingModel buildingModel;

        public FactoryBaseMocks() {
            visualizationModel = mock(BuildingVisualizationModel.class);
            buildingModel = mock(GLBuildingModel.class);
        }

        /**
         * Creates the result object using the factory with the mocks. Must only be called once all mocks have been set
         * up.
         *
         * @return the created instance
         */
        private GLBuildingViews createResult() {
            BuildingVisualizationProperties properties = new BuildingVisualizationProperties() {
            };
            return GLBuildingViews.createInstance(visualizationModel, properties, buildingModel);
        }
    }
}
