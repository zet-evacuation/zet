/*
 * Room.java
 * Created on 26. November 2007, 21:32
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import ds.z.event.ChangeEvent;
import ds.z.exception.AreaNotInsideException;
import ds.z.exception.PolygonNotClosedException;
import ds.z.exception.RoomEdgeInvalidTargetException;
import ds.z.exception.TeleportEdgeInvalidTargetException;
import ds.z.exception.TeleportEdgeNotConnected;
import ds.z.exception.TeleportEdgeTargetLengthException;
import ds.z.exception.TooManyPeopleException;
import io.z.RoomConverter;
import io.z.XMLConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import localization.Localization;

/**
 * Represents a room in a {@link BuildingPlan}. Generally a room is nothing else than
 * a closed polygon. The difference is, that the bounding edges need to be of the type
 * {@link RoomEdge}. These edges can be connected to two rooms. This allows modelling
 * passages between two rooms.
 * @author Joscha Kulbatzki, Jan-Philipp Kappmeier, Timon Kelter
 */
@XStreamAlias( "room" )
@XMLConverter( RoomConverter.class )
public class Room extends BaseRoom<RoomEdge> {
//public class Room<T extends RoomEdge> extends PlanPolygon<T> {
	@XStreamAsAttribute()
	/** The name of the <code>Room</code>. */
	private String name;
	/** The floor containing the <code>Room</code>. */
	private Floor associatedFloor;
	/** A list of all assignment areas of the current assignment in the room. */
	private ArrayList<AssignmentArea> assignmentAreas;
	/** A list of all barriers of the current room. */
	private ArrayList<Barrier> barriers;
	/** A list of all delay areas of the current room*/
	private ArrayList<DelayArea> delayAreas;
	/** A list of all evacuation areas of the current room. */
	private ArrayList<EvacuationArea> evacuationAreas;
	/** A list of all inaccessible areas of the current room, excluding barriers. */
	private ArrayList<InaccessibleArea> inaccessibleAreas;
	/** A list of all save areas of the current room, including evacuation areas. */
	private ArrayList<SaveArea> saveAreas;
	/** A list of all stair areas of the current room. */
	private ArrayList<StairArea> stairAreas;

	/**
	 * Creates a new <code>Room</code> with a default name "Room x", where x
	 * is the number of the new room.
	 * @param floor the floor containing the room
	 */
	public Room( Floor floor ) {
		super( RoomEdge.class );
		name = "Room " + Integer.toString( floor.getRooms().size() );
		associatedFloor = floor;
		assignmentAreas = new ArrayList<AssignmentArea>();
		barriers = new ArrayList<Barrier>();
		delayAreas = new ArrayList<DelayArea>();
		evacuationAreas = new ArrayList<EvacuationArea>();
		inaccessibleAreas = new ArrayList<InaccessibleArea>();
		saveAreas = new ArrayList<SaveArea>();
		stairAreas = new ArrayList<StairArea>();
		floor.addRoom( this );
	}

	/**
	 * Creates a new <code>Room</code> with a specified name and adds it to a {@link Floor}.
	 * @param floor the floor containing the room
	 * @param name the name of the room
	 */
	public Room( Floor floor, String name ) {
		super( RoomEdge.class );
		this.name = name;
		associatedFloor = floor;
		assignmentAreas = new ArrayList<AssignmentArea>();
		barriers = new ArrayList<Barrier>();
		delayAreas = new ArrayList<DelayArea>();
		evacuationAreas = new ArrayList<EvacuationArea>();
		inaccessibleAreas = new ArrayList<InaccessibleArea>();
		saveAreas = new ArrayList<SaveArea>();
		stairAreas = new ArrayList<StairArea>();
		floor.addRoom( this );
	}

	/** {@inheritDoc}
	 * 
	 * The Areas that are contained within this Room are assigned to the new room that contains them.
	 * If any area intersects with both rooms, then the area will not be contained in any of the rooms.
	 * 
	 * @throws IllegalArgumentException 
	 */
	//TODO: Split up areas that intersect with both rooms
	@Override
	public PlanPolygon<RoomEdge> splitClosedPolygon( RoomEdge edge1, RoomEdge edge2 ) throws IllegalArgumentException {
		Room newPolygon = (Room) super.splitClosedPolygon( edge1, edge2 );

		for( Area a : getAreas() ) {
			if( newPolygon.contains( a ) ) {
				a.setAssociatedRoom( newPolygon );
			}
		// All other areas stay registered at "this"
		}

		return newPolygon;
	}

	/** This method copies the current polygon without it's edges. Every other setting, as f.e. the floor
	 * for Rooms or the associated Room for Areas is kept as in the original polygon. */
	@Override
	protected PlanPolygon<RoomEdge> createPlainCopy() {
		return new Room( getAssociatedFloor(), getName() );
	}

	/** Just as super.toString() but includes the rooms name. */
	@Override
	public String toString() {
		return name + ": " + super.toString();
	}

	/**
	 * Tests, if this room equals the given room r. Two rooms are equal, if they
	 * have the same shape, name and associated floors.
	 * @param r the room that is to to be tested for equality
	 * @return true if the two rooms are equal
	 */
	public boolean equals( Room r ) {
		return super.equals( r ) && r.getName().equals( name ) &&
						((r.getAssociatedFloor() != null) ? r.getAssociatedFloor().equals( associatedFloor ) : associatedFloor == null);
	}

