
package batch.plugins.impl.simulation;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonBackToFront;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonRandom;
import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import algo.ca.algorithm.evac.EvacuationSimulationResult;
import algo.ca.framework.EvacuationCellularAutomatonAlgorithm;
import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import ds.PropertyContainer;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class RandomOrderPlugin implements AlgorithmicPlugin<EvacuationSimulationProblem,EvacuationSimulationResult> {

  @Override
  public String getName() {
    return "Randomized";
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
    EvacuationCellularAutomatonAlgorithm algo = new EvacuationCellularAutomatonRandom();
    //double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
    //algo.setMaxTimeInSeconds( caMaxTime );
    return algo;
  }
}