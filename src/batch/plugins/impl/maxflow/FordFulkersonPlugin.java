
package batch.plugins.impl.maxflow;

import batch.plugins.AlgorithmPlugin;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.netflow.ds.flow.MaximumFlow;
import ds.graph.problem.RawToFullMaximumFlowProblemConverter;
import ds.graph.problem.RawMaximumFlowProblem;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class FordFulkersonPlugin implements AlgorithmPlugin<RawMaximumFlowProblem, MaximumFlow> {

	@Override
	public String getName() {
		return "Ford Fulkerson Max Flow Algorithm";
	}

	@Override
	public Class<RawMaximumFlowProblem> accepts() {
		return RawMaximumFlowProblem.class;
	}

	@Override
	public Class<MaximumFlow> generates() {
		return MaximumFlow.class;
	}

	@Override
	public Algorithm<RawMaximumFlowProblem, MaximumFlow> getAlgorithm() {
		
		Algorithm<RawMaximumFlowProblem, MaximumFlow> algo = new Algorithm<RawMaximumFlowProblem, MaximumFlow>() {

      @Override
      protected MaximumFlow runAlgorithm( RawMaximumFlowProblem problem ) {
        // Convert raw to network flow
        RawToFullMaximumFlowProblemConverter ftfmfp = new RawToFullMaximumFlowProblemConverter();
        ftfmfp.setProblem( problem );
        ftfmfp.run();
        org.zetool.netflow.classic.maxflow.FordFulkerson ff = new org.zetool.netflow.classic.maxflow.FordFulkerson();
        ff.setProblem( ftfmfp.getSolution() );
        ff.run();
        return ff.getSolution();
      }
    };
    return algo;
	}

  @Override
  public String toString() {
    return getName();
  }
}
