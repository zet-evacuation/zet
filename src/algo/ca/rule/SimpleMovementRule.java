/*
 * SimpleMovementRule.java
 * Created on 26.01.2008, 17:27:04
 */
package algo.ca.rule;

import ds.ca.Cell;
import ds.ca.Individual;
import java.util.ArrayList;
import util.random.RandomUtils;

/**
 * A simple movement rule that does not care about anything like slack, speed,
 * panic or anything else. Steps are always performed, there is no special
 * behaviour on {@link isDirectExecute()}.
 * @author Jan-Philipp Kappmeier
 */
public class SimpleMovementRule extends AbstractMovementRule {
	/**
	 * 
	 * @param cell
	 * @return
	 */
	@Override
	public boolean executableOn( Cell cell ) {
		return cell.getIndividual() != null;
	}

	/**
	 * 
	 * @param cell
	 */
	@Override
	protected void onExecute( Cell cell ) {
		Cell targetCell = selectTargetCell( cell, selectPossibleTargets( cell, true ) );
		
		if( cell.equals( targetCell) )
			return;

		this.move( cell.getIndividual(), targetCell );
	}

	public void move( Individual i, Cell targetCell ) {
//		if( i.getCell().getRoom() != targetCell.getRoom() )
			caController().getCA().moveIndividual( i.getCell(), targetCell );
			//i.getCell().getRoom().moveIndividual( i.getCell(), targetCell );
//		else
//			i.getCell().getRoom().moveIndividual( i.getCell(), targetCell );
	}

	@Override
	public Cell selectTargetCell( Cell cell, ArrayList<Cell> targets ) {
		if( targets.size() == 0 )
			return cell;

		double p[] = new double[targets.size()];
		for( int i = 0; i < targets.size(); i++ )
			p[i] = Math.exp( parameters.effectivePotential( cell, targets.get( i ) ) );

		return targets.get( RandomUtils.getInstance().chooseRandomlyAbsolute( p ) );
	}

	@Override
	public void swap( Cell cell1, Cell cell2 ) {
		throw new UnsupportedOperationException( "Not supported." );
	}
}
