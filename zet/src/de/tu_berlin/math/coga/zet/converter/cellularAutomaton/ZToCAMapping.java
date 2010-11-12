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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.tu_berlin.math.coga.zet.converter.cellularAutomaton;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class maps bidirectionally between "Cell"-Objects and "ZToCARasterSquare"-Objects, 
 * and moreover maps bidirectionally between Rooms of the Z-data structure and Rooms of
 * the CellularAutomaton-Datastructure.
 * @author marcel
 *
 */
public class ZToCAMapping 
{
	/**
	 * A Map that maps Cell-Objects to ZToCARasterSquare-Objects
	 */
	private Map<Cell, ZToCARasterSquare> cellToRasterSquare;
	
	/**
	 * A Map that maps ZToCARasterSquare-Objects to Cell-Objects
	 */
	private Map<ZToCARasterSquare, Cell> rasterSquareToCell;
	
	/**
	 * A Map that maps Room-Objects of the Z-Datastructure to
	 * Room-Objects of the ZA-Datastructure
	 */
	private Map<ZToCARoomRaster, ds.ca.Room> zRoomToZARoom;
	
	/**
	 * A Map that maps Room-Objects of the ZA-Datastructure to
	 * Room-Objects of the Z-Datastructure
	 */
	private Map<ds.ca.Room, ZToCARoomRaster> zARoomToZRoom;
	
	private Map<Integer, ds.z.Floor> zaFloorToZFloor;
	
	private Map<ds.z.Floor, Integer> zFloorToZAFloor;
	
	/**
	 * Constructs a new empty ZToCAMapping-Object, which is able to map
	 * bidirectionally between Cell-Objects and ZToCARasterSquare-Objects and
	 * moreover is able to map bidirectionally between Rooms of the 
	 * Z-Datastructure and the ZA-Datastructure.
	 */
	public ZToCAMapping()
	{
		this.cellToRasterSquare = new HashMap<Cell, ZToCARasterSquare>();
		this.rasterSquareToCell = new HashMap<ZToCARasterSquare, Cell>();
		this.zRoomToZARoom = new HashMap<ZToCARoomRaster, ds.ca.Room>();
		this.zARoomToZRoom = new HashMap<ds.ca.Room, ZToCARoomRaster>();
		this.zaFloorToZFloor = new HashMap<Integer, ds.z.Floor>();
		this.zFloorToZAFloor = new HashMap<ds.z.Floor, Integer>();
	}
	
	/**
	 * Inserts a new tuple (Cell, ZToCARasterSquare). This means that "Cell" and
	 * "ZToCARasterSquare" are associated to each other from now on bidirectionally.
	 * If at least one of the two parameter-values Cell or ZToCARasterSquare already
	 * exists in another tuple, this old tuple is removed and the new tuple will
	 * be inserted. In this case "true" is returned by this method.
	 * @param cell The Cell which shall be associated with the RasterSquare.
	 * @param rasterSquare The RasterSquare which shall be associated with the Cell.
	 * @return Returns "true", if another tuple has been removed from the list of
	 * tuples, because at least one the two parameter-values had already been inserted
	 * before, and "false" if not.
	 * @throws IllegalArgumentException Throws an "IllegalArgumentException" if at
	 * least one of the two parameter-values is a null-pointer. 
	 */
	public boolean insertTuple(Cell cell, ZToCARasterSquare rasterSquare) throws IllegalArgumentException {
		if (cell == null)
			throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.CellIsNullException"));
		if (rasterSquare == null)
			throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.RasterSquareIsNullException"));
		boolean overwrite = false;
		
		if(this.cellToRasterSquare.containsKey(cell) || this.rasterSquareToCell.containsKey(rasterSquare)){
		    overwrite = true;
		    ZToCARasterSquare oldSquare = this.cellToRasterSquare.get(cell);
		    ds.ca.Cell oldCell = this.rasterSquareToCell.get(rasterSquare);
		    
		    cellToRasterSquare.remove(oldCell);
		    rasterSquareToCell.remove(oldSquare);		    
		}
		
		this.cellToRasterSquare.put(cell, rasterSquare);
		this.rasterSquareToCell.put(rasterSquare, cell);
		return overwrite;
	}
	
