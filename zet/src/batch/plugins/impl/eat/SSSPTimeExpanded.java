
package batch.plugins.impl.eat;

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.components.batch.plugins.AlgorithmicPlugin;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import org.zetool.netflow.dynamic.earliestarrival.old.EATransshipmentWithTHSSSP;
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
        problem.setTimeHorizon( 27 );
        eat.setProblem( problem );
        eat.run();

        PathBasedFlowOverTime df = eat.getSolution().getPathBased();
        //String result = String.format( "Sent %1$s of %2$s flow units in %3$s time units successfully.",
        //        eat.getSolution().getFlowAmount(), problem.getTotalSupplies(), algo.getSolution().getTimeHorizon() );
        //System.out.println( result );
        //System.out.println( "Total cost: " + algo.getSolution() .getTotalCost() );
        //AlgorithmTask.getInstance().publish(100, result, "");
        System.out.println( "Sending the flow units required " + eat.getRuntime() );

        return eat.getSolution().getPathBased();
      }
    };
    return algo;
	}
}
