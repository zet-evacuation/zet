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

import static de.zet_evakuierung.visualization.ca.model.GLCellularAutomatonModelTest.createMockList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.visualization.BuildingResults;
import io.visualization.BuildingResults.Floor;
import io.visualization.BuildingResults.Wall;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLBuildingModelTest {

    @Test
    public void builderInitialization() {
        assertThrows(NullPointerException.class, () -> new GLBuildingModel.Builder(null));
    }

    /**
     * Asserts that a builder with an empty {@link BuildingResults building} produces an empty output model.
     */
    @Test
    public void emptyModel() {
        BuildingResults buildingMock = createSimpleMocks();
        GLBuildingModel fixture = buildFixture(buildingMock);

        verify(buildingMock, atLeastOnce()).getWalls();
        verify(buildingMock, atLeastOnce()).getFloors();
        verifyNoMoreInteractions(buildingMock);

        assertThat(fixture.getFloorCount(), is(equalTo(0)));
    }

    @Test
    public void exceptionForNonExistingFloors() {
        BuildingResults buildingMock = createSimpleMocks(1);
        GLBuildingModel fixture = buildFixture(buildingMock);

        assertThrows(IndexOutOfBoundsException.class, () -> fixture.getWallModels(-1));
        assertThat(fixture.getWallModels(0), is(not(nullValue())));
        assertThrows(IndexOutOfBoundsException.class, () -> fixture.getWallModels(1));
    }

    /**
     * Asserts that a builder with an empty {@link BuildingResults building} produces an empty output model.
     */
    @Test
    public void withWallsOnFloor() {
        BuildingResults buildingMock = createSimpleMocks(3);
        GLBuildingModel fixture = buildFixture(buildingMock);

        verify(buildingMock, atLeastOnce()).getWalls();
        verify(buildingMock, atLeastOnce()).getFloors();
        verifyNoMoreInteractions(buildingMock);

        assertThat(fixture.getFloorCount(), is(equalTo(1)));
        assertThat(fixture.getWallModels(0), is(hasSize(3)));
    }

    /**
     * Creates floor mocks and some simple walls. The number of floors is equal to elements in {@code wallsOnFloor}.
     * When called without parameters the result will not contain any floors.
     *
     * @param wallsOnFloor the number of floors per floor
     * @return initialized mocks
     */
    private BuildingResults createSimpleMocks(int... wallsOnFloor) {
        BuildingResults buildingMock = builderBaseMock();

        List<Floor> floors = setUpFloors(buildingMock, wallsOnFloor.length);
        setUpWalls(buildingMock, floors, wallsOnFloor);

        return buildingMock;
    }

    /**
     * Creates the mock required for all building operations.
     */
    private static BuildingResults builderBaseMock() {
        BuildingResults buildingResults = mock(BuildingResults.class);
        return buildingResults;
    }

    /**
     * Builds the building model by executing its builder. Must only be called once all mocks have been set up.
     *
     * @param buildingMock the result mock used as input
     * @return the built instance
     */
    private static GLBuildingModel buildFixture(BuildingResults buildingMock) {
        return new GLBuildingModel.Builder(buildingMock).build();
    }

    /**
     * Sets up building mock floors. Defines as many floors as specified. The {@link Floor} mocks support their own
     * {@link Floor#id id}.
     *
     * @param buildingMock the building mock that is set up
     * @param wallsOnFloor number of floors
     * @return a list of created floor mocks, ordered by id
     */
    private static List<Floor> setUpFloors(BuildingResults buildingMock, int floorCount) {
        List<Floor> floors = createMockList(Floor.class, floorCount);

        when(buildingMock.getFloors()).thenReturn(floors);
        for (int i = 0; i < floorCount; ++i) {
            when(floors.get(i).id()).thenReturn(i);
        }

        return floors;
    }

    /**
     * Sets up the mocks for walls. The {@code buildingMock} returns all created {@link Wall walls} and the wall macks
     * return their floor.
     *
     * @param buildingMock the building mock that is set up
     * @param floors list of created {@link Floor floors}; can be mocks
     * @param wallsOnFloor number of walls for each floor; must have the same size as {@code floors}
     * @return a list of all walls, ordered by floor
     */
    private static List<Wall> setUpWalls(BuildingResults buildingMock, List<Floor> floors, int... wallsOnFloor) {
        assertThat(floors.size(), is(equalTo(wallsOnFloor.length)));

        List<Wall> wallsOnFloors = new ArrayList<>(Arrays.stream(wallsOnFloor).sum());
        for (int i = 0; i < floors.size(); ++i) {
            for (int j = 0; j < wallsOnFloor[i]; ++j) {
                Wall wallMock = mock(Wall.class);
                wallsOnFloors.add(wallMock);
                when(wallMock.getFloor()).thenReturn(floors.get(i));
            }
        }

        when(buildingMock.getWalls()).thenReturn(wallsOnFloors);

        return wallsOnFloors;
    }
}
