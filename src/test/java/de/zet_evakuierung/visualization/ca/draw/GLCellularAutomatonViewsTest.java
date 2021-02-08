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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import de.zet_evakuierung.visualization.ca.model.CellularAutomatonVisualizationModel;
import de.zet_evakuierung.visualization.ca.model.GLFloorModel;
import de.zet_evakuierung.visualization.ca.model.GLCellModel;
import de.zet_evakuierung.visualization.ca.model.GLCellularAutomatonModel;
import de.zet_evakuierung.visualization.ca.model.GLRoomModel;
import ds.PropertyContainer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.SaveCell;
import org.zet.cellularautomaton.StairCell;
import org.zet.cellularautomaton.TeleportCell;

import static de.zet_evakuierung.visualization.ModelContainerTestUtils.MockHierarchyBuilder.hierarchyMocks;

import de.zet_evakuierung.visualization.ModelContainerTestUtils.MockHierarchyBuilder;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLCellularAutomatonViewsTest {

    /**
     * Some properties that have to be defined when view classes are created by the factory.
     */
    @BeforeClass
    public static void initializeProperty() {
        PropertyContainer.getGlobal().define("options.visualization.view.grid", Boolean.class, true);
        PropertyContainer.getGlobal().define("options.visualization.appeareance.colors.wallColor", Color.class,
                Color.RED);
        PropertyContainer.getGlobal().define("options.visualization.appeareance.colors.delayAreaColor", Color.class,
                Color.RED);
        PropertyContainer.getGlobal().define("options.visualization.appeareance.colors.safeAreaColor", Color.class,
                Color.RED);
        PropertyContainer.getGlobal().define("options.visualization.appeareance.colors.stairAreaColor", Color.class,
                Color.RED);
        PropertyContainer.getGlobal().define("options.visualization.appeareance.colors.evacuationColor", Color.class,
                Color.RED);
    }

    @Test
    public void testEmpty() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        int floorCount = 0;
        setUpFloors(baseMocks, floorCount);

        GLCellularAutomatonViews result = baseMocks.createResult();

        assertThat(result.getView(), is(not(nullValue())));
        assertCounts(result.getView(), floorCount, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Tests creation of an empty floor.
     */
    @Test
    public void testFactoryFloors() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        int floorCount = 1;
        setUpFloors(baseMocks, floorCount);

        GLCellularAutomatonViews result = createWithGLContext(() -> baseMocks.createResult());

        assertCounts(result.getView(), floorCount, List.of(0), Collections.emptyList());
    }

    /**
     * Tests creation of rooms in a floor.
     */
    @Test
    public void testFactoryRooms() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        int floorCount = 1;
        setUpFloors(baseMocks, floorCount);
        setUpRooms(baseMocks, floorCount, 2);

        GLCellularAutomatonViews result = createWithGLContext(() -> baseMocks.createResult());

        assertCounts(result.getView(), 1, List.of(2), List.of(0, 0));
    }

    /**
     * Tests creation of cells in a room.
     */
    @Test
    public void testFactoryCells() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        int floorCount = 1;
        setUpFloors(baseMocks, floorCount);
        List<Room> mockRooms = setUpRooms(baseMocks, floorCount, 1);
        setUpCells(baseMocks, mockRooms, 2);

        GLCellularAutomatonViews result = createWithGLContext(() -> baseMocks.createResult());

        assertCounts(result.getView(), floorCount, List.of(1), List.of(2));
    }

    /**
     * Tests creation of cell views of different type, depending on the cell type.
     */
    @Test
    public void testFactoryCellType() {
        // Standard types and cases that do not require additional mocking
        Map<Class<? extends EvacCell>, Class<? extends GLCell>> standardViewCreation = Map.of(
                RoomCell.class, GLDelayCell.class,
                DoorCell.class, GLDelayCell.class,
                ExitCell.class, GLEvacuationCell.class,
                SaveCell.class, GLSaveCell.class,
                StairCell.class, GLStairCell.class,
                TeleportCell.class, GLSaveCell.class
        );

        for (Map.Entry<Class<? extends EvacCell>, Class<? extends GLCell>> testCase : standardViewCreation.entrySet()) {
            testFactoryCellType(testCase.getKey(), testCase.getValue(), unused -> {
            });
        }

        // Additional test case for room and door cells with standard speed factor
        Consumer<EvacCell> mockPreparation
                = mockCell -> when(mockCell.getSpeedFactor()).thenReturn(RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR);

        testFactoryCellType(RoomCell.class, GLCell.class, mockPreparation);
        testFactoryCellType(DoorCell.class, GLCell.class, mockPreparation);
    }

    private void testFactoryCellType(Class<? extends EvacCell> cellType, Class<? extends GLCell> viewType,
            Consumer<EvacCell> cellPreparation) {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        int floorCount = 1;
        setUpFloors(baseMocks, floorCount);
        List<Room> mockRooms = setUpRooms(baseMocks, floorCount, 1);
        List<EvacCell> mockCells = setUpCells(baseMocks, mockRooms, cellType, 1);

        // Addiitonal mocking for the single created mock cell model
        cellPreparation.accept(mockCells.get(0));

        GLCellularAutomatonViews result = createWithGLContext(() -> baseMocks.createResult());

        assertCounts(result.getView(), floorCount, List.of(1), List.of(1));

        GLCell createdCellView = result.getView().iterator().next().iterator().next().iterator().next();
        assertThat(createdCellView.getClass(), is(equalTo(viewType)));
    }

    /**
     * Sets up the mocks for floors models.
     * <p>
     * Creates mocked instances and sets up the cellular automaton model to return as many as required.</p>
     * <p>
     * Initializes the mocks also to be prepared for floor view object creation.</p>
     *
     * @param baseMocks the base mocks that are set up
     * @param floorCount the number of floor mocks to be created
     */
    private static List<GLFloorModel> setUpFloors(FactoryBaseMocks baseMocks, int floorCount) {
        when(baseMocks.cellularAutomatonModel.getFloorCount()).thenReturn(floorCount);
        List<GLFloorModel> floorMocks = new ArrayList<>(floorCount);
        for (int i = 0; i < floorCount; ++i) {
            GLFloorModel mockFloor = mock(GLFloorModel.class);
            floorMocks.add(mockFloor);
            when(baseMocks.cellularAutomatonModel.getFloorModel(i)).thenReturn(mockFloor);
        }

        // Prepare base mocks for floor view object creation
        when(baseMocks.visualizationModel.getIndividuals()).thenReturn(Collections.emptyList());

        return floorMocks;
    }

    /**
     * Sets up the mocks for room models. Creates {@link Room} mocks and {@link GLRoomModel} mocks that correspond to
     * each other.
     * <p>
     * Sets up the cellular automaton mock to return the respective list of {@link Room rooms} for each floor and the
     * model to retrieve the {@link GLRoomModel} for its corresponding room.</p>
     *
     * @param baseMocks the base mocks that are set up
     * @param floorCount the number floors
     * @param roomsOnFloor number of rooms for each floor; must have the same size as {@code floorMocks}
     * @return a list of all created mocked rooms, ordered by floor
     */
    private static List<Room> setUpRooms(FactoryBaseMocks baseMocks, int floorCount,
            int... roomsOnFloor) {
        Function<Integer, Iterable<Room>> roomMockSupplier
                = i -> (List<Room>) baseMocks.cellularAutomaton.getRoomsOnFloor(i);
        Function<Room, GLRoomModel> internalMockFunction
                = evacCellMock -> baseMocks.cellularAutomatonModel.getRoomModel(evacCellMock);
        return hierarchyMocks(Room.class, GLRoomModel.class)
                .forParentCount(floorCount)
                .withModelMockAccessor(roomMockSupplier)
                .withViewModelMockAccessor(internalMockFunction)
                .withChildrenInParent(roomsOnFloor)
                .build();
    }

    private static List<? extends EvacCell> setUpCells(FactoryBaseMocks baseMocks, List<Room> roomMocks,
            int... cellsInRoom) {
        return setUpCells(baseMocks, roomMocks, RoomCell.class, cellsInRoom);
    }

    /**
     * Sets up the mocks for cell models. Creates {@link EvacCell} mocks and {@link GLCellModel} mocks that correspond
     * to each other.
     * <p>
     * Sets up the room mocks to return the respective list of created {@link EvacCell} mocks. Sets up the cellular
     * automaton model to return the {@link GLCellModel cell model} class for each created cell.</p>
     * <p>
     * Supports to set up the view factory to create different {@link GLCell cell view} types depending on the model.
     * The type of model is specified by the {@code cellType}.</p>
     *
     * @param baseMocks the base mocks that are set up
     * @param roomMocks the room mocks for which cells are mocked up
     * @param cellType the type of created cell model mocks
     * @param cellsInRoom the number of cell mocks created for the respective rooms
     * @return the list of created cells, ordered by room
     */
    private static <T> List<EvacCell> setUpCells(FactoryBaseMocks baseMocks, List<Room> roomMocks,
            Class<? extends EvacCell> cellType, int... cellsInRoom) {
        Function<Integer, List<EvacCell>> cellMockSupplier = i -> roomMocks.get(i).getAllCells();
        Function<EvacCell, GLCellModel> intermalMockFunction
                = evacCellMock -> baseMocks.cellularAutomatonModel.getCellModel(evacCellMock);
        return MockHierarchyBuilder.<EvacCell, GLCellModel>hierarchyMocks(cellType, GLCellModel.class)
                .forParentCount(roomMocks.size())
                .withModelMockAccessor(cellMockSupplier)
                .withViewModelMockAccessor(intermalMockFunction)
                .withChildrenInParent(cellsInRoom)
                .build();
    }

    /**
     * Asserts the numbers of {@code floor}s, {@code room}s, and {@code cell}s in a created model object.
     *
     * @param resultView that is asserted
     * @param floorCount the expected number of floors
     * @param roomCount the expected number of rooms
     * @param cellCounts the expected number of cells
     */
    private static void assertCounts(GLCA resultView, int floorCount, List<Integer> roomCounts,
            List<Integer> cellCounts) {
        assertThat(resultView, is(iterableWithSize(floorCount)));
        int currentFloor = -1;
        int currentRoom = -1;
        for (GLCAFloor floor : resultView) {
            currentFloor++;
            assertThat(floor, is(iterableWithSize(roomCounts.get(currentFloor))));
            for (GLRoom room : floor) {
                currentRoom++;
                assertThat(room, is(iterableWithSize(cellCounts.get(currentRoom))));
            }

        }
    }

    /**
     * Structure containing two important mocks to initialize the factory with.
     */
    private static class FactoryBaseMocks {

        final CellularAutomatonVisualizationModel visualizationModel;
        final MultiFloorEvacuationCellularAutomaton cellularAutomaton;
        final GLCellularAutomatonModel cellularAutomatonModel;

        public FactoryBaseMocks() {
            visualizationModel = mock(CellularAutomatonVisualizationModel.class);
            cellularAutomaton = mock(MultiFloorEvacuationCellularAutomaton.class);
            cellularAutomatonModel = mock(GLCellularAutomatonModel.class);
        }

        /**
         * Creates the result object using the factory with the mocks. Must only be called once all mocks have been set
         * up.
         *
         * @return the created instance
         */
        private GLCellularAutomatonViews createResult() {
            return GLCellularAutomatonViews.createInstance(visualizationModel, cellularAutomaton,
                    cellularAutomatonModel);
        }
    }
}