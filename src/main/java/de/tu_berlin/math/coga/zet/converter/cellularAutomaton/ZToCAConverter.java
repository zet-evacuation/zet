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
package de.tu_berlin.math.coga.zet.converter.cellularAutomaton;

import de.tu_berlin.math.coga.zet.ZETLocalization2;
import static org.zetool.common.util.Direction8.*;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import de.tu_berlin.math.coga.zet.converter.RoomRasterSquare;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.Floor;
import de.zet_evakuierung.model.FloorInterface;
import de.zet_evakuierung.model.Project;
import de.zet_evakuierung.model.TeleportArea;
import evacuationplan.BidirectionalNodeCellMapping;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.SaveCell;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Level;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton.EvacuationCellularAutomatonBuilder;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.RoomImpl;
import org.zet.cellularautomaton.StairCell;
import org.zet.cellularautomaton.TeleportCell;
import org.zet.cellularautomaton.potential.PotentialAlgorithm;
import org.zetool.simulation.cellularautomaton.tools.CellMatrixFormatter;

/**
 * This singleton class converts a rasterized z-Project to a cellular automaton.
 *
 * @author Daniel R. Schmidt
 * @author Jan-Philipp Kappmeier
 *
 */
public class ZToCAConverter extends AbstractAlgorithm<BuildingPlan, ConvertedCellularAutomaton> {

    /** The latest created mapping of the z-format to the cellular automaton. */
    private ZToCAMapping lastMapping = null;
    /** The latest created container of rastered elements. */
    private ZToCARasterContainer lastContainer = null;
    /** A list of all exit cells in the cellular automaton. */
    private ArrayList<ExitCell> exitCells = null;
    /** The latest created cellular automaton. */
    private MultiFloorEvacuationCellularAutomaton lastCA = null;
    /** A map that maps rastered rooms to rooms in the cellular automaton. */
    private HashMap<ZToCARoomRaster, RoomImpl> roomRasterRoomMapping = null;

