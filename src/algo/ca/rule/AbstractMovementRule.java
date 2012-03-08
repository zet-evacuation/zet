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
 * Created on 23.01.2008
 *
 */
package algo.ca.rule;

import ds.ca.evac.Cell;
import ds.ca.evac.Individual;
import java.util.ArrayList;

/**
 * @author Jan-Philipp Kappmeier
 *
 */
public abstract class AbstractMovementRule extends AbstractRule {
	private boolean directExecute;
	private boolean moveCompleted;
	private ArrayList<Cell> possibleTargets;
	
	public AbstractMovementRule() {
		directExecute = true;
		moveCompleted = false;
	}
	
	public boolean isDirectExecute() {
		return directExecute;
	}

	public void setDirectExecute( boolean directExecute ) {
		this.directExecute = directExecute;
	}

	public boolean isMoveCompleted() {
		return moveCompleted;
	}

	protected void setMoveRuleCompleted( boolean moveCompleted ) {
		this.moveCompleted = moveCompleted;
	}

	protected ArrayList<Cell> selectPossibleTargets( Cell fromCell, boolean onlyFreeNeighbours ) {
		return onlyFreeNeighbours ? fromCell.getFreeNeighbours() : fromCell.getNeighbours();
	}

	protected void setPossibleTargets( ArrayList<Cell> possibleTargets ) {
		this.possibleTargets = possibleTargets;
	}
	
	/**
	 * Returns the possible targets already sorted by priority.
	 * @return a list of possible targets.
	 */
	public ArrayList<Cell> getPossibleTargets() {
		return possibleTargets;
	}
	
	/**
	 * In this simple implementation always the first possible cell is returned.
	 * As this method should be overridden, a warning is printed to the err log
	 * if it is used.
	 * @param cell not used in the simple imlementation
	 * @param targets possible targets (only the first one is used)
	 * @return the first cell of the possible targets
	 */
	public Cell selectTargetCell( Cell cell, ArrayList<Cell> targets ) {
		System.err.println( "WARNUNG nicht überschriebene target cell selection wird ausgeführt" );
		return targets.get( 0 );
	}
	
	public abstract void move( Individual i, Cell target );
	
	public abstract void swap( Cell cell1, Cell cell2 );
}
