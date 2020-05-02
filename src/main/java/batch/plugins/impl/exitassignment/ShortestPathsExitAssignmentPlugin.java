
package batch.plugins.impl.exitassignment;

import algo.graph.exitassignment.ExitAssignment;
import algo.graph.exitassignment.ShortestPathExitAssignment;
import batch.plugins.AlgorithmPlugin;
import org.zetool.common.algorithm.AbstractAlgorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class ShortestPathsExitAssignmentPlugin extends ShortestPathExitAssignment implements AlgorithmPlugin<NetworkFlowModel, ExitAssignment> {

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
  public String toString() {
    return getName();
  }

  @Override
	public AbstractAlgorithm<NetworkFlowModel, ExitAssignment> getAlgorithm() {  
    return new ShortestPathExitAssignment();
	}
}
