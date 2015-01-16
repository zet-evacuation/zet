
package batch.plugins.impl.eat;

import batch.plugins.AlgorithmicPlugin;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.dynamic.eatapprox.EarliestArrivalApproximationAlgorithm;
import org.zetool.netflow.dynamic.eatapprox.EatPatternBFS;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class SSSPIncreasingTimeExpanded implements AlgorithmicPlugin<EarliestArrivalFlowProblem, PathBasedFlowOverTime> {
	@Override
	public String getName() {
		return "SSSP in increasing time expanded network";
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
        EatPatternBFS algo = new EatPatternBFS();
        //problem.setTimeHorizon( 18 ); // 64: zeitpunkt 42 ist falsch, // 1676 for max flow
        algo.setProblem( problem );
        algo.run();

        System.out.println( "Arrival pattern: " );
        System.out.println( algo.getSolution() );
        int total = 0;
        for( int i = 1; i < algo.getSolution().getTimeHorizon(); ++i ) {
          total += (algo.getSolution().getValue( i ) - algo.getSolution().getValue( i-1 ))*i;
        }
        System.out.println( "Total cost: " + total );

        System.out.println( "Sending the flow units required " + algo.getRuntimeAsString() );
        return null;
      }
    };
    return algo;
  }
}
