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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algo.ca.algorithm;

import de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton.CellularAutomatonSimulationProblem;
import de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton.CellularAutomatonSimulationResult;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;

/**
 *
 * @param <U>
 * @param <V>
 * @author Jan-Philipp Kappmeier
 */
public abstract class CellularAutomatonAlgorithm<U extends CellularAutomatonSimulationProblem, V extends CellularAutomatonSimulationResult> extends Algorithm<U, V> {
	public abstract void initialize();
}
