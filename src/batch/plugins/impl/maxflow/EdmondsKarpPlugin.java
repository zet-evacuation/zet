
package batch.plugins.impl.maxflow;

import batch.plugins.AlgorithmicPlugin;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.netflow.classic.maxflow.EdmondsKarp;
import org.zetool.netflow.ds.flow.MaximumFlow;
import ds.graph.problem.RawToFullMaximumFlowProblemConverter;
import ds.graph.problem.RawMaximumFlowProblem;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class EdmondsKarpPlugin implements AlgorithmicPlugin<RawMaximumFlowProblem, MaximumFlow> {

	@Override
	public String getName() {
		return "Edmonds Karp Max Flow Algorithm";
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
        EdmondsKarp ek = new EdmondsKarp();
        ek.setProblem( ftfmfp.getSolution() );
        ek.run();
        return ek.getSolution();
      }
    };
    return algo;
	}
}
