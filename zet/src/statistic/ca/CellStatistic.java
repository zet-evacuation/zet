/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package statistic.ca;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import ds.ca.evac.EvacCell;
import ds.ca.evac.Room;
import statistic.ca.results.StoredCAStatisticResultsForCells;

/**
 * The CellStatistic class is responsible for storing information about the
 * utilization of a cell. The following information can be extracted:
 * <ul>
 * <li> Does an individual occupies a specific cell at time t?
 * <li> How often was a specific cell occupied until time t?
 * <li> What is the overall utilization a specific cell?
 * </ul>
 *
 * @author Matthias Woste
 */
public class CellStatistic implements CellStatisticMethods {

  /**
   * associate each cell with an ArrayList which contains all timesteps the cell
   * is occupied
   */
  private HashMap<EvacCell, ArrayList<Integer>> cellUtilization;

  /**
   * associate each cell with an ArrayList which contains all timesteps at which
   * an individual standing at the cell is waiting
   */
  private HashMap<EvacCell, ArrayList<Integer>> waitingTime;

  /**
   * stores utilization for each room
   */
  private HashMap<Room, Double> overallRoomUtilization;

  /**
   * stores waiting time for each room
   */
  private HashMap<Room, Double> overallRoomWaitingTime;

  /**
   * The maximal utilization of all cells.
   */
  private int maxUtilization;
  /**
   * The maximal waiting time of all cells.
   */
  private int maxWaiting;

  public CellStatistic( StoredCAStatisticResultsForCells stored ) {
    cellUtilization = stored.getHashMapCellUtilization();
    waitingTime = stored.getHashMapWaitingTime();
    overallRoomUtilization = new HashMap<Room, Double>();
    overallRoomWaitingTime = new HashMap<Room, Double>();
    for( ArrayList<Integer> array : cellUtilization.values() ) {
      maxUtilization = Math.max( maxUtilization, array.size() );
    }
    for( ArrayList<Integer> array : cellUtilization.values() ) {
      maxWaiting = Math.max( maxWaiting, array.size() );
    }
  }

  /**
   * Returns the ArrayList containing the timesteps at which on the cell stands
   * an waiting individual
   *
   * @param c the (@link EvacCell)
   * @return ArrayList of timesteps, null if cell c is not mapped in this
   * statistic
   * @throws IllegalArgumentException
   */
  public ArrayList<Integer> getCellUtilizationStatistic( EvacCell c ) throws IllegalArgumentException {
    if( cellUtilization.containsKey( c ) ) {
      return cellUtilization.get( c );
    }
    return null;
  }

  /**
   * Returns the ArrayList containing the timesteps at which the cell is
   * occupied
   *
   * @param c the (@link EvacCell)
   * @return ArrayList of timesteps, null if cell c is not mapped in this
   * statistic
   * @throws IllegalArgumentException
   */
  public ArrayList<Integer> getCellWaitingStatistic( EvacCell c ) throws IllegalArgumentException {
    if( cellUtilization.containsKey( c ) ) {
      return cellUtilization.get( c );
    }
    return null;
  }

