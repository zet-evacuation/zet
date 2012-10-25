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
import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class maps bidirectionally between "EvacCell"-Objects and "ZToCARasterSquare"-Objects, 
 * and moreover maps bidirectionally between Rooms of the Z-data structure and Rooms of
 * the EvacuationCellularAutomaton-Datastructure.
 * @author marcel
 *
 */
public class ZToCAMapping 
{
	/**
	 * A Map that maps EvacCell-Objects to ZToCARasterSquare-Objects
	 */
	private Map<EvacCell, ZToCARasterSquare> cellToRasterSquare;
	
	/**
	 * A Map that maps ZToCARasterSquare-Objects to EvacCell-Objects
	 */
	private Map<ZToCARasterSquare, EvacCell> rasterSquareToCell;
	
	/**
	 * A Map that maps Room-Objects of the Z-Datastructure to
	 * Room-Objects of the ZA-Datastructure
	 */
	private Map<ZToCARoomRaster, ds.ca.evac.Room> zRoomToZARoom;
	
	/**
	 * A Map that maps Room-Objects of the ZA-Datastructure to
	 * Room-Objects of the Z-Datastructure
	 */
	private Map<ds.ca.evac.Room, ZToCARoomRaster> zARoomToZRoom;
	
	private Map<Integer, ds.z.Floor> zaFloorToZFloor;
	
	private Map<ds.z.Floor, Integer> zFloorToZAFloor;
	
	/**
	 * Constructs a new empty ZToCAMapping-Object, which is able to map
	 * bidirectionally between EvacCell-Objects and ZToCARasterSquare-Objects and
	 * moreover is able to map bidirectionally between Rooms of the 
	 * Z-Datastructure and the ZA-Datastructure.
	 */
	public ZToCAMapping()
	{
		this.cellToRasterSquare = new HashMap<EvacCell, ZToCARasterSquare>();
		this.rasterSquareToCell = new HashMap<ZToCARasterSquare, EvacCell>();
		this.zRoomToZARoom = new HashMap<ZToCARoomRaster, ds.ca.evac.Room>();
		this.zARoomToZRoom = new HashMap<ds.ca.evac.Room, ZToCARoomRaster>();
		this.zaFloorToZFloor = new HashMap<Integer, ds.z.Floor>();
		this.zFloorToZAFloor = new HashMap<ds.z.Floor, Integer>();
	}
	
