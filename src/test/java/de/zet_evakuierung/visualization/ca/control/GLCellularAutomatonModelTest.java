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
package de.zet_evakuierung.visualization.ca.control;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.visualization.CellularAutomatonVisualizationResults;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.Room;
import org.zetool.math.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLCellularAutomatonModelTest {

    /**
     * Some pre-defined {@code x} coordinates of objects; allows to create 3 test objects.
     */
    private static final List<Integer> X_POSITIONS = List.of(100, 0, 1000);
    /**
     * Some pre-defined {@code <} coordinates of objects; allows to create 3 test objects.
     */
    private static final List<Integer> Y_POSITIONS = List.of(200, 0, 3000);

    @Test
    public void builderInitialization() {
        assertThrows(NullPointerException.class,
                () -> new GLCellularAutomatonModel.Builder(null, mock(CellularAutomatonVisualizationResults.class)));
        assertThrows(NullPointerException.class,
                () -> new GLCellularAutomatonModel.Builder(mock(MultiFloorEvacuationCellularAutomaton.class), null));
    }

    /**
     * Asserts that a builder with an empty {@link MultiFloorEvacuationCellularAutomaton cellular automaton} produces an
     * empty output model.
     */
    @Test
    public void emptyModel() {
        BuilderBaseMocks baseMocks = new BuilderBaseMocks();

        setUpFloors(baseMocks);

        GLCellularAutomatonModel fixture = buildFixture(baseMocks);

        verify(baseMocks.cellularAutomaton, atLeastOnce()).getFloors();
        verifyNoMoreInteractions(baseMocks.cellularAutomaton);
        verifyNoMoreInteractions(baseMocks.visualizationResults);

        assertThat(fixture.getCAModel(), is(not(nullValue())));
        assertCounts(fixture, 0, 0, 0);
    }

    @Test
    public void multipleEmptyFloors() {
        BuilderBaseMocks baseMocks = new BuilderBaseMocks();

        List<String> floors = setUpFloors(baseMocks, "floor1", "another floor", "last floor");
        setUpRooms(baseMocks, floors);

        GLCellularAutomatonModel fixture = buildFixture(baseMocks);

        verify(baseMocks.cellularAutomaton, atLeastOnce()).getFloors();
        for (int i = 0; i < floors.size(); ++i) {
            verify(baseMocks.cellularAutomaton, atLeastOnce()).getRoomsOnFloor(i);
            verify(baseMocks.visualizationResults, atLeastOnce()).get(i);
        }
        verifyNoMoreInteractions(baseMocks.cellularAutomaton);
        verifyNoMoreInteractions(baseMocks.visualizationResults);

        assertCounts(fixture, 3, 0, 0);

        // Assert each floor object
        assertObjectsFromIteratorAndMapAreEqual(fixture.floors(), List.of(0, 1, 2), fixture::getFloorModel);
        assertPositions(fixture::getFloorModel, List.of(0, 1, 2));

        // Assert floor indices and z position based on them
        double defaultFloorHeight = 10;
        for (int i = 0; i < floors.size(); ++i) {
            assertThat(fixture.getFloorModel(i).getZPosition(), is(closeTo(i * defaultFloorHeight, 0.1)));
            assertThat(fixture.getFloorModel(i).getFloorNumber(), is(equalTo(i)));
        }
    }

    /**
     * Asserts that the height and scaling settings from a {@link CellularAutomatonVisualizationModel} passed to the
     * builder are actually used to compute {@code z}-coordinates for floors.
     */
    @Test
    public void customVisualizationModelParametersTaken() {
        BuilderBaseMocks baseMocks = new BuilderBaseMocks();

        List<String> floors = setUpFloors(baseMocks, "1", "2", "3");

        double customFloorHeight = 30;
        double customScaling = 7;
        Function<Integer, Double> expectedHeight = (i) -> i * customFloorHeight * customScaling;

        CellularAutomatonVisualizationModel visualizationModel = new CellularAutomatonVisualizationModel();
        visualizationModel.setDefaultFloorHeight(customFloorHeight);
        visualizationModel.scaling = customScaling;

        GLCellularAutomatonModel fixture = new GLCellularAutomatonModel.Builder(
                baseMocks.cellularAutomaton, baseMocks.visualizationResults)
                .withVisualizationModel(visualizationModel)
                .build();

        for (int i = 0; i < floors.size(); ++i) {
            assertThat(fixture.getFloorModel(i).getZPosition(), is(closeTo(expectedHeight.apply(i), 0.1)));
        }
    }

    /**
     * Asserts rooms are placed on correct floors.
     */
    @Test
    public void roomsOnCorrectFloors() {
        BuilderBaseMocks baseMocks = new BuilderBaseMocks();

        // Set up two floors with one and two rooms, respectively
        List<String> floors = setUpFloors(baseMocks, "one room", "two rooms");
        List<Room> rooms = setUpRooms(baseMocks, floors, 1, 2);

        GLCellularAutomatonModel fixture = buildFixture(baseMocks);

        verify(baseMocks.cellularAutomaton, atLeastOnce()).getFloors();
        for (int i = 0; i < floors.size(); ++i) {
            verify(baseMocks.cellularAutomaton, atLeastOnce()).getRoomsOnFloor(i);
            verify(baseMocks.visualizationResults, atLeastOnce()).get(i);
        }
        rooms.forEach(room -> verify(baseMocks.visualizationResults, atLeastOnce()).get(room));

        verifyNoMoreInteractions(baseMocks.cellularAutomaton);
        verifyNoMoreInteractions(baseMocks.visualizationResults);

        assertCounts(fixture, 2, 3, 0);
        assertObjectsFromIteratorAndMapAreEqual(fixture.rooms(), rooms, fixture::getRoomModel);
        assertPositions(fixture::getRoomModel, rooms);
    }

    @Test
    public void roomBuilt() {
        BuilderBaseMocks baseMocks = new BuilderBaseMocks();

        // Set up a single room on a single floor with 3 cells
        List<String> floors = setUpFloors(baseMocks, "single floor");
        Room room = setUpRooms(baseMocks, floors, 1).get(0);
        List<EvacCell> cells = setUpCells(baseMocks, room, 3);

        GLCellularAutomatonModel fixture = buildFixture(baseMocks);

        verify(baseMocks.cellularAutomaton, atLeastOnce()).getFloors();
        verify(baseMocks.cellularAutomaton, atLeastOnce()).getRoomsOnFloor(0);
        verify(baseMocks.cellularAutomaton, atLeastOnce()).getExits();
        verify(baseMocks.visualizationResults, atLeastOnce()).get(0);
        verify(baseMocks.visualizationResults, atLeastOnce()).get(room);
        for (EvacCell cell : cells) {
            verify(baseMocks.visualizationResults, atLeastOnce()).get(cell);
        }
        verify(baseMocks.visualizationResults, atLeast(0)).getCa();

        verifyNoMoreInteractions(baseMocks.cellularAutomaton);
        verifyNoMoreInteractions(baseMocks.visualizationResults);

        assertCounts(fixture, 1, 1, 3);
        assertObjectsFromIteratorAndMapAreEqual(fixture.cells(), cells, fixture::getCellModel);
        assertPositions(fixture::getCellModel, cells);
    }

    /**
     * Sets up the mocks for floors. Supports at most as many floors as {@link #X_POSITIONS positions} are defined.
     * <p>
     * Sets up the cellular automaton to return the given floors.
     * </p>
     *
     * @param baseMocks the base mocks that are set up
     * @param names the floor names, if none present the result is empty
     */
    private static List<String> setUpFloors(BuilderBaseMocks baseMocks, String... names) {
        List<String> floors = List.of(names);

        when(baseMocks.cellularAutomaton.getFloors()).thenReturn(floors);
        for (int i = 0; i < floors.size(); ++i) {
            when(baseMocks.visualizationResults.get(i))
                    .thenReturn(new Vector3(X_POSITIONS.get(i), Y_POSITIONS.get(i), -1 /* ignored */));
            when(baseMocks.cellularAutomaton.getRoomsOnFloor(i)).thenReturn(Collections.emptyList());
        }
        return floors;
    }

    /**
     * Sets up floor mocks having no rooms.
     *
     * @param baseMocks the base mocks that are set up
     * @param floors list of floors that has been mocked already
     * @return an empty list
     */
    private static List<Room> setUpRooms(BuilderBaseMocks baseMocks, List<String> floors) {
        return setUpRooms(baseMocks, floors, new int[floors.size()]);
    }

    /**
     * Sets up the mocks for rooms. Also prepares the floor mops to work with the rooms. Supports as many rooms as
     * {@link #X_POSITIONS positions} are defined.
     * <p>
     * Sets up the cellular automaton to return the room mocks defined per floor. Initializes coordinates for each room.
     *
     * @param baseMocks the base mocks that are set up
     * @param floors list of floors that has been mocked already
     * @param roomsOnFloor number of rooms for each floors; must have the same size as {@code floors}
     * @return a list of all rooms, ordered by floor
     */
    private static List<Room> setUpRooms(BuilderBaseMocks baseMocks, List<String> floors, int... roomsOnFloor) {
        assertThat(floors.size(), is(equalTo(roomsOnFloor.length)));

        List<List<Room>> roomsByFloor = Arrays.stream(roomsOnFloor)
                .mapToObj(roomCount -> createMockList(Room.class, roomCount))
                .collect(toList());

        List<Room> rooms = roomsByFloor.stream().flatMap(List::stream).collect(toList());

        for (int i = 0; i < floors.size(); ++i) {
            when(baseMocks.cellularAutomaton.getRoomsOnFloor(i)).thenReturn(roomsByFloor.get(i));
        }

        for (int i = 0; i < rooms.size(); ++i) {
            when(baseMocks.visualizationResults.get(rooms.get(i))).thenReturn(new Vector3(X_POSITIONS.get(i),
                    Y_POSITIONS.get(i), -1 /* ignored */));
        }

        return rooms;
    }

    /**
     * Sets up cell mocks for a room. Also prepares the room mock to work with the cells. Supports as many cells as
     * {@link #X_POSITIONS positions} are defined.
     * <p>
     * Sets the room to return the created cells. Assigns positions to the cells to be verified. For most simple GL cell
     * model creation the {@link BuilderBaseMocks#cellularAutomaton mocked cellular automaton} will provide no exit.
     * This will disable all potentials.</p>
     *
     * @param baseMocks the base mocks that are set up
     * @param room the room in which the cells are created
     * @param cellCount the number of cell mocks created
     * @return the list of created cells
     */
    private static List<EvacCell> setUpCells(BuilderBaseMocks baseMocks, Room room, int cellCount) {
        List<EvacCell> cells = createMockList(EvacCell.class, cellCount);
        when(room.getAllCells()).thenReturn(cells);

        for (int i = 0; i < cells.size(); ++i) {
            when(baseMocks.visualizationResults.get(cells.get(i)))
                    .thenReturn(new Vector3(X_POSITIONS.get(i), Y_POSITIONS.get(i), -1 /* ignored */));
        }

        when(baseMocks.cellularAutomaton.getExits()).thenReturn(Collections.emptyList());

        return cells;
    }

    public static <T> List<T> createMockList(Class<? extends T> mockType, int count) {
        return Stream.generate(() -> mock(mockType)).limit(count).collect(toList());
    }

    /**
     * Builds the cellular automaton model by executing its builder. Must only be called once all mocks have been set
     * up.
     *
     * @param baseMocks the base mocks
     * @return the built instance
     */
    private static GLCellularAutomatonModel buildFixture(BuilderBaseMocks baseMocks) {
        return new GLCellularAutomatonModel.Builder(
                baseMocks.cellularAutomaton, baseMocks.visualizationResults).build();
    }

    /**
     * Asserts the numbers of {@code floor}s, {@code room}s, and {@code cell}s in a created model object.
     *
     * @param fixture the model object that is asserted
     * @param floorCount the expected number of floors
     * @param roomCount the expected number of rooms
     * @param cellCount the expected number of cells
     */
    private static void assertCounts(GLCellularAutomatonModel fixture, int floorCount, int roomCount, int cellCount) {
        assertThat(fixture.getFloorCount(), is(equalTo(floorCount)));
        assertThat(fixture.floors(), is(iterableWithSize(floorCount)));
        assertThat(fixture.rooms(), is(iterableWithSize(roomCount)));
        assertThat(fixture.cells(), is(iterableWithSize(cellCount)));
    }

    /**
     *
     * @param <I> the input model object type
     * @param <O> the created model type
     * @param iterable iterable of all generated output objects
     * @param inputObjects list of all input obects
     * @param fromMap extracts output objects for input objects from map
     */
    private static <I, O> void assertObjectsFromIteratorAndMapAreEqual(Iterable<O> iterable, List<I> inputObjects,
            Function<I, O> fromMap) {
        List<O> cellsFromIterator = StreamSupport.stream(iterable.spliterator(), false).collect(toList());
        List<O> cellsFromMap = inputObjects.stream().map(fromMap).collect(toList());
        assertThat(cellsFromIterator, hasSize(cellsFromMap.size()));
        assertThat(cellsFromIterator, containsInAnyOrder(cellsFromMap.toArray()));
    }

    /**
     * Asserts that objects have positions as specified in {@link #X_POSITIONS} and {@link #Y_POSITIONS}.
     *
     * @param <T> the input model object type
     * @param <R> the created model type that is asserted
     * @param accessor retrieves a created model object for an input model object
     * @param objects the input model objects
     */
    private static <T, R extends VisualizationNodeModel> void assertPositions(Function<T, R> accessor, List<T> objects) {
        for (int i = 0; i < objects.size(); ++i) {
            assertThat(accessor.apply(objects.get(i)).getXPosition(), is(closeTo(X_POSITIONS.get(i), 0.1)));
            assertThat(accessor.apply(objects.get(i)).getYPosition(), is(closeTo(-Y_POSITIONS.get(i), 0.1)));
        }
    }

    /**
     * Structure containing two important mocks to initialize the builder with.
     */
    private static class BuilderBaseMocks {

        final MultiFloorEvacuationCellularAutomaton cellularAutomaton;
        final CellularAutomatonVisualizationResults visualizationResults;

        public BuilderBaseMocks() {
            cellularAutomaton = mock(MultiFloorEvacuationCellularAutomaton.class);
            visualizationResults = mock(CellularAutomatonVisualizationResults.class);
            when(visualizationResults.getCa()).thenReturn(cellularAutomaton);
        }

    }

}
