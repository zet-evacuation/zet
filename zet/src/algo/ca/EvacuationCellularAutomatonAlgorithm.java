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
	
	public void setMaxTimeInSeconds(double maxTimeInSeconds) {
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
