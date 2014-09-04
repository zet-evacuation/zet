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

import de.tu_berlin.coga.common.util.Direction8;
import de.tu_berlin.coga.common.util.Level;
import ds.ca.evac.EvacCell;
import ds.ca.evac.DoorCell;
import ds.ca.evac.Individual;
import ds.ca.evac.StairCell;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jan-Philipp Kappmeier
 *
 */
public abstract class AbstractMovementRule extends AbstractRule {
	protected double speed;
	protected double dist;
	protected Individual ind;
	private boolean directExecute;
	private boolean moveCompleted;
	private ArrayList<EvacCell> possibleTargets;

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

	/**
	 * Computes and returns possible targets and also sets them, such that they
	 * can be retrieved using {@link #getPossibleTargets() }.
	 * @param fromCell
	 * @param onlyFreeNeighbours
	 * @return
	 */
	protected List<EvacCell> computePossibleTargets( EvacCell fromCell, boolean onlyFreeNeighbours ) {
		possibleTargets = new ArrayList<>();
		ArrayList<EvacCell> neighbors = onlyFreeNeighbours ? fromCell.getFreeNeighbours() : fromCell.getNeighbours();

		Direction8 dir = fromCell.getIndividual().getDirection();

		for( EvacCell c : neighbors ) {
			if( ind.isSafe() && !((c instanceof ds.ca.evac.SaveCell) || (c instanceof ds.ca.evac.ExitCell)) )
				continue; // ignore all moves that would mean walking out of safe areas

			if( fromCell instanceof DoorCell && c instanceof DoorCell ) {
				possibleTargets.add( c );
				continue;
			}
			Direction8 rel = fromCell.getRelative( c );
			if( dir == rel )
				possibleTargets.add( c );
			else if( dir == rel.getClockwise() )
				possibleTargets.add( c );
			else if( dir == rel.getClockwise().getClockwise() )
				possibleTargets.add( c );
			else if( dir == rel.getCounterClockwise() )
				possibleTargets.add( c );
			else if( dir == rel.getCounterClockwise().getCounterClockwise() )
				possibleTargets.add( c );
		}
		return possibleTargets;
	}

	protected void setPossibleTargets( ArrayList<EvacCell> possibleTargets ) {
		this.possibleTargets = possibleTargets;
	}

	/**
	 * Returns the possible targets already sorted by priority. The possible
	 * targets either have been set before using {@link #setPossibleTargets(java.util.ArrayList) }
	 * ore been computed using {@link #getPossibleTargets(ds.ca.evac.EvacCell, boolean) }.
	 * @return a list of possible targets.
	 */
	public List<EvacCell> getPossibleTargets() {
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
	public EvacCell selectTargetCell( EvacCell cell, List<EvacCell> targets ) {
		System.err.println( "WARNUNG nicht überschriebene target cell selection wird ausgeführt" );
		return targets.get( 0 );
	}

	protected Direction8 getMovementDirection( EvacCell start, EvacCell target ) {
		Direction8 d = start.getRelative( target );
		return d;
	}

			// Calculate a factor that is later multiplied with the speed,
		// this factor is only != 1 for stair cells to
		// give different velocities for going a stair up or down.
	protected double getStairSpeedFactor( Direction8 direction, StairCell stairCell ) {
		double stairSpeedFactor = 1;
			Level lvl = stairCell.getLevel( direction );
			if( lvl == Level.Higher )
				stairSpeedFactor = stairCell.getSpeedFactorUp();
			else if( lvl == Level.Lower )
				stairSpeedFactor = stairCell.getSpeedFactorDown();
		return stairSpeedFactor;
	}

	protected double getSwayDelay( Individual ind, Direction8 direction ) {
		if( ind.getDirection() == direction )
			return 0;
		else if( ind.getDirection() == direction.getClockwise() || ind.getDirection() == direction.getCounterClockwise() )
			return 0.5;
		else if( ind.getDirection() == direction.getClockwise().getClockwise() || ind.getDirection() == direction.getCounterClockwise().getCounterClockwise() )
			return 1;
		else
			throw new IllegalStateException( "Change direction by more than 90 degrees." );
	}

	/**
	 * Sets the time when the current movement is over for an individual and
	 * actualizates the needed time in the cellular automaton.
	 * @param i the individual
	 * @param d the (real) time when the movement is over
	 */
	protected void setStepEndTime( Individual i, double d ) {
		i.setStepEndTime( d );
		esp.eca.setNeededTime( (int) Math.ceil( d ) );
	}

  public final void move( EvacCell target ) {
    move( ind.getCell(), target );
  }

  public abstract void move( EvacCell from, EvacCell target );

  public abstract void swap( EvacCell cell1, EvacCell cell2 );
}
