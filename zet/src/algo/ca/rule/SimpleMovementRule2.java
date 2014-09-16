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
import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.generators.GeneralRandom;
import ds.ca.evac.DoorCell;
import ds.ca.evac.EvacCell;
import ds.ca.evac.ExitCell;
import ds.ca.evac.Individual;
import ds.ca.evac.StairCell;
import ds.ca.evac.StaticPotential;
import ds.ca.results.IndividualStateChangeAction;
import ds.ca.results.VisualResultsRecorder;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier, Sylvie Temme
 */
public class SimpleMovementRule2 extends AbstractMovementRule {

	/**
	 * Decides whether the rule can be applied to the current cell.
	 * Returns {@code true} if the cell is occupied by an individual
	 * or {@code false} otherwise. Individuals standing on an exit cell
	 * do not move any more. This is necessary, as the rule can take out
	 * individuals out of the simulation only, if their last step is finished.
	 * To avoid problems of individuals moving forever, the movement rule
	 * should only be applied if an individual is not already standing on
	 * an evacuation cell.
	 * @param cell the cell
 	 * @return true if the rule can be executed
	 */
	@Override
	public boolean executableOn( ds.ca.evac.EvacCell cell ) {
		return !(cell instanceof ExitCell) && cell.getIndividual() != null;
	}

	@Override
	protected void onExecute( ds.ca.evac.EvacCell cell ) {
		ind = cell.getIndividual();
		if( ind.isAlarmed() ) {
			if( canMove( ind ) )
				if( isDirectExecute() ) { // we are in a "normal" simulation
					EvacCell targetCell = selectTargetCell( cell, computePossibleTargets( cell, true ) );
					setMoveRuleCompleted( true );
					move( targetCell );
				} else { // only calculate possible movements, used for swap cellular automaton
					computePossibleTargets( cell, false );
					setMoveRuleCompleted( true );
				}
			else
				// Individual can't move, it is already moving
				setMoveRuleCompleted( false ); // TODO why is here false?
		} else { // Individual is not alarmed, that means it remains standing on the cell
			setMoveRuleCompleted( true );
			noMove();
		}

		VisualResultsRecorder.getInstance().recordAction( new IndividualStateChangeAction( ind ) );
	}

	@Override
  public void move( EvacCell from, EvacCell targetCell ) {
//  public void move( EvacCell targetCell ) {
    if( ind.getCell().equals( targetCell ) ) {
      esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals()
              .addWaitedTimeToStatistic( ind, esp.eca.getTimeStep() );
      esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForCells()
              .addCellToWaitingStatistic( targetCell, esp.eca.getTimeStep() );
      esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForCells()
              .addCellToUtilizationStatistic( targetCell, esp.eca.getTimeStep() );
			noMove();
		} else {
      esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForCells()
              .addCellToUtilizationStatistic( targetCell, esp.eca.getTimeStep() );
      initializeMove( from, targetCell );
      performMove( from, targetCell );
			setMoveRuleCompleted( false );
		}
	}

	/**
	 * A function called if the individual is not moving. The individual will stand
	 * on the cell for exactly one time step bevore it can move again. But, even
	 * if the individual does not move, the view direction may be changed.
	 */
	protected void noMove() {
		ind.setStepStartTime( ind.getStepEndTime() );
		setStepEndTime( ind, ind.getStepEndTime() + 1 );
		esp.eca.moveIndividual( ind.getCell(), ind.getCell() );

		ind.setDirection( getDirection() );
		setMoveRuleCompleted( false );
		esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic( ind, esp.eca.getTimeStep(), 0 );
	}

	/**
	 * Computes a new viewing direction if the individual is not moving.
	 * @return
	 */
	protected Direction8 getDirection() {
		Direction8 current = ind.getDirection();
		Direction8[] possible = {current.getClockwise().getClockwise(),
			current.getClockwise(),
			current,
			current.getCounterClockwise(),
			current.getCounterClockwise().getCounterClockwise()};
		GeneralRandom rnd = (RandomUtils.getInstance()).getRandomGenerator();
		int randomDirection = rnd.nextInt( 5 );
		Direction8 ret = possible[randomDirection];
		int minDistance = Integer.MAX_VALUE;
		EvacCell cell = ind.getCell();
		for( Direction8 dir : possible ) {
			EvacCell target = cell.getNeighbor( dir );
			if( target != null && !target.isOccupied() ) {
				StaticPotential staticPotential = ind.getStaticPotential();
				int cellDistance = staticPotential.getPotential( cell );
				if( cellDistance < minDistance ) {
					minDistance = cellDistance;
					ret = dir;
				}
			}
		}

		if( ret != current )
			return ret;

		return possible[randomDirection];
	}

