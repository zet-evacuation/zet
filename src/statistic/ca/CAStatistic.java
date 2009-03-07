package statistic.ca;

import java.util.HashMap;
import ds.ca.Room;
import ds.ca.Individual;
import statistic.ca.results.*;
/**
 * @author Matthias Woste
 *
 */

/**
 * This class provides access to all statistic functions concerning the Cellular Automaton for one cycle. 
 */
public class CAStatistic {

	private CellStatisticMethods cs;
        private IndividualStatistic is;

    
	
	public CAStatistic(StoredCAStatisticResults stored){
            cs = new CellStatistic(stored.getStoredCAStatisticResultsForCells());
            is = new IndividualStatistic(stored.getStoredCAStatisticResultsForIndividuals());
	}

	public CellStatisticMethods getCellStatistic() {
		return cs;
	}

	public void setCellStatistic(CellStatisticMethods cs) {
		this.cs = cs;
	}
        
        public void setIndividualStatistic (IndividualStatistic is) {
            this.is = is;
        }

        public IndividualStatistic getIndividualStatistic() {
            return is;
        }
	
}
