
package batch.plugins.impl.eat;

import de.tu_berlin.coga.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.netflow.ds.flow.PathBasedFlowOverTime;
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
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

}