	/**
	 * Tests, if this room equals the given room r. First assures that the object is an instance of the room class.
	 * Returns false if the object is not a room.
	 * Two rooms are equal, if they have the same name and associated floors.
	 * @param o the object that is to be tested for equality
	 * @return true if the two rooms are equal
	 */
	@Override
	public boolean equals( Object o ) {
		if( o instanceof Room ) {
			return this.equals( (Room) o );
		} else {
			return false;
		}
	}

	/** {@inheritDoc}
	 *
	 * The Room implementation also rasterizes all Areas within this Room.
	 */
	@Override
	public void rasterize() {
		super.rasterize();
		for( Area a : assignmentAreas ) {
			a.rasterize();
		}
		for( Area a : barriers ) {
			a.rasterize();
		}
		for( Area a : delayAreas ) {
			a.rasterize();
		}
		for( Area a : evacuationAreas ) {
			a.rasterize();
		}
		for( Area a : inaccessibleAreas ) {
			a.rasterize();
		}
		for( Area a : saveAreas ) {
			a.rasterize();
		}
		for( Area a : stairAreas ) {
			a.rasterize();
		}
	}

	/**
	 * Returns the name of the room.
	 * @return the name of the room
	 */
	public String getName() {
		return name;
	}

	/**
	 * Renames the room.
	 * @param val the new room name
	 */
	public void setName( String val ) {
		name = val;
		throwChangeEvent( new ChangeEvent( this ) );
	}

	/**
	 * Adds a new area to the applicable area list.
	 * @param area the area to be added
	 * @throws IllegalArguementException if the area is already contained in the associated area list
	 */
	void addArea( Area area ) throws IllegalArgumentException {
		if( area instanceof AssignmentArea ) {
			if( assignmentAreas.contains( area ) ) {
				throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.AlreadyContainsAreaException" ) );
			} else {
				assignmentAreas.add( (AssignmentArea) area );
			}
		}
		if( area instanceof DelayArea ) {
			if( delayAreas.contains( area ) ) {
				throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.AlreadyContainsAreaException" ) );
			} else {
				delayAreas.add( (DelayArea) area );
			}
		}
		if( area instanceof Barrier ) {
			if( barriers.contains( area ) ) {
				throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.AlreadyContainsAreaException" ) );
			} else {
				barriers.add( (Barrier) area );
			}
		// Check also for evacuation barriers
		} else if( area instanceof InaccessibleArea ) {
			if( inaccessibleAreas.contains( area ) ) {
				throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.AlreadyContainsAreaException" ) );
			}
			// is _not_ contained because if it would be in inaccessibleAreas and the exception is already thrown
			inaccessibleAreas.add( (InaccessibleArea) area );
		}
		if( area instanceof SaveArea ) {
			if( saveAreas.contains( area ) ) {
				throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.AlreadyContainsAreaException" ) );
			} else {
				saveAreas.add( (SaveArea) area );
			}
			// Check also for evacuation area
			if( area instanceof EvacuationArea ) // is _not_ contained because if it would be in saveAreas and the exception is already thrown
			{
				evacuationAreas.add( (EvacuationArea) area );
			}
		}
		if( area instanceof StairArea ) {
			if( stairAreas.contains( area ) ) {
				throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.AlreadyContainsAreaException" ) );
			} else {
				stairAreas.add( (StairArea) area );
			}
		}

		area.addChangeListener( this );
		throwChangeEvent( new ChangeEvent( this ) );
	}

