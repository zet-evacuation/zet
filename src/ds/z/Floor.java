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

/*
 * Floor.java
 * Created on 26. November 2007, 21:32
 */

package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import ds.z.exception.AreaNotInsideException;
import ds.z.exception.PolygonNotClosedException;
import ds.z.exception.RoomIntersectException;
import ds.z.exception.TeleportEdgeInvalidTargetException;
import ds.z.exception.UnknownZModelError;
import ds.z.exception.ZModelErrorException;
import gui.ZETMain;
import io.z.FloorConverter;
import io.z.XMLConverter;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A {@code Floor} is a plane that can contain {@link Room}-objects. It is
 * generally not allowed that the rooms (which are basically polygons) intersect
 * each other. In fact, it is possible to create such a not allowed state. It is
 * recommended to check whether a {@code Floor} is valid, or not before
 * using it for critical operations.
 * @see Floor#check( boolean )
 * @see ds.z.PlanPolygon
 */
@XStreamAlias( "floor" )
@XMLConverter( FloorConverter.class )
public class Floor implements Serializable, Cloneable, Iterable<Room> {
	/** The name of the floor. */
	@XStreamAsAttribute()
	private String name;
	/** A list of all rooms contained in the floor. */
	private ArrayList<Room> rooms;
	/** In the past this was intended to be a list of all single edges contained in the floor. 
	 * This concept was never used and now this field is only still here because every example file
	 * has this field. */
	// TODO Delete this field and adjust all example files
	private ArrayList<Edge> edges;

	/** The leftmost point coordinate of the {@code Floor}. */
	@XStreamAsAttribute ()
	private int xOffset = 0;
	/** The Room that has the minimum x value (xOffset). */
	private Room minX_DefiningRoom;
	/** The uppermost point coordinate of the {@code Floor}. */
	@XStreamAsAttribute ()
	private int yOffset = 0;
	/** The Room that has the minimum y value (yOffset). */
	private Room minY_DefiningRoom;
	/** The difference between the left- and rightmost point coordinate of the {@code Floor}. */
	@XStreamAsAttribute ()
	private int width = 0;
	/** The Room that has the maximum x value (xOffset + width). */
	private Room maxX_DefiningRoom;
	/** The difference between the upper- and lowermost point coordinate of the {@code Floor}. */
	@XStreamAsAttribute ()
	private int height = 0;
	/** The Room that has the maximum y value (yOffset + width). */
	private Room maxY_DefiningRoom;

	/**
	 * Creates a new empty instance of {@code Floor} with the name "NewFloor".
	 */
	public Floor() {
		this ("NewFloor");
	}

	/**
	 * Creates a new empty instance of {@code Floor} with the indicated name.
	 * @param name the name of the floor
	 */
	public Floor( String name ) {
		this.name = name;
		rooms = new ArrayList<Room>();
	}

	/**
	 * To be called before the floor is finally deleted.
	 */
	void delete () {
		while( !rooms.isEmpty() )
			rooms.get( 0 ).delete();
	}
	
	/**
	 * Adds a room to the floor and sets this floor as the associated floor of the room to be added.
	 * @param room the room to be added
	 * @throws IllegalArgumentException if the room already exists on this floor
	 */
	void addRoom( Room room ) throws IllegalArgumentException {
		try {
			if( rooms.contains( room ) )
				throw new IllegalArgumentException(ZLocalization.getSingleton ( ).getString ("ds.z.RoomAlreadyExistsException"));
			rooms.add( room );
		} catch( IllegalArgumentException ex ) {
			throw ex;
		}
	}

	/**
	 * Returns the number of rooms on the floor.
	 * @return the number of rooms on the floor
	 */
	public int roomCount() {
		return rooms.size();
	}

	/**
	 * Removes a room from this floor and deletes the associated floor of the removed room.
	 * @param the room to be removed
	 * @throws IllegalArgumentException if the given room is not associated with this floor
	 */
	public void deleteRoom( Room room ) throws IllegalArgumentException {
		if( !( rooms.contains( room ) ) )
			throw new IllegalArgumentException(ZLocalization.getSingleton ( ).getString ("ds.z.NoRoomException"));
		else {
			rooms.remove( room );
			roomDeleteHandler (room);
		}
	}

