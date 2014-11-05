/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
package de.tu_berlin.math.coga.zet.converter;

import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.tu_berlin.coga.zet.model.Floor;
import de.tu_berlin.coga.zet.model.Room;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @param <T>
 */
public class RasterContainer<T extends RoomRaster<?>> {
  /** A hash map that maps rooms to their rastered versions. */
  protected HashMap<Floor, HashMap<Room, T>> map;
  /** A list containing all floors. */
  protected List<Floor> floors;
  /** An array list containing all rastered rooms contained in the hash map. */
  protected ArrayList<T> rasteredRooms;

  /**
   * Creates a new {@code ZToGraphRasterContainer} object.
   */
  public RasterContainer() {
    map = new HashMap<>();
    rasteredRooms = new ArrayList<>();
  }

  /**
   * Adds a room and its rastered version to the container.
   * @param room the room to be add.
   * @param rasteredRoom the rastered version of the room to be add.
   */
  public void setRoomRaster( Room room, T rasteredRoom ) {
    if( room == null ) {
      throw new IllegalArgumentException( ZETLocalization2.loc.getString( "converter.RoomIsNullException" ) );
    }
    Floor floor = room.getAssociatedFloor();
    if( floor == null ) {
      throw new IllegalArgumentException( ZETLocalization2.loc.getString( "converter.NoAssociatedRoomException" ) );
    }
    if( !floors.contains( floor ) ) {
      throw new IllegalArgumentException( ZETLocalization2.loc.getString( "converter.FloorNotInListException" ) );
    }
    if( !map.containsKey( floor ) ) {
      HashMap<Room, T> zmap = new HashMap<>();
      zmap.put( room, rasteredRoom );
      map.put( floor, zmap );
    } else {
      if( (map.get( floor )).containsKey( room ) ) {
        rasteredRooms.remove( map.get( room ) );
      }
      (map.get( floor )).put( room, rasteredRoom );
    }
    rasteredRooms.add( rasteredRoom );
  }

  /**
   * Returns the rooms in the floor {@code floor} that the container has rastered versions for.
   * @param floor A floor in the Z-Format.
   * @return All rooms (in Z-Format) for which the container has rastered versions.
   */
  public Set<Room> getRooms( Floor floor ) {
    if( floor == null ) {
      throw new IllegalArgumentException( ZETLocalization2.loc.getString( "converter.NoAssociatedRoomException" ) );
    }
    if( !map.containsKey( floor ) ) {
      throw new IllegalArgumentException( ZETLocalization2.loc.getString( "converter.FloorNotInMapException" ) );
    }
    return map.get( floor ).keySet();
  }

  /**
   * Returns the rastered version of the room {@code room} that is stored in this container.
   * @param room the rastered version of this room will be returned.
   * @return the rastered version of {@code room}.
   */
  public T getRasteredRoom( Room room ) {
    if( room == null ) {
      throw new IllegalArgumentException( ZETLocalization2.loc.getString( "converter.RoomIsNullException" ) );
    }
    Floor floor = room.getAssociatedFloor();
    if( floor == null ) {
      throw new IllegalArgumentException( ZETLocalization2.loc.getString( "converter.NoAssociatedRoomException" ) );
    }
    try {
      map.get( floor ).get( room );
    } catch (Exception e ) {
      System.out.println( "Error" );
    }
    return map.get( floor ).get( room );
  }

  /**
   * Returns an unmodifiable list of all rastered versions of rooms contained in this {@code ZToGraphRasterContainer}.
   * @return an unmodifiable list of all rooms contained in this {@code ZToGraphRasterContainer}.
   */
  public List<T> getAllRasteredRooms() {
    return Collections.unmodifiableList( rasteredRooms );
  }

  /**
   * Returns an unmodifiable collection of all rastered versions of rooms contained in this
   * {@code ZToGraphRasterContainer} that lie in the floor {@code floor}.
   * @param floor the floor which rastered rooms are wished.
   * @return all an unmodifiable collection of all rastered versions of rooms contained in this
   * {@code ZToGraphRasterContainer} that lie in the floor {@code floor}.
   */
  public Collection<T> getAllRasteredRooms( Floor floor ) {
    if( map.containsKey( floor ) ) {
      return Collections.unmodifiableCollection( ((map.get( floor )).values()) );
    } else {
      return null;
    }
  }

  public void setFloors( List<Floor> floors ) {
    this.floors = floors;
  }

  public List<Floor> getFloors() {
    return Collections.unmodifiableList( floors );
  }
}
