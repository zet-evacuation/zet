/*
 * EvacuationArea.java
 * Created on 26. November 2007, 21:32
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import ds.z.event.EvacuationAreaCreatedEvent;
import localization.Localization;

/**
 * Represents a EvacuationArea. 
 * An EvacuationArea is an area, where the evacuees reach this area, they are 
 * completly save an they have no influence on the system any more. Every 
 * EvacuationArea is associated to exactly one {@link Room} at every time.
 */
@XStreamAlias( "evacuationArea" )
public class EvacuationArea extends SaveArea {
	/** The attractivity of this <code>EvacuationArea</code> */
	private int attractivity;
 	@XStreamAsAttribute()
	/** The name of the <code>EvacuationArea</code>. */
	private String name = "EvacuationArea";

	/**
	 * Creates a new instance of <code>EvacuationArea</code> and sets the
	 * room containing this area. The attractivity is initialized to 100.
	 * @param room the room
	 * @see #setAttractivity()
	 */
	public EvacuationArea( Room room ) {
		this( room, 100 );
	}
	/**
	 * Creates a new instance of <code>EvacuationArea</code> and sets the
	 * room containing this area.
	 * @param room the room
	 * @param attractivity the initial attractivity
	 * @see #setAttractivity()
	 */
	public EvacuationArea( Room room, int attractivity ) {
		super( room );
		setAttractivity( attractivity );
		throwChangeEvent( new EvacuationAreaCreatedEvent( this, 1 ) );
	}
	
	/** This method copies the current polygon without it's edges. Every other setting, as f.e. the floor
	 * for Rooms or the associated Room for Areas is kept as in the original polygon.
	 * @return 
	 */
	@Override
	protected PlanPolygon<Edge> createPlainCopy() {
		return new EvacuationArea( getAssociatedRoom(), getAttractivity() );
	}
	
	/**
	 * Returns the attractivity of this evacuation area.
	 * @return the attractivity
	 */
	public int getAttractivity() {
		return attractivity;
	}
	
	/**
	 * Sets the attractivity of this evacuation area. An area with a higher
	 * attractivity is supposed to used more often for evacuation. This can be
	 * used to set the main entrance(s).
	 * @param attractivity
	 * @throws java.lang.IllegalArgumentException if attractivity is less or equal
	 * to zero
	 */
	public void setAttractivity( int attractivity ) throws java.lang.IllegalArgumentException {
		if( attractivity <=0 )
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.EvacuationArea.AttractivityLessThanZeroException" ) );
		this.attractivity = attractivity;
	}

	/**
	 * Returns the name of this <code>AssignmentArea</code>
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this <code>AssignmentArea</code> to a given string value.
	 * @param name the new name
	 */
	public void setName( String name ) {
		this.name = name;
	}
	
	/**
	 * Deletes this <code>EvacuationArea</code>. The exit of all assignemt areas
	 * that use this <code>EvacuationArea</code> as exit are set to <code>null</code>
	 * using the event system.
	 */
	@Override
	public void delete() {
		throwChangeEvent( new EvacuationAreaCreatedEvent( this, 2 ) );
		super.delete();
	}
}
