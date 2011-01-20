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
 * EvacuationArea.java
 * Created on 26. November 2007, 21:32
 */
package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Represents a EvacuationArea. 
 * An EvacuationArea is an area, where the evacuees reach this area, they are 
 * completly save an they have no influence on the system any more. Every 
 * EvacuationArea is associated to exactly one {@link Room} at every time.
 */
@XStreamAlias( "evacuationArea" )
public class EvacuationArea extends SaveArea {
	/** The attractivity of this {@code EvacuationArea} */
	private int attractivity;
 	@XStreamAsAttribute()
	/** The name of the {@code EvacuationArea}. */
	private String name = "EvacuationArea";

	/**
	 * Creates a new instance of {@code EvacuationArea} and sets the
	 * room containing this area. The attractivity is initialized to 100.
	 * @param room the room
	 * @see #setAttractivity( int )
	 */
	public EvacuationArea( Room room ) {
		this( room, 100 );
	}

	/**
	 * Creates a new instance of {@code EvacuationArea} and sets the
	 * room containing this area.
	 * @param room the room
	 * @param attractivity the initial attractivity
	 * @see #setAttractivity( int )
	 */
	public EvacuationArea( Room room, int attractivity ) {
		super( room );
		setAttractivity( attractivity );
//		throwChangeEvent( new EvacuationAreaCreatedEvent( this, 1 ) );
	}
	
	/**
	 * Creates a new instance of {@code EvacuationArea} and sets the
	 * room containing this area.
	 * @param room the room
	 * @param attractivity the initial attractivity
	 * @param name the name of the evacuation area
	 * @see #setAttractivity( int )
	 */
	public EvacuationArea( Room room, int attractivity, String name ) {
		super( room );
		setAttractivity( attractivity );
		setName( name );
//		throwChangeEvent( new EvacuationAreaCreatedEvent( this, 1 ) );
	}

	/**
	 * This method copies the current polygon without it's edges. Every other
	 * setting, as f.e. the floor for Rooms or the associated Room for Areas is
	 * kept as in the original polygon.
	 * @return a copy of the polygon
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
			throw new java.lang.IllegalArgumentException( ZLocalization.getSingleton().getString( "ds.z.EvacuationArea.AttractivityLessThanZeroException" ) );
		this.attractivity = attractivity;
	}

	/**
	 * Returns the name of this {@code AssignmentArea}
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this {@code AssignmentArea} to a given string value.
	 * Spaces at the front and end are removed.
	 * @param name the new name
	 */
	public void setName( String name ) {
		this.name = name.trim();
	}
	
	/**
	 * Deletes this {@code EvacuationArea}. The exit of all assignemt areas
	 * that use this {@code EvacuationArea} as exit are set to {@code null}
	 * using the event system.
	 */
//	@Override
//	public void delete() {
//		throwChangeEvent( new EvacuationAreaCreatedEvent( this, 2 ) );
//		super.delete();
//	}
}
