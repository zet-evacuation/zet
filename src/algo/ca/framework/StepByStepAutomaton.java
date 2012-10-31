/**
 * StepByStepAutomaton.java
 * Created: Oct 30, 2012, 5:32:47 PM
 */
package algo.ca.framework;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class StepByStepAutomaton {
	private StepByStepAutomaton() { }

	public static EvacuationCellularAutomatonAlgorithm getStepByStepAlgorithm( EvacuationCellularAutomatonAlgorithm eca )  {
		EvacuationCellularAutomatonAlgorithm stepbystep = new StepByStepAlgorithm( eca );
		return stepbystep;
	}

	public static EvacuationCellularAutomatonAlgorithm getSlowAlgorithm( EvacuationCellularAutomatonAlgorithm eca ) {
		EvacuationCellularAutomatonAlgorithm slow = new SlowAlgorithm( eca );
		return slow;
	}
}
