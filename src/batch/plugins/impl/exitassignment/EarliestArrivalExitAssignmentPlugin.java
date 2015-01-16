
package batch.plugins.impl.exitassignment;

import algo.graph.exitassignment.EarliestArrivalTransshipmentExitAssignment;
import algo.graph.exitassignment.ExitAssignment;
import algo.graph.exitassignment.ShortestPathExitAssignment;
import batch.plugins.AlgorithmicPlugin;
import org.zetool.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@PluginImplementation
public class EarliestArrivalExitAssignmentPlugin extends ShortestPathExitAssignment implements AlgorithmicPlugin<NetworkFlowModel, ExitAssignment> {

	@Override
	public String getName() {
		return "Earliest Arrival Transshipment based Exit Assignment";
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
    return new EarliestArrivalTransshipmentExitAssignment();
	}
}