  /* (non-Javadoc)
   * @see statistic.ca.CellStatisticMethods#getCellUtilization(ds.ca.EvacCell, int)
   */
  public int getCellUtilization( EvacCell c, int t ) throws IllegalArgumentException {
    if( cellUtilization.containsKey( c ) ) {
      int index = (Collections.binarySearch( cellUtilization.get( c ), t ));
      if( index < 0 ) {
        index = -index - 2;
      }
      if( index >= 0 ) {
        return index + 1;
      }
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   *
   * @return the maximal utilization
   */
  public int getMaxUtilization() {
    return maxUtilization;
  }

  /**
   * {@inheritDoc}
   *
   * @return the maximal waiting time
   */
  public int getMaxWaiting() {
    return maxUtilization;
  }

  /* (non-Javadoc)
   * @see statistic.ca.CellStatisticMethods#getCellWaitingTime(ds.ca.EvacCell, int)
   */
  public int getCellWaitingTime( EvacCell c, int t ) throws IllegalArgumentException {
    if( waitingTime.containsKey( c ) ) {
      int index = (Collections.binarySearch( waitingTime.get( c ), t ));
      if( index < 0 ) {
        index = -index - 2;
      }
      if( index >= 0 ) {
        return index + 1;
      }
    }
    return 0;
  }

  /**
   * Checks whether a given cell is occupied at a given timestep.
   *
   * @param c the (@link EvacCell)
   * @param t timestep
   * @return true, if the given cell is occupied at the given timestep.
   * Otherwise false is returned.
   */
  public boolean isCellOccupied( EvacCell c, int t ) {
    if( cellUtilization.containsKey( c ) ) {
      return cellUtilization.get( c ).contains( new Integer( t ) );
    }
    return false;
  }

  /* (non-Javadoc)
   * @see statistic.ca.CellStatisticMethods#getOverallCellUtilization(ds.ca.EvacCell, int)
   */
  public double getOverallCellUtilization( EvacCell c, int o ) {
    if( cellUtilization.containsKey( c ) ) {
      return cellUtilization.get( c ).get( cellUtilization.get( c ).size() ) / o;
    }
    return 0.0;
  }

  /* (non-Javadoc)
   * @see statistic.ca.CellStatisticMethods#getOverallWaitingTime(ds.ca.EvacCell, int)
   */
  public double getOverallWaitingTime( EvacCell c, int o ) {
    if( waitingTime.containsKey( c ) ) {
      return waitingTime.get( c ).get( waitingTime.get( c ).size() ) / o;
    }
    return 0.0;
  }

  /* (non-Javadoc)
   * @see statistic.ca.CellStatisticMethods#calculatedOverallSingleRoomUtilization(ds.ca.Room, int)
   */
  public double calculatedOverallSingleRoomUtilization( Room r, int o ) {
    return calculatedSingleRoomUtilization( r, o );
  }

  /* (non-Javadoc)
   * @see statistic.ca.CellStatisticMethods#calculatedSingleRoomUtilization(ds.ca.Room, int)
   */
  public double calculatedSingleRoomUtilization( Room r, int t ) {
    double averageRoomUtilization = 0.0;
    for( EvacCell c : r.getAllCells() ) {
      averageRoomUtilization += getCellUtilization( c, t );
    }
    return (averageRoomUtilization / r.getAllCells().size());
  }

  /* (non-Javadoc)
   * @see statistic.ca.CellStatisticMethods#calculateOverallRoomUtilization(java.util.ArrayList, int)
   */
  public HashMap<Room, Double> calculateOverallRoomUtilization( ArrayList<Room> rooms, int o ) {
    for( Room r : rooms ) {
      if( overallRoomUtilization.get( r ) == null ) {
        overallRoomUtilization.put( r, calculatedOverallSingleRoomUtilization( r, o ) );
      }
    }
    return overallRoomUtilization;
  }

  /* (non-Javadoc)
   * @see statistic.ca.CellStatisticMethods#calculatedOverallSingleRoomWaitingTime(ds.ca.Room, int)
   */
  public double calculatedOverallSingleRoomWaitingTime( Room r, int o ) {
    return calculatedSingleRoomWaitingTime( r, o );
  }

  /* (non-Javadoc)
   * @see statistic.ca.CellStatisticMethods#calculatedSingleRoomWaitingTime(ds.ca.Room, int)
   */
  public double calculatedSingleRoomWaitingTime( Room r, int t ) {
    double averageRoomWaitingTime = 0.0;
    for( EvacCell c : r.getAllCells() ) {
      averageRoomWaitingTime += getCellWaitingTime( c, t );
    }
    return (averageRoomWaitingTime / r.getAllCells().size());
  }

  /* (non-Javadoc)
   * @see statistic.ca.CellStatisticMethods#calculateOverallRoomWaitingTime(java.util.ArrayList, int)
   */
  public HashMap<Room, Double> calculateOverallRoomWaitingTime( ArrayList<Room> rooms, int o ) {
    for( Room r : rooms ) {
      if( overallRoomWaitingTime.get( r ) == null ) {
        overallRoomWaitingTime.put( r, calculatedOverallSingleRoomWaitingTime( r, o ) );
      }
    }
    return overallRoomWaitingTime;
  }
}