	/**
	 * Checks, whether this is a valid room. This is true, if all his edges define a closed area
	 * and if all his associated areas lie inside the room and if this room contains not too many people.
	 * @throws PolygonNotClosedException if the room is not closed
	 * @throws AreaNotInsideException if one of the areas of this room lies not completely inside that room
	 * @throws TeleportEdgeInvalidTargetException if the linkTarget of the teleport edge is on the same floor as the start
	 * @throws ds.z.exception.TooManyPeopleException If this room contains too many people.
	 * @param rasterized Indicates, if the BuildingPlan should be rasterized.
	 */
	@Override
	public void check( boolean rasterized ) throws PolygonNotClosedException, AreaNotInsideException,
					TeleportEdgeInvalidTargetException, TooManyPeopleException {
		super.check( rasterized );
		//if (!(isClosed())) throw new PolygonNotClosedException( this, "This room is not a closed polygon"); // done by super.check()
		for( RoomEdge e : getEdges() ) {
			if( e instanceof TeleportEdge ) {
				TeleportEdge e2 = ((TeleportEdge) e).getLinkTarget();
				if( e2.getLinkTarget() == null ) {
					throw new TeleportEdgeNotConnected( e2, Localization.getInstance().getString( "ds.z.TeleportEdge.LinkTargetNotSet" ) );
				} else if( e2.getLinkTarget().getLinkTarget() != e2 ) {
					throw new TeleportEdgeNotConnected( e2, Localization.getInstance().getString( "ds.z.TeleportEdge.InconsistentLinkage" ) );
				}

				Room r = (Room) e2.getAssociatedPolygon();
				if( r.getAssociatedFloor() == associatedFloor ) {
					throw new TeleportEdgeInvalidTargetException( e2,
									Localization.getInstance().getString( "ds.z.TeleportEdge.SameFloorException" ) );
				}
				if( e.length() != e2.length() ) {
					throw new TeleportEdgeTargetLengthException( e2,
									Localization.getInstance().getString( "ds.z.TeleportEdge.LengthNotMatchException" ) + "(" + e2 + ", " + e + ")" );
				}
			} else {
				if( e.isPassable() && e.getLinkTarget().getRoom().getAssociatedFloor() != associatedFloor ) {
					throw new RoomEdgeInvalidTargetException( e,
									Localization.getInstance().getString( "ds.z.Room.DifferentFloorException" ) );
				}
			}
		}
		for( AssignmentArea aa : assignmentAreas ) {
			aa.check( rasterized );
			if( !(contains( aa )) ) {
				throw new AreaNotInsideException( aa, Localization.getInstance().getString( "ds.z.Room.NotCompletelyInException" ) );
			}
		}
		for( Barrier b : barriers ) {
			b.check( rasterized );
			if( !(contains( b )) ) {
				throw new AreaNotInsideException( b, Localization.getInstance().getString( "ds.z.Room.NotCompletelyInException" ) );
			}
		}
		for( DelayArea da : delayAreas ) {
			da.check( rasterized );
			if( !(contains( da )) ) {
				throw new AreaNotInsideException( da, Localization.getInstance().getString( "ds.z.Room.NotCompletelyInException" ) );
			}
		}
		for( InaccessibleArea ia : inaccessibleAreas ) { // including barriers
			ia.check( rasterized );
			if( !(contains( ia )) ) {
				throw new AreaNotInsideException( ia, Localization.getInstance().getString( "ds.z.Room.NotCompletelyInException" ) );
			}
		}
		for( SaveArea sa : saveAreas ) { // Including evacuation areas
			sa.check( rasterized );
			if( !(contains( sa )) ) {
				throw new AreaNotInsideException( sa, Localization.getInstance().getString( "ds.z.Room.NotCompletelyInException" ) );
			}
		}
		for( StairArea sa : stairAreas ) {
			sa.check( rasterized );
			if( !(contains( sa )) ) {
				throw new AreaNotInsideException( sa, Localization.getInstance().getString( "ds.z.Room.NotCompletelyInException" ) );
			}
		}
		checkTooManyPersonsInRoom();
	}

	/**
	 * Checks, if this room contains too many people.
	 * @throws ds.z.exception.TooManyPeopleException If this room contains too many people.
	 */
	private void checkTooManyPersonsInRoom() throws TooManyPeopleException {
		int persons = 0;
		for( AssignmentArea aa : assignmentAreas ) {
			persons += aa.getEvacuees();
		}
		// Avoid Division by Zero here!
		if( persons != 0 && (area() / persons) < Assignment.spacePerPerson ) {
			throw new TooManyPeopleException( this, Localization.getInstance().getString( "ds.z.Room.OverfullRoomException" ) );
		}
	}

	/** {@inheritDoc}
	 * 
	 *  In case of a Room, this method has to consider passable edges as a special case:
	 *  If you replace a passable edge, the linkTarget edge, to which this edge is linked, will
	 *  also be replaced by the same list of points, and then these two new polygon parts
	 *  will be linked together so that the whole replacement part is passable after this
	 *  method finishes.
	 */
	@Override
	public ArrayList<RoomEdge> replaceEdge( RoomEdge e, List<PlanPoint> p ) {
		if( e.isPassable() ) {
			RoomEdge partner = e.getLinkTarget();
			PlanPoint partnerSource = partner.getSource();
			PlanPoint partnerTarget = partner.getTarget();
			PlanPoint eSource = e.getSource();
			PlanPoint eTarget = e.getTarget();

			if( e instanceof TeleportEdge ) {
				((TeleportEdge) e).setRevertLinkTargetOnDelete( false );
			}
			try {
				// Possibly the partner edge is specified in the reversed direction
				boolean reversedPartnerEdge = eSource.equals( partnerTarget ) &&
								eTarget.equals( partnerSource );
				RoomEdge predecessorMe = (RoomEdge) eSource.getPreviousEdge();
				RoomEdge predecessorPartner = (RoomEdge) (reversedPartnerEdge ? partnerTarget.getNextEdge() : partnerSource.getPreviousEdge());
				PlanPoint old_end_point = eTarget;

				// Do the real replacement in this polygon
				ArrayList<RoomEdge> result = this.replaceEdgeInternal( e, p );
				// Do the real replacement in the partner room.
				if( e instanceof TeleportEdge ) {
					// Move the parameter points so that they are on the link-target edge. This is only neccessary 
					// for TeleportEdges because normal passable edges always are equal to their link target.
					int xOffset = partnerSource.x - eSource.x;
					int yOffset = partnerSource.y - eSource.y;
					List<PlanPoint> new_p = new LinkedList<PlanPoint>();
					for( PlanPoint point : p ) {
						new_p.add( new PlanPoint( point.x + xOffset, point.y + yOffset ) );
					}
					p = new_p;
				}
				partner.getRoom().replaceEdgeInternal( partner, p );

				// Connect replacement edges
				RoomEdge edgeMe = predecessorMe;
				RoomEdge edgePartner = predecessorPartner;
				do {
					edgeMe = (RoomEdge) edgeMe.getTarget().getNextEdge();
					edgePartner = (RoomEdge) (reversedPartnerEdge ? edgePartner.getSource().getPreviousEdge() : edgePartner.getTarget().getNextEdge());

					assert (edgeMe.equals( edgePartner ));

					edgeMe.setLinkTarget( edgePartner );
					edgePartner.setLinkTarget( edgeMe );
				} while( !edgeMe.getTarget().equals( old_end_point ) );

				return result;
			} finally {
				if( e instanceof TeleportEdge ) {
					((TeleportEdge) e).setRevertLinkTargetOnDelete( true );
				}
			}
		} else {
			return super.replaceEdge( e, p );
		}
	}