	/**
	 * Checks, whether a floor is a valid floor. It is, if all his associated rooms are valid and none of
	 * his rooms intersects.
	 * @throws PolygonNotClosedException if one of the rooms on this floor is not closed
	 * @throws AreaNotInsideException if one of the areas of one room lies not completely inside that room
	 * @throws RoomIntersectException if some rooms on this floor intersects
	 * @throws TeleportEdgeInvalidTargetException if the target of the teleport edge is on the same floor as the start
	 * @param rasterized Indicates, if the BuildingPlan should be rasterized.
	 */
	public void check( boolean rasterized ) throws PolygonNotClosedException, AreaNotInsideException, RoomIntersectException, TeleportEdgeInvalidTargetException {
		for( Room room : rooms ) {
			room.check( rasterized );
			// Check floors using direct access as rooms is an ArrayList
			for( int i = 0; i < rooms.size(); i++ )
				for( int j = i+1; j < rooms.size(); j++ )
					if( rooms.get(i).intersects( rooms.get(j) ) )
						throw new RoomIntersectException( rooms.get(i), rooms.get(j) );
		}
	}

	/**
	 * Tests, if this floor equals the given floor f. Two floors are equal, if
	 * they have the same name.
	 * @param p the floor to be tested for equality
	 * @return true if the two floors are equal
	 */
	public boolean equals( Floor p ) {
		return p.name.equals( name );
	}

	/**
	 * Tests, if this floor equals the given object. First assures that the object is an instance of the floor class.
	 * Returns false if the object is not a floor. Two floors are equal, if they have the same name.
	 * @param o the object that is to be tested for equality
	 * @return
	 */
	@Override
	public boolean equals( Object o ) {
		return o instanceof Floor ? this.equals( (Floor)o) : false;
	}

	/**
	 * Returns the height of the {@code Floor}. That is the difference between the
	 * uppermost and lowermost y-coordinates of contained rooms.
	 * @return the height
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * Returns the name of the floor.
	 * @return the name of the floor
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the width of the {@code Floor}. That is the difference between the
	 * leftmost and rightmost x-coordinates of contained rooms.
	 * @return the width
	 */
	public final int getWidth() {
		return width;
	}

	/**
	 * The leftmost point coordinate of the {@code Floor}.
	 * @return the leftmost coordinate
	 */
	public final int getxOffset() {
		return this.xOffset;
	}

	/**
	 * The uppermost point coordinate of the {@code Floor}.
	 * @return the uppermost coordinate
	 */
	public final int getyOffset() {
		return this.yOffset;
	}

	/**
	 * Returns a view of the list of rooms on this floor.
	 * @return a list of rooms
	 */
	public List<Room> getRooms() {
		return Collections.unmodifiableList( rooms );
	}

	/**
	 * Renames the floor.
	 * @param val the new name of the floor
	 */
	public void setName( String val ) {
		name = val;
//		throwChangeEvent( new ChangeEvent( this, null, "name" ) );
	}

	/**
	 * Returns the bounding box of this {@code Floor}. The bounding box
	 * is the smallest {@link java.awt.Rectangle} that completely contains the
	 * whole Floor. The calculation of this bounding box is accurate in the
	 * integer coordinates of millimeter positions.
	 * @return a rectangle that defines the bounds
	 */
	public Rectangle bounds () {
		return new Rectangle (xOffset, yOffset, width, height);
	}
	
