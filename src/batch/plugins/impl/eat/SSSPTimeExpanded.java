
package batch.plugins.impl.eat;

import de.tu_berlin.coga.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.netflow.ds.flow.PathBasedFlowOverTime;
import de.tu_berlin.coga.netflow.dynamic.earliestarrival.old.EATransshipmentWithTHSSSP;
import net.xeoh.plugins.base.annotations.PluginImplementation;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class SSSPTimeExpanded implements AlgorithmicPlugin<EarliestArrivalFlowProblem, PathBasedFlowOverTime> {

	@Override
	public String getName() {
		return "Time Expanded SSP";
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
    System.err.println( "Testing with fixed time horizon 18!" );

    Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime> algo = new Algorithm<EarliestArrivalFlowProblem, PathBasedFlowOverTime>() {

      @Override
      protected PathBasedFlowOverTime runAlgorithm( EarliestArrivalFlowProblem problem ) {
        EATransshipmentWithTHSSSP eat = new EATransshipmentWithTHSSSP();
        //problem.setTimeHorizon( getFeasibleTimeHorizon() );
        problem.setTimeHorizon( 18 );

        eat.setProblem( problem );
        eat.run();
        return eat.getSolution().getPathBased();
      }
    };
    return algo;

	}

}
