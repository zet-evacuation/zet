/**
 * ZModelRoomEvent.java
 * Created: Nov 6, 2012, 12:57:14 PM
 */
package ds.z;

import java.util.Collection;
import java.util.Collections;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ZModelRoomEvent implements ZModelChangedEvent {
	Collection<Room> affectedRooms;

	public ZModelRoomEvent( Collection<Room> affectedRooms ) {
		this.affectedRooms = affectedRooms;
	}

	public Collection<Room> getAffectedRooms() {
		return Collections.unmodifiableCollection( affectedRooms );
	}
}
