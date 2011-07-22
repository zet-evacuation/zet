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
/**
 * Class EvacuationCellularAutomatonAlgorithm
 * Erstellt 04.07.2008, 14:07:15
 */
package algo.ca;

import ds.ca.CAController;
import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class EvacuationCellularAutomatonAlgorithm implements CellularAutomatonAlgorithm {
	protected CAController caController;
	protected CellularAutomaton ca;
	protected RuleSet rs;
	private boolean initialized;
	private boolean stepByStep = false;
	private boolean finished = false;
	private boolean pause = false;
	private boolean cancelled = false;
	private double maxTimeInSteps = 1;
	private double maxTimeInSeconds = 300;

	public EvacuationCellularAutomatonAlgorithm( CellularAutomaton ca ) {
		this.ca = ca;
		this.initialized = false;
	}

	protected abstract void executeInitialization();

	protected abstract void executeStep();

	public void setMaxTimeInSeconds( double maxTimeInSeconds ) {
		this.maxTimeInSeconds = maxTimeInSeconds;
		calculateMaxTimeInSteps();
		//maxTimeInSteps = maxTimeInSeconds * ca.getStepsPerSecond();
	}

	protected void calculateMaxTimeInSteps() {
		maxTimeInSteps = maxTimeInSeconds * ca.getStepsPerSecond();
	}

	public double getMaxTimeInSeconds() {
		return maxTimeInSeconds;
	}

	public double getMaxTimeInSteps() {
		return maxTimeInSteps;
	}

	public CellularAutomaton getCellularAutomaton() {
		return ca;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished( boolean finished ) {
		this.finished = finished;
	}

	public CAController getCaController() {
		return caController;
	}

	public void setStepByStep( boolean b ) {
		stepByStep = b;
	}

	public boolean isStepByStep() {
		return stepByStep;
	}

	public boolean isInitialized() {
		return initialized;
	}

	protected void setInitialized( boolean initialized ) {
		this.initialized = initialized;
	}

	public void pause( boolean pause ) {
		this.pause = pause;
	}

	public boolean isPaused() {
		return pause;
	}

	public void cancel() {
		cancelled = true;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public List<Individual> getIndividuals() {
		return ca.getIndividuals();
	}
}
