/**
 * Framework.java
 * Created: 31.07.2014, 16:39:07
 */
package batch.plugins.impl.eat;

import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.netflow.ds.flow.PathBasedFlowOverTime;
import de.tu_berlin.coga.netflow.dynamic.eatapprox.EarliestArrivalApproximationAlgorithm;
import de.tu_berlin.coga.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import net.xeoh.plugins.base.annotations.PluginImplementation;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class Framework implements AlgorithmicPlugin<EarliestArrivalFlowProblem, PathBasedFlowOverTime> {
	@Override
	public String getName() {
		return "2-Approx-Based";
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
        EarliestArrivalApproximationAlgorithm algo = new EarliestArrivalApproximationAlgorithm();
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


        System.out.println( "Runtime: " + algo.getRuntimeAsString() );
        return null;
      }
    };
    return algo;
  }
}
