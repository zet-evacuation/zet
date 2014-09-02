/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package algo.ca.algorithm.evac;

import algo.ca.util.IndividualDistanceComparator;
import ds.ca.evac.Individual;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class {@code EvacuationCellularAutomatonFrontToBack} ...
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonFrontToBack extends algo.ca.framework.EvacuationCellularAutomatonAlgorithm {

	/**
	 * Sorts the (living and not save) individuals within the cellular automaton
	 * by increasing distance to the exit and returns a list of this individuals.
	 * @return the ordered list of individuals
	 */
	@Override
	public List<Individual> getIndividuals() {
		List<Individual> copy = new ArrayList<>( getProblem().eca.getIndividuals() );
		IndividualDistanceComparator<Individual> idc = new IndividualDistanceComparator<>();
		Collections.sort( copy, idc );
		return Collections.unmodifiableList( copy );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "EvacuationCellularAutomatonFrontToBack";
	}
}
