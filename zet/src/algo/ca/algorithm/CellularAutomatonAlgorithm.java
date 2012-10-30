/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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

package algo.ca.algorithm;

import de.tu_berlin.math.coga.algorithm.simulation.SimulationAlgorithm;
import de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton.CellularAutomatonSimulationProblem;
import de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton.CellularAutomatonSimulationResult;

/**
 *
 * @param <U>
 * @param <V>
 * @author Jan-Philipp Kappmeier
 */
public abstract class CellularAutomatonAlgorithm<U extends CellularAutomatonSimulationProblem, V extends CellularAutomatonSimulationResult> extends SimulationAlgorithm<U, V> {
	public abstract void initialize();
}
