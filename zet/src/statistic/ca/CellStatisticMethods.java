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