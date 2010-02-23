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

import ds.z.*;

/**
 * The <code>RoomInterstException</code> indicates that two rooms in one {@link ds.z.Floor} intersect
 * each other, what is not allowed.
 * @author Joscha Kulbatzki
 */
public class RoomIntersectException extends ValidationException {
  /**
   * Creates a new instance of <code>RoomIntersectException</code>. The submittet parameters
   * are a pair of rooms which intersect and have caused the exception.
   * @param room1 the first room
   * @param room2 the second room
   */
  public RoomIntersectException( Room room1, Room room2 ) {
    super();
    super.source = new RoomPair( room1, room2 );
  }
  
  /**
   * Creates a new instance of <code>RoomIntersectException</code>. The submittet parameters
   * are a pair of rooms which intersect and have caused the exception.
   * @param room1 the first room
   * @param room2 the second room
   * @param s a message further describing the exception
   */
  public RoomIntersectException(Room room1, Room room2, String s ) {
    super(s);
    
    super.source = new RoomPair(room1, room2);
  }
  
  /**
   * Returns a pair of rooms that that intersects and has caused this exception.
   * @return the pair of rooms
   */
  public RoomPair getIntersectingRooms() {
    return (RoomPair)getSource();
  }
  
  /**
   * <code>RoomPair</code> is a container for two rooms that is merely used
   * to connect two intersecting rooms .
   */
  public class RoomPair {
    /** The first room. */
    public final Room room1;
    /** The second room. */
    public final Room room2;
    
    /**
     * Creates a new instance of <code>RoomPair</code> with two rooms.
     * @param r1 the first room
     * @param r2 the second room
     */
    public RoomPair( Room r1, Room r2 ) {
      room1 = r1;
      room2 = r2;
    }
  }
}