	/**
	 * Inserts a new tuple (EvacCell, ZToCARasterSquare). This means that "EvacCell" and
	 * "ZToCARasterSquare" are associated to each other from now on bidirectionally.
	 * If at least one of the two parameter-values EvacCell or ZToCARasterSquare already
	 * exists in another tuple, this old tuple is removed and the new tuple will
	 * be inserted. In this case "true" is returned by this method.
	 * @param cell The EvacCell which shall be associated with the RasterSquare.
	 * @param rasterSquare The RasterSquare which shall be associated with the EvacCell.
	 * @return Returns "true", if another tuple has been removed from the list of
	 * tuples, because at least one the two parameter-values had already been inserted
	 * before, and "false" if not.
	 * @throws IllegalArgumentException Throws an "IllegalArgumentException" if at
	 * least one of the two parameter-values is a null-pointer. 
	 */
	public boolean insertTuple(EvacCell cell, ZToCARasterSquare rasterSquare) throws IllegalArgumentException {
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
		    ds.ca.evac.EvacCell oldCell = this.rasterSquareToCell.get(rasterSquare);
		    
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
	public boolean insertTuple(ZToCARoomRaster roomRaster, ds.ca.evac.Room zARoom) throws IllegalArgumentException {
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
			ds.ca.evac.Room oldRoom = this.zRoomToZARoom.get(roomRaster);
			
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
	 * Returns the ZToCARasterSquare which corresponds to the parameter "EvacCell".
	 * @param cell The cell, whose corresponding "ZToCARasterSquare" is requested.
	 * @return The ZToCARasterSquare which corresponds to parameter "EvacCell".
	 * @throws IllegalArgumentException Throws an "IllegalArgumentException", if
	 * the parameter "EvacCell" is a null-pointer.
	 */
	public ZToCARasterSquare get(EvacCell cell) throws IllegalArgumentException {
		if (cell == null)
			throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.CellIsNullException"));
        
		return this.cellToRasterSquare.get(cell);
	}
	
	/**
	 * Returns the EvacCell which corresponds to the parameter "rasterSquare".
	 * @param rasterSquare The rasterSquare, whose corresponding EvacCell is requested.
	 * @return The EvacCell which corresponds to the parameter "rasterSquare".
	 * @throws IllegalArgumentException Throws an "IllegalArgumentException", if
	 * the parameter "rasterSquare" is a null-pointer.
	 */
	public EvacCell get(ZToCARasterSquare rasterSquare) throws IllegalArgumentException {
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
	public ZToCARoomRaster get(ds.ca.evac.Room zARoom) throws IllegalArgumentException {
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
	public ds.ca.evac.Room get(ZToCARoomRaster rasteredRoom) throws IllegalArgumentException {
		if (rasteredRoom == null)
			throw new IllegalArgumentException(DefaultLoc.getSingleton (
			).getString ("converter.ZRoomParameterIsNullException"));
		return this.zRoomToZARoom.get(rasteredRoom);
	}
	
	/**
	 * Returns the number of (EvacCell,ZToCARasterSquare)-tuples existing
	 * in this ZToCAMapping-Object.
	 * @return The number of (EvacCell,ZToCARasterSquare)-tuples existing
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
	public boolean contains(EvacCell cell) {
		return this.cellToRasterSquare.containsKey(cell);
	}
	
	/**
	 * Checks, whether there exists a tuple mapping from "rasterSquare" to an
	 * arbitrary EvacCell.
	 * @param rasterSquare The rasterSquare to be checked (whether it exists or not).
	 * @return Returns "true", if there exists a tuple mapping from "rasterSquare" 
	 * to an arbitrary EvacCell, "false" if not.
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
	public boolean contains(ds.ca.evac.Room zARoom) {
		return this.zARoomToZRoom.containsKey(zARoom);
	}
	
	
	public boolean contains(Integer floorID){
	    return this.zaFloorToZFloor.containsKey(floorID);
	}

	public boolean contains(ds.z.Floor floor){
        return this.zFloorToZAFloor.containsKey(floor);
    }
	
	public Set<ds.ca.evac.Room> getCARooms(){
	    return this.zARoomToZRoom.keySet();
	}
	
	public Set<Integer> getCAFloors(){
	    return this.zaFloorToZFloor.keySet();
	}
	
	public Set<ds.ca.evac.EvacCell> getCACells(){
	    return this.cellToRasterSquare.keySet();
	}
	
	public ZToCAMapping adoptToCA(EvacuationCellularAutomaton ca){
	    ZToCAMapping adoptedMapping = new ZToCAMapping();
	    
	    for(int floor : getCAFloors()){
	        adoptedMapping.insertTuple(floor, zaFloorToZFloor.get(floor));
	    }
	    
	    for(ds.ca.evac.Room room : getCARooms()){
	        adoptedMapping.insertTuple(zARoomToZRoom.get(room), ca.getRoom(room.getID()));
	    }
	    
	    for(ds.ca.evac.EvacCell cell : getCACells()){
	        ds.ca.evac.Room newRoom = ca.getRoom(cell.getRoom().getID());
	        ds.ca.evac.EvacCell adoptedCell = newRoom.getCell(cell.getX(), cell.getY());	        
	        
	        adoptedMapping.insertTuple(adoptedCell, cellToRasterSquare.get(cell));
	    }
	    
	    return adoptedMapping;
	}
}
