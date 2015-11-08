package algo.ca.framework;

import org.zet.cellularautomaton.algorithm.EvacuationCellularAutomatonAlgorithm;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class StepByStepAutomaton {

    private StepByStepAutomaton() {
    }

    public static EvacuationCellularAutomatonAlgorithm getStepByStepAlgorithm(EvacuationCellularAutomatonAlgorithm eca) {
        EvacuationCellularAutomatonAlgorithm stepbystep = new StepByStepAlgorithm(eca);
        return stepbystep;
    }

    public static EvacuationCellularAutomatonAlgorithm getSlowAlgorithm(EvacuationCellularAutomatonAlgorithm eca) {
        EvacuationCellularAutomatonAlgorithm slow = new SlowAlgorithm(eca);
        return slow;
    }
}
