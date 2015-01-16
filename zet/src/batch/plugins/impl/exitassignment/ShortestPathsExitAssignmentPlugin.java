
package batch.plugins.impl.exitassignment;

import algo.graph.exitassignment.Assignable;
import algo.graph.exitassignment.ExitAssignment;
import algo.graph.exitassignment.ShortestPathExitAssignment;
import org.zetool.components.batch.plugins.AlgorithmicPlugin;
import org.zetool.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class ShortestPathsExitAssignmentPlugin extends ShortestPathExitAssignment implements AlgorithmicPlugin<NetworkFlowModel, ExitAssignment> {

	@Override
	public String getName() {
		return "Shortest Paths based Exit Assignment";
	}

	@Override
	public Class<NetworkFlowModel> accepts() {
		return NetworkFlowModel.class;
	}

	@Override
	public Class<ExitAssignment> generates() {
		return ExitAssignment.class;
	}

	@Override
	public Algorithm<NetworkFlowModel, ExitAssignment> getAlgorithm() {
    
    return new ShortestPathExitAssignment();
    
//		
//		Algorithm<NetworkFlowModel, ExitAssignment> algo = new Algorithm<NetworkFlowModel, ExitAssignment>() {
//
//      @Override
//      protected ExitAssignment runAlgorithm( NetworkFlowModel problem ) {
//        ShortestPathExitAssignment spExitAssignment = new ShortestPathExitAssignment ();;
//        spExitAssignment.setProblem( problem );
//        spExitAssignment.run();
//        return spExitAssignment.getExitAssignment();
//      }
//    };
//    return algo;
	}
}
