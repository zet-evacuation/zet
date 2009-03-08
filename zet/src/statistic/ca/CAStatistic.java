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