    @Override
    protected ConvertedCellularAutomaton runAlgorithm(BuildingPlan problem) {
        log.info("Converting building plan into cellular automaton.");
        try {
            convert(problem);
        } catch (ConversionNotSupportedException ex) {
            Logger.getLogger(ZToCAConverter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        log.setLevel(java.util.logging.Level.FINEST);
        log.log(java.util.logging.Level.FINE, "Created cellular automaton with {0} rooms.", lastCA.getRooms().size());
        for (Room r : lastCA.getRooms()) {
            log.log(java.util.logging.Level.FINER, "Room {0}", r.getID());
            log.finer(CellMatrixFormatter.format(r));
        }
        ConvertedCellularAutomaton cca = new ConvertedCellularAutomaton(lastCA, lastMapping, lastContainer);
        return cca;
    }

    public static class ConversionNotSupportedException extends Exception {

        private static final long serialVersionUID = 6776678031861741260L;

        /**
         * Creates a new default instance of {@code ConversionNotSupportedException}.
         *
         */
        public ConversionNotSupportedException() {
            super(ZETLocalization2.loc.getString("converter.ZConversionException"));
        }

        /**
         * Creates a new instance of {@code ConversionNotSupportedException} with a specified error message.
         *
         * @param message the error message
         */
        public ConversionNotSupportedException(String message) {
            super(message);
        }
    }

    /**
     * Creates a new instance of this singleton class.
     */
    public ZToCAConverter() {
    }

    public ZToCAConverter(Project project) {
        setProblem(project.getBuildingPlan());
    }

    /**
     * Call this method to convert the rastered rooms of a z-project to a cellular automaton. The returned automaton is
     * structure-only, i.e. there are no individuals in the automaton. However, all cell types are being set correctly,
     * the doors are being linked and obstacles are being set as well.
     *
     * @param buildingPlan the building plan of a Z-project
     * @return A cellular automaton corresponding to the rastered rooms.
     * @throws converter.ZToCAConverter.ConversionNotSupportedException
     */
    private EvacuationCellularAutomaton convert(BuildingPlan buildingPlan) throws ConversionNotSupportedException {
        EvacuationCellularAutomatonBuilder caBuilder = new EvacuationCellularAutomatonBuilder();
        lastMapping = new ZToCAMapping();
        lastContainer = RasterContainerCreator.getInstance().ZToCARasterContainer(buildingPlan);
        exitCells = new ArrayList<>();
        roomRasterRoomMapping = new HashMap<>();

        for (FloorInterface floor : lastContainer.getFloors()) {
            createAllRooms(floor, lastContainer.getAllRasteredRooms(floor), buildingPlan.getFloorID(floor));
        }

        int floorLevel = 0;
        List<Exit> exits = new LinkedList<>();
        for (FloorInterface floor : lastContainer.getFloors()) {
            caBuilder.addFloor(floorLevel++, floor.getName());
            Collection<ZToCARoomRaster> rooms = lastContainer.getAllRasteredRooms(floor);
            if (rooms != null) {
                for (ZToCARoomRaster rasteredRoom : rooms) {
                    RoomImpl convertedRoom = convertRoom(rasteredRoom, floor, buildingPlan.getFloorID(floor));
                    exits.addAll(caBuilder.addRoom(convertedRoom));
                }
            }
        }

        computeAndAddStaticPotentials(caBuilder, exits);
        MultiFloorEvacuationCellularAutomaton convertedCA = caBuilder.build();

        lastCA = convertedCA;
        return convertedCA;
    }

    /**
     * Private method that calculates the static potentials for a converted ca and adds them to a (new) potential
     * controller for the ca.
     *
     * @param caBuilder The cellular automaton that needs potentials.
     * @param exits
     */
    protected void computeAndAddStaticPotentials(EvacuationCellularAutomatonBuilder caBuilder, List<Exit> exits) {
        //calculate and defineByPoints staticPotentials to CA
        for (Exit exit : exits) {
            PotentialAlgorithm pa = new PotentialAlgorithm();
            pa.setProblem(exit.getExitCluster());
            StaticPotential sp = pa.call();
            caBuilder.setPotentialFor(exit, sp);
            // Bestimme die angrenzenden Save-Cells
            saveCellSearch(exit.getExitCluster(), sp);
        }
        
        //PotentialManager pm = convertedCA.getPotentialManager();
        generateSafePotential(caBuilder);
    }
    
    public void generateSafePotential(EvacuationCellularAutomatonBuilder caBuilder) {
        StaticPotential safePotential = new StaticPotential();
        Iterable<Room> rooms = caBuilder.getRooms();
        for (Room r : rooms) {
            List<EvacCell> cells = r.getAllCells();
            for (EvacCell c : cells) {
                safePotential.setPotential(c, 1);
            }
        }
        safePotential.setName("SafePotential");
        caBuilder.setSafePotential(safePotential);
    }

    /**
     * <p>
     * Searches all reachable save cells from a given bunch of exit cells. If a save cell that is found has no static
     * potential assigned, the potential of the exit cells is assigned. If the save cell has already a static potential
     * assigned, the potential of the exit cells is assigned if it is better.</p>
     * <p>
     * The cells are searched in a breadth first search manner, all found cells are stored in a queue. As the cells have
     * no label, another list is used to retrieve all cells already labeled. This needs more space, otherwise we would
     * have to reset the labels after every call of the method.</p>
     *
     * @param exitCells the bunch of connected exit cells from that the search starts
     * @param sp the potential starting at the exit cells
     */
    private void saveCellSearch(Collection<ExitCell> exitCells, StaticPotential sp) {
        ArrayDeque<SaveCell> Q = new ArrayDeque<>();
        ArrayList<SaveCell> V = new ArrayList<>();
        for (EvacCell cell : exitCells) {
            for (EvacCellInterface c : cell.getNeighbours()) {
                if (c instanceof SaveCell && !V.contains((SaveCell) c)) {
                    Q.addLast((SaveCell) c);
                    V.add((SaveCell) c);
                }
            }
        }
        while (Q.size() > 0) {
            SaveCell c = Q.pollFirst();
            if (c.getExitPotential() == null || c.getExitPotential().getPotential(c) > sp.getPotential(c)) {
                c.setExitPotential(sp);
            }
            for (EvacCellInterface cell : c.getNeighbours()) {
                if (cell instanceof SaveCell && !V.contains((SaveCell) cell)) {
                    Q.addLast((SaveCell) cell);
                    V.add((SaveCell) cell);
                }
            }
        }
    }

    /**
     * When a {@link ZToCARasterContainer} is converted, a mapping between the squares in the raster and the cells of
     * the cellular automaton is stored. Each time you call {@link #convertRoom( ZToCARoomRaster, Floor, int)}, the
     * stored mapping is overwritten. This method retrieves the mapping from the last convert-operation.
     *
     * @return The mapping that was created during the last conversion.
     */
    public ZToCAMapping getMapping() {
        if (lastMapping == null) {
            throw new IllegalStateException(ZETLocalization2.loc.getString("converter.CallConvertFirstException"));
        }

        return lastMapping;
    }

    /**
     * This method returns the data needed from thes ca converter to create a {@code BidirectionalNodeCellMapping}.
     *
     * @return A {@code BidirectionalNodeCellMapping.CAPartOfMapping} object containing a raster container and a
     * {@code ZToCAMapping}.
     */
    public BidirectionalNodeCellMapping.CAPartOfMapping getLatestCAPartOfNodeCellMapping() {
        return new BidirectionalNodeCellMapping.CAPartOfMapping(getContainer(), getMapping());
    }

    /**
     * Returns the last {@code ZToCARasterContainer} that has been created.
     *
     * @return the last {@code ZToCARasterContainer} that has been created.
     * @throws IllegalStateException if no container has been created yet
     */
    public ZToCARasterContainer getContainer() throws IllegalStateException {
        if (lastContainer == null) {
            throw new IllegalStateException(ZETLocalization2.loc.getString("converter.CallConvertFirstException"));
        }

        return lastContainer;
    }

    /**
     * Returns the last {@code EvacuationCellularAutomaton}that was created.
     *
     * @return the cellular automaton
     */
    public MultiFloorEvacuationCellularAutomaton getCellularAutomaton() {
        return lastCA;
    }

    /**
     * Converts a rastered room (from z-format) to a {@code Room} used by the cellular automaton.
     *
     * @param rasteredRoom the rastered room
     * @param onFloor the floor on which the room lies
     * @param floorID the id of the floor (that means the level)
     * @return the created {@link ds.ca.Room}
     * @throws de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException if
     * an error occurs
     */
    protected RoomImpl convertRoom(ZToCARoomRaster rasteredRoom, FloorInterface onFloor, int floorID) throws ConversionNotSupportedException {
        final int width = rasteredRoom.getColumnCount();
        final int height = rasteredRoom.getRowCount();
        RoomImpl convertedRoom = roomRasterRoomMapping.get(rasteredRoom);
        System.out.println("Convert room " + onFloor.getName() + " " + rasteredRoom.getRoom().getName());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                EvacCell aCell = convertCell(rasteredRoom.getSquare(x, y), x, y, convertedRoom);
                if (aCell != null) {
                    //convertedRoom.setCell( aCell );
                    copyBounds(rasteredRoom.getSquare(x, y), aCell);
                    copyLevels(rasteredRoom.getSquare(x, y), aCell);
                }
            }
        }

        connectBounds(convertedRoom);
        lastMapping.insertTuple(rasteredRoom, convertedRoom);

        return convertedRoom;
    }

