/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
package io.visualization;

import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import java.util.HashMap;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zetool.math.vectormath.Vector3;

/**
 * A data structure containing all information about a {@link EvacuationCellularAutomaton}, including the (real
 * z-format) positions of the cells. The results do not contain actual simulation result, but only the static input.
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonVisualizationResults {

    /**
     * A mapping from a cell to its offset relative to the room containing it. (The offset of the room is NOT
     * included!).
     */
    private final HashMap<org.zet.cellularautomaton.EvacCell, Vector3> caCellToZOffsetMapping;
    /**
     * A mapping from a room to its offset relative to the floor containing it. (The offset of the floor is NOT
     * included!).
     */
    private final HashMap<org.zet.cellularautomaton.Room, Vector3> caRoomToZOffsetMapping;
    /**
     * A mapping from a floor (given by ID) to its offset relative to the origin of the z-project.
     */
    private final HashMap<Integer, Vector3> caFloorToZOffsetMapping;
    private MultiFloorEvacuationCellularAutomaton cellularAutomaton;

    /**
     * Creates the visualization results. Takes a ca data structure, a visual recording object, a ZToCAMapping and
     * creates all necessary objects.
     *
     * @param visRecording
     * @param caMapping
     */
    public CellularAutomatonVisualizationResults(ZToCAMapping caMapping, MultiFloorEvacuationCellularAutomaton ca) {
        caCellToZOffsetMapping = new HashMap<>();
        caRoomToZOffsetMapping = new HashMap<>();
        caFloorToZOffsetMapping = new HashMap<>();
        this.cellularAutomaton = ca;

        caMapping = caMapping.adoptToCA(ca);

        convertMapping(caMapping);
    }

    public CellularAutomatonVisualizationResults(ZToCAMapping caMapping) {
        caCellToZOffsetMapping = new HashMap<>();
        caRoomToZOffsetMapping = new HashMap<>();
        caFloorToZOffsetMapping = new HashMap<>();
        convertMapping(caMapping);
    }

    private void convertMapping(ZToCAMapping caMapping) {
        for (Integer floorID : caMapping.getCAFloors()) {
            de.zet_evakuierung.model.Floor zFloor = caMapping.get(floorID);
            double xOffset = zFloor.getxOffset();
            double yOffset = zFloor.getyOffset();

            caFloorToZOffsetMapping.put(floorID, new Vector3(xOffset, yOffset, 0));
        }

        for (org.zet.cellularautomaton.Room room : caMapping.getCARooms()) {
            de.zet_evakuierung.model.Room zRoom = caMapping.get(room).getRoom();
            double xOffset = zRoom.getPolygon().getxOffset();
            double yOffset = zRoom.getPolygon().getyOffset();

            caRoomToZOffsetMapping.put(room, new Vector3(xOffset, yOffset, 0));
        }

        for (org.zet.cellularautomaton.EvacCell cell : caMapping.getCACells()) {
            de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterSquare zRasterSquare = caMapping.get(cell);

            double xOffset = zRasterSquare.getRelativeX();
            double yOffset = zRasterSquare.getRelativeY();

            caCellToZOffsetMapping.put(cell, new Vector3(xOffset, yOffset, 0));
        }
    }

    public Vector3 get(org.zet.cellularautomaton.EvacCell cell) {
        if (caCellToZOffsetMapping.get(cell) == null) {
            return new Vector3(0.0d, 0.0d, 0.0d);
        }
        return caCellToZOffsetMapping.get(cell);
    }

    public Vector3 get(org.zet.cellularautomaton.Room room) {
        if (caRoomToZOffsetMapping.get(room) == null) {
            return new Vector3(0.0d, 0.0d, 0.0d);
        }

        return caRoomToZOffsetMapping.get(room);
    }

    public Vector3 get(Integer floorID) {
        if (caFloorToZOffsetMapping.get(floorID) == null) {
            return new Vector3(0.0d, 0.0d, 0.0d);
        }
        return caFloorToZOffsetMapping.get(floorID);
    }

    public EvacuationCellularAutomaton getCa() {
        return cellularAutomaton;
    }
}
