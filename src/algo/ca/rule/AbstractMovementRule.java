/*
 * Created on 23.01.2008
 *
 */
package algo.ca.rule;

import ds.ca.Cell;
import ds.ca.Individual;
import java.util.ArrayList;

/**
 * @author Jan-Philipp Kappmeier
 *
 */
public abstract class AbstractMovementRule extends AbstractRule {
	private boolean directExecute;
	private boolean performMove;
	private ArrayList<Cell> possibleTargets;
	
	public AbstractMovementRule() {
		directExecute = true;
		performMove = false;
	}
	
	public boolean isDirectExecute() {
		return directExecute;
	}

	public void setDirectExecute( boolean directExecute ) {
		this.directExecute = directExecute;
	}

	public boolean performMove() {
		return performMove;
	}

	protected void setPerformMove( boolean performMove ) {
		this.performMove = performMove;
	}

	protected ArrayList<Cell> selectPossibleTargets( Cell fromCell, boolean onlyFreeNeighbours ) {
		if( onlyFreeNeighbours )
			return fromCell.getFreeNeighbours();
		else
			return fromCell.getNeighbours();
	}

	protected void setPossibleTargets( ArrayList<Cell> possibleTargets ) {
		this.possibleTargets = possibleTargets;
	}
	
	/**
	 * Returns the possible targets already sorted by priority.
	 * @return
	 */
	public ArrayList<Cell> getPossibleTargets() {
		return possibleTargets;
	}
	
	/**
	 * 
	 * @param cell 
	 * @param targets
	 * @return
	 */
	public Cell selectTargetCell( Cell cell, ArrayList<Cell> targets ) {
		System.err.println( "WARNUNG nicht überschriebene target cell selection wird ausgeführt" );
		return targets.get(0);
	}
	
	public abstract void move( Individual i, Cell target );
	
	public abstract void swap( Cell cell1, Cell cell2 );
}