	/**
	 * This helper method updates the values returned by {@link #bounds()} after
	 * a {@link ds.z.Room} has been added or modified.
	 *
	 * @see #roomDeleteHandler
	 */
	private void roomChangeHandler (Room r) {
		// Suppress temporary faults from new polygons (These have xoffset and
		// yoffset 0, and would pull the floor offsets to 0 while they are created
		// on the screen.)
		if (r.getNumberOfEdges () == 0) {
			if (rooms.size () == 1) {
				minX_DefiningRoom = r;
				minY_DefiningRoom = r;
				maxX_DefiningRoom = r;
				maxY_DefiningRoom = r;
			}
			return;
		}
		
		// Update of offsets, width and height

		// All updates follow a certain schema:
		// 1) Check for shrinked bounds: In case that one of the boundary rooms
		//    was changed, so that is is now no longer sure who is defining the
		//    boundary, scan all rooms for a new minimum / maximum
		// 2) Check for grown bounds: Simple comparison

		// The minimum values are updated first, because they are used in the
		// computations that are performed during the updates of the maximums

		// Updates of the minimums
		if (r == minX_DefiningRoom && r.boundLeft () > xOffset) {
			// Init with feasible data
			xOffset = rooms.get (0).boundLeft ();
			minX_DefiningRoom = rooms.get (0);

			// Scan all edges
			int value;
			for (Room scanEdge : rooms) {
				value = scanEdge.boundLeft ();

				if (value < xOffset) {
					xOffset = value;
					minX_DefiningRoom = scanEdge;
				}
			}
			width = maxX_DefiningRoom.boundRight () - xOffset;
		} else if (r.boundLeft () <= xOffset) {
			// New xOffset more left then the old one
			width += Math.abs (r.boundLeft () - xOffset);
			xOffset = r.boundLeft ();

			minX_DefiningRoom = (Room) r;
		}
		if (r == minY_DefiningRoom && r.boundUpper () > yOffset) {
			// Init with feasible data
			yOffset = rooms.get (0).boundUpper ();
			minY_DefiningRoom = rooms.get (0);

			// Scan all edges
			int value;
			for (Room scanEdge : rooms) {
				value = scanEdge.boundUpper ();

				if (value < yOffset) {
					yOffset = value;
					minY_DefiningRoom = scanEdge;
				}
			}
			height = maxY_DefiningRoom.boundLower () - yOffset;
		} else if (r.boundUpper () <= yOffset) {
			// New yOffset is over the old one, correct also the height
			height += Math.abs (r.boundUpper () - yOffset);
			yOffset = r.boundUpper ();

			minY_DefiningRoom = (Room) r;
		}

		// Update of the maximums
		if (r == maxX_DefiningRoom && r.boundRight () < (xOffset + width)) {
			// Init with feasible data
			// No Math.abs needed - r.boundRight is always bigger than xOffset
			width = rooms.get (0).boundRight () - xOffset;
			maxX_DefiningRoom = rooms.get (0);

			// Scan all edges
			int value;
			for (Room scanEdge : rooms) {
				value = scanEdge.boundRight () - xOffset;

				if (value > width) {
					width = value;
					maxX_DefiningRoom = scanEdge;
				}
			}
		} else if (r.boundRight () >= xOffset + width) {
			// No Math.abs needed - r.boundRight is always bigger than xOffset
			width = r.boundRight () - xOffset;

			maxX_DefiningRoom = (Room) r;
		}
		if (r == maxY_DefiningRoom && r.boundLower () < (yOffset + height)) {
			// Init with feasible data
			// No Math.abs needed - r.boundLower is always bigger than yOffset
			height = rooms.get (0).boundLower () - yOffset;
			maxY_DefiningRoom = rooms.get (0);

			// Scan all edges
			int value;
			for (Room scanEdge : rooms) {
				value = scanEdge.boundLower () - yOffset;

				if (value > height) {
					height = value;
					maxY_DefiningRoom = scanEdge;
				}
			}
		} else if (r.boundLower () >= yOffset + height) {
			// No Math.abs needed - r.boundLower is always bigger than yOffset
			height = r.boundLower () - yOffset;

			maxY_DefiningRoom = (Room) r;
		}
	}

