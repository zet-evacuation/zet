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

/**
 * Class EvacuationPlanCellularAutomatonInOrder
 * Erstellt 07.07.2008, 01:15:06
 */

package algo.ca;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonInOrder;
import algo.ca.rule.EvacuationPlanMovementRule;
import algo.ca.rule.Rule;
import evacuationplan.CAPathPassabilityChecker;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationPlanCellularAutomatonInOrder extends EvacuationCellularAutomatonInOrder {
	CAPathPassabilityChecker checker;
					
	public EvacuationPlanCellularAutomatonInOrder( CAPathPassabilityChecker checker ) {
		this.checker = checker;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		for( Rule rule : getProblem().ruleSet ) {
			if( rule instanceof EvacuationPlanMovementRule ) {
				((EvacuationPlanMovementRule)rule).setChecker( checker );
			}
		}
	}
	
	@Override
	public String toString() {
		return "EvacuationCellularAutomatonInOrder";
	}
}