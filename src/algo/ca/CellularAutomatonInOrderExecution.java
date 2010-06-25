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
 * CA.java
 * Created on 26.01.2008, 14:39:39
 */
package algo.ca;

import ds.ca.CAController;
import algo.ca.parameter.AbstractDefaultParameterSet;
import algo.ca.parameter.ParameterSet;
import algo.ca.rule.Rule;
import ds.PropertyContainer;
import ds.ca.Cell;
import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import java.util.Iterator;

import de.tu_berlin.math.coga.common.localization.Localization;
import batch.tasks.AlgorithmTask;
import statistic.ca.CAStatisticWriter;
import util.ProgressBooleanFlags;

/**
 * Executes the cellular automaton. Can be called by a worker thread.
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonInOrderExecution extends EvacuationCellularAutomatonAlgorithm {
	protected static final int stepsBetweenProgressOutputs = 25;
	private boolean initRulesPerformed = false;

	/**
	 * 
	 * @param ca the cellular automaton that is executed
	 */
	public CellularAutomatonInOrderExecution( CellularAutomaton ca ) {
		super( ca );
	}

	public void run() {
		if( ProgressBooleanFlags.CA_PROGRESS )
			System.out.println( "Progress: Starting simulation of cellular automaton." );
		if( isFinished() | isCancelled() ) {
			getCellularAutomaton().stopRecording();
			return;
		}
		if( !isInitialized() | !isStepByStep() )
			initialize();
		if( isStepByStep() )
			if( !initRulesPerformed )
				executeInitialization();
			else
				executeStep();
		else {
			int individuals = ca.individualCount();

			// Execute initialization rules
			executeInitialization();

			// Execute loop rules
			if( ca.individualCount() > 0 )
				AlgorithmTask.getInstance().publish( 0, ca.evacuatedIndividualsCount() + " " + Localization.getInstance().getString( "algo.ca.IndividualEvacuated" ), Localization.getInstance().getString( "algo.ca.execute" ) + " " + (ca.getTimeStep() + 1) + ". " + Localization.getInstance().getString( "algo.ca.step" ) );
			while( (ca.getNotSafeIndividualsCount() > 0 || ca.getTimeStep() < ca.getNeededTime()) && ca.getTimeStep() < getMaxTimeInSteps() && !isCancelled() ) {
				if( isPaused() ) {
					try {
						Thread.sleep( 500 );
					} catch( InterruptedException ignore ) {
					}
					continue;
				}
				executeStep();
				int individualProgress = Math.min( 100 - (int)Math.round( ((double)ca.individualCount() / individuals) * 100 ), 99 );
				int timeProgress = Math.min( (int)Math.round( (ca.getTimeStep() / getMaxTimeInSteps()) * 100 ), 99 );
				AlgorithmTask.getInstance().publish( Math.max( individualProgress, timeProgress ), (ca.getInitialIndividualCount() - ca.getNotSafeIndividualsCount() - ca.deadIndividualsCount()) + " " + Localization.getInstance().getString( "algo.ca.safe" ) + " " + ca.deadIndividualsCount() + " " + Localization.getInstance().getString( "algo.ca.notSafe" ), Localization.getInstance().getString( "algo.ca.execute" ) + " " + ca.getTimeStep() + ". " + Localization.getInstance().getString( "algo.ca.step" ) );
			}
			// let die all individuals which are not already dead and not safe
			if( ca.getNotSafeIndividualsCount() != 0 ) {
				Individual[] individualsCopy = ca.getIndividuals().toArray( new Individual[ca.getIndividuals().size()] );
				for( Individual i : individualsCopy )
					if( !i.getCell().getIndividual().isSafe() )
						ca.setIndividualDead( i, Individual.DeathCause.NOT_ENOUGH_TIME );
			}
			setFinished( true );
			if( !isCancelled() ) {
				String text = (ca.getInitialIndividualCount() - ca.getNotSafeIndividualsCount() - ca.deadIndividualsCount()) + " " + Localization.getInstance().getString( "algo.ca.safe" ) + " " + ca.deadIndividualsCount() + " " + Localization.getInstance().getString( "algo.ca.notEvacuated" );
				AlgorithmTask.getInstance().publish( 100, Localization.getInstance().getString( "algo.ca.end" ) + " " + text, ca.getTimeStep() + " " + Localization.getInstance().getString( "algo.ca.steps" ) );
			}

			ca.stop();
			if( ProgressBooleanFlags.CA_PROGRESS )
				System.out.println( "Progress: Simulation of cellular automaton finished." );
		}
	}

	/**
	 * 
	 */
	public void initialize() {
		if( util.DebugFlags.CA_ALGO )
			System.out.print( toString() + " wird ausgeführt. " );
		PropertyContainer props = PropertyContainer.getInstance();

		rs = RuleSet.createRuleSet( props.getAsString( "algo.ca.ruleSet" ) );

		ParameterSet ps = AbstractDefaultParameterSet.createParameterSet( props.getAsString( "algo.ca.parameterSet" ) );

		PotentialController pc = new SPPotentialController( ca );
		CAStatisticWriter casw = new CAStatisticWriter();
		pc.setCA( ca );
		pc.setPm( ca.getPotentialManager() );
		ca.setAbsoluteMaxSpeed( ps.getAbsoluteMaxSpeed() );
		// Calculate maximal step count now, never before! Absolute max speed must be set!
		calculateMaxTimeInSteps();

		caController = new CAController( ca, rs, ps, pc, casw );

		setInitialized( true );
		initRulesPerformed = false;
	}

	/**
	 * 
	 */
	protected void executeInitialization() {
		ca.start();
		Individual[] individualsCopy = ca.getIndividuals().toArray( new Individual[ca.getIndividuals().size()] );
		for( Individual i : individualsCopy ) {
			Iterator<Rule> primary = rs.primaryIterator();
			Cell c = i.getCell();
			while( primary.hasNext() ) {
				Rule r = primary.next();
				r.execute( c );
			}
		}
		initRulesPerformed = true;
		ca.removeMarkedIndividuals();
	}

	/**
	 * 
	 */
	protected void executeStep() {
		if( !isInitialized() )
			throw new IllegalArgumentException( Localization.getInstance().getString( "algo.ca.NotInitializedException" ) );

		if( ProgressBooleanFlags.CA_PROGRESS ) {
			int t = ca.getTimeStep();
			if( t % stepsBetweenProgressOutputs == 1 )
				System.out.println( "Progress: Starting step " + t + "." );
		}

		// Execute the rules for all individuals
		for( Individual i : getIndividuals() ) {
			Iterator<Rule> loop = rs.loopIterator();
			while( loop.hasNext() ) { // Execute all rules
				Rule r = loop.next();
				r.execute( i.getCell() );
			}
		}
		ca.removeMarkedIndividuals();
		caController.getPotentialController().updateDynamicPotential( caController.parameterSet.probabilityDynamicIncrease(), caController.parameterSet.probabilityDynamicDecrease() );
		ca.nextTimeStep();
	}

	@Override
	public String toString() {
		return "CellularAutomatonInOrderExecution";
	}
}
