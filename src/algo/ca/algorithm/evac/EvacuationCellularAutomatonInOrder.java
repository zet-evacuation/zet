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
 * EvacuationCellularAutomatonInOrder.java
 * Created on 26.01.2008, 14:39:39
 */
package algo.ca.algorithm.evac;

import algo.ca.rule.Rule;
import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import ds.ca.evac.Cell;
import ds.ca.evac.Individual;
import java.util.Iterator;

/**
 * Executes the cellular automaton. Can be called by a worker thread.
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonInOrder extends EvacuationCellularAutomatonAlgorithm {

	private boolean initRulesPerformed = false;

	/**
	 * 
	 */
	public EvacuationCellularAutomatonInOrder() {
	}

	@Override
	protected EvacuationSimulationResult runAlgorithm( EvacuationSimulationProblem problem ) {
		if( isFinished() | isCancelled() ) {
			getCellularAutomaton().stopRecording();
			return esr;
		}
		if( !isInitialized() | !isStepByStep() )
			initialize();
		if( isStepByStep() )
			if( !initRulesPerformed )
				executeInitialization();
			else {
				executeStep();
				progress();
				if( (problem.eca.getNotSafeIndividualsCount() == 0 && problem.eca.getTimeStep() >= problem.eca.getNeededTime()) || problem.eca.getTimeStep() >= getMaxTimeInSteps() || isCancelled() )
					setFinished( true );
			}
		else {
			// Execute initialization rules
			executeInitialization();

			// Execute loop rules
			if( problem.eca.getIndividualCount() > 0 )
				fireProgressEvent( 0, String.format( "0 von %1$s Personen evakuiert.", getProblem().eca.getInitialIndividualCount() ) );
				while( (problem.eca.getNotSafeIndividualsCount() > 0 || problem.eca.getTimeStep() < problem.eca.getNeededTime()) && problem.eca.getTimeStep() < getMaxTimeInSteps() && !isCancelled() ) {
					if( isPaused() ) {
						try {
							Thread.sleep( 500 );
						} catch( InterruptedException ignore ) {
						}
						continue;
					}
					executeStep();
					progress();
				}
			// let die all individuals which are not already dead and not safe
			if( problem.eca.getNotSafeIndividualsCount() != 0 ) {
				Individual[] individualsCopy = problem.eca.getIndividuals().toArray( new Individual[problem.eca.getIndividuals().size()] );
				for( Individual i : individualsCopy )
					if( !i.getCell().getIndividual().isSafe() )
						problem.eca.setIndividualDead( i, Individual.DeathCause.NOT_ENOUGH_TIME );
			}
			setFinished( true );
			fireProgressEvent( 1, isCancelled() ? "Simulation abgebrochen" : "Simulation abgeschlossen" );

			problem.eca.stop();
		}
		return esr;
	}

	/**
	 * 
	 */
	@Override
	public void initialize() {
		if( util.DebugFlags.CA_ALGO )
			System.out.print( toString() + " wird ausgeführt. " );

		// Calculate maximal step count now, never before! Absolute max speed must be set!
		calculateMaxTimeInSteps();

		setInitialized( true );
		initRulesPerformed = false;
		if( getProblem().eca.getIndividualCount() == 0 )
			setFinished( true );
	}

	/**
	 * 
	 */
	@Override
	protected void executeInitialization() {
		getProblem().eca.start();
		Individual[] individualsCopy = getProblem().eca.getIndividuals().toArray( new Individual[getProblem().eca.getIndividuals().size()] );
		for( Individual i : individualsCopy ) {
			Iterator<Rule> primary = getProblem().ruleSet.primaryIterator();
			Cell c = i.getCell();
			while( primary.hasNext() ) {
				Rule r = primary.next();
				r.execute( c );
			}
		}
		initRulesPerformed = true;
		getProblem().eca.removeMarkedIndividuals();
	}

	/**
	 * 
	 */
	@Override
	protected void executeStep() {
		if( !isInitialized() )
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "algo.ca.NotInitializedException" ) );

		// Execute the rules for all individuals
		for( Individual i : getIndividuals() ) {
			Iterator<Rule> loop = getProblem().ruleSet.loopIterator();
			while( loop.hasNext() ) { // Execute all rules
				Rule r = loop.next();
				r.execute( i.getCell() );
			}
		}
		getProblem().eca.removeMarkedIndividuals();
		getProblem().potentialController.updateDynamicPotential( getProblem().parameterSet.probabilityDynamicIncrease(), getProblem().parameterSet.probabilityDynamicDecrease() );
		//caController.getPotentialController().updateDynamicPotential( caController.parameterSet.probabilityDynamicIncrease(), caController.parameterSet.probabilityDynamicDecrease() );
		getProblem().eca.nextTimeStep();
	}

	@Override
	public String toString() {
		return "CellularAutomatonInOrderExecution";
	}

	/**
	 * Sends a progress event. The progress is defined as the maximum of the percentage
	 * of already evacuated individuals and the fraction of time steps of the 
	 * maximum amount of time steps already simulated.
	 */
	protected void progress() {
		double individualProgress = 1.0 - ((double)getProblem().eca.getIndividualCount() / getProblem().eca.getInitialIndividualCount());
		double timeProgress = ((double)getProblem().eca.getTimeStep() / getMaxTimeInSteps());
		double progress = Math.max( individualProgress, timeProgress );
		fireProgressEvent( progress, String.format( "%1$s von %2$s Personen evakuiert.", getProblem().eca.getInitialIndividualCount()-getProblem().eca.getIndividualCount(), getProblem().eca.getIndividualCount() ) );
	}
}