	/** Just calls super.replaceEdge (). */
	private ArrayList<RoomEdge> replaceEdgeInternal( RoomEdge e, List<PlanPoint> p ) {
		return super.replaceEdge( e, p );
	}

	/** {@inheritDoc}
	 *  
	 * Just as the replaceEdge method for Rooms, this method must mind the case that two edges
	 * are combined where at least one of them is passable. There are the following cases:
	 * <table>
	 * <tr><td>One of them is passable </td><td>Then the previous linkTarget will be the linkTarget of the
	 * resulting edge and will be relocated to fit the new edge.</td></tr>
	 * <tr><td>Both are passable</td><td>If they lead to two linkTarget edges who are neighbours themselves
	 * these two linkTarget edges will also be combined and the be the new linkTarget. In every other case an
	 * IllegalArgumentException is thrown.</td></tr>
	 * </table>
	 * 
	 * @exception IllegalArgumentException See superclass + If two edges are combined who have targets
	 * which are not neighbours (only possible for TeleportEdges) or if two RoomEdges shall be combined
	 * who point to different rooms with their link targets.
	 */
	@Override
	public RoomEdge combineEdges( RoomEdge e1, RoomEdge e2, boolean keepMinSize ) throws IllegalArgumentException,
					IllegalStateException {

		RoomEdge result = null;
		if( e1.isPassable() || e2.isPassable() ) {
			if( e1.isPassable() && e2.isPassable() ) {
				// Both passable

				// TeleportEdges will be treated almost as any other passable edge with the difference, that
				// we have to check some cases first in which the combination of the edges is illegal
				boolean teleportEdges = (e1 instanceof TeleportEdge) && (e2 instanceof TeleportEdge);
				if( teleportEdges ) {
					// Both are TeleportEdges
					if( !e1.isNeighbour( e2 ) ) {
						throw new IllegalArgumentException( Localization.getInstance().getString(
										"ds.z.Room.CombineTeleportEdgesNonincidentTargets" ) );
					}
				} else if( (e1 instanceof TeleportEdge) || (e2 instanceof TeleportEdge) ) {
					// One of them is a teleport edge
					throw new IllegalArgumentException( Localization.getInstance().getString(
									"ds.z.Room.CombineTeleportEdgeWithNonTeleportEdge" ) );
				}

				// Get partners now because e1 & e2 will be modified later
				RoomEdge e1_partner = e1.getLinkTarget();
				RoomEdge e2_partner = e2.getLinkTarget();

				if( !e1.getLinkTarget().getRoom().equals( e2.getLinkTarget().getRoom() ) ) {
					// Combine passables to different rooms - This is normally not allowed
					if( !teleportEdges && e1.equals( e2 ) ) {
						// Special case : Using normal edges which cover each other
						// We combine a doorway that was reduced to size zero. We have to connect
						// the two partner edges in the two rooms and to delete e1/e2 by calling
						// super.combineEdges (e1, e2)
						result = combineEdgesInternal( e1, e2, keepMinSize );
						e1_partner.setLinkTarget( e2_partner );
						e2_partner.setLinkTarget( e1_partner );
					} else {
						// Normal behaviour: Edges point to different rooms --> Exception
						throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.CombinePassablesToDifferentRooms" ) );
					}
				} else {
					// Combine passables to a single link-target room
					Room target = e1.getLinkTarget().getRoom();
					// Stop here if the operation will fail because of the target size
					if( keepMinSize && target.getNumberOfEdges() - 1 < 3 ) {
						throw new IllegalStateException( Localization.getInstance().getString( "ds.z.Room.TargetNotEnoughEdgesException" ) );
					}

					result = combineEdgesInternal( e1, e2, keepMinSize );
					RoomEdge newTarget = target.combineEdgesInternal( e1_partner, e2_partner, keepMinSize );

					if( result != null ) {
						// Reconnect the new edges (only if the edges are not
						// combined to null -> "zipfel/thorn edges")
						result.setLinkTarget( newTarget );
						newTarget.setLinkTarget( result );
					}
				}
			} else {
				// One of them is passable
				RoomEdge oldTarget = e1.isPassable() ? e1.getLinkTarget() : e2.getLinkTarget();
				PlanPoint common = e1.commonPoint( e2 );
				PlanPoint oldTargetEndpoint = oldTarget.getSource().equals( common ) ? oldTarget.getSource() : oldTarget.getTarget();
				PlanPoint newTargetEndpoint = (e1.isPassable()) ? e2.getOther( common ) : e1.getOther( common );

				result = combineEdgesInternal( e1, e2, keepMinSize );

				if( result != null ) {
					// The combined edges are made passable and the old target edge is moved accordingly
					if( oldTarget instanceof TeleportEdge ) {
						// TODO: Apply an offset to the target point - Teleport Edge
					} else {
						// Directly set the new location
						oldTargetEndpoint.setLocation( newTargetEndpoint );
					}

					result.setLinkTarget( oldTarget );
					oldTarget.setLinkTarget( result );
				} else {
					// The combined edges were deleted, so inform the oldTarget about this
					oldTarget.setLinkTarget( null );
				}
			}

			return result;
		} else {
			return super.combineEdges( e1, e2, keepMinSize );
		}
	}

