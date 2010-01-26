/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * DefaultEvacuationFloor.java
 * Created on 13. Dezember 2007, 19:59
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import java.util.ArrayList;
import de.tu_berlin.math.coga.common.localization.Localization;

/**
 * A <code>DefaultEvacuationFloor</code> basically behaves as a normal {@link ds.z.Floor}.
 * It can create automatically designed "evacuation rooms" that are rooms containing only
 * one evacuation area that are connected to another floor with an {@link ds.z.TeleportEdge}.
 * It is possible to use the normal methods to edit the floor, but it is recommended only
 * to use only the auto creating functison since they normally do not check wheter there
 * where manual edits.
 * <p>The default behaviour creates quadratic rooms of the same size arranged to a square.
 * This allows comfortable viewing the floor, but does not check for incompatibilities to
 * possibly changes. The behaviour can be changed, so that the new rooms are added
 * in a long line of rooms, just outside the currently used area. This is recommended if
 * direct changes to the floor structure are applied. </p>
 * <p> Warning! Once changed the behaviour, it cannot be changed back to default behaviour.
 * The created rooms in default mode have a defined size, which can be changed to allow
 * wider teleport edges. </p>
 * @see Floor#check()
 * @see ds.z.Floor
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "evacuationFloor" )
public class DefaultEvacuationFloor extends Floor {
	/** Stores if normal mode is on, or not. */
	@XStreamAsAttribute()
	private boolean normalMode = true;
	/** Sets the width of the used raster. */
	@XStreamAsAttribute()
	private int rasterSize = 1;
	/** The minimal size of a new room, the exact size in normal mode. */
	@XStreamAsAttribute()
	private int defaultRoomSize = 4000;
	/** The number of rooms already created */
	@XStreamAsAttribute()
	private int roomCount = 0;

	/**
	 * Creates a new instance of <code>DefaultEvacuationFloor</code> with the default name
	 * "EvacuationFloor".
	 */
	public DefaultEvacuationFloor() {
		super();
		this.setName( Localization.getInstance().getString( "ds.z.DefaultName.EvacuationFloor" ) );
	}

	/**
	 * Creates a new instance of <code>DefaultEvacuationFloor</code> with the specified name
	 * @param name the name of the floor
	 */
	public DefaultEvacuationFloor( String name ) {
		super( name );
	}

	/**
	 * Creates a new room that will be connected to the sourceEdge by calling
	 * Room.connectToWithTeleportEdge after having created the new room. The
	 * sourceEdge has to be aligned horizontally or vertically.
	 *
	 * @param sourceEdge the edge that should be made to a {@link TeleportEdge}
	 */
	public void addEvacuationRoom( RoomEdge sourceEdge ) {
		if( sourceEdge.length() > defaultRoomSize )
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.DefaultEvacuationFloor.EdgeToLongException" ) );
		if( !( sourceEdge.isHorizontal() | sourceEdge.isVertical() ) )
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.DefaultEvacuationFloor.EdgeNotVerticalOrHorizontalException" ) );
		if( sourceEdge.length() % rasterSize != 0 )
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.DefaultEvacuationFloor.EdgeLengthRasterException" ) );

		//int i = ++roomCount;
		int index = (int)Math.ceil( Math.sqrt( ++roomCount ) );
		int xPos;
		int yPos;
		if( roomCount <= ( index - 1 ) * ( index - 1 ) + index ) {
			// Is in the upper row
			xPos = roomCount - ( index - 1 ) * ( index - 1 );
			yPos = index - 1;
		} else {
			// Is in the right row
			xPos = index;
			yPos = index * index - roomCount;
		}

		// Create new room
		// Each room has width/height roomSize and space between is one raster
		// if edge is to long, we need to augmentate the defaultRoomSize
		while( sourceEdge.length() > defaultRoomSize / 2 )
			defaultRoomSize *= 2; // TODO: respect the rasterSize
		int roomX = xPos * ( rasterSize + defaultRoomSize );
		int roomY = yPos * ( rasterSize + defaultRoomSize );
		Room newRoom = new Room( this, Localization.getInstance().getString( "ds.z.DefaultName.EvacuationRoom" ) + " " + Integer.toString( roomCount ) );
		int w1 = rasterSize * (int)Math.floor( ( defaultRoomSize / 4 ) / rasterSize ); // Coordinate of the left / upper coordinate of the door in local coordinate system starting with (roomX, roomY)
		PlanPoint p1;
		PlanPoint p2;
		PlanPoint ul = new PlanPoint( roomX, roomY );
		PlanPoint ur = new PlanPoint( roomX + defaultRoomSize, roomY );
		PlanPoint lr = new PlanPoint( roomX + defaultRoomSize, roomY + defaultRoomSize );
		PlanPoint ll = new PlanPoint( roomX, roomY + defaultRoomSize );
		ArrayList<PlanPoint> points = new ArrayList<PlanPoint>( 6 );
		points.add( ul );
		points.add( ur );
		if( sourceEdge.isHorizontal() ) {
			points.add( lr );			// Lower right point
			// let the door start at a quarter, its maximal length is defaultRoomSize/2
			p1 = new PlanPoint( roomX + w1 + sourceEdge.length(), roomY + defaultRoomSize );			// Right point of the door
			points.add( p1 );
			p2 = new PlanPoint( roomX + w1, roomY + defaultRoomSize );														// Left point of the door
			points.add( p2 );
		} else {
			p1 = new PlanPoint( roomX + defaultRoomSize, roomY + w1 );														// Upper point of the door
			points.add( p1 );
			p2 = new PlanPoint( roomX + defaultRoomSize, roomY + w1 + sourceEdge.length() );			// Lower point of the door
			points.add( p2 );
			points.add( lr );
		}
		points.add( ll );
		newRoom.defineByPoints( points );
		Edge door = newRoom.getEdge( p1, p2 );

		// defineByPoints evacuation area
		EvacuationArea evac = new EvacuationArea( newRoom );
		points = new ArrayList<PlanPoint>( 4 );
		points.add( ul );
		points.add( ur );
		points.add( lr );
		points.add( ll );
		evac.defineByPoints( points );
		evac.setName( getNewEvacuationAreaName() );

		// door wird zur teleport edge
		Room.connectToWithTeleportEdge( sourceEdge, (RoomEdge)door );
	}
	
	/**
	 * Returns the number of evacuation exits on the default evacuation floor.
	 * @return the number of rooms
	 */
	private String getNewEvacuationAreaName() {
		String base = Localization.getInstance().getString( "ds.z.DefaultName.DefaultEvacuationArea" );
		int rooms = this.getRooms().size();
		return base + " " + rooms;
	}

	/**
	 * Sets the default size for new rooms. In normal mode all new rooms will have exactly the
	 * size defined by this value. In simple mode all rooms have at least the specified size.
	 * Already created rooms are not touched if the value is changed.
	 * @param size the default size
	 * @throws java.lang.IllegalArgumentException if the size is less than the former size in
	 * normal mode or if it does not fit to the raster.
	 */
	public void setDefaultRoomSize( int size ) {
		if( size % rasterSize != 0 )
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.DefaultEvacuationFloor.RoomSizeRasterException" ) );
		if( size < defaultRoomSize & normalMode )
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.DefaultEvacuationFloor.DefaultRoomSizeDecreasedException" ) );
		defaultRoomSize = size;
	}

	/**
	 * Sets the raster size to the specified value. The rooms already created are not aligned to
	 * the new raster.
	 * @param size the new raster size
	 * @throws java.lang.IllegalArgumentException if the raster is less or equal zero
	 */
	public void setRasterSize( int size ) {
		if( size <= 0 )
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.DefaultEvacuationFloor.RasterNegativeException" ) );
		rasterSize = size;
	}

	/**
	 * Allows switching to simple mode. Note that it is not possible to switch back.
	 * @param val must be true as it is not possible to switch back to normal mode
	 * @throws java.lang.UnsupportedOperationException if val is set to false
	 */
	public void setSimpleMode( boolean val ) throws java.lang.UnsupportedOperationException {
		if( val == true )
			normalMode = false;
		else
			throw new java.lang.UnsupportedOperationException( Localization.getInstance().getString( "ds.z.DefaultEvacuationFloor.SwitchToNormalModeException" ) );
	}
}
