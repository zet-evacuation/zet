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
 * RoomEdge.java
 * Created on 26. November 2007, 21:32
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Implements a special type of {@link Edge} that has to be associated with a
 * room, but cannot be associated to a general object of {@link PlanPolygon}-type.
 * With this distinction a differentiation between rooms and areas in a
 * building plan can be realized.
 * A room edge has the special ability to can be associated to two rooms at
 * the same time. As an {@code Edge} has to be in the border of a simple
 * polygon (at least, if it is valid), all instances of {@code RoomEdge}
 * which are associated to two rooms realize an inner edge in a polygonial net.
 * These are considered as intersections between the rooms.
 * The order of the rooms is not important.
 * 
 * In a scenario with a passable RoomEdge, this single edge is represented by two
 * room edges, because each RoomEdge is a PlanPolygon, and can thus only belong to 
 * one Room. But to indicate that the RoomEdges are passable one can use the
 * setLinkTarget method. So in our scenario with 2 RoomEdges which are positioned
 * on the same points, each of these edges would set its linkTarget to point to the
 * other one. --> A Room Edge is passable if you set the linkTarget property.
 * 
 * If the linkTarget is set, both edges are using different planpoint objects, but changes
 * that are made to one of the RoomEdges are automatically applied to the linkTarget
 * edge, too, by using the event handling mechanism.
 * 
 * @author Jan-Philipp Kappmeier, Timon Kelter
 */
@XStreamAlias ("roomEdge")
public class RoomEdge extends Edge {

	/** When this flag is turned {@code true} the RoomEdge will only accept targets
	 * which have the same location as itself and it will hand through all changes
	 * that are made to one of it's points to the linkTarget edge. This behavior
	 * is not feasible for some subclasses and thus can be turned off. */
	@XStreamOmitField
	protected transient boolean ensureMatchWithLinkTarget = true;
	/** Only subclasses should access this field. */
	protected RoomEdge linkTarget;

	RoomEdge (PlanPoint p1, PlanPoint p2) throws IllegalStateException, IllegalArgumentException {
		super (p1, p2);
	}

	/**
	 * Creates a new instance of {@code RoomEdge} specified by the two
	 * end points of the edge. A {@link Room} is needed that is associated to
	 * the instance. The edge is added to the room. If an error occurs, e.g.
	 * the edge does not fix to the room an exception is throws.
	 * @param p1 one point
	 * @param p2 the other point
	 * @param p the associated room
	 * @throws java.lang.IllegalStateException if the polygon is closed. It is not
	 * possible to add further edges to a closed polygon.
	 * @throws java.lang.IllegalArgumentException if the polygon already contains
	 * the edge or if the edge is not connected to the polygon
	 */
	public RoomEdge (PlanPoint p1, PlanPoint p2, Room p) throws IllegalStateException, IllegalArgumentException {
		super (p1, p2, p);
	}

	/**
	 * Determines whether the represented edge is considered as passable. That happens 
	 * if and only if the edge is associated to two (different) rooms.
	 * @return true if the edge is connected to two different rooms
	 */
	public boolean isPassable () {
		return getLinkTarget () != null;
	}

	/**
	 * Returns the associated {@link Room} that this edge is part of.
	 * @return the room
	 */
	public Room getRoom () {
		return (Room) getAssociatedPolygon ();
	}

	/** {@inheritDoc}
	 * 
	 * In addition to the superclass' delete method this one also must take care
	 * of setting the linkTarget edge's "linkTarget" field to null (Ending of the mutual
	 * relationship.) */
	@Override ()
	public void delete () {
		if( linkTarget != null )
			linkTarget.setLinkTarget( null );

		super.delete();
	}

	/** {@inheritDoc}
	 * <p>
	 * In the case of {@code RoomEdge} this is only allowed, if the associated 
	 * polygon is a room. If the polygon is a room, the same restrictions
	 * as for {@link Edge#setAssociatedPolygon( PlanPolygon )} hold.
	 *
	 * @param p the new room
	 * @throws java.lang.IllegalArgumentException if the polygon is not an instance of {@link Room}
	 * @throws java.lang.IllegalStateException if the edge is connected to two rooms
	 */
	@Override
	protected void setAssociatedPolygon (PlanPolygon p) throws NullPointerException, IllegalArgumentException,
			IllegalStateException {
		if (this.linkTarget != null) {
			throw new java.lang.IllegalStateException (ZLocalization.getSingleton ().getString ("ds.z.RoomEdge.RoomChangeOnlyIfImpassable"));
		}
		if (!(p instanceof Room)) {
			throw new java.lang.IllegalArgumentException (ZLocalization.getSingleton ().getString ("ds.z.RoomEdge.OnlyRoomsAsOwnerPolygon"));
		}
		super.setAssociatedPolygon (p);
	}

	/** Returns the edge at which you will pop up, when you "pass through" this 
	 * edge. This will be null, if the edge is not passable.
	 *
	 * @return
	 */
	public RoomEdge getLinkTarget() {
		return linkTarget;
	}

	/** Use this method to indicate that the roomEdge is passable and that you get to 
	 * the Edge "linkTarget" when you pass through "this" edge.
	 * @param target 
	 */
	public void setLinkTarget( RoomEdge target ) {
		// The edges must be equal with respect to their location on the plan. Their linkTarget values
		// are not forced to match each other. This would be nonsense, because two edges wich linkTarget
		// each other, have the same coordinates, but of course different targets (the other edge)
		// Therefore we use super.equals() here
		if( ensureMatchWithLinkTarget && target != null && !super.equals( target ) )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.z.RoomEdge.InequalLinkTarget" ) );
		this.linkTarget = target;
	}

	/**
	 * Determines whether this edge is combineable with the given other edge.
	 * In this case, combineable means, that the other Edge is a neighbor of this edge (see {@link Edge#isNeighbour( Edge )})
	 * AND that all x-coordinates or all y-coordinates of both edges are the same.
	 * @return true if this edge is combineable with the given other edge
	 * @param otherEdge the given other edge
	 */
	public boolean combineableWith( Edge otherEdge ) {
		if( !(this.isNeighbour( otherEdge )) )
			return false;
		else {
			PlanPoint p2 = this.commonPoint( otherEdge );
			PlanPoint p1 = this.getOther( p2 );
			PlanPoint p3 = otherEdge.getOther( p2 );
			return (((p1.getXInt() == p2.getXInt()) && (p2.getXInt() == p3.getXInt())) || ((p1.getYInt() == p2.getYInt()) && (p2.getYInt() == p3.getYInt())));
		}
	}
	
	/**
	 * This makes the edge impassable. Trivial for RoomEdges, not so trivial for
	 * TeleportEdges which must override this method.
	 */
	void makeImpassable () {
		getLinkTarget().setLinkTarget( null );
		setLinkTarget( null );
	}

}