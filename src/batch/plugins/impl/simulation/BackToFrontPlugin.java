package batch.plugins.impl.simulation;

import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationResult;
import org.zet.cellularautomaton.algorithm.EvacuationCellularAutomatonAlgorithm;
import batch.plugins.AlgorithmPlugin;
import org.zetool.common.algorithm.AbstractAlgorithm;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class BackToFrontPlugin implements AlgorithmPlugin<EvacuationSimulationProblem, EvacuationSimulationResult> {

    @Override
    public String getName() {
        return "Back-to-Front";
    }

    @Override
    public Class<EvacuationSimulationProblem> accepts() {
        return EvacuationSimulationProblem.class;
    }

    @Override
    public Class<EvacuationSimulationResult> generates() {
        return EvacuationSimulationResult.class;

    }

    @Override
    public AbstractAlgorithm<EvacuationSimulationProblem, EvacuationSimulationResult> getAlgorithm() {
        return EvacuationCellularAutomatonAlgorithm.getBackToFrontAlgorithm();
    }

    @Override
    public String toString() {
        return getName();
    }
}
