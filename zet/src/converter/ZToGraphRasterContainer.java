/*
 * ZToGraphRasterContainer.java
 *
 */

package converter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * The class <code>ZToGraphRasterContainer</code> contains rastered versions of 
 * (all) rooms of a Z-Plan. 
 * The rastered rooms are stored as a <code>ZToGraphRoomRaster</code>.
 * 
 * The class extends the <code>RasterContainer</code> by a list of 
 * <code>ZToGraphRasteredDoor</code> objects to mark the doors in the
 * rastered plan.
 */
public class ZToGraphRasterContainer extends RasterContainer<ZToGraphRoomRaster>{
	
	private Collection<ZToGraphRasteredDoor> doors;
	
	public ZToGraphRasterContainer(){
		super();
		doors = new LinkedList<ZToGraphRasteredDoor>();
	}
	
	public void addDoor(ZToGraphRasteredDoor door){
		doors.add(door);
	}
	
	public Collection<ZToGraphRasteredDoor> getDoors(){
		return Collections.unmodifiableCollection(doors);
	}

}
