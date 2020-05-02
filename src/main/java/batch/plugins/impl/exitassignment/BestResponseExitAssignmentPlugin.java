
package batch.plugins.impl.exitassignment;

import algo.graph.exitassignment.EarliestArrivalTransshipmentExitAssignment;
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
public class BestResponseExitAssignmentPlugin extends ShortestPathExitAssignment implements AlgorithmPlugin<NetworkFlowModel, ExitAssignment> {

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
	public AbstractAlgorithm<NetworkFlowModel, ExitAssignment> getAlgorithm() {
    return new EarliestArrivalTransshipmentExitAssignment();
	}

  @Override
  public String toString() {
    return getName();
  }
}
