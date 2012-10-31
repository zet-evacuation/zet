/**
 * EvacuationCellularAutomatonAlgorithm.java
 * Created: 25.10.2012, 17:26:13
 */
package algo.ca.framework;

import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import algo.ca.algorithm.evac.EvacuationSimulationResult;
import algo.ca.rule.Rule;
import de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton.AbstractCellularAutomatonSimulationAlgorithm;
import ds.ca.evac.DeathCause;
import ds.ca.evac.EvacCell;
import ds.ca.evac.Individual;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class EvacuationCellularAutomatonAlgorithm extends AbstractCellularAutomatonSimulationAlgorithm<EvacCell, EvacuationSimulationProblem, EvacuationSimulationResult> {
	EvacuationSimulationResult evacuationSimulationResult;

	public final void setMaxTimeInSeconds( double time ) {
		int maxTimeInSteps = (int)Math.ceil( time * getProblem().eca.getStepsPerSecond() );
		setMaxSteps( maxTimeInSteps );
	}

	@Override
	protected void initialize() {
		log.log( Level.INFO, "{0} wird ausgef\u00fchrt. ", toString());
		evacuationSimulationResult = new EvacuationSimulationResult();

		getProblem().eca.start();
		Individual[] individualsCopy = getProblem().eca.getIndividuals().toArray( new Individual[getProblem().eca.getIndividuals().size()] );
		for( Individual i : individualsCopy ) {
			Iterator<Rule> primary = getProblem().ruleSet.primaryIterator();
			EvacCell c = i.getCell();
			while( primary.hasNext() ) {
				Rule r = primary.next();
				r.execute( c );
			}
		}
		getProblem().eca.removeMarkedIndividuals();
	}

	@Override
	protected void performStep() {
		super.performStep();

		super.increaseStep();

		getProblem().eca.removeMarkedIndividuals();
		getProblem().potentialController.updateDynamicPotential( getProblem().parameterSet.probabilityDynamicIncrease(), getProblem().parameterSet.probabilityDynamicDecrease() );
		//caController.getPotentialController().updateDynamicPotential( caController.parameterSet.probabilityDynamicIncrease(), caController.parameterSet.probabilityDynamicDecrease() );
		getProblem().eca.nextTimeStep();

		fireProgressEvent( getProgress(), String.format( "%1$s von %2$s Personen evakuiert.", getProblem().eca.getInitialIndividualCount()-getProblem().eca.getIndividualCount(), getProblem().eca.getIndividualCount() ) );
	}

	@Override
	protected final void execute( EvacCell cell ) {

		Individual i = Objects.requireNonNull( cell.getIndividual(), "Execute called on EvacCell that does not contain an individual!" );
		System.out.println( "Executing rules for individual " + i );
		Iterator<Rule> loop = getProblem().ruleSet.loopIterator();
		while( loop.hasNext() ) { // Execute all rules
			Rule r = loop.next();
			r.execute( i.getCell() );
		}
	}

	@Override
	protected EvacuationSimulationResult terminate() {
			// let die all individuals which are not already dead and not safe
		if( getProblem().eca.getNotSafeIndividualsCount() != 0 ) {
			Individual[] individualsCopy = getProblem().eca.getIndividuals().toArray( new Individual[getProblem().eca.getIndividuals().size()] );
			for( Individual i : individualsCopy )
				if( !i.getCell().getIndividual().isSafe() )
					getProblem().eca.setIndividualDead( i, DeathCause.NotEnoughTime );
		}
		fireProgressEvent( 1, "Simulation abgeschlossen" );

		getProblem().eca.stop();
		return evacuationSimulationResult;
	}

	@Override
	protected boolean isFinished() {
		boolean continueCondition = ( (getProblem().eca.getNotSafeIndividualsCount() > 0 || getProblem().eca.getTimeStep() < getProblem().eca.getNeededTime()) /*&& !isCancelled()*/ );
		return super.isFinished() || !continueCondition;
	}


	/**
	 * Sends a progress event. The progress is defined as the maximum of the percentage
	 * of already evacuated individuals and the fraction of time steps of the
	 * maximum amount of time steps already simulated.
	 *
	 * @return
	 */
	@Override
	protected final double getProgress() {
		double timeProgress = super.getProgress();
		double individualProgress = 1.0 - ((double)getProblem().eca.getIndividualCount() / getProblem().eca.getInitialIndividualCount());
		double progress = Math.max( individualProgress, timeProgress );
		return progress;
	}

	@Override
	public final Iterator<EvacCell> iterator() {
		return new CellIterator( getIndividuals() );

	}

	protected abstract List<Individual> getIndividuals();

	private static class CellIterator implements Iterator<EvacCell> {
		private Iterator<Individual> individuals;

		private CellIterator( List<Individual> individuals ) {
			this.individuals = Objects.requireNonNull( individuals, "Individuals list must not be null." ).iterator();
		}

		@Override
		public boolean hasNext() {
			return individuals.hasNext();
		}

		@Override
		public EvacCell next() {
			return individuals.next().getCell();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException( "Removal of cells is not supported." );
		}
	}
}