	/**
	 * This helper method updates the values returned by {@link #bounds()} after
	 * an {@link ds.z.Room} has been deleted.
	 *
	 * @param r An Room that was deleted.
	 *
	 * @see #roomChangeHandler
	 */
	private void roomDeleteHandler (Room r) {
		// Update of offsets, width and height

		// The only thing that can happen due to the deletion of a room is that
		// bounds may shrink. We must test if the deleted room defined any bound
		// and replace it if that is true.

		// The minimum values are updates first, because they are used in the
		// computations that are performed during the updates of the maximums

		// Updates of the minimums
		if (r == minX_DefiningRoom) {
			// Init with feasible data
			minX_DefiningRoom = rooms.isEmpty () ? null : rooms.get (0);
			if (minX_DefiningRoom != null) {
				xOffset = minX_DefiningRoom.boundLeft ();

				// Scan all edges
				int value;
				for (Room scanEdge : rooms) {
					value = scanEdge.boundLeft ();

					if (value < xOffset) {
						xOffset = value;
						minX_DefiningRoom = scanEdge;
					}
				}
			}
		}
		if (r == minY_DefiningRoom) {
			// Init with feasible data
			minY_DefiningRoom = rooms.isEmpty () ? null : rooms.get (0);
			if (minY_DefiningRoom != null) {
				yOffset = minY_DefiningRoom.boundUpper ();

				// Scan all edges
				int value;
				for (Room scanEdge : rooms) {
					value = scanEdge.boundUpper ();

					if (value < yOffset) {
						yOffset = value;
						minY_DefiningRoom = scanEdge;
					}
				}
			}
		}

		// Update of the maximums
		if (r == maxX_DefiningRoom) {
			// Init with feasible data
			maxX_DefiningRoom = rooms.isEmpty () ? null : rooms.get (0);
			if (maxX_DefiningRoom != null) {
				// No Math.abs needed - r.boundRight is always bigger than xOffset
				width = maxX_DefiningRoom.boundRight () - xOffset;

				// Scan all edges
				int value;
				for (Room scanEdge : rooms) {
					value = scanEdge.boundRight () - xOffset;

					if (value > width) {
						width = value;
						maxX_DefiningRoom = scanEdge;
					}
				}
			}
		}
		if (r == maxY_DefiningRoom) {
			// Init with feasible data
			maxY_DefiningRoom = rooms.isEmpty () ? null : rooms.get (0);
			if (maxY_DefiningRoom != null) {
				// No Math.abs needed - r.boundLower is always bigger than yOffset
				height = maxY_DefiningRoom.boundLower () - yOffset;

				// Scan all edges
				int value;
				for (Room scanEdge : rooms) {
					value = scanEdge.boundLower () - yOffset;

					if (value > height) {
						height = value;
						maxY_DefiningRoom = scanEdge;
					}
				}
			}
		}
	}
	
	/** Recomputes all bounds from scratch.
	 * 
	 * This method is not intended to be used by any other class except 
	 * for io.z.FloorConverter for legacy support of old example files.
	 *
	 * @param holdLastSize does not decrease the size of the floor, if the new bounds are smaller, than the old ones, if set to {@code true}
	 */
	public void recomputeBounds ( boolean holdLastSize ) {
		minX_DefiningRoom = null;
		minY_DefiningRoom = null;
		maxX_DefiningRoom = null;
		maxY_DefiningRoom = null;
		
		int minX = 0;
		int minY = 0;
		int maxX = 0;
		int maxY = 0;
		
		for (Room r : rooms) {
			if (r.boundLeft () < minX) {
				minX = r.boundLeft ();
				minX_DefiningRoom = r;
			}
			if (r.boundUpper () < minY) {
				minY = r.boundUpper ();
				minY_DefiningRoom = r;
			}
			if (r.boundRight () > maxX) {
				maxX = r.boundRight ();
				maxX_DefiningRoom = r;
			}
			if (r.boundLower () > maxY) {
				maxY = r.boundLower ();
				maxY_DefiningRoom = r;
			}
		}
		
		xOffset = holdLastSize ? Math.min( minX, xOffset ) : minX;
		yOffset = holdLastSize ? Math.min( minY, yOffset ) : minY;
		width = holdLastSize ? Math.max( maxX - minX, width ) + Math.max( minX - xOffset , 0 ): maxX - minX;
		height = holdLastSize ? Math.max( maxY - minY, height ) + Math.max( minY - yOffset, 0 ) : maxY - minY;
	}

