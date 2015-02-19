
package batch.plugins.impl.simulation;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonInOrder;
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
public class InOrderPlugin implements AlgorithmPlugin<EvacuationSimulationProblem,EvacuationSimulationResult> {

  @Override
  public String getName() {
    return "In Order";
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
    EvacuationCellularAutomatonAlgorithm algo = new EvacuationCellularAutomatonInOrder();
    return algo;
  }

  @Override
  public String toString() {
    return getName();
  }
}
