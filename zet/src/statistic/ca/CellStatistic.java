package statistic.ca;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import ds.ca.Cell;
import ds.ca.Room;
import statistic.ca.results.StoredCAStatisticResultsForCells;

/**
 * @author Matthias Woste
 *
 */

/**
 * The CellStatistic class is responsible for storing information about the utilization of a cell.
 * The following information can be extracted:
 * <ul>
 * <li> Does an individual occupies a specific cell at time t?
 * <li> How often was a specific cell occupied until time t?
 * <li> What is the overall utilization a specific cell?
 * </ul>
 */
public class CellStatistic implements CellStatisticMethods {

	/**
	 * associate each cell with an ArrayList which contains all timesteps the cell is occupied 
	 */
	private HashMap<Cell, ArrayList<Integer>> cellUtilization;
	
	/**
	 * associate each cell with an ArrayList which contains all timesteps at which an individual
	 * standing at the cell is waiting
	 */
	private HashMap<Cell, ArrayList<Integer>> waitingTime;
	
	/** stores utilization for each room */
	private HashMap<Room, Double> overallRoomUtilization;
	
	/** stores waiting time for each room */
	private HashMap<Room, Double> overallRoomWaitingTime;
	
	/** The maximal utilization of all cells. */
	private int maxUtilization;
	/** The maximal waiting time of all cells. */
	private int maxWaiting;
	
	public CellStatistic(StoredCAStatisticResultsForCells stored){
		cellUtilization = stored.getHashMapCellUtilization();
		waitingTime = stored.getHashMapWaitingTime();
		overallRoomUtilization = new HashMap<Room, Double>();
		overallRoomWaitingTime = new HashMap<Room, Double>();
		for( ArrayList<Integer> array : cellUtilization.values() )
			maxUtilization = Math.max( maxUtilization, array.size() );
		for( ArrayList<Integer> array : cellUtilization.values() )
			maxWaiting = Math.max( maxWaiting, array.size() );
	}
	
	/**
	 * Returns the ArrayList containing the timesteps at which on the cell stands an waiting individual
	 * @param c the (@link Cell)
	 * @return ArrayList of timesteps, null if cell c is not mapped in this statistic
	 * @throws IllegalArgumentException 
	 */
	public ArrayList<Integer> getCellUtilizationStatistic(Cell c) throws IllegalArgumentException{
		if(cellUtilization.containsKey(c)){
			return cellUtilization.get(c);
		}
		return null;
	}
	
