package batch.plugins.impl.eat;

import de.tu_berlin.coga.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import de.tu_berlin.coga.netflow.dynamic.earliestarrival.SEAAPAlgorithm;
import batch.plugins.AlgorithmicPlugin;
import org.zetool.common.algorithm.Algorithm;
import de.tu_berlin.coga.netflow.ds.flow.PathBasedFlowOverTime;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class Tjandra implements AlgorithmicPlugin<EarliestArrivalFlowProblem, PathBasedFlowOverTime> {

  @Override
  public String getName() {
    return "Tjandra";
  }

  @Override
  public Class<EarliestArrivalFlowProblem> accepts() {
    return EarliestArrivalFlowProblem.class;
  }

  @Override
  public Class<PathBasedFlowOverTime> generates() {
    return PathBasedFlowOverTime.class;
  }

  @Override
  public Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime> getAlgorithm() {

    Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime> algo = new Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime>() {
      @Override
      protected PathBasedFlowOverTime runAlgorithm( EarliestArrivalFlowProblem problem ) {
        System.err.println( "Running the plugin for Tjandras SEAAP-Algorithm with an estimated time horizon of "
                + problem.getTimeHorizon() );
        SEAAPAlgorithm algo = new SEAAPAlgorithm();
        algo.setProblem( problem );
        algo.run();

        PathBasedFlowOverTime df = algo.getSolution().getPathBased();
        String result = String.format( "Sent %1$s of %2$s flow units in %3$s time units successfully.",
                algo.getSolution().getFlowAmount(), problem.getTotalSupplies(), algo.getSolution().getTimeHorizon() );
        System.out.println( result );
        System.out.println( "Total cost: " + algo.getSolution().getTotalCost() );
        //AlgorithmTask.getInstance().publish(100, result, "");
        System.out.println( "Sending the flow units required " + algo.getRuntime() );
        return df;
      }
    };
    return algo;
  }
}
