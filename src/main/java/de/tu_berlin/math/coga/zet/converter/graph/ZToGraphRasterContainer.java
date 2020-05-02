/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
/*
 * ZToGraphRasterContainer.java
 *
 */

package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.zet.converter.RasterContainer;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * The class {@code ZToGraphRasterContainer} contains rastered versions of 
 * (all) rooms of a Z-Plan. 
 * The rastered rooms are stored as a {@code ZToGraphRoomRaster}.
 * 
 * The class extends the {@code RasterContainer} by a list of 
 * {@code ZToGraphRasteredDoor} objects to mark the doors in the
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
