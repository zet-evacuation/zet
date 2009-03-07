package statistic;

import statistic.ca.CAStatistic;

/**
 * @author Matthias Woste
 *
 */

/**
 * The StatisticController provides access to all static functions implemented. All functions are partitioned
 * into two groups resp. object. Via {@link getCellStatistic()} one will obtain an object of type 
 * {@link CAStatistikController} which is responsible for all statistics concerning
 * the Cellular Automaton. To get access to the graph statistics using an object of type ??? call get???.
 */
public class StatisticController {

	/**
	 * the CAStatisticController object
	 */
	private CAStatistic caStatistic;
	
        //Klasse wird nicht benutzt
	/*public StatisticController(){
		caStatistic = new CAStatistic();
	}

	public CAStatistic getCAStatisticController() {
		return caStatistic;
	}	*/
	
}
