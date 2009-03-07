

package statistic.ca.results;

/**
 *
 * @author Sylvie
 */

import java.util.HashMap;
import java.util.ArrayList;
import ds.ca.Cell;
import ds.ca.Room;
import gui.EditorStart;

/**
 *
 * @author Sylvie
 */
public class StoredCAStatisticResultsForCells {




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
        
        
    public StoredCAStatisticResultsForCells(){
        
		cellUtilization = new HashMap<Cell,ArrayList<Integer>>();
		waitingTime = new HashMap<Cell, ArrayList<Integer>>();
    }
    
	
	/**
	 * Is invoked if an individual occupied a cell c in timestep t and stores this values.
	 * @param c cell occupied
	 * @param t timestep
	 */
	public void addCellToUtilizationStatistic(Cell c, int t){
		if( !EditorStart.useStatistic ) 
			return;
		
		if(cellUtilization.containsKey(c)){
			cellUtilization.get(c).add(t);
		} else {
			cellUtilization.put(c, new ArrayList<Integer>());
			cellUtilization.get(c).add(t);
		}
	}
	

	
	/**
	 * Is invoked if an individual occupied a cell c in timestep t and is waiting.
	 * @param c cell occupied
	 * @param t timestep
	 */
	public void addCellToWaitingStatistic(Cell c, int t){
		if( !EditorStart.useStatistic ) 
			return;
		if(waitingTime.containsKey(c)){
			waitingTime.get(c).add(t);
		} else {
			waitingTime.put(c, new ArrayList<Integer>());
			waitingTime.get(c).add(t);
		}
	}
	
        
    public HashMap<Cell, ArrayList<Integer>> getHashMapCellUtilization() {
        return cellUtilization;
    }


    public HashMap<Cell, ArrayList<Integer>> getHashMapWaitingTime() {
        return waitingTime;
    }        

 
    
 
}//end class


