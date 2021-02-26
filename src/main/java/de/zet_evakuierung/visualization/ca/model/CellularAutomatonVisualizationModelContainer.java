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
package de.zet_evakuierung.visualization.ca.model;

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
public class CellularAutomatonVisualizationModelContainer {

    private final static String ILLEGAL_NULL_OBJECT_ERROR = "Erroneous builder returned null.";
    private final GLRootModel cellularAutomaton;
    private final List<GLFloorModel> floors;
    private final Map<Room, GLRoomModel> roomModelMap;
    private final Map<EvacCellInterface, GLCellModel> cellModelMap;

    private CellularAutomatonVisualizationModelContainer(Builder builder) {
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
    public GLRootModel getCAModel() {
        return cellularAutomaton;
    }

    /**
     * Returns floor model instances for the cellular automaton hierarchy. The {@code floor} is in the range {@code 0}
     * to the {@link #getFloorCount() floor count} exclusive.
     *
     * @param floor the floor number for which the model is returned
     * @return the floor visualization model instance
     */
    public GLFloorModel getFloorModel(int floor) {
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
     * @param room the instance for which the corresponding model should be returned; not {@code null}
     * @return the room visualization model instance
     */
    public GLRoomModel getRoomModel(Room room) {
        return roomModelMap.get(room);
    }

    /**
     * Returns cell model instances for the cellular automaton visualization hierarchy. The {@code cell} specifies the
     * corresponding instance in the cellular automaton.
     *
     * @param cell the instance for which the corresponding model should be returned; not {@code null}
     * @return the cell visualization model instance
     */
    public GLCellModel getCellModel(EvacCellInterface cell) {
        return cellModelMap.get(cell);
    }

    /**
     * Iterates over all floor visualization model instances.
     *
     * @see #getFloorModel(int)
     * @return iterable of all floor visualization model instances
     */
    Iterable<GLFloorModel> floors() {
        return floors;
    }

    /**
     * Iterates over all room visualization model instances.
     *
     * @see #getRoomModel(org.zet.cellularautomaton.Room)
     * @return iterable of all room visualization model instances
     */
    Iterable<GLRoomModel> rooms() {
        return roomModelMap.values();
    }

    /**
     * Iterates over all cell visualization model instances.
     *
     * @see #getCellModel(org.zet.cellularautomaton.EvacCellInterface)
     * @return iterable of all cell visualization model instances
     */
    Iterable<GLCellModel> cells() {
        return cellModelMap.values();
    }

    /**
     * Creates instances of the {@link CellularAutomatonVisualizationModelContainer}. The builder is not thread safe.
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
        private GLRootModel cellularAutomatonModel;
        /**
         * The {@link #build() built} floor visualization model instances.
         */
        private List<GLFloorModel> floors;
        /**
         * Map of the {@link #build() built} room visualization model instances.
         */
        private Map<Room, GLRoomModel> roomMap;
        /**
         * Map of the {@link #build() built} cell visualization model instances.
         */
        private Map<EvacCellInterface, GLCellModel> cellMap;

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
         * {@link GLFloorModel floors}, the {@link GLRoomControl rooms}, and the {@link GLCellModel cells}.
         *
         * @return the container object instance for all the built visualization model instances
         */
        public CellularAutomatonVisualizationModelContainer build() {
            this.floors = buildFloorModels();
            this.roomMap = createRoomMapping();
            this.cellMap = buildCells(roomMap.keySet());
            this.cellularAutomatonModel = new GLRootModel(visualizationModel);

            return new CellularAutomatonVisualizationModelContainer(this);
        }

        private List<GLFloorModel> buildFloorModels() {
            ArrayList<GLFloorModel> floorModels = new ArrayList<>(cellularAutomaton.getFloors().size());
            for (int i = 0; i < cellularAutomaton.getFloors().size(); ++i) {
                GLFloorModel floormodel = new GLFloorModel(caVisResults, i, visualizationModel);
                floorModels.add(floormodel);
            }
            return floorModels;
        }

        /**
         * Builds a map of all rooms to their respective GL model. The rooms in the cellular automaton must be unique.
         *
         * @return a mapping of all rooms
         */
        private Map<Room, GLRoomModel> createRoomMapping() {
            List<Map<Room, GLRoomModel>> roomModels = new ArrayList<>();
            for (int i = 0; i < cellularAutomaton.getFloors().size(); ++i) {
                Map<Room, GLRoomModel> roomModelsOnFloor = buildRoomModels(cellularAutomaton.getRoomsOnFloor(i));
                roomModels.add(roomModelsOnFloor);
            }
            return roomModels.stream()
                    .map(Map::entrySet)
                    .flatMap(Set::stream)
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        private Map<Room, GLRoomModel> buildRoomModels(Collection<Room> roomsOnTheFloor) {
            HashMap<Room, GLRoomModel> result = new HashMap<>();
            roomsOnTheFloor.forEach(
                    room -> result.put(room, new GLRoomModel(caVisResults, room, visualizationModel)));
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
        private Map<EvacCellInterface, GLCellModel> buildCells(Iterable<Room> rooms) throws ArithmeticException {
            long cellCount = StreamSupport.stream(rooms.spliterator(), false)
                    .map(Room::getAllCells)
                    .map(List::size)
                    .count();
            HashMap<EvacCellInterface, GLCellModel> result = new HashMap<>(Math.toIntExact(cellCount));
            for (Room room : rooms) {
                room.getAllCells().forEach(
                        cell -> result.put(cell, new GLCellModel(caVisResults, cell, visualizationModel)));
            }
            return result;
        }
    }
}
