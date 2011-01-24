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
 * Area.java
 * Created on 26. November 2007, 21:32
 */

package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @param <T> the edge type
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "area" )
public abstract class Area<T extends Edge> extends PlanPolygon<T> implements ZFormatObject {
	private Room associatedRoom;

	Area( Class<T> edgeClassType, Room associatedPolygon ) {
		super( edgeClassType );
		setAssociatedRoom( associatedPolygon );
	}

	/**
	 * Deletes the area.
	 */
	@Override
	void delete() {
		getAssociatedRoom().deleteArea( this );
		associatedRoom = null;
		
		// SHORT NOTE: In constructor: Call superconstruktor at the begin
		//             In  destructor: Call  superdestruktor at the end
		// MORE DETAIL:
		// super.delete must be called at last, because otherwise the polygon 
		// will have been deleted at the time when you want to tell the associated
		// room that you want to delete this area. this will lead to an exception,
		// because Room.deleteArea() uses the equals method to determine which
		// area it must delete. as we would have deleted the polygon edges before
		// the call to Room.deleteArea() this equals method will throw a 
		// null pointer exception.
		super.delete();
	}
	
	/**
	 * Returns the associatedRoom of the area.
	 * @return associatedRoom of the area.
	 */
	public Room getAssociatedRoom() {
		return associatedRoom;
	}

	/**
	 * This operation takes care of setting the room that is associated to this
	 * area. It also conserves the consistence with the area lists in the rooms.
	 * @param room is the associated room of the area.
	 * @throws java.lang.IllegalArgumentException when room is {@code null}.
	 */
	final public void setAssociatedRoom( Room room ) throws IllegalArgumentException {
		if( room == null )
			throw new IllegalArgumentException( ZLocalization.getSingleton().getString("ds.z.Area.NoRoomException") );

		if(associatedRoom != null )
			associatedRoom.deleteArea( this );
		associatedRoom= room;
		associatedRoom.addArea( this );
	}

	@Override
	public boolean equals( Object o ) {
		if( o instanceof Area ) {
			Area p = (Area)o;
			return super.equals( p ) && ( (associatedRoom == null ) ? p.getAssociatedRoom() == null :associatedRoom.equals( p.getAssociatedRoom() ) );
		} else
			return false;
	}

	public abstract AreaTypes getAreaType();
}