    /**
     * <p>
     * Creates all rooms for a given floor. The rooms have to be submitted as a collection, also the {@code Floor} and
     * the corresponding id in the z-format have to be submitted to the method.</p>
     * <p>
     * The rooms are only created, they are not filled with cells yet.</p>
     *
     * @param onFloor the Floor that contains the rooms
     * @param rooms a collection of rooms on the floor. This is not checked!
     * @param floorID the id of the floor
     */
    protected void createAllRooms(FloorInterface onFloor, Collection<ZToCARoomRaster> rooms, int floorID) {
        if (rooms != null) {
            for (ZToCARoomRaster rasteredRoom : rooms) {
                final int width = rasteredRoom.getColumnCount();
                final int height = rasteredRoom.getRowCount();
                final int xOffset = rasteredRoom.getRoom().getPolygon().getxOffset() / 400;
                final int yOffset = rasteredRoom.getRoom().getPolygon().getyOffset() / 400;
                RoomImpl room = new RoomImpl(width, height, floorID, xOffset, yOffset);
                roomRasterRoomMapping.put(rasteredRoom, room);
            }
        }
    }

    /**
     * <p>
     * Converts a {@link ZToCARasterSquare} to a {@link ds.ca.EvacCell} of the appropriate type e.g. DoorCell or
     * ExitCell. The position in the room has to be submitted and the converted Room which should contain the created
     * cell.</p>
     * <p>
     * During creation of door cells another cell can be created (the partner). In that case, the cell already existing
     * is returned.</p>
     *
     * @param square the square
     * @param x the column of the cell in the room
     * @param y the row of the cell in the room
     * @param convertedRoom the room that contains the cell
     * @return the new (or already existing) cell or null if the square is isInaccessible
     * @throws ConversionNotSupportedException if an initialization error occurred.
     */
    protected EvacCell convertCell(ZToCARasterSquare square, int x, int y, RoomImpl convertedRoom) throws ConversionNotSupportedException {
        if (square == null) {
            return null;
        }

        if (square.isInaccessible() && square.isDoor()) {
            throw new ConversionNotSupportedException(ZETLocalization2.loc.getString("algo.ca.NotInitializedException" + x + ", " + y));
        }

//        if(square.isExit() && square.isDoor()){
//            throw new ConversionNotSupportedException("Doors in exit areas are currently not supported.");
//        }
//
//        if(square.isSave() && square.isDoor()){
//            throw new ConversionNotSupportedException("Doors in save areas are currently not supported.");
//        }
        if (square.isInaccessible()) {
            return null;
        }

        if (square.isDoor()) {
            DoorCell door = (DoorCell) lastMapping.get(square);
            if (door == null) {
                door = new DoorCell(square.getSpeedFactor(), x, y);
                convertedRoom.setCell(door);
                lastMapping.insertTuple(door, square);
            }

            for (ZToCARasterSquare partner : square.getPartners()) {

                DoorCell partnerDoor = (DoorCell) lastMapping.get(partner);
                if (partnerDoor == null) {
                    ZToCARoomRaster partnerRoom = getContainer().getRasteredRoom((de.zet_evakuierung.model.Room) (partner.getPolygon()));
                    int newX = de.tu_berlin.math.coga.zet.converter.RasterTools.polyCoordToRasterCoord(partner.getX(), partnerRoom.getXOffset(), partnerRoom);
                    int newY = de.tu_berlin.math.coga.zet.converter.RasterTools.polyCoordToRasterCoord(partner.getY(), partnerRoom.getYOffset(), partnerRoom);

                    partnerDoor = new DoorCell(partner.getSpeedFactor(), newX, newY);
                    RoomImpl newRoom = roomRasterRoomMapping.get(partnerRoom);
                    newRoom.setCell(partnerDoor);
                    lastMapping.insertTuple(partnerDoor, partner);
                }

                door.addTarget(partnerDoor);
            }

            return door;
        }

        if (square.isExit()) {
            ExitCell newCell = new ExitCell(square.getSpeedFactor(), x, y);
            convertedRoom.setCell(newCell);
            newCell.setAttractivity(square.getAttractivity());
            newCell.setName(square.getName());
            lastMapping.insertTuple(newCell, square);
            exitCells.add(newCell);
            return newCell;
        }

        if (square.isSave()) {
            EvacCell newCell = new SaveCell(square.getSpeedFactor(), x, y);
            convertedRoom.setCell(newCell);
            lastMapping.insertTuple(newCell, square);
            return newCell;
        }

        // TODO insertTuple is very inefficient!
        if (square.isStair()) {
            EvacCell newCell = new StairCell(square.getSpeedFactor(), square.getUpSpeedFactor(), square.getDownSpeedFactor(), x, y);
            convertedRoom.setCell(newCell);
            lastMapping.insertTuple(newCell, square);
            return newCell;
        }

        if (square.isTeleport()) {
            System.out.println("this was a teleport cell");

            TeleportCell teleport = (TeleportCell) lastMapping.get(square);
            // create only, if not already was created
            if (teleport == null) {
                teleport = new TeleportCell(square.getSpeedFactor(), x, y);
                convertedRoom.setCell(teleport);
                lastMapping.insertTuple(teleport, square);
            }

            //System.out.println( square.getPolygon() );
            // Find the appropriate TeleportArea
            de.zet_evakuierung.model.Room r = (de.zet_evakuierung.model.Room) square.getPolygon();
            for (TeleportArea t : r.getTeleportAreas()) {
                if (t.contains(square.getSquare())) {
                    System.out.println("Teleport-Area gefunden: " + t.getName());
                    if (t.getTargetArea() == null) {
                        System.out.println("Zielgebiet ist: " + " --- ");
                    } else {
                        System.out.println("Zielgebiet ist: " + t.getTargetArea().getName());
                        if (t.equals(t.getTargetArea())) {
                            System.out.println("Die gleichen Bereiche. Ignorieren. ");
                        } else {
                            System.out.println("Suche die Zielzelle:");

                            de.zet_evakuierung.model.Room targetRoom = t.getAssociatedRoom();
                            ZToCARoomRaster targetRoomRaster = lastContainer.getRasteredRoom(targetRoom);
                            //ZToCARoomRaster roomRaster = lastMapping.get( lastContainer.getRasteredRoom( targetRoom) );
                            for (ZToCARasterSquare sq : targetRoomRaster.getAccessibleSquares()) {
                                if (t.getTargetArea().contains(sq.getSquare())) {
                                    // Die zielarea liegt im rastersquare sq
                                    TeleportCell targetCell = (TeleportCell) lastMapping.get(sq);
                                    if (targetCell == null) {
                                        // zielzelle muss erstellt werden
                                        int newX = de.tu_berlin.math.coga.zet.converter.RasterTools.polyCoordToRasterCoord(sq.getX(), targetRoomRaster.getXOffset(), targetRoomRaster);
                                        int newY = de.tu_berlin.math.coga.zet.converter.RasterTools.polyCoordToRasterCoord(sq.getY(), targetRoomRaster.getYOffset(), targetRoomRaster);
                                        targetCell = new TeleportCell(sq.getSpeedFactor(), newX, newY);
                                        RoomImpl newRoom = roomRasterRoomMapping.get(targetRoomRaster);
                                        newRoom.setCell(targetCell);
                                        lastMapping.insertTuple(targetCell, sq);

                                    }

                                    // Setze die partnerzelle
                                    teleport.addTarget(targetCell);
//                                    }
                                }
                            }

                            System.out.println("");
                        }
                    }
                }
            }

            // Find the partner cell
            //ZToCARoomRaster partnerRoom = getInstance().getContainer() .getRasteredRoom( (ds.z.Room) () );
            return teleport;
        }

        EvacCell newCell = new RoomCell(square.getSpeedFactor(), x, y);
        convertedRoom.setCell(newCell);
        lastMapping.insertTuple(newCell, square);
        return newCell;
    }

