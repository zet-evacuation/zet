
package batch.plugins.impl.maxflow;

import batch.plugins.AlgorithmPlugin;
import org.zetool.common.algorithm.AbstractAlgorithm;
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
public class Goldberg implements AlgorithmPlugin<RawMaximumFlowProblem, MaximumFlow> {
  
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
	public AbstractAlgorithm<RawMaximumFlowProblem, MaximumFlow> getAlgorithm() {
		
		AbstractAlgorithm<RawMaximumFlowProblem, MaximumFlow> algo = new AbstractAlgorithm<RawMaximumFlowProblem, MaximumFlow>() {

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

  @Override
  public String toString() {
    return getName();
  }
}
