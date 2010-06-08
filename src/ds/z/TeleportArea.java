/**
 * TeleportArea.java
 * Created: Jun 8, 2010,5:32:19 PM
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias("delayArea")
public class TeleportArea extends Area<Edge> {

	/**
	 * Constucts a new <code>DelayArea</code> with the default <code>speedFactor</code>
	 * provided by the specified {@link DelayType}.
	 *
	 * @param room to which the area belongs
	 * @param type the source/type of the delay
	 */
	public TeleportArea( Room room ) {
		super( Edge.class, room );
		//this (room, type, type.defaultSpeedFactor);
	}

	/**
	 * This method copies the current polygon without it's edges. Every other setting, as f.e. the floor
	 * for Rooms or the associated Room for Areas is kept as in the original polygon.
	 */
	@Override
	protected PlanPolygon<Edge> createPlainCopy() {
		return new TeleportArea( getAssociatedRoom() );
	}

//	/** Constucts a new DelayArea with the given parameters.
//	 *
//	 * @param room to which the area belongs
//	 * @param type the source/type of the delay
//	 * @param speedFactor affects the speed of the evacuees.
//	 */
//	public TeleportArea (Room room ) {
//		super ( Edge.class, room);
//		setDelayType (type);
//		setSpeedFactor (speedFactor);
//	}
	@Override
	public boolean equals( Object o ) {
		if( o instanceof DelayArea ) {
			DelayArea p = (DelayArea) o;
			return super.equals( p );
		} else
			return false;
	}
}