	/**
	 * Inserts a new tuple (ds.z.Room, ds.ca.Room). This means that "zRoom" of 
	 * the Z-Datastructure and "zARoom" of the ZA-Datastructure are associated 
	 * to each other from now on bidirectionally.
	 * If at least one of the two parameter-values "zRoom" or "zARoom" already
	 * exists in another tuple, this old tuple is removed and the new tuple will
	 * be inserted. In this case "true" is returned by this method.
	 * @param roomRaster The room of the Z-Datastructure which shall be associated with 
	 * the room of the ZA-Datastructure.
	 * @param zARoom The room of the ZA-Datastructure which shall be associated with 
	 * the room of the Z-Datastructure.
	 * @return Returns "true", if another tuple has been removed from the list of
	 * tuples, because at least one the two parameter-values had already been inserted
	 * before, and "false" if not.
	 * @throws IllegalArgumentException Throws an "IllegalArgumentException" if at
	 * least one of the two parameter-values is a null-pointer. 
	 */
	public boolean insertTuple(ZToCARoomRaster roomRaster, ds.ca.Room zARoom) throws IllegalArgumentException {
		if (roomRaster == null)
			throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.zRoomIsNullException"));
		if (zARoom == null)
			throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.ZARoomIsNullException"));
		boolean overwrite = false;
		
		if (this.zRoomToZARoom.containsKey(roomRaster) || this.zARoomToZRoom.containsKey(zARoom)) {
			overwrite = true;
			
			ZToCARoomRaster oldRaster = this.zARoomToZRoom.get(zARoom);
			ds.ca.Room oldRoom = this.zRoomToZARoom.get(roomRaster);
			
			this.zARoomToZRoom.remove(oldRoom);
			this.zRoomToZARoom.remove(oldRaster);
		}
		
		this.zRoomToZARoom.put(roomRaster, zARoom);
		this.zARoomToZRoom.put(zARoom, roomRaster);
		return overwrite;
	}
	
	public boolean insertTuple(Integer floorID, ds.z.Floor zFloor){
       if (floorID == null)
            throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.FloorIDIsNullException"));
        if (zFloor == null)
            throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.ZFloorIsNullException"));
        
        boolean overwrite = false;
        if (this.zaFloorToZFloor.containsKey(floorID) || this.zFloorToZAFloor.containsKey(zFloor)) {
            overwrite = true;
            ds.z.Floor oldFloor = zaFloorToZFloor.get(floorID);
            Integer oldID = zFloorToZAFloor.get(oldFloor);
            this.zaFloorToZFloor.remove(oldID);
            this.zFloorToZAFloor.remove(oldFloor);
        }

        this.zaFloorToZFloor.put(floorID, zFloor);
        this.zFloorToZAFloor.put(zFloor, floorID);
        return overwrite;
	}
	
	/**
	 * Returns the ZToCARasterSquare which corresponds to the parameter "Cell".
	 * @param cell The cell, whose corresponding "ZToCARasterSquare" is requested.
	 * @return The ZToCARasterSquare which corresponds to parameter "Cell".
	 * @throws IllegalArgumentException Throws an "IllegalArgumentException", if
	 * the parameter "Cell" is a null-pointer.
	 */
	public ZToCARasterSquare get(Cell cell) throws IllegalArgumentException {
		if (cell == null)
			throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.CellIsNullException"));
        
		return this.cellToRasterSquare.get(cell);
	}
	
	/**
	 * Returns the Cell which corresponds to the parameter "rasterSquare".
	 * @param rasterSquare The rasterSquare, whose corresponding Cell is requested.
	 * @return The Cell which corresponds to the parameter "rasterSquare".
	 * @throws IllegalArgumentException Throws an "IllegalArgumentException", if
	 * the parameter "rasterSquare" is a null-pointer.
	 */
	public Cell get(ZToCARasterSquare rasterSquare) throws IllegalArgumentException {
		if (rasterSquare == null)
			throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.RasterSquareIsNullException"));
		return this.rasterSquareToCell.get(rasterSquare);
	}
	
	/**
	 * Returns the Room of the Z-Datastructure corresponding to the Room of
	 * the ZA-Datastructure.
	 * @param zARoom The Room of the ZA-Datastructure, whose corresponding
	 * Room of the Z-Datastructure is required.
	 * @return The Room of the Z-Datastructure corresponding to the Room of
	 * the ZA-Datastructure.
	 * @throws IllegalArgumentException Throws an "IllegalArgumentException", if
	 * the parameter "zARoom" is a null-pointer.
	 */
	public ZToCARoomRaster get(ds.ca.Room zARoom) throws IllegalArgumentException {
		if (zARoom == null)
			throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.ZARoomParameterException"));
		return this.zARoomToZRoom.get(zARoom);
	}
	
	public ds.z.Floor get(Integer floorID){
	    return zaFloorToZFloor.get(floorID);
	}
	
	public Integer get(ds.z.Floor floor){
	    return zFloorToZAFloor.get(floor);
	}
	
