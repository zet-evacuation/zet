/*
 * RoomEdge.java
 * Created on 26. November 2007, 21:32
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import ds.z.event.ChangeEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import localization.Localization;

/**
 * Implements a special type of {@link Edge} that has to be associated with a
 * room, but cannot be associated to a general object of {@link PlanPolygon}-type.
 * With this distinction a differentiation between rooms and areas in a
 * building plan can be realized.
 * A room edge has the special ability to can be associated to two rooms at
 * the same time. As an <code>Edge</code> has to be in the border of a simple
 * polygon (at least, if it is valid), all instances of <code>RoomEdge</code>
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

	/** When this flag is turned "true" the RoomEdge will only accept targets
	 * which have the same location as itself and it will hand through all changes
	 * that are made to one of it's points to the linkTarget edge. This behaviour
	 * is not feasible for some subclasses and thus can be turned off. */
	@XStreamOmitField
	protected transient boolean ensureMatchWithLinkTarget = true;
	/** Only subclasses should access this field. */
	protected RoomEdge linkTarget;

	RoomEdge (PlanPoint p1, PlanPoint p2) throws IllegalStateException, IllegalArgumentException {
		super (p1, p2);
	}

	/**
	 * Creates a new instance of <code>RoomEdge</code> specified by the two
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

	/** {@inheritDoc} 
	 *
	 * This method performs the standard behaviour (forwarding of events to all listeners)
	 * and additionally applies the changes that were made to this edge to the linkTarget edge
	 * (if it is != null).
	 */
	@Override
	public void stateChanged (ChangeEvent e) {
		// In case a passable edge is moved we have to move the attached edge in the other room too
		if (ensureMatchWithLinkTarget && isPassable ()) {
			if (e.getSource () instanceof PlanPoint) {
				// First p = myPoint ; Second p = linkTarget point --> Equal?
				boolean p1p1 = linkTarget.getSource ().equals (getSource ());
				boolean p2p2 = linkTarget.getTarget ().equals (getTarget ());
				boolean p2p1 = linkTarget.getSource ().equals (getTarget ());
				boolean p1p2 = linkTarget.getTarget ().equals (getSource ());
				//assert (p1p1 ^ p1p2 ^ p2p1 ^ p2p2);

				// The following cases may be true here:
				// 1) Both edges were of the form p1,p2 before
				// 2) One edge was of the form p1,p2 and the other was p2,p1
				//    (this makes no difference since we are using undirected edges)

				if (p1p1) {
					// Case 1) (where "this" edge has already changed)
					// Point 2 has changed
					linkTarget.getTarget ().setLocation (getTarget ());
				} else if (p2p2) {
					// Case 1) (where "this" edge has already changed)
					// Point 1 has changed
					linkTarget.getSource ().setLocation (getSource ());
				} else if (p1p2) {
					// Case 2) (where "this" edge has already changed)
					// Point 2 at "this" has changed --> Point 1 at "linkTarget""
					linkTarget.getSource ().setLocation (getTarget ());
				} else if (p2p1) {
					// Case 2) (where "this" edge has already changed)
					// Point 1 at "this" has changed --> Point 2 at "linkTarget""
					linkTarget.getTarget ().setLocation (getSource ());
				}
			}
			super.stateChanged (e);
		} else {
			super.stateChanged (e);
		}
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
		if (linkTarget != null) {
			linkTarget.setLinkTarget (null);
		}

		super.delete ();
	}

	/** {@inheritDoc}
	 * <p>
	 * In the case of <code>RoomEdge</code> this is only allowed, if the associated 
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
			throw new java.lang.IllegalStateException (Localization.getInstance ().getString ("ds.z.RoomEdge.RoomChangeOnlyIfImpassable"));
		}
		if (!(p instanceof Room)) {
			throw new java.lang.IllegalArgumentException (Localization.getInstance ().getString ("ds.z.RoomEdge.OnlyRoomsAsOwnerPolygon"));
		}
		super.setAssociatedPolygon (p);
	}

	/** Returns the edge at which you will pop up, when you "pass through" this 
	 * edge. This will be null, if the edge is not passable.
	 */
	public RoomEdge getLinkTarget () {
		return linkTarget;
	}

	/** Use this method to indicate that the roomEdge is passable and that you get to 
	 * the Edge "linkTarget" when you pass through "this" edge. */
	public void setLinkTarget (RoomEdge target) {
		// The edges must be equal with respect to their location on the plan. Their linkTarget values
		// are not forced to match each other. This would be nonsense, because two edges wich linkTarget
		// each other, have the same coordinates, but of course different targets (the other edge)
		// Therefore we use super.equals() here
		if (ensureMatchWithLinkTarget && target != null && !super.equals (target)) {
			throw new IllegalArgumentException (Localization.getInstance ().getString ("ds.z.RoomEdge.InequalLinkTarget"));
		}
		this.linkTarget = target;

		throwChangeEvent (new ChangeEvent (this));
	}

	/**
	 * Determines whether this edge is combineable with the given other edge.
	 * In this case, combineable means, that the other Edge is a neighbour of this edge (see {@link Edge#isNeighbour( Edge )})
	 * AND that all x-coordinates or all y-coordinates of both edges are the same.
	 * @return true if this edge is combineable with the given other edge
	 * @param otherEdge the given other edge
	 */
	public boolean combineableWith (Edge otherEdge) {
		if (!(this.isNeighbour (otherEdge))) {
			return false;
		} else {
			PlanPoint p2 = this.commonPoint (otherEdge);
			PlanPoint p1 = this.getOther (p2);
			PlanPoint p3 = otherEdge.getOther (p2);
			return (((p1.getXInt () == p2.getXInt ()) && (p2.getXInt () == p3.getXInt ())) || ((p1.getYInt () == p2.getYInt ()) && (p2.getYInt () == p3.getYInt ())));
		}
	}
	
	/** This makes the edge impassable. Trivial for RoomEdges, not so trivial for 
	 * TeleportEdges which must override this method. */
	public void makeImpassable () {
		getLinkTarget ().setLinkTarget (null);
		setLinkTarget (null);
	}
}