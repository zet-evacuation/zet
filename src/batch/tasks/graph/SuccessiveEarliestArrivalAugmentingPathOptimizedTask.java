/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package batch.tasks.graph;

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.netflow.dynamic.LongestShortestPathTimeHorizonEstimator;
import org.zetool.netflow.dynamic.earliestarrival.SEAAPAlgorithm;
import org.zetool.common.algorithm.Transformation;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import org.zetool.netflow.ds.flow.FlowOverTimeImplicit;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;

/**
 * Transforms an instance of {@link NetworkFlowModel} to an {@link EarliestArrivalFlowProblem}
 * in order to solve it. It afterwards creates a path based flow solution.
 * @author Jan-Philipp Kappmeier
 */
public class SuccessiveEarliestArrivalAugmentingPathOptimizedTask extends Transformation<NetworkFlowModel, EarliestArrivalFlowProblem, FlowOverTimeImplicit, PathBasedFlowOverTime> {
	public SuccessiveEarliestArrivalAugmentingPathOptimizedTask() {
		setAlgorithm( new SEAAPAlgorithm() );
	}

	@Override
	protected EarliestArrivalFlowProblem transformProblem( NetworkFlowModel originalProblem ) {
		System.out.println( "Earliest arrival transshipment calculation starts" );
		EarliestArrivalFlowProblem problem = originalProblem.getEAFP();
		LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
		estimator.setProblem( problem );
		estimator.run();
		System.out.println( "Geschätzte Lösung:" + estimator.getSolution() );
		problem = originalProblem.getEAFP( estimator.getSolution().getUpperBound() );
		return problem;
	}

	@Override
	protected PathBasedFlowOverTime transformSolution( FlowOverTimeImplicit transformedSolution ) {
		PathBasedFlowOverTime df = transformedSolution.getPathBased();
		String result = String.format( "Sent %1$s of %2$s flow units in %3$s time units successfully.", transformedSolution.getFlowAmount(), getAlgorithm().getProblem().getTotalSupplies(), transformedSolution.getTimeHorizon() );
		System.out.println( result );
		System.out.println( "Total cost: " + transformedSolution.getTotalCost() );
		//AlgorithmTask.getInstance().publish(100, result, "");
		System.out.println( "Sending the flow units required " + getAlgorithm().getRuntime() );
		return df;
	}
}
