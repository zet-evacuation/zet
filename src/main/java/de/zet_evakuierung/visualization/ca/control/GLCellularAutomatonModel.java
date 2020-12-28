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

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

import de.zet_evakuierung.visualization.ca.draw.GLCA;
import io.visualization.CellularAutomatonVisualizationResults;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.Room;

/**
 * Container class for all model elements of a cellular automaton visualization.
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLCellularAutomatonModel {

    private final static String ILLEGAL_NULL_OBJECT_ERROR = "Erroneous builder returned null.";
    private final GLCAControl cellularAutomaton;
    private final List<GLCAFloorControl> floors;
    private final Map<Room, GLRoomControl> roomModelMap;
    private final Map<EvacCellInterface, GLCellControl> cellModelMap;

    private GLCellularAutomatonModel(Builder builder) {
        this.cellularAutomaton = Objects.requireNonNull(builder.cellularAutomatonModel, ILLEGAL_NULL_OBJECT_ERROR);
        this.floors = Objects.requireNonNull(builder.floors, ILLEGAL_NULL_OBJECT_ERROR);
        this.roomModelMap = Objects.requireNonNull(builder.roomMap, ILLEGAL_NULL_OBJECT_ERROR);
        this.cellModelMap = Objects.requireNonNull(builder.cellMap, ILLEGAL_NULL_OBJECT_ERROR);
    }

    /**
     * Returns the root model instance for the cellular automaton hierarchy.
     *
     * @return the cellular automaton root instance
     */
    public GLCAControl getCAModel() {
        return cellularAutomaton;
    }

    /**
     * Returns floor model instances for the cellular automaton hierarchy. The {@code floor} is in the range {@code 0}
     * to the {@link #getFloorCount() floor count} exclusive.
     *
     * @param floor the floor number for which the model is returned
     * @return the floor visualization model instance
     */
    public GLCAFloorControl getFloorModel(int floor) {
        return floors.get(floor);
    }

    /**
     * Returns the number of floors.
     *
     * @see #getFloorModel(int)
     * @return the number of floors, at least {@code 0}
     */
    public int getFloorCount() {
        return floors.size();
    }

    /**
     * Returns room model instances for the cellular automaton visualization hierarchy. The {@code room} specifies the
     * corresponding instance in the cellular automaton.
     *
     * @param room the instane for which the corresponding model should be returned; not {@code null}
     * @return the room visualization model instance
     */
    public GLRoomControl getRoomModel(Room room) {
        return roomModelMap.get(room);
    }

    /**
     * Returns cell model instances for the cellular automaton visualization hierarchy. The {@code cell} specifies the
     * corresponding instance in the cellular automaton.
     *
     * @param cell the instance for which the corresponding model should be returned; not {@code null}
     * @return the cell visualization model instance
     */
    public GLCellControl getCellModel(EvacCellInterface cell) {
        return cellModelMap.get(cell);
    }

    /**
     * Iterates over all floor visualization model instances.
     *
     * @see #getFloorModel(int)
     * @return iterable of all floor visualization model instances
     */
    Iterable<GLCAFloorControl> floors() {
        return floors;
    }

    /**
     * Iterates over all room visualization model instances.
     *
     * @see #getRoomModel(org.zet.cellularautomaton.Room)
     * @return iterable of all room visualization model instances
     */
    Iterable<GLRoomControl> rooms() {
        return roomModelMap.values();
    }

    /**
     * Iterates over all cell visualization model instances.
     *
     * @see #getCellModel(org.zet.cellularautomaton.EvacCellInterface)
     * @return iterable of all cell visualization model instances
     */
    Iterable<GLCellControl> cells() {
        return cellModelMap.values();
    }

    /**
     * Creates instances of the {@link GLCellularAutomatonModel}. The builder is not thread safe.
     */
    public static class Builder {

        /**
         * Required cellular automaton used to {@link #build() build} the visualization model.
         */
        private final MultiFloorEvacuationCellularAutomaton cellularAutomaton;
        /**
         * Required visualization results used to {@link #build() build} the visualization model.
         */
        private final CellularAutomatonVisualizationResults caVisResults;

        /**
         * Optional visualization model used during the {@link #build() building process}.
         */
        private CellularAutomatonVisualizationModel visualizationModel = new CellularAutomatonVisualizationModel();

        /**
         * The {@link #build() built} root visualization model instance.
         */
        private GLCAControl cellularAutomatonModel;
        /**
         * The {@link #build() built} floor visualization model instances.
         */
        private List<GLCAFloorControl> floors;
        /**
         * Map of the {@link #build() built} room visualization model instances.
         */
        private Map<Room, GLRoomControl> roomMap;
        /**
         * Map of the {@link #build() built} cell visualization model instances.
         */
        private Map<EvacCellInterface, GLCellControl> cellMap;

        public Builder(MultiFloorEvacuationCellularAutomaton cellularAutomaton, CellularAutomatonVisualizationResults caVisResults) {
            this.cellularAutomaton = Objects.requireNonNull(cellularAutomaton);
            this.caVisResults = Objects.requireNonNull(caVisResults);
        }

        public Builder withVisualizationModel(CellularAutomatonVisualizationModel visualizationModel) {
            this.visualizationModel = Objects.requireNonNull(visualizationModel);
            return this;
        }

        /**
         * Builds the complete model instances for the {@link GLCA cellular automaton root}, the
         * {@link GLCAFloorControl floors}, the {@link GLRoomControl rooms}, and the {@link GLCellControl cells}.
         *
         * @return the container object instance for all the built visualization model instances
         */
        public GLCellularAutomatonModel build() {
            this.floors = buildFloorModels();
            this.roomMap = createRoomMapping();
            this.cellMap = buildCells(roomMap.keySet());
            this.cellularAutomatonModel = new GLCAControl(visualizationModel, cellMap.values());

            return new GLCellularAutomatonModel(this);
        }

        private List<GLCAFloorControl> buildFloorModels() {
            ArrayList<GLCAFloorControl> floorModels = new ArrayList<>(cellularAutomaton.getFloors().size());
            for (int i = 0; i < cellularAutomaton.getFloors().size(); ++i) {
                GLCAFloorControl floormodel = new GLCAFloorControl(caVisResults, i, visualizationModel);
                floorModels.add(floormodel);
            }
            return floorModels;
        }

        /**
         * Builds a map of all rooms to their respective GL model. The rooms in the cellular automaton must be unique.
         *
         * @return a mapping of all rooms
         */
        private Map<Room, GLRoomControl> createRoomMapping() {
            List<Map<Room, GLRoomControl>> roomModels = new ArrayList<>();
            for (int i = 0; i < cellularAutomaton.getFloors().size(); ++i) {
                Map<Room, GLRoomControl> roomModelsOnFloor = buildRoomModels(cellularAutomaton.getRoomsOnFloor(i));
                roomModels.add(roomModelsOnFloor);
            }
            return roomModels.stream()
                    .map(Map::entrySet)
                    .flatMap(Set::stream)
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        private Map<Room, GLRoomControl> buildRoomModels(Collection<Room> roomsOnTheFloor) {
            HashMap<Room, GLRoomControl> result = new HashMap<>();
            roomsOnTheFloor.forEach(
                    room -> result.put(room, new GLRoomControl(caVisResults, room, visualizationModel)));
            return result;
        }

        /**
         * Creates cell objects.
         *
         * @param rooms an iterable of all rooms whose cells need conversion
         * @throws ArithmeticException if the total number of cells is larger than 2147483647
         * ({@code Integer.MAX_VALUE})
         * @return
         */
        private Map<EvacCellInterface, GLCellControl> buildCells(Iterable<Room> rooms) throws ArithmeticException {
            long cellCount = StreamSupport.stream(rooms.spliterator(), false)
                    .map(Room::getAllCells)
                    .map(List::size)
                    .count();
            HashMap<EvacCellInterface, GLCellControl> result = new HashMap<>(Math.toIntExact(cellCount));
            for (Room room : rooms) {
                room.getAllCells().forEach(
                        cell -> result.put(cell, new GLCellControl(caVisResults, cell, visualizationModel)));
            }
            return result;
        }
    }
}
