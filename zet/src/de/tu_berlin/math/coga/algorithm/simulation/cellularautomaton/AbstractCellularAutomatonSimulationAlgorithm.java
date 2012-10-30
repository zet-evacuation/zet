/**
 * AbstractCellularAutomatonSimulationAlgorithm.java
 * Created: 25.10.2012, 17:12:16
 */
package de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton;

import de.tu_berlin.math.coga.algorithm.simulation.SimulationAlgorithm;
import de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton.Cell;
import java.util.Iterator;


/**
 * @param <S> the simulation problem class
 * @param <Ce> the cell type
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractCellularAutomatonSimulationAlgorithm<Ce extends Cell<Ce,?>,S extends CellularAutomatonSimulationProblem<Ce>, T> extends SimulationAlgorithm<S,T> implements Iterable<Ce> {
	private int maxSteps;

	public int getMaxSteps() {
		return maxSteps;
	}

	protected void setMaxSteps( int maxSteps ) {
		this.maxSteps = maxSteps;
	}

	@Override
	protected double getProgress() {
		return (double) getStep() / getMaxSteps();
	}

	/**
	 * Supposed to be called from the algorithm before actual simulation starts.
	 */
	@Override
	protected abstract void initialize();

	/**
	 * Performs one step of the simulation.
	 */
	@Override
	protected void performStep() {
		for( Ce c : this ) {
			execute( c );
		}
	}

	protected abstract void execute( Ce cell );


	/**
	 * Performs the simulation, usually iterates a lot of steps.
	 */
	@Override
	protected void performSimulation() {
		// assume, initialization has been performed. simulation consits of continuous
		// calls of perform step, until a break-condition is reached.
		while( !isFinished() ) {
			performStep();
		}
	}

	/**
	 * A simple imlementation of the termination checker that checks the number
	 * of steps.
	 * @return
	 */
	protected boolean isFinished() {
		return getStep() >= maxSteps;
	}

	/**
	 * Supposed to be called from the algorithm after simulation is finshed.
	 */
	@Override
	protected abstract T terminate();


	@Override
	public abstract Iterator<Ce> iterator();
}
