/**
 * Class EvacuationCellularAutomatonInOrder
 * Erstellt 07.07.2008, 01:15:06
 */

package algo.ca;

import algo.ca.rule.EvacuationPlanMovementRule;
import algo.ca.rule.Rule;
import ds.ca.CellularAutomaton;
import evacuationplan.CAPathPassabilityChecker;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonInOrder extends CellularAutomatonInOrderExecution {
	CAPathPassabilityChecker checker;
					
	public EvacuationCellularAutomatonInOrder( CellularAutomaton ca, CAPathPassabilityChecker checker ) {
		super(ca);
		this.checker = checker;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		for( Rule rule : rs ) {
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