	/**
	 * Performs an actual move of an individual (from its cell to another, different
	 * cell).
	 * @param ind
	 * @param targetCell
	 * @param performMove decides if the move is actually performed. If swapping is active, only values have to be updated.
	 */
  private void initializeMove( EvacCell from, EvacCell targetCell ) {
    Individual ind = from.getIndividual();
    if( ind == null ) {
      throw new IllegalStateException( "No Individual on from cell " + from );
    }
		this.esp.potentialController.increaseDynamicPotential( targetCell );

    if( from instanceof DoorCell && targetCell instanceof DoorCell ) {
      if( esp.eca.absoluteSpeed( ind.getRelativeSpeed() ) >= 0.0001 ) { // if individual moves, update times
        speed = esp.eca.absoluteSpeed( ind.getRelativeSpeed() );
				speed *= targetCell.getSpeedFactor() * 1;
				ind.setStepStartTime( Math.max( ind.getCell().getOccupiedUntil(), ind.getStepEndTime() ) );
				setStepEndTime( ind, ind.getStepEndTime() + (dist / speed) * esp.eca.getStepsPerSecond() + 0 );
				ind.setDirection( ind.getDirection() );
			} else
				throw new IllegalStateException( "Individuum has no speed." );

		} else {
      Direction8 direction = getMovementDirection( from, targetCell );

			double stairSpeedFactor = targetCell instanceof StairCell ? getStairSpeedFactor( direction, (StairCell) targetCell )*1.1 : 1;
			dist = direction.distance() * 0.4; // calculate distance
			double add = getSwayDelay( ind, direction ); // add a delay if the person is changing direction

      if( esp.eca.absoluteSpeed( ind.getRelativeSpeed() ) >= 0.0001 ) { // if individual moves, update times
        speed = esp.eca.absoluteSpeed( ind.getRelativeSpeed() );
        double factor = targetCell.getSpeedFactor() * stairSpeedFactor;
        //System.out.println( "Speed factor: " + factor + " stairspeed: " + stairSpeedFactor );
				speed *= factor;
        ind.setStepStartTime( Math.max( from.getOccupiedUntil(), ind.getStepEndTime() ) );
				setStepEndTime( ind, ind.getStepEndTime() + (dist / speed) * esp.eca.getStepsPerSecond() + add*esp.eca.getStepsPerSecond() );
				ind.setDirection( direction );
			} else
				throw new IllegalStateException( "Individuum has no speed." );
		}
	}

	/**
	 * Performs a move after the parameters ({@code speed} and {@code dist}) have
	 * alredy been set by {@link #initializeMove(ds.ca.evac.Individual, ds.ca.evac.EvacCell) }
	 * @param targetCell
	 */
  protected void performMove( EvacCell from, EvacCell targetCell ) {
    Individual ind = from.getIndividual();

    from.setOccupiedUntil( ind.getStepEndTime() );
    esp.eca.moveIndividual( from, targetCell );
		esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic( ind, esp.eca.getTimeStep(), speed * esp.eca.getSecondsPerStep() );
		esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCoveredDistanceToStatistic( ind, (int) Math.ceil( ind.getStepEndTime() ), dist );
	}

	/**
	 * Chooses the possible target cell with the smallest potential value.
	 * @param cell The starting cell
	 * @return A neighbour of {@code cell} chosen at random.
	 */
	@Override
	public EvacCell selectTargetCell( EvacCell cell, List<EvacCell> targets ) {
		EvacCell target = cell;
		double minPot = esp.parameterSet.effectivePotential( cell, cell );
		for( EvacCell c : targets ) {
			double pot = esp.parameterSet.effectivePotential( cell, c );
			if( pot > minPot ) {
				target = c;
				minPot = pot;
			}
		}
		return target;
	}

	/**
	 * Decides, if an individual can move in this step. This is possible, when the
	 * last move was already finished at a time earlier than this time step.
	 * @param i An individual with a given parameterSet
	 * @return {@code true} if the individual moves or
	 * {@code false} otherwise.
	 */
	protected boolean canMove( Individual i ) {
		return esp.eca.getTimeStep() >= i.getStepEndTime();
	}

	@Override
	public void swap( EvacCell cell1, EvacCell cell2 ) {
		if( cell1.getIndividual() == null )
			throw new IllegalArgumentException( "No Individual standing on cell #1!" );
		if( cell2.getIndividual() == null )
			throw new IllegalArgumentException( "No Individual standing on cell #2!" );
		if( cell1.equals( cell2 ) )
			throw new IllegalArgumentException( "The cells are equal. Can't swap on equal cells." );
		ind = cell1.getIndividual();
    initializeMove( cell1, cell2 );
		ind = cell2.getIndividual();
    initializeMove( cell2, cell1 ); // do not actually move!
		esp.eca.swapIndividuals( cell1, cell2 );
	}

	/**
	 * Selects the possible targets including the current cell.
	 * @param fromCell the current sell
	 * @param onlyFreeNeighbours indicates whether only free neighbors or all neighbors are included
	 * @return a list containing all neighbors and the from cell
	 */
	@Override
	protected List<EvacCell> computePossibleTargets( EvacCell fromCell, boolean onlyFreeNeighbours ) {
		List<EvacCell> targets = super.computePossibleTargets( fromCell, onlyFreeNeighbours );
		targets.add( fromCell );
		return Collections.unmodifiableList( targets );
	}
}
