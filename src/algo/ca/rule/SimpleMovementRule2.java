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
import ds.ca.evac.Cell;
import ds.ca.evac.Individual;
import ds.ca.evac.StairCell;
import ds.ca.results.VisualResultsRecorder;
import ds.ca.results.IndividualStateChangeAction;
import util.DebugFlags;
import de.tu_berlin.math.coga.common.util.Direction;
import de.tu_berlin.math.coga.common.util.Level;

/**
 *
 * @author Sylvie Temme
 */
public class SimpleMovementRule2 extends AbstractMovementRule {

	public SimpleMovementRule2() {
	}

	/**
	 * Decides whether the rule can be applied to the current cell. 
	 * Returns {@code true} if the cell is occupied by an individual
	 * or {@code false} otherwise.
	 * @param cell
	 * @return true if the rule can be executed
	 */
	@Override
	public boolean executableOn( ds.ca.evac.Cell cell ) {
		// Regel nicht anwendbar, wenn auf der Zelle kein Individuum steht.
		return cell.getIndividual() != null;
	}

	@Override
	protected void onExecute( ds.ca.evac.Cell cell ) {
		if( DebugFlags.EVAPLANCHECKER )
			System.out.print( "Move individual " + cell.getIndividual().id() + " " );
		Individual actor = cell.getIndividual();

		if( actor.isAlarmed() ) {
			if( canMove( actor ) )
				if( isDirectExecute() ) {
					Cell targetCell = this.selectTargetCell( cell, selectPossibleTargets( cell, true ) );
					setMoveRuleCompleted( true );
					move( actor, targetCell );
				} else {
					this.setPossibleTargets( selectPossibleTargets( cell, false ) );
					setMoveRuleCompleted( true );
				}
			else
				// Individual can't move, it is already moving
				setMoveRuleCompleted( false ); // TODO why is here false?
		} else { // Individual is not alarmed, that means it remains standing on the cell			
			setMoveRuleCompleted( true );
			doMove( actor, cell );
		}

		VisualResultsRecorder.getInstance().recordAction( new IndividualStateChangeAction( actor ) );
	}

	public void move( Individual i, Cell targetCell ) {
		if( i.isSafe() && !((targetCell instanceof ds.ca.evac.SaveCell) || (targetCell instanceof ds.ca.evac.ExitCell)) )
			// Rauslaufen aus sicheren Bereichen ist nicht erlaubt // TODO verschiebe woanders hin. Macht sinn, bei selection evtl.
			targetCell = i.getCell();
		if( i.getCell().equals( targetCell ) ) {
			esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addWaitedTimeToStatistic( i, esp.eca.getTimeStep() );
			esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToWaitingStatistic( targetCell, esp.eca.getTimeStep() );
		}
		//set statistic for targetCell and timestep
		esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToUtilizationStatistic( targetCell, esp.eca.getTimeStep() );
		doMove( i, targetCell );
		setMoveRuleCompleted( false );
	}

	private void doMove( Individual i, Cell targetCell ) {
		if( i.getCell().equals( targetCell ) ) {
			i.setStepStartTime( i.getStepEndTime() );
			setStepEndTime( i, i.getStepEndTime() + 1 );
			esp.eca.moveIndividual( i.getCell(), targetCell );
			setMoveRuleCompleted( false );
			esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic( i, esp.eca.getTimeStep(), 0 );
			return;
		}

		doMoveWithDecision( i, targetCell, true );
		setMoveRuleCompleted( false );
	}