	/** Just calls super.combineEdges (). */
	private RoomEdge combineEdgesInternal( RoomEdge e1, RoomEdge e2, boolean keepMinSize ) {
		return super.combineEdges( e1, e2, keepMinSize );
	}

	/**
	 * Connects this room with another room at a specified {@link RoomEdge}. 
	 * This edge must be contained in both rooms. That means, the
	 * underlying polygons must intersect themselves at this edge.
	 * <p>The <code>RoomEdge</code> is associated to both rooms and is set as passable.
	 * This creates a "door" between the two rooms.</p>
	 * @param r the other room
	 * @param e the edge
	 */
	public void connectTo( Room r, RoomEdge e ) {
		// Connects this room to a specified room at the edge e)
		RoomEdge e1 = getEdge( e );
		RoomEdge e2 = r.getEdge( e );
		if( e1.isPassable() || e2.isPassable() ) {
			throw new IllegalStateException( Localization.getInstance().getString( "ds.z.Room.NoMoreConnectionsPossibleException" ) );
		}
		e1.setLinkTarget( e2 );
		e2.setLinkTarget( e1 );
	}

	/**
	 * Connects this room with another room at an {@link RoomEdge}. The edge is specified by its
	 * two end points. This edge must be contained in both rooms. That means, the
	 * underlying polygons must intersect themselves at this edge.
	 * <p>The <code>RoomEdge</code> is associated to both rooms and is set as passable.
	 * This creates a "door" between the two rooms.</p>
	 * @param r the other room
	 * @param p1 one end point of the connecting edge
	 * @param p2 the other end point of the connecting edge
	 */
	public void connectTo( Room r, PlanPoint p1, PlanPoint p2 ) {
		RoomEdge e1 = getEdge( p1, p2 );
		RoomEdge e2 = r.getEdge( p1, p2 );
		if( e1.isPassable() || e2.isPassable() ) {
			throw new java.lang.IllegalStateException( Localization.getInstance().getString( "ds.z.Room.NoMoreConnectionsPossibleException" ) );
		}
		e1.setLinkTarget( e2 );
		e2.setLinkTarget( e1 );
	}

	/** This method replaces sourceEdge and targetEdge with two TeleportEdges.
	 * The old edges are deleted and the new TeleportEdges are connected to each 
	 * other. This method is only guaranteed to work on closed polygons.
	 *
	 * @param sourceEdge An edge within a room
	 * @param targetEdge An edge within another room on a different floor
	 * @exception IllegalArgumentException If both edges are in the same room or 
	 * the same floor or if one of the room edges is already passable. If the 
	 * polygon is not closed, an IllegalArgumentException may be thrown because 
	 * the deletion of a RoomEdge (source/linkTarget) may fail.
	 */
	public static void connectToWithTeleportEdge( RoomEdge sourceEdge,
					RoomEdge targetEdge ) throws IllegalArgumentException {
		if( sourceEdge.isPassable() || targetEdge.isPassable() ) {
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.PassableException" ) );
		}
		if( targetEdge.getRoom() == sourceEdge.getRoom() ) {
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.NoDifferentRoomsException" ) );
		}
		if( targetEdge.getRoom().getAssociatedFloor() ==
						sourceEdge.getRoom().getAssociatedFloor() ) {
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.NoDifferentFloorsException" ) );
		}

		Room s = sourceEdge.getRoom();
		Room t = targetEdge.getRoom();
		PlanPoint s_p1 = sourceEdge.getSource();
		PlanPoint s_p2 = sourceEdge.getTarget();
		PlanPoint t_p1 = targetEdge.getSource();
		PlanPoint t_p2 = targetEdge.getTarget();

		sourceEdge.delete();
		targetEdge.delete();

		TeleportEdge t1 = new TeleportEdge( s_p1, s_p2, s );
		TeleportEdge t2 = new TeleportEdge( t_p1, t_p2, t );
		t1.setLinkTarget( t2 );
		t2.setLinkTarget( t1 );
	}