	/**
	 * Sets a minimum size that this floor should have.
	 * @param xOffset the x-coordinate of the left upper corner
	 * @param yOffset the y-coordinate of the left upper corner
	 * @param width the width of the usable area
	 * @param height the height of the usable area
	 */
	public void setMinimumSize( int xOffset, int yOffset, int width, int height ) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;
		recomputeBounds( true );
	}
	
	/**
	 * Indicates whether this is a {@code Floor} that was loaded from a legacy file.
	 * @return {@code true} if the squared bounding box is known, {@code false} otherwise
	 */
	public boolean boundStructureAvailable () {
		return minX_DefiningRoom != null;
	}

	/**
	 * Returns a copy of the {@code Floor}, but deletes all {@link AssignmentArea} objects
	 * as they would most likely refer to assignments that do not exist.
	 * @throws UnknownZModelError if an unexpected error occurred. This usually means that something in the model is incorrect.
	 * @return a copy of the floor
	 */
	@Override
	public Floor clone() throws UnknownZModelError {
		Floor deepCopy = new Floor( this.name );
		try {
			HashMap<Room,Room> m = new HashMap<Room,Room>();

			for( Room r : getRooms() ) {
				Room newRoom = new Room( deepCopy, r.getName() );
				System.out.println( "Copying room " + r.getName() );
				newRoom.defineByPoints( PlanPoint.pointCopy( r.getBorderPlanPoints() ) );

				// Reconnect the rooms
				m.put( r, newRoom );
				for( RoomEdge e : r ) {
					if( e.isPassable() ) {
						Room connectedRoom = e.getLinkTarget().getRoom();
						if( m.containsKey( connectedRoom ) ) {
							Room connectToRoom = m.get( connectedRoom );
							newRoom.connectTo( connectToRoom, e );
						}
					}
				}

				// Recreate the areas: Inaccessible, Save, Evacuation, Delay, Stair
				// _NOT_ AssignmentArea
				for( Barrier t : r.getBarriers() ) {
					Barrier b = new Barrier( newRoom );
					b.defineByPoints( PlanPoint.pointCopy( t.getPlanPoints() ) );
				}
				for( DelayArea t : r.getDelayAreas() ) {
					DelayArea d = new DelayArea( newRoom, t.getDelayType(), t.getSpeedFactor() );
					d.defineByPoints( PlanPoint.pointCopy ( t.getPlanPoints() ) );
				}
				for( InaccessibleArea t : r.getInaccessibleAreas() ) {
					InaccessibleArea i = new InaccessibleArea( newRoom );
					i.defineByPoints( PlanPoint.pointCopy ( t.getPlanPoints() ) );
				}
				for( SaveArea t : r.getSaveAreas() ) { // Evacuation areas are contained!
					if( !(t instanceof EvacuationArea) ) {
						SaveArea s = new SaveArea( newRoom );
						s.defineByPoints( PlanPoint.pointCopy( t.getPlanPoints() ) );
					}
				}
				for( TeleportArea t : r.getTeleportAreas() ) {

				}
				for( EvacuationArea t : r.getEvacuationAreas() ) {
					EvacuationArea e = new EvacuationArea( newRoom, t.getAttractivity(), t.getName() );
					e.defineByPoints( PlanPoint.pointCopy( t.getPlanPoints() ) );
				}
				for( StairArea t : r.getStairAreas() ) {
					StairArea s = new StairArea( newRoom );
					List<PlanPoint> points = PlanPoint.pointCopy( t.getPlanPoints() );
					s.defineByPoints( points );

					PlanPoint lowerStart = null;
					PlanPoint lowerEnd = null;
					PlanPoint upperStart = null;
					PlanPoint upperEnd = null;
					for( PlanPoint p : s.getPlanPoints() ) {
						if( p.x == t.getLowerLevelStart().x && p.y == t.getLowerLevelStart().y )
							lowerStart = p;
						if( p.x == t.getLowerLevelEnd().x && p.y == t.getLowerLevelEnd().y )
							lowerEnd = p;
						if( p.x == t.getUpperLevelStart().x && p.y == t.getUpperLevelStart().y )
							upperStart = p;
						if( p.x == t.getUpperLevelEnd().x && p.y == t.getUpperLevelEnd().y )
							upperEnd = p;
					}
					s.setLowerLevel( lowerStart, lowerEnd );
					s.setUpperLevel( upperStart, upperEnd );
					s.setSpeedFactorDown( t.getSpeedFactorDown() );
					s.setSpeedFactorUp( t.getSpeedFactorUp() );
				}
			}
		} catch ( Exception ex ) {
			ZETMain.sendError( ex.getMessage() );
			throw new UnknownZModelError( "Unexpected error during copying. Try to check the model.", ex );
		}
		return deepCopy;
	}

	/**
	 * Returns an iterator over all rooms contained in this floor.
	 * @return an iterator over all rooms contained in this floor
	 */
	@Override
	public Iterator<Room> iterator() {
		return this.getRooms().iterator();
	}

	/**
	 * Returns the name of the floor as string representation.
	 * @return the name of the floor as string representation
	 */
	@Override
	public String toString() {
		return this.getName();
	}

	StringBuilder summaryBuilder() {
		StringBuilder sb = new StringBuilder();

		sb.append( "Floor: " + name + ", " + rooms.size() + " Räume.\n" );
		for( Room room : rooms ) {
			sb.append( room.summaryBuilder() );
			sb.append( '\n' );
		}

		return sb;
	}

	public String summary() {
		return summaryBuilder().toString();
	}
}