	/**
	 * Returns the ArrayList containing the timesteps at which the cell is occupied
	 * @param c the (@link Cell)
	 * @return ArrayList of timesteps, null if cell c is not mapped in this statistic
	 * @throws IllegalArgumentException 
	 */
	public ArrayList<Integer> getCellWaitingStatistic(Cell c) throws IllegalArgumentException{
		if(cellUtilization.containsKey(c)){
			return cellUtilization.get(c);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see statistic.ca.CellStatisticMethods#getCellUtilization(ds.za.Cell, int)
	 */
	public int getCellUtilization(Cell c, int t) throws IllegalArgumentException{
		if(cellUtilization.containsKey(c)) {
                    int index= (Collections.binarySearch(cellUtilization.get(c),t));
                    if (index<0) {
                        index = -index-2;
                    }
                    if (index >=0) {
                        return index+1;
                    }   
                }
                return 0;
        }
	
	/**
	 * {@inheritDoc}
	 * @return the maximal utilization
	 */
	public int getMaxUtilization() {
		return maxUtilization;
	}

	/**
	 * {@inheritDoc}
	 * @return the maximal waiting time
	 */
	public int getMaxWaiting() {
		return maxUtilization;
	}

	/* (non-Javadoc)
	 * @see statistic.ca.CellStatisticMethods#getCellWaitingTime(ds.za.Cell, int)
	 */
	public int getCellWaitingTime(Cell c, int t) throws IllegalArgumentException{
		if(waitingTime.containsKey(c)){
                    int index= (Collections.binarySearch(waitingTime.get(c),t));
                    if (index<0) {
                        index = -index-2;
                    }
                    if (index >=0) {
                        return index+1;
                    }   
                }
                return 0;
        }
	
	/**
	 * Checks whether a given cell is occupied at a given timestep.
	 * @param c the (@link Cell)
	 * @param t timestep
	 * @return true, if the given cell is occupied at the given timestep. Otherwise false is returned.
	 */
	public boolean isCellOccupied(Cell c, int t){
		if(cellUtilization.containsKey(c)){
			return cellUtilization.get(c).contains(new Integer(t));
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see statistic.ca.CellStatisticMethods#getOverallCellUtilization(ds.za.Cell, int)
	 */
	public double getOverallCellUtilization(Cell c, int o){
		if(cellUtilization.containsKey(c)){
			return cellUtilization.get(c).get(cellUtilization.get(c).size())/o;
		}
		return 0.0; 
	}
	
	/* (non-Javadoc)
	 * @see statistic.ca.CellStatisticMethods#getOverallWaitingTime(ds.za.Cell, int)
	 */
	public double getOverallWaitingTime(Cell c, int o){
		if(waitingTime.containsKey(c)){
			return waitingTime.get(c).get(waitingTime.get(c).size())/o;
		}
		return 0.0; 
	}

	/* (non-Javadoc)
	 * @see statistic.ca.CellStatisticMethods#calculatedOverallSingleRoomUtilization(ds.za.Room, int)
	 */
	public double calculatedOverallSingleRoomUtilization(Room r, int o){
		return calculatedSingleRoomUtilization(r,o);
	}
	
	/* (non-Javadoc)
	 * @see statistic.ca.CellStatisticMethods#calculatedSingleRoomUtilization(ds.za.Room, int)
	 */
	public double calculatedSingleRoomUtilization(Room r, int t){
		double averageRoomUtilization = 0.0;
		for(Cell c : r.getAllCells()){
			averageRoomUtilization += getCellUtilization(c, t);
		}
		return (averageRoomUtilization / r.getAllCells().size());
	}
	
	/* (non-Javadoc)
	 * @see statistic.ca.CellStatisticMethods#calculateOverallRoomUtilization(java.util.ArrayList, int)
	 */
	public HashMap<Room, Double> calculateOverallRoomUtilization(ArrayList<Room> rooms, int o){
		for(Room r : rooms){
			if(overallRoomUtilization.get(r) == null){
				overallRoomUtilization.put(r, calculatedOverallSingleRoomUtilization(r,o));
			}
		}
		return overallRoomUtilization;
	}
	
	/* (non-Javadoc)
	 * @see statistic.ca.CellStatisticMethods#calculatedOverallSingleRoomWaitingTime(ds.za.Room, int)
	 */
	public double calculatedOverallSingleRoomWaitingTime(Room r, int o){
		return calculatedSingleRoomWaitingTime(r,o);
	}
	
	/* (non-Javadoc)
	 * @see statistic.ca.CellStatisticMethods#calculatedSingleRoomWaitingTime(ds.za.Room, int)
	 */
	public double calculatedSingleRoomWaitingTime(Room r, int t){
		double averageRoomWaitingTime = 0.0;
		for(Cell c : r.getAllCells()){
			averageRoomWaitingTime += getCellWaitingTime(c, t);
		}
		return (averageRoomWaitingTime / r.getAllCells().size());
	}
	
	/* (non-Javadoc)
	 * @see statistic.ca.CellStatisticMethods#calculateOverallRoomWaitingTime(java.util.ArrayList, int)
	 */
	public HashMap<Room, Double> calculateOverallRoomWaitingTime(ArrayList<Room> rooms, int o){
		for(Room r : rooms){
			if(overallRoomWaitingTime.get(r) == null){
				overallRoomWaitingTime.put(r, calculatedOverallSingleRoomWaitingTime(r,o));
			}
		}
		return overallRoomWaitingTime;
	}
}
