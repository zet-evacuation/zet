
package batch.plugins.impl.maxflow;

import org.zetool.components.batch.plugins.AlgorithmicPlugin;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.netflow.classic.maxflow.PushRelabelHighestLabelGlobalGapRelabelling;
import org.zetool.netflow.ds.flow.MaximumFlow;
import ds.graph.problem.RawToFullMaximumFlowProblemConverter;
import ds.graph.problem.RawMaximumFlowProblem;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class Goldberg implements AlgorithmicPlugin<RawMaximumFlowProblem, MaximumFlow> {
  
	@Override
	public String getName() {
		return "Push Relabel Algorithm";
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
        
        PushRelabelHighestLabelGlobalGapRelabelling hipr = new PushRelabelHighestLabelGlobalGapRelabelling();
        hipr.setProblem( ftfmfp.getSolution() );
        hipr.run();
        
        return hipr.getSolution();
      }
    };
    return algo;
	}
}
