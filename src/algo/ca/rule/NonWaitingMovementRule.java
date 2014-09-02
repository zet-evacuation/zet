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
package algo.ca.rule;

import ds.ca.evac.EvacCell;
import java.util.List;

/**
 * A movement rule with which the individuals try to move, if possible.
 * @author Jan-Philipp Kappmeier
 */
public class NonWaitingMovementRule extends WaitingMovementRule {

	/**
	 * Selects the possible targets excluding the current cell.
	 * @param fromCell the current sell
	 * @param onlyFreeNeighbours indicates whether only free neighbours or all neighbours are included
	 * @return a list containing all neighbours and the from cell
	 */
	@Override
	protected List<EvacCell> computePossibleTargets( EvacCell fromCell, boolean onlyFreeNeighbours ) {
		List<EvacCell> targets = super.computePossibleTargets( fromCell, onlyFreeNeighbours );
		targets.remove( fromCell );
		return targets;
	}
}