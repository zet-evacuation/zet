/**
 * TeleportArea.java
 * Created: Jun 8, 2010,5:32:19 PM
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import ds.z.DelayArea.DelayType;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias("teleportArea")
public class TeleportArea extends Area<Edge> {
	/** The evacuation area representing the exit that the pearsons this rooms should use. */
	private EvacuationArea exit = null;
	/** The evacuation area representing the exit that the pearsons this rooms should use. */
	private TeleportArea target = null;
 	@XStreamAsAttribute()
	/** The name of the <code>EvacuationArea</code>. */
	private String name = "TeleportArea";


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
	 * Returns the exit assigned to this <code>AssignmentArea</code>
	 * @return the assigned exit
	 */
	public EvacuationArea getExitArea() {
		return exit;
	}

	/**
	 * Sets the exit assigned to this <code>AssignmentArea</code>.
	 * @param exit the {@link EvacuationArea} representing the exit.
	 */
	public void setExitArea( EvacuationArea exit ) {
		this.exit = exit;
	}

	/**
	 * Returns the exit assigned to this <code>AssignmentArea</code>
	 * @return the assigned exit
	 */
	public TeleportArea getTargetArea() {
		return target;
	}

	/**
	 * Sets the exit assigned to this <code>AssignmentArea</code>.
	 * @param exit the {@link EvacuationArea} representing the exit.
	 */
	public void setTargetArea( TeleportArea target ) {
		this.target = target;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name.trim();
	}

	/**
	 * This method copies the current polygon without it's edges. Every other setting, as f.e. the floor
	 * for Rooms or the associated Room for Areas is kept as in the original polygon.
	 * @return the copy
	 */
	@Override
	protected PlanPolygon<Edge> createPlainCopy () {
		final TeleportArea tp = new TeleportArea( getAssociatedRoom() );
		tp.setExitArea( exit );
		tp.setTargetArea( target );
		tp.setName( name );
		return tp;
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
//	@Override
//	public boolean equals( Object o ) {
//		if( o instanceof TeleportArea ) {
//			TeleportArea t = (TeleportArea) o;
//			return super.equals( t );
//		} else
//			return false;
//	}
}
