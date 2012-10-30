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

import ds.ca.evac.Individual;
import java.util.List;

/**
 * Executes the cellular automaton. Can be called by a worker thread.
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellularAutomatonInOrder extends algo.ca.framework.EvacuationCellularAutomatonAlgorithm {

	@Override
	public List<Individual> getIndividuals() {
		return getProblem().eca.getIndividuals();
	}

	@Override
	public String toString() {
		return "EvacuationCellularAutomatonInOrder";
	}
}

//	@Override
//	protected EvacuationSimulationResult runAlgorithm( EvacuationSimulationProblem problem ) {
//		if( isFinished() | isCancelled() ) {
//			getCellularAutomaton().stopRecording();
//			return esr;
//		}
//		if( !isInitialized() | !isStepByStep() )
//			initialize();
//		if( isStepByStep() )
//			if( !initRulesPerformed )
//				executeInitialization();
//			else {
//				executeStep();
//				progress();
//				if( (problem.eca.getNotSafeIndividualsCount() == 0 && problem.eca.getTimeStep() >= problem.eca.getNeededTime()) || problem.eca.getTimeStep() >= getMaxTimeInSteps() || isCancelled() )
//					setFinished( true );
//			}
//		else {
//			// Execute initialization rules
//			executeInitialization();
//
//			// Execute loop rules
//			if( problem.eca.getIndividualCount() > 0 )
//				fireProgressEvent( 0, String.format( "0 von %1$s Personen evakuiert.", getProblem().eca.getInitialIndividualCount() ) );
//				while( (problem.eca.getNotSafeIndividualsCount() > 0 || problem.eca.getTimeStep() < problem.eca.getNeededTime()) && problem.eca.getTimeStep() < getMaxTimeInSteps() && !isCancelled() ) {
//					if( isPaused() ) {
//						try {
//							Thread.sleep( 500 );
//						} catch( InterruptedException ignore ) {
//						}
//						continue;
//					}
//					executeStep();
//					progress();
//				}
//			// let die all individuals which are not already dead and not safe
//			if( problem.eca.getNotSafeIndividualsCount() != 0 ) {
//				Individual[] individualsCopy = problem.eca.getIndividuals().toArray( new Individual[problem.eca.getIndividuals().size()] );
//				for( Individual i : individualsCopy )
//					if( !i.getCell().getIndividual().isSafe() )
//						problem.eca.setIndividualDead( i, DeathCause.NotEnoughTime );
//			}
//			setFinished( true );
//			fireProgressEvent( 1, isCancelled() ? "Simulation abgebrochen" : "Simulation abgeschlossen" );
//
//			problem.eca.stop();
//		}
//		return esr;
//	}
