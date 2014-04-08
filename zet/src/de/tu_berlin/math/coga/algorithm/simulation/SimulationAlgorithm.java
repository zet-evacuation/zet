package de.tu_berlin.math.coga.algorithm.simulation;

import de.tu_berlin.coga.common.algorithm.Algorithm;

/**
 *
 * @param <S>
 * @param <T>
 * @author Jan-Philipp Kappmeier
 */
public abstract class SimulationAlgorithm<S,T> extends Algorithm<S,T> {
	private int stepCount;

	protected final void increaseStep() {
		stepCount++;
	}

	public final int getStep() {
		return stepCount;
	}

	protected abstract double getProgress();

	/**
	 * Performs the simulation, usually iterates a lot of steps.
	 */
	protected abstract void performSimulation();

	/**
	 * Supposed to be called from the algorithm before actual simulation starts.
	 */
	protected abstract void initialize();

	/**
	 * Performs one step of the simulation.
	 */
	protected abstract void performStep();

	/**
	 * Supposed to be called from the algorithm after simulation is finshed.
	 */
	protected abstract T terminate();

	@Override
	final protected T runAlgorithm( S problem ) {
		initialize();
		performSimulation();
		return terminate();
	}
}
