/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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

import java.util.ArrayList;
import java.util.HashMap;

import ds.ca.Cell;
import ds.ca.Room;

public interface CellStatisticMethods {

	
	/**
	 * Returns the utilization of the given cell at the given timestep
	 * @param c the (@link Cell)
	 * @param t timestep
	 * @return utilization up to the timestep, 0 if no utilization at all
	 */
	public abstract int getCellUtilization(Cell c, int t)
			throws IllegalArgumentException;
	
	/**
	 * Returns the maximal utilization value for all cells.
	 * @return the maximal utilization value for all cells.
	 */
	public int getMaxUtilization();

	/**
	 * Returns the maximal waiting time for all cells.
	 * @return the maximal waiting time for all cells.
	 */
	public int getMaxWaiting();
	
	/**
	 * Returns the waiting time of the given cell at the given timestep
	 * @param c the (@link Cell)
	 * @param t timestep
	 * @return utilization up to the timestep, 0 if no utilization at all
	 */
	public abstract int getCellWaitingTime(Cell c, int t)
			throws IllegalArgumentException;

	/**
	 * Returns the overall utilization of a given cell. 
	 * @param c the Cell
	 * @param o the total amount of timesteps
	 * @return the overall utilization
	 */
	public abstract double getOverallCellUtilization(Cell c, int o);

	/**
	 * Returns the overall waiting time of a given cell. 
	 * @param c the Cell
	 * @param o the total amount of timesteps
	 * @return the overall waiting time
	 */
	public abstract double getOverallWaitingTime(Cell c, int o);

	public abstract double calculatedOverallSingleRoomUtilization(Room r, int o);

	public abstract double calculatedSingleRoomUtilization(Room r, int t);

	public abstract HashMap<Room, Double> calculateOverallRoomUtilization(
			ArrayList<Room> rooms, int o);

	public abstract double calculatedOverallSingleRoomWaitingTime(Room r, int o);

	public abstract double calculatedSingleRoomWaitingTime(Room r, int t);

	public abstract HashMap<Room, Double> calculateOverallRoomWaitingTime(
			ArrayList<Room> rooms, int o);

}