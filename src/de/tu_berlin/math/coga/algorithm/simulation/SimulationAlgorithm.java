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

package de.tu_berlin.math.coga.algorithm.simulation;

import org.zetool.common.algorithm.AbstractAlgorithm;

/**
 *
 * @param <S> the simulation problem class
 * @param <T> the simulation result
 * @author Jan-Philipp Kappmeier
 */
public abstract class SimulationAlgorithm<S,T> extends AbstractAlgorithm<S,T> {
  /* Counter for the current simulation step. **/
  private int stepCount;

  /**
   * Increases the step counter by one.
   */
	protected final void increaseStep() {
		stepCount++;
	}

  /**
   * Returns the current simulation step.
   * @return the current simulation step
   */
	public final int getStep() {
		return stepCount;
	}

	/**
	 * Returns the current progress of the simulation. This can be the percentage of a limited time or measured by success
   * quantity that has completed after simulation.
	 * @return the current progress as percentage
	 */
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
   * @return returns the simulation result
	 */
	protected abstract T terminate();

	@Override
	final protected T runAlgorithm( S problem ) {
		stepCount = 0;
    initialize();
		performSimulation();
		return terminate();
	}
}