	/**
	 * Removes the specified area from the applicable area list.
	 * @param area the area to be removed
	 * @throws java.lang.IllegalArgumentException if the area was not contained in
	 * the room.
	 */
	void deleteArea( Area area ) throws IllegalArgumentException {
		boolean result = true;
		if( area instanceof AssignmentArea ) {
			result = assignmentAreas.remove( (AssignmentArea) area );
		}
		if( area instanceof Barrier ) {	// needs to be before inaccessible area, do not delete twice.
			result = barriers.remove( (Barrier) area );						// must be contained!
		} else if( area instanceof InaccessibleArea ) {
			result = inaccessibleAreas.remove( (InaccessibleArea) area );
		}
		if( area instanceof DelayArea ) {
			result = delayAreas.remove( (DelayArea) area );
		}
		if( area instanceof SaveArea ) {
			result = saveAreas.remove( (SaveArea) area );
			// delete from evacuation area list
			if( area instanceof EvacuationArea ) {
				result = evacuationAreas.remove( (EvacuationArea) area );
			}				// must be contained!
		}
		if( area instanceof StairArea ) {
			result = stairAreas.remove( (StairArea) area );
		}
		if( !result ) {
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.NoAreaException" ) );
		}

		area.removeChangeListener( this );
		throwChangeEvent( new ChangeEvent( this ) );
	}

	/**
	 * Removes this room from his associated floor, deleting it effectively.
	 */
	@Override
	public void delete() throws IllegalArgumentException, IllegalStateException {
		try {
			//ChangeEvent is thrown by Floor
			associatedFloor.deleteRoom( this );
			associatedFloor = null;

			// Delete assignment areas
			AssignmentArea[] areas = getAssignmentAreas().toArray( new AssignmentArea[getAssignmentAreas().size()] );

			for( AssignmentArea area : areas ) {
				//deleteArea( area );
				area.delete();
			}


			// Call this at the END of this method, it destroys structures that 
			// are neccessary for associatedFloor.deleteRoom (this) to work
			super.delete();
		} catch( IllegalArgumentException ex ) {
			throw ex;
		} catch( IllegalStateException ex ) {
			throw ex;
		}
	}

	/**
	 * Returns a view of all assignment areas.
	 * @return a list of all assignment areas
	 */
	public List<AssignmentArea> getAssignmentAreas() {
		return Collections.unmodifiableList( assignmentAreas );
	}

	/**
	 * Returns the floor associated with this room.
	 * @return the associated floor of this room
	 */
	public Floor getAssociatedFloor() {
		return associatedFloor;
	}

	/**
	 * Returns a view of all barriers.
	 * @return the list of all barriers
	 */
	public List<Barrier> getBarriers() {
		return Collections.unmodifiableList( barriers );
	}

	/**
	 * Returns a view of all delay areas.
	 * @return the list of all delay areas
	 */
	public List<DelayArea> getDelayAreas() {
		return Collections.unmodifiableList( delayAreas );
	}

	/**
	 * Returns a view of all evacuation areas.
	 * @return the list of all delay areas
	 */
	public List<EvacuationArea> getEvacuationAreas() {
		return Collections.unmodifiableList( evacuationAreas );
	}

	/**
	 * Returns a view of all inaccessible areas.
	 * @return the list of all inaccessible areas
	 */
	public List<InaccessibleArea> getInaccessibleAreas() {
		return Collections.unmodifiableList( inaccessibleAreas );
	}

	/**
	 * Returns a view of the list of all save areas.
	 * @return the view of all save areas
	 */
	public List<SaveArea> getSaveAreas() {
		return Collections.unmodifiableList( saveAreas );
	}

	/**
	 * Returns a view of the list of all stair areas.
	 * @return the view of all stair areas
	 */
	public List<StairArea> getStairAreas() {
		return Collections.unmodifiableList( stairAreas );
	}

	/** @return All areas that are in the room. */
	public List<Area> getAreas() {
		ArrayList<Area> a = new ArrayList<Area>( assignmentAreas.size() +
						barriers.size() + delayAreas.size() + evacuationAreas.size() +
						inaccessibleAreas.size() + saveAreas.size() + stairAreas.size() );
		a.addAll( assignmentAreas );
		a.addAll( barriers );
		a.addAll( delayAreas );
		a.addAll( evacuationAreas );
		a.addAll( inaccessibleAreas );
		a.addAll( saveAreas );
		a.addAll( stairAreas );
		return a;
	}

	/**
	 * Sets the associated floor of this room.
	 * @param floor the new associated floor of this room
	 * @throws IllegalArgumentException if the specified floor equals NULL
	 */
	public void setAssociatedFloor( Floor floor ) throws IllegalArgumentException {
		try {
			if( floor != null ) {
				//ChangeEvents are thrown by Floor class
				associatedFloor.deleteRoom( this );
				associatedFloor = floor;
				floor.addRoom( this );
			} else {
				throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Room.NoFloorException" ) );
			}
		} catch( IllegalArgumentException ex ) {
			throw ex;
		}
	}

	/** This is a convenience method that returns all PlanPoints of all 
	 * Edges of this polygon and all PlanPoints of all Areas that are 
	 * registered at this Room.
	 */
	public List<PlanPoint> getPlanPoints() {
		// Use linked list instaed of computing the right size for an ArrayList
		LinkedList<PlanPoint> planPoints = new LinkedList<PlanPoint>();
		getPlanPoints( planPoints );
		return planPoints;
	}