	private void doMoveWithDecision( Individual i, Cell targetCell, boolean performMove ) {
		this.esp.potentialController.increaseDynamicPotential( targetCell );
		// Calculate a factor that is later multiplied with the speed,
		// this factor is only != 1 for stair cells to 
		// give different velocities for going a stair up or down.
		double stairSpeedFactor = 1;
		if( targetCell instanceof StairCell ) {

			StairCell stairCell = (StairCell) targetCell;
			int x = targetCell.getX() - i.getCell().getX();
			int y = targetCell.getY() - i.getCell().getY();
			Direction direction = Direction.getDirection( x, y );
			Level lvl = stairCell.getLevel( direction );
			if( lvl == Level.Higher )
				stairSpeedFactor = stairCell.getSpeedFactorUp();
			else if( lvl == Level.Lower )
				stairSpeedFactor = stairCell.getSpeedFactorDown();
		}

		// TODO check if this big stuff is really necessery! maybe easier!
		// calculate distance
		double dist;
		final double sqrt2 = Math.sqrt( 2 ) * 0.4;
		if( !targetCell.getRoom().equals( i.getCell().getRoom() ) )
			if( i.getCell().getX() + i.getCell().getRoom().getXOffset() == targetCell.getX() + targetCell.getRoom().getXOffset() && i.getCell().getY() + i.getCell().getRoom().getYOffset() == targetCell.getY() + targetCell.getRoom().getYOffset() ) {
				System.err.println( "SelfCell reached or Stockwerkwechsel!" );
				dist = 0.4;
			} else if( i.getCell().getX() + i.getCell().getRoom().getXOffset() == targetCell.getX() + targetCell.getRoom().getXOffset() | i.getCell().getY() + i.getCell().getRoom().getYOffset() == targetCell.getY() + targetCell.getRoom().getYOffset() )
				dist = 0.4;
			else
				dist = sqrt2;
		else
			if( i.getCell().getX() == targetCell.getX() && i.getCell().getY() == targetCell.getY() )
				dist = 0;
			else if( i.getCell().getX() == targetCell.getX() | i.getCell().getY() == targetCell.getY() )
				dist = 0.4;
			else
				dist = sqrt2;

		// update times
		if( esp.eca.absoluteSpeed( i.getCurrentSpeed() ) >= 0.0001 ) {
			double speed = esp.eca.absoluteSpeed( i.getCurrentSpeed() );
			speed *= targetCell.getSpeedFactor() * stairSpeedFactor;
			//System.out.println( "Speed ist " + speed );
			// zu diesem zeitpunkt ist die StepEndtime aktualisiert, falls ein individual vorher geslackt hat
			// oder sich nicht bewegen konnte.
			i.setStepStartTime( i.getStepEndTime() );
			setStepEndTime( i, i.getStepEndTime() + (dist / speed) * esp.eca.getStepsPerSecond() );
			if( performMove ) {
				esp.eca.moveIndividual( i.getCell(), targetCell );
				esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic( i, esp.eca.getTimeStep(), speed * esp.eca.getSecondsPerStep() );
				esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCoveredDistanceToStatistic( i, (int) Math.ceil( i.getStepEndTime() ), dist );
			} else
				if( util.DebugFlags.CA_SWAP_USED_OUTPUT )
					System.err.println( "Individuum Läuft doch nicht!!" );
		} else
			throw new IllegalStateException( "Individuum has no speed." );
	}

	/**
	 * Given a starting cell, this method picks one 
	 * of its reachable neighbours at random. The i-th neighbour is 
	 * chosen with probability {@code p(i) := N * exp[mergePotentials(i, cell)]}
	 * where N is a constant used for normalisation. 
	 * 
	 * @param cell The starting cell
	 * @return A neighbour of {@code cell} chosen at random.
	 */
	@Override
	public Cell selectTargetCell( Cell cell, ArrayList<Cell> targets ) {
		if( targets.isEmpty() )
			return cell;

		// wähle die target-cell mit dem kleinsten potenzial aus
		//int pot = Integer.MAX_VALUE;
		Cell target = targets.get( 0 );
		double minPot = esp.parameterSet.effectivePotential( cell, target );
		for( Cell c : targets ) {
			double pot = esp.parameterSet.effectivePotential( cell, c );
			if( pot > minPot ) {
				target = c;
				minPot = pot;
			}
		}
		return target;
	}

	/**
	 * Decides randomly if an individual moves. (falsch)
	 * @param i An individual with a given parameterSet
	 * @return {@code true} if the individual moves or
	 * {@code false} otherwise. 
	 */
	//gibt true wieder, wenn geschwindigkeit von zelle und individuel (wkeit darueber) bewegung bedeuten
	protected boolean canMove( Individual i ) {
		if( esp.eca.getTimeStep() >= i.getStepEndTime() )
			return true;
		return false;
	}

	@Override
	public void swap( Cell cell1, Cell cell2 ) {
		if( cell1.getIndividual() == null )
			throw new IllegalArgumentException( "No Individual standing on cell #1!" );
		if( cell2.getIndividual() == null )
			throw new IllegalArgumentException( "No Individual standing on cell #2!" );
		if( cell1.equals( cell2 ) )
			throw new IllegalArgumentException( "The cells are equal. Can't swap on equal cells." );
		doMoveWithDecision( cell1.getIndividual(), cell2, false );
		doMoveWithDecision( cell2.getIndividual(), cell1, false );
		//cell1.getRoom().swapIndividuals( cell1, cell2 );
		esp.eca.swapIndividuals( cell1, cell2 );
	}

	/**
	 * Sets the time when the current movement is over for an individual and
	 * actualizates the needed time in the cellular automaton.
	 * @param i the individual
	 * @param d the (real) time when the movement is over
	 */
	private void setStepEndTime( Individual i, double d ) {
		i.setStepEndTime( d );
		esp.eca.setNeededTime( (int) Math.ceil( d ) );
	}

	/**
	 * Selects the possible targets including the current cell.
	 * @param fromCell the current sell
	 * @param onlyFreeNeighbours indicates whether only free neighbours or all neighbours are included
	 * @return a list containing all neighbours and the from cell
	 */
	@Override
	protected ArrayList<Cell> selectPossibleTargets( Cell fromCell, boolean onlyFreeNeighbours ) {
		ArrayList<Cell> targets = super.selectPossibleTargets( fromCell, onlyFreeNeighbours );
		targets.add( fromCell );
		return targets;
	}
}
