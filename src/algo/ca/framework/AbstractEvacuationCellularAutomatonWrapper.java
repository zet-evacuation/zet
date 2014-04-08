/**
 * AbstractEvacuationCellularAutomatonWrapper.java Created: 31.10.2012, 14:45:43
 */
package algo.ca.framework;

import algo.ca.algorithm.evac.EvacuationSimulationResult;
import de.tu_berlin.coga.common.algorithm.parameter.ParameterSet;
import ds.ca.evac.Individual;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
abstract class AbstractEvacuationCellularAutomatonWrapper extends EvacuationCellularAutomatonAlgorithm {
	protected EvacuationCellularAutomatonAlgorithm wrapped;
	private EvacuationSimulationResult result = null;

	AbstractEvacuationCellularAutomatonWrapper( EvacuationCellularAutomatonAlgorithm wrapped ) {
		this.wrapped = wrapped;
	}

	@Override
	protected List<Individual> getIndividuals() {
		return wrapped.getIndividuals();
	}

	@Override
	protected void initialize() {
		wrapped.addAlgorithmListener( this );
		wrapped.setProblem( getProblem() );
	}

	protected abstract void perform();

	@Override
	protected void performStep() {
		wrapped.initialize();

		perform();

		result = wrapped.terminate();
	}

	@Override
	protected EvacuationSimulationResult terminate() {
		return result;
	}

	@Override
	protected boolean isFinished() {
		return result != null;
	}

	@Override
	public int getMaxSteps() {
		return wrapped.getMaxSteps();
	}

	@Override
	public void setMaxSteps( int maxSteps ) {
		wrapped.setMaxSteps( maxSteps );
	}

	@Override
	public String getDescription() {
		return wrapped.getDescription();
	}

	@Override
	public void setDescription( String description ) {
		wrapped.setDescription( description );
	}

	@Override
	public String getName() {
		return wrapped.getName();
	}

	@Override
	public void setName( String name ) {
		wrapped.setName( name );
	}

	@Override
	public ParameterSet getParameterSet() {
		return wrapped.getParameterSet();
	}

	@Override
	public void setParameterSet( ParameterSet parameterSet ) {
		wrapped.setParameterSet( parameterSet );
	}

	@Override
	public double getAccuracy() {
		return wrapped.getAccuracy();
	}

	@Override
	public void setAccuracy( double accuracy ) throws IllegalArgumentException {
		wrapped.setAccuracy( accuracy );
	}

	@Override
	public void setAccuracy( int possibleChanges ) {
		wrapped.setAccuracy( possibleChanges );
	}

	@Override
	public Logger getLogger() {
		return wrapped.getLogger();
	}

	@Override
	public void setLogger() {
		wrapped.setLogger();
	}

	@Override
	public void setLogger( Logger log ) {
		wrapped.setLogger( log );
	}

	@Override
	public String toString() {
		return wrapped.toString();
	}
}