	/** This is a convenience method that adds all PlanPoints of all 
	 * Edges of this polygon to the given list. The Room implementation 
	 * also adds all PlanPoints of all Areas that are registered at this Room.
	 */
	public void getPlanPoints( List<PlanPoint> planPoints ) {
		super.getPlanPoints( planPoints );

		for( Area a : assignmentAreas ) {
			a.getPlanPoints( planPoints );
		}
		for( Area a : barriers ) {
			a.getPlanPoints( planPoints );
		}
		for( Area a : delayAreas ) {
			a.getPlanPoints( planPoints );
		}
		for( Area a : evacuationAreas ) {
			a.getPlanPoints( planPoints );
		}
		for( Area a : inaccessibleAreas ) {
			a.getPlanPoints( planPoints );
		}
		for( Area a : saveAreas ) {
			a.getPlanPoints( planPoints );
		}
		for( Area a : stairAreas ) {
			a.getPlanPoints( planPoints );
		}
	}

	/**
	 * First traversion of cleanUp in Rooms.
	 * Deletes (regardless of passabiblity of the edges) all "thorns"
	 * (=pairs of neighboured edges, one from a to b and the other from b to a),
	 * deletes all edges that are made up of the same two points
	 * and combines all pairs of normal (=not passable) edges that are combineable
	 * (=all x-coordinates OR all y-coordinates of both edges are the same).
	 * (Deleting a thorn of two passable edges that are passable to differnent rooms,
	 * connects these rooms together = deleting too little door-rooms)
	 * 
	 * In addition:
	 * Calls cleanUp for the room`s "normal" areas and its barriers,
	 * Temporally stores each InaccessibleArea for a potential restoring.
	 * Then calls the normal cleanUp for Areas (see {@link Area#cleanUpThornsAndNormalEdges() }).
	 * If that cleanUp founds out that the InaccessibleArea has area 0,
	 * this InaccessibleArea is restored and a special cleanUp for "zero-InaccessibleAreas"
	 * is called for the restored InaccessibleArea.
	 */
	void cleanUpThornsAndNormalEdgesForRooms() {
		if( 1 != 0 ) {
			return;
		}
		//toDo: Zipefl und combinedZipfel einarbeiten!
		// cleanUp for Rooms (first traversion)
		PlanPoint p1, p2, p3;
		RoomEdge e1, e2, temp1, temp2;
		e1 = (RoomEdge) getFirstEdge();
		e2 = (RoomEdge) (e1.getTarget().getNextEdge());
		p1 = e1.getSource();
		p2 = e1.getTarget();
		p3 = e2.getTarget();

		boolean lastStepsBegin = false;
		boolean lastSteps = false;

		while( getNumberOfEdges() > 1 ) {
			// for the breaking condition:
			if( e2.equals( getLastEdge() ) ) {
				lastStepsBegin = true;
			}
			if( lastStepsBegin && e1.equals( getLastEdge() ) ) {
				lastSteps = true;
			}



			// if the edges e1 and e2 are a "thorn":
			if( p1.equals( p3 ) ) {
				temp1 = (RoomEdge) e1.getSource().getPreviousEdge();
				temp2 = (RoomEdge) e2.getTarget().getNextEdge();
				combineEdges( e1, e2, false );
				if( getNumberOfEdges() == 0 ) {
					break;
				} else {
					e1 = temp1;
					e2 = temp2;
					p1 = e1.getSource();
					p2 = e1.getTarget();
					p3 = e2.getTarget();
				}
				continue;
			}


			// if the points of one edge are the same:
			// edge e1:
			if( p1.equals( p2 ) ) {
				e1 = (RoomEdge) e1.getSource().getPreviousEdge();
				e1.getTarget().getNextEdge().delete();
				p1 = e1.getSource();
				continue;
			}
			// edge e2:
			if( p2.equals( p3 ) ) {
				e2 = (RoomEdge) e2.getTarget().getNextEdge();
				e2.getSource().getPreviousEdge().delete();
				p3 = e2.getTarget();
				continue;
			}

			// if the edges are combineable and both are not passable Edges
			boolean combineable = (((p1.getXInt() == p2.getXInt()) && (p2.getXInt() == p3.getXInt())) || ((p1.getYInt() == p2.getYInt()) && (p2.getYInt() == p3.getYInt())));
			boolean bothNotPassable = (!(e1.isPassable()) && !(e2.isPassable()));
			if( combineable && bothNotPassable ) {
				e1 = (RoomEdge) combineEdges( e1, e2, false );
				e2 = (RoomEdge) e1.getTarget().getNextEdge();
				p2 = e1.getTarget();
				p3 = e2.getTarget();
			} //if none of the cases above is true
			else {
				if( lastSteps ) {
					break;
				} else {
					e1 = (RoomEdge) e1.getTarget().getNextEdge();
					e2 = (RoomEdge) e2.getTarget().getNextEdge();
					p1 = e1.getSource();
					p2 = e1.getTarget();
					p3 = e2.getTarget();
				}
			}


		}

		List<PlanPoint> temp_points;

		// Ennumeration?? temp_type;


		for( SaveArea sa : saveAreas ) {
			//get copies of the area`s points
			temp_points = sa.getPlanPoints();
			for( int i = 0; i < temp_points.size(); i++ ) {
				temp_points.set( i, (PlanPoint) (temp_points.get( i ).clone()) );
			}
			if( sa instanceof EvacuationArea ) {
				//temp_type=EVACUATIONAREA;
			} else {
				//temp_type=EVACUATIONAREA;
			}

			sa.cleanUpForAreas();
			if( sa.getNumberOfEdges() < 4 ) {
				//staticClass.add(temp_type,temp_points,getFloor());
				sa.delete();
			}
		}


		//temp_type =DELAYAREA
		for( DelayArea da : delayAreas ) {
			//get copies of the area`s points
			temp_points = da.getPlanPoints();
			for( int i = 0; i < temp_points.size(); i++ ) {
				temp_points.set( i, (PlanPoint) (temp_points.get( i ).clone()) );
			}
			da.cleanUpForAreas();
			if( da.getNumberOfEdges() < 4 ) {
				//staticClass.add(temp_type,temp_points,getFloor());
				da.delete();
			}
		}


		for( StairArea da : stairAreas ) {
			//get copies of the area`s points
			temp_points = da.getPlanPoints();
			for( int i = 0; i < temp_points.size(); i++ ) {
				temp_points.set( i, (PlanPoint) (temp_points.get( i ).clone()) );
			}
			da.cleanUpForAreas();
			if( da.getNumberOfEdges() < 4 ) {
				//staticClass.add(temp_type,temp_points,getFloor());
				da.delete();
			}
		}


		//temp_type =ASSIGNMENTAREA
		for( AssignmentArea aa : assignmentAreas ) {
			//get copies of the area`s points
			temp_points = aa.getPlanPoints();
			for( int i = 0; i < temp_points.size(); i++ ) {
				temp_points.set( i, (PlanPoint) (temp_points.get( i ).clone()) );
			}
			aa.cleanUpForAreas();
			if( aa.getNumberOfEdges() < 4 ) {
				//staticClass.add(temp_type,temp_points,getFloor(),aa.getAssignmentType(),aa.getEvacuees());
				aa.delete();
			}
		}



		//temp_type =INACCESSIBLEAREA
		boolean convertedToBarrier = false;
		for( InaccessibleArea ia : inaccessibleAreas ) {
			//get copies of the area`s points
			temp_points = ia.getPlanPoints();
			for( int i = 0; i < temp_points.size(); i++ ) {
				temp_points.set( i, (PlanPoint) (temp_points.get( i ).clone()) );
			}
			convertedToBarrier = ia.cleanUpForInaccessibleAreas();
			if( ia.getNumberOfEdges() < 4 ) {
				ia.delete();
				if( !(convertedToBarrier) ) {
					//staticClass.add(temp_type,temp_points,getFloor());
				}
			}
		}
	}

