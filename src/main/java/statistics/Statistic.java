/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package statistics;

import org.zetool.common.datastructure.SimpleTuple;
import statistics.collectors.AgeCollector;
import statistics.collectors.SpecificFlowCollector;
import statistics.collectors.SpeedCollector;

/**
 * A general class that collects data. Which data is collected can be
 * enabled or disables. It also supports listener for the data.
 * @author Jan-Philipp Kappmeier
 */
public class Statistic {

	private int numberOfPersons;
	AgeCollector ageCollector;
	SpeedCollector speedCollector;
	SpecificFlowCollector specificFlowCollector;
	public static Statistic instance = new Statistic();
	private int ageTries = 0;

	/**
	 * Make singleton for the moment.
	 */
	private Statistic() {
		ageCollector = new AgeCollector();
		speedCollector = new SpeedCollector();
		specificFlowCollector = new SpecificFlowCollector();
	}

	/**
	 * Call this method if an individual with the given age was created.
	 * @param age
	 */
	public void collectAge( double age ) {
		ageCollector.add( age );
	}

	public void collectAge( double age, int tries ) {
		ageCollector.add( age );
		ageTries += tries;
	}

	public void collectAgeSpeed( double age, double speed ) {
		speedCollector.add( new SimpleTuple( age, speed ) );
	}

	/**
	 * Returns the age collector
	 * @return
	 */
	public AgeCollector getAgeCollector() {
		return ageCollector;
	}

	public void addPerson() {
		numberOfPersons++;
	}

	/**
	 * Returns the number of persons stored in the statistic.
	 * @return the number of persons stored in the statistic
	 */
	public int getNumberOfPersons() {
		return numberOfPersons;
	}

	public int getAgeTries() {
		return ageTries;
	}

	public SpecificFlowCollector getSpecificFlowCollector() {
		return specificFlowCollector;
	}



	

}
