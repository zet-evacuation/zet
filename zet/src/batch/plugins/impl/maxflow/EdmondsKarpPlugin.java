
package batch.plugins.impl.maxflow;

import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.netflow.classic.maxflow.EdmondsKarp;
import de.tu_berlin.coga.netflow.ds.flow.MaximumFlow;
import de.tu_berlin.math.coga.batch.input.converter.RawToFullMaximumFlowProblemConverter;
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