	/**
	 * Second traversion of cleanUp in Rooms.
	 * Combines all pairs of passable edges that are combineable (=all x-coordinates OR all y-coordinates
	 * of both edges are the same) and which`s linkTargets are neighbours in the same room and also combineable.
	 */
	void cleanUpPassableEdgesForRooms() {

		// cleanUp for Rooms (second traversion)
		PlanPoint p1, p2, p3;
		RoomEdge e1, e2;
		e1 = (RoomEdge) getFirstEdge();
		e2 = (RoomEdge) (e1.getTarget().getNextEdge());
		p1 = e1.getSource();
		p2 = e1.getTarget();
		p3 = e2.getTarget();

		boolean lastStepsBegin = false;
		boolean lastSteps = false;

		while( getNumberOfEdges() > 3 ) {
			// for the breaking condition:
			if( e2.equals( getLastEdge() ) ) {
				lastStepsBegin = true;
			}
			if( lastStepsBegin && e1.equals( getLastEdge() ) ) {
				lastSteps = true;
			}

			// if the edges are combineable and both are passable and the linkTargets are combineable
			boolean bothPassable = (e1.isPassable() && e2.isPassable());
			boolean linkTargetsAreCombineable = false;
			if( bothPassable ) {
				linkTargetsAreCombineable = e1.getLinkTarget().combineableWith( e2.getLinkTarget() );
			}
			boolean combineable = (((p1.getXInt() == p2.getXInt()) && (p2.getXInt() == p3.getXInt())) || ((p1.getYInt() == p2.getYInt()) && (p2.getYInt() == p3.getYInt())));

			if( combineable && bothPassable && linkTargetsAreCombineable ) {
				e1 = (RoomEdge) combineEdges( e1, e2, false );
				e2 = (RoomEdge) e1.getTarget().getNextEdge();
				p2 = e1.getTarget();
				p3 = e2.getTarget();
			} else {
				if( lastSteps ) {
					break;
				} else {
					e1 = (RoomEdge) e1.getTarget().getNextEdge();
					e2 = (RoomEdge) e2.getTarget().getNextEdge();
					p1 = e1.getSource();
					p2 = e1.getTarget();
					p3 = e2.getTarget();
				}
			}
		}
	}
}