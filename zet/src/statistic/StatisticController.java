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
