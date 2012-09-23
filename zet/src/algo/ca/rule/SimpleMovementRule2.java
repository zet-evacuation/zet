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

import de.tu_berlin.math.coga.common.util.Direction;
import ds.ca.evac.Cell;
import ds.ca.evac.DoorCell;
import ds.ca.evac.ExitCell;
import ds.ca.evac.Individual;
import ds.ca.evac.StairCell;
import ds.ca.results.IndividualStateChangeAction;
import ds.ca.results.VisualResultsRecorder;
import java.util.ArrayList;

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
	 * @param cell
	 * @return true if the rule can be executed
	 */
	@Override
	public boolean executableOn( ds.ca.evac.Cell cell ) {
		return !(cell instanceof ExitCell) && cell.getIndividual() != null;
	}

	@Override
	protected void onExecute( ds.ca.evac.Cell cell ) {
		ind = cell.getIndividual();
		if( ind.isAlarmed() ) {
			if( canMove( ind ) )
				if( isDirectExecute() ) { // we are in a "normal" simulation
					Cell targetCell = selectTargetCell( cell, computePossibleTargets( cell, true ) );
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
	public void move( Cell targetCell ) {
		if( ind.getCell().equals( targetCell ) ) {
			esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addWaitedTimeToStatistic( ind, esp.eca.getTimeStep() );
			esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToWaitingStatistic( targetCell, esp.eca.getTimeStep() );
			esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToUtilizationStatistic( targetCell, esp.eca.getTimeStep() );
			noMove();
		} else {
			esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToUtilizationStatistic( targetCell, esp.eca.getTimeStep() );
			initializeMove( targetCell );
			performMove( targetCell );
			setMoveRuleCompleted( false );		
		}
	}

	/**
	 * A function called if the individual is not moving.
	 * @param ind the individual that is not moving
	 */
	protected void noMove() {
		ind.setStepStartTime( ind.getStepEndTime() );
		setStepEndTime( ind, ind.getStepEndTime() + 1 );
		esp.eca.moveIndividual( ind.getCell(), ind.getCell() );
		setMoveRuleCompleted( false );
		esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic( ind, esp.eca.getTimeStep(), 0 );
		return;
	}
		
	/**
	 * Performs an actual move of an individual (from its cell to another, different
	 * cell).
	 * @param ind
	 * @param targetCell
	 * @param performMove decides if the move is actually performed. If swapping is active, only values have to be updated.
	 */
	private void initializeMove( Cell targetCell  ) {
		this.esp.potentialController.increaseDynamicPotential( targetCell );

		if( ind.getCell() instanceof DoorCell && targetCell instanceof DoorCell ) {
			if( esp.eca.absoluteSpeed( ind.getCurrentSpeed() ) >= 0.0001 ) { // if individual moves, update times
				speed = esp.eca.absoluteSpeed( ind.getCurrentSpeed() );
				speed *= targetCell.getSpeedFactor() * 1;
				ind.setStepStartTime( Math.max( ind.getCell().getOccupiedUntil(), ind.getStepEndTime() ) );
				setStepEndTime( ind, ind.getStepEndTime() + (dist / speed) * esp.eca.getStepsPerSecond() + 0 );
				ind.setDirection( ind.getDirection() );			
			} else
				throw new IllegalStateException( "Individuum has no speed." );
			
		} else {
			Direction direction = getMovementDirection( ind.getCell(), targetCell );

			double stairSpeedFactor = targetCell instanceof StairCell ? stairSpeedFactor = getStairSpeedFactor( direction, (StairCell) targetCell ) : 1;
			dist = direction.distance() * 0.4; // calculate distance
			double add = getSwayDelay( ind, direction ); // add a delay if the person is changing direction

			if( esp.eca.absoluteSpeed( ind.getCurrentSpeed() ) >= 0.0001 ) { // if individual moves, update times
				speed = esp.eca.absoluteSpeed( ind.getCurrentSpeed() );
				speed *= targetCell.getSpeedFactor() * stairSpeedFactor;
				ind.setStepStartTime( Math.max( ind.getCell().getOccupiedUntil(), ind.getStepEndTime() ) );
				setStepEndTime( ind, ind.getStepEndTime() + (dist / speed) * esp.eca.getStepsPerSecond() + add );
				ind.setDirection( direction );			
			} else
				throw new IllegalStateException( "Individuum has no speed." );
		}
		

	}
	
	/**
	 * Performs a move after the parameters ({@code speed} and {@code dist}) have
	 * alredy been set by {@link #initializeMove(ds.ca.evac.Individual, ds.ca.evac.Cell) }
	 * @param ind
	 * @param targetCell 
	 */
	protected void performMove( Cell targetCell ) {
		ind.getCell().setOccupiedUntil( ind.getStepEndTime()  );
		esp.eca.moveIndividual( ind.getCell(), targetCell );
		esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic( ind, esp.eca.getTimeStep(), speed * esp.eca.getSecondsPerStep() );
		esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCoveredDistanceToStatistic( ind, (int) Math.ceil( ind.getStepEndTime() ), dist );
	}

	/**
	 * Chooses the possible target cell with the smallest potential value.
	 * @param cell The starting cell
	 * @return A neighbour of {@code cell} chosen at random.
	 */
	@Override
	public Cell selectTargetCell( Cell cell, ArrayList<Cell> targets ) {
		Cell target = cell;
		double minPot = esp.parameterSet.effectivePotential( cell, cell );
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
	public void swap( Cell cell1, Cell cell2 ) {
		if( cell1.getIndividual() == null )
			throw new IllegalArgumentException( "No Individual standing on cell #1!" );
		if( cell2.getIndividual() == null )
			throw new IllegalArgumentException( "No Individual standing on cell #2!" );
		if( cell1.equals( cell2 ) )
			throw new IllegalArgumentException( "The cells are equal. Can't swap on equal cells." );
		ind = cell1.getIndividual();
		initializeMove( cell2 );
		ind = cell2.getIndividual();
		initializeMove( cell1 ); // do not actually move!
		esp.eca.swapIndividuals( cell1, cell2 );
	}

	/**
	 * Selects the possible targets including the current cell.
	 * @param fromCell the current sell
	 * @param onlyFreeNeighbours indicates whether only free neighbours or all neighbours are included
	 * @return a list containing all neighbours and the from cell
	 */
	@Override
	protected ArrayList<Cell> computePossibleTargets( Cell fromCell, boolean onlyFreeNeighbours ) {
		ArrayList<Cell> targets = super.computePossibleTargets( fromCell, onlyFreeNeighbours );
		targets.add( fromCell );
		return targets;
	}
}