	/**
	 * Returns the Room of the ZA-Datastructure corresponding to the Room of
	 * the Z-Datastructure.
	 * @param rasteredRoom The Room of the Z-Datastructure, whose corresponding
	 * Room of the ZA-Datastructure is required.
	 * @return The Room of the ZA-Datastructure corresponding to the Room of
	 * the Z-Datastructure.
	 * @throws IllegalArgumentException Throws an "IllegalArgumentException", if
	 * the parameter "zRoom" is a null-pointer.
	 */
	public ds.ca.Room get(ZToCARoomRaster rasteredRoom) throws IllegalArgumentException {
		if (rasteredRoom == null)
			throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.ZRoomParameterIsNullException"));
		return this.zRoomToZARoom.get(rasteredRoom);
	}
	
	/**
	 * Returns the number of (Cell,ZToCARasterSquare)-tuples existing
	 * in this ZToCAMapping-Object.
	 * @return The number of (Cell,ZToCARasterSquare)-tuples existing
	 * in this ZToCAMapping-Object.
	 */
	public int nrOfCellRasterSquareTupels() {
		return this.cellToRasterSquare.size();
	}
	
	public int nrOfzRoomZARoomTupels() {
		return this.zRoomToZARoom.size();
	}
	
	/**
	 * Checks, whether there exists a tuple mapping from "cell" to an
	 * arbitrary rasterSquare.
	 * @param cell The cell to be checked (whether it exists or not).
	 * @return Returns "true", if there exists a tuple mapping from "cell" 
	 * to an arbitrary rasterSquare, "false" if not.
	 */
	public boolean contains(Cell cell) {
		return this.cellToRasterSquare.containsKey(cell);
	}
	
	/**
	 * Checks, whether there exists a tuple mapping from "rasterSquare" to an
	 * arbitrary Cell.
	 * @param rasterSquare The rasterSquare to be checked (whether it exists or not).
	 * @return Returns "true", if there exists a tuple mapping from "rasterSquare" 
	 * to an arbitrary Cell, "false" if not.
	 */
	public boolean contains(ZToCARasterSquare rasterSquare) {
		return this.rasterSquareToCell.containsKey(rasterSquare);
	}
	
	/**
	 * Checks, whether there exists a tuple mapping from "zRoom" of the 
	 * Z-Datastructure to an arbitrary Room of the ZA-Datastructure.
	 * @param rasteredRoom The Room of the Z-Datastructure to be checked 
	 * (whether it exists or not).
	 * @return Returns "true", if there exists a tuple mapping from "zRoom" 
	 * of the Z-Datastructure to an arbitrary Room of the ZA-Datastructure, 
	 * "false" if not.
	 */
	public boolean contains(ZToCARoomRaster rasteredRoom) {
		return this.zRoomToZARoom.containsKey(rasteredRoom);
	}
	
	/**
	 * Checks, whether there exists a tuple mapping from "zARoom" of the 
	 * ZA-Datastructure to an arbitrary Room of the Z-Datastructure.
	 * @param zARoom The Room of the ZA-Datastructure to be checked 
	 * (whether it exists or not).
	 * @return Returns "true", if there exists a tuple mapping from "zARoom" 
	 * of the ZA-Datastructure to an arbitrary Room of the Z-Datastructure, 
	 * "false" if not.
	 */
	public boolean contains(ds.ca.Room zARoom) {
		return this.zARoomToZRoom.containsKey(zARoom);
	}
	
	
	public boolean contains(Integer floorID){
	    return this.zaFloorToZFloor.containsKey(floorID);
	}

	public boolean contains(ds.z.Floor floor){
        return this.zFloorToZAFloor.containsKey(floor);
    }
	
	public Set<ds.ca.Room> getCARooms(){
	    return this.zARoomToZRoom.keySet();
	}
	
	public Set<Integer> getCAFloors(){
	    return this.zaFloorToZFloor.keySet();
	}
	
	public Set<ds.ca.Cell> getCACells(){
	    return this.cellToRasterSquare.keySet();
	}
	
	public ZToCAMapping adoptToCA(CellularAutomaton ca){
	    ZToCAMapping adoptedMapping = new ZToCAMapping();
	    
	    for(int floor : getCAFloors()){
	        adoptedMapping.insertTuple(floor, zaFloorToZFloor.get(floor));
	    }
	    
	    for(ds.ca.Room room : getCARooms()){
	        adoptedMapping.insertTuple(zARoomToZRoom.get(room), ca.getRoom(room.getID()));
	    }
	    
	    for(ds.ca.Cell cell : getCACells()){
	        ds.ca.Room newRoom = ca.getRoom(cell.getRoom().getID());
	        ds.ca.Cell adoptedCell = newRoom.getCell(cell.getX(), cell.getY());	        
	        
	        adoptedMapping.insertTuple(adoptedCell, cellToRasterSquare.get(cell));
	    }
	    
	    return adoptedMapping;
	}
}
