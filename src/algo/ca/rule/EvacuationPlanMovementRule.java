package algo.ca.rule;

import java.util.ArrayList;
import java.util.Iterator;

import ds.ca.Cell;
import evacuationplan.CAPathPassabilityChecker;

public class EvacuationPlanMovementRule extends WaitingMovementRule {
	CAPathPassabilityChecker checker;

	public void setChecker( CAPathPassabilityChecker checker ) {
		this.checker = checker;
	}

	@Override
	protected ArrayList<Cell> selectPossibleTargets( Cell fromCell, boolean onlyFreeNeighbours ) {
		ArrayList<Cell> targets = super.selectPossibleTargets( fromCell, onlyFreeNeighbours );
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
