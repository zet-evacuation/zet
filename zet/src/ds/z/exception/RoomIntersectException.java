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
package ds.z.exception;

import de.tu_berlin.math.coga.datastructure.Tuple;
import ds.z.PlanPoint;
import ds.z.Room;
import java.io.IOException;

/**
 * The {@code RoomInterstException} indicates that two rooms in one {@link ds.z.Floor} intersect
 * each other, what is not allowed.
 * @author Joscha Kulbatzki
 */
@SuppressWarnings( "serial" )
public class RoomIntersectException extends ValidationException {
	PlanPoint point = null;
	/**
	 * Creates a new instance of {@code RoomIntersectException}. The submitted parameters
	 * are a pair of rooms which intersect and have caused the exception.
	 * @param room1 the first room
	 * @param room2 the second room
	 */
	public RoomIntersectException( Room room1, Room room2 ) {
		super( new Tuple<>( room1, room2 ) );
	}

	public RoomIntersectException( Room room1, Room room2, PlanPoint point ) {
		super( new Tuple<>( room1, room2 ) );
		this.point = point;
	}

	/**
	 * Creates a new instance of {@code RoomIntersectException}. The submitted parameters
	 * are a pair of rooms which intersect and have caused the exception.
	 * @param room1 the first room
	 * @param room2 the second room
	 * @param s a message further describing the exception
	 */
	public RoomIntersectException( Room room1, Room room2, String s ) {
		super( new Tuple<>( room1, room2 ), s );
	}

	/**
	 * Returns a pair of rooms that that intersects and has caused this exception.
	 * @return the pair of rooms
	 */
	@SuppressWarnings( "unchecked" )
	public Tuple<Room, Room> getIntersectingRooms() {
		return (Tuple<Room, Room>) getSource();
	}

	/**
	 * Returns the point in which the two rooms intersect (or maybe {@code null}.
	 * @return the point in which the two rooms intersect (or maybe {@code null}
	 */
	public PlanPoint getIntersectionPoiont() {
		return point;
	}

	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}
