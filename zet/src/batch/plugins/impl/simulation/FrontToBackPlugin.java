
package batch.plugins.impl.simulation;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonFrontToBack;
import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import algo.ca.algorithm.evac.EvacuationSimulationResult;
import algo.ca.framework.EvacuationCellularAutomatonAlgorithm;
import batch.plugins.AlgorithmPlugin;
import org.zetool.common.algorithm.Algorithm;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class FrontToBackPlugin implements AlgorithmPlugin<EvacuationSimulationProblem,EvacuationSimulationResult> {

  @Override
  public String getName() {
    return "Front-to-Back";
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
  public Algorithm<EvacuationSimulationProblem,EvacuationSimulationResult> getAlgorithm() {
    EvacuationCellularAutomatonAlgorithm algo = new EvacuationCellularAutomatonFrontToBack();
    return algo;
  }

  @Override
  public String toString() {
    return getName();
  }
}
