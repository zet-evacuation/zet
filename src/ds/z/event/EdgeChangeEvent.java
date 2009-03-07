package ds.z.event;

import ds.z.Edge;
import ds.z.PlanPoint;

/** This event is thrown whenever a change to a PlanPoint, that belongs to an Edge
 * is made. In this case the Edge catches the PlanPoint's event and rethrows an
 * EdgeChangeEvent.
 *
 * @author Timon Kelter
 */
public class EdgeChangeEvent extends ChangeEvent {
	protected PlanPoint target;
	
	/** @param source The edge that was changed
	 * @param target This field gives the user the affected PlanPoint, of it is 
	 * null, if both PlanPoints have been edited. */
	public EdgeChangeEvent (Edge source, PlanPoint target) {
		this (source, target, null);
	}
	/** @param source The edge that was changed
	 * @param target This field gives the user the affected PlanPoint, of it is 
	 * null, if both PlanPoints have been edited.
	 * @param message A message that describes the event
	 */
	public EdgeChangeEvent (Edge source, PlanPoint target, String message) {
		super (source, message);
		this.target = target;
	}

	/** The edge that was changed */
	public Edge getEdge () {
		return (Edge)source;
	}

	/** This field gives the user the affected PlanPoint, of it is 
	 * null, if both PlanPoints have been edited. */
	public PlanPoint getTarget () {
		return target;
	}
}