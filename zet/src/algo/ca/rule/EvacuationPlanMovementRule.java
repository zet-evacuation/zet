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
package algo.ca.rule;

import java.util.ArrayList;
import java.util.Iterator;

import ds.ca.evac.Cell;
import evacuationplan.CAPathPassabilityChecker;

public class EvacuationPlanMovementRule extends WaitingMovementRule {
	CAPathPassabilityChecker checker;

	public void setChecker( CAPathPassabilityChecker checker ) {
		this.checker = checker;
	}

	@Override
	protected ArrayList<Cell> computePossibleTargets( Cell fromCell, boolean onlyFreeNeighbours ) {
		ArrayList<Cell> targets = super.computePossibleTargets( fromCell, onlyFreeNeighbours );
		Iterator<Cell> it = targets.iterator();
		while( it.hasNext() ) {
			Cell cell = it.next();
			if( cell != fromCell ) {
				if( !checker.canPass( fromCell.getIndividual(), fromCell, cell ) ) {
					it.remove();
				}
			}
		}
		return targets;
	}
}