    /**
     * Copies the bonds from a square to the corresponding cell. That means, if a direction of the square is impassable
     * the direction is set impassable in the cell, too and vice versa.
     *
     * @param fromSquare the square
     * @param toCell the cell
     */
    protected static void copyBounds(RoomRasterSquare fromSquare, EvacCell toCell) {
        if (toCell == null) {
            return;
        }
        for (Direction8 direction : Direction8.values()) {
            if (fromSquare.isBlocked(direction)) {
                toCell.setUnPassable(direction);
            } else {
                toCell.setPassable(direction);
            }
        }
    }

    /**
     * Copies the level from a square to the corresponding cell.
     *
     * @param fromSquare the square
     * @param toCell the cell
     */
    protected static void copyLevels(RoomRasterSquare fromSquare, EvacCell toCell) {
        if (toCell == null) {
            return;
        }
        for (Direction8 direction : Direction8.values()) {
            Level level = fromSquare.getLevel(direction);
            toCell.setLevel(direction, level);
        }
    }

    static final boolean WALK_DIAGONAL_STRICT = true;

    /**
     * <p>
     * Sets the bounds for the cells in a specified room. That means, that bounds are set, if a cell in a direction is
     * not reachable.</p>
     * <p>
     * A cell is not reachable diagonally if only one of the horizontal or vertical neighbor cells is not reachable. For
     * example, the upper Left cell is not reachable if the upper or the Left cell is not reachable.</p>
     *
     * @param room the room for that the borders are set up
     */
    protected static void connectBounds(org.zet.cellularautomaton.Room room) {
        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                if (room.existsCellAt(x, y)) {
                    EvacCell aCell = room.getCell(x, y);

                    if (!aCell.isPassable(Left)) {
                        if (room.existsCellAt(x, y + 1) && !room.getCell(x, y + 1).isPassable(Left)) {
                            aCell.setUnPassable(DownLeft);
                            room.getCell(x, y + 1).setUnPassable(TopLeft);
                        }
                    }

                    if (!aCell.isPassable(Right)) {
                        if (room.existsCellAt(x, y + 1) && !room.getCell(x, y + 1).isPassable(Right)) {
                            aCell.setUnPassable(DownRight);
                            room.getCell(x, y + 1).setUnPassable(TopRight);
                        }
                    }

                    if (!aCell.isPassable(Top)) {
                        if (room.existsCellAt(x + 1, y) && !room.getCell(x + 1, y).isPassable(Top)) {
                            aCell.setUnPassable(TopRight);
                            room.getCell(x + 1, y).setUnPassable(TopLeft);
                        }
                    }

                    if (!aCell.isPassable(Down)) {
                        if (room.existsCellAt(x + 1, y) && !room.getCell(x + 1, y).isPassable(Down)) {
                            aCell.setUnPassable(DownRight);
                            room.getCell(x + 1, y).setUnPassable(DownLeft);
                        }
                    }
                }
            }
        }

        // Disable diagonally reachable cells if one edge is blocked. Behaviour changed:
        // it is not neccessary to have two sides blocked, but only one.
        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                if (room.existsCellAt(x, y)) {
                    EvacCell aCell = room.getCell(x, y);
                    if ((WALK_DIAGONAL_STRICT && (isDirectionBlocked(aCell, Top) || isDirectionBlocked(aCell, Left))) || (!WALK_DIAGONAL_STRICT && (isDirectionBlocked(aCell, Top) && isDirectionBlocked(aCell, Left)))) {
                        aCell.setUnPassable(TopLeft);

                        if (room.existsCellAt(x - 1, y - 1)) {
                            room.getCell(x - 1, y - 1).setUnPassable(DownRight);
                        }
                    }

                    if ((WALK_DIAGONAL_STRICT && (isDirectionBlocked(aCell, Top) || isDirectionBlocked(aCell, Right))) || (!WALK_DIAGONAL_STRICT && (isDirectionBlocked(aCell, Top) && isDirectionBlocked(aCell, Right)))) {
                        aCell.setUnPassable(Direction8.TopRight);

                        if (room.existsCellAt(x + 1, y - 1)) {
                            room.getCell(x + 1, y - 1).setUnPassable(DownLeft);
                        }
                    }

                    if ((WALK_DIAGONAL_STRICT && (isDirectionBlocked(aCell, Down) || isDirectionBlocked(aCell, Left))) || (!WALK_DIAGONAL_STRICT && (isDirectionBlocked(aCell, Down) && isDirectionBlocked(aCell, Left)))) {
                        aCell.setUnPassable(Direction8.DownLeft);

                        if (room.existsCellAt(x - 1, y + 1)) {
                            room.getCell(x - 1, y + 1).setUnPassable(TopRight);
                        }
                    }

                    if ((WALK_DIAGONAL_STRICT && (isDirectionBlocked(aCell, Down) || isDirectionBlocked(aCell, Right))) || (!WALK_DIAGONAL_STRICT && (isDirectionBlocked(aCell, Down) && isDirectionBlocked(aCell, Right)))) {
                        aCell.setUnPassable(Direction8.DownRight);

                        if (room.existsCellAt(x + 1, y + 1)) {
                            room.getCell(x + 1, y + 1).setUnPassable(TopLeft);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks whether a given direction for a cell is blocked, or not.
     *
     * @param aCell the cell
     * @param direction the direction
     * @return true if the cell cannot be leaved in the given direction.
     */
    private static boolean isDirectionBlocked(EvacCell aCell, Direction8 direction) {
        int x = aCell.getX();
        int y = aCell.getY();
        org.zet.cellularautomaton.Room room = aCell.getRoom();

        return !aCell.isPassable(direction) || !room.existsCellAt(x + direction.xOffset(), y + direction.yOffset());
    }
}
