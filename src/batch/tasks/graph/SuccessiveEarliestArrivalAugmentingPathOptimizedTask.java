/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
/*
 * EATransshipmentTask.java
 * 
 */
package batch.tasks.graph;

import de.tu_berlin.math.coga.common.algorithm.Transformation;
import de.tu_berlin.math.coga.common.util.Formatter;
import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.flow.FlowOverTime;
import ds.graph.flow.PathBasedFlowOverTime;

/**
 * Transforms an instance of {@link NetworkFlowModel} to an {@link EarliestArrivalFlowProblem}
 * in order to solve it. It afterwards creates a path based flow solution.
 * @author Jan-Philipp Kappmeier
 */
public class SuccessiveEarliestArrivalAugmentingPathOptimizedTask extends Transformation<NetworkFlowModel, EarliestArrivalFlowProblem, FlowOverTime, PathBasedFlowOverTime> {

    public SuccessiveEarliestArrivalAugmentingPathOptimizedTask() {
        setAlgorithm(new SEAAPAlgorithm());
        //setAlgorithm(new OldSEAAPAlgorithm());
    }

    @Override
    protected EarliestArrivalFlowProblem transformProblem(NetworkFlowModel originalProblem) {
        System.out.println("Earliest arrival transshipment calculation starts");
        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(originalProblem.getEdgeCapacities(), originalProblem.getNetwork(), originalProblem.getNodeCapacities(), originalProblem.getSupersink(), originalProblem.getSources(), 0, originalProblem.getTransitTimes(), originalProblem.getCurrentAssignment());
        System.out.println("assign" + originalProblem.getCurrentAssignment());
        LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();
        System.out.println("Geschätzte Lösung:" + estimator.getSolution());
        problem = new EarliestArrivalFlowProblem(originalProblem.getEdgeCapacities(), originalProblem.getNetwork(), originalProblem.getNodeCapacities(), originalProblem.getSupersink(), originalProblem.getSources(), estimator.getSolution().getUpperBound(), originalProblem.getTransitTimes(), originalProblem.getCurrentAssignment());
        return problem;
    }

    @Override
    protected PathBasedFlowOverTime transformSolution(FlowOverTime transformedSolution) {
        PathBasedFlowOverTime df = transformedSolution.getPathBased();
        String result = String.format("Sent %1$s of %2$s flow units in %3$s time units successfully.", transformedSolution.getFlowAmount(), getAlgorithm().getProblem().getTotalSupplies(), transformedSolution.getTimeHorizon());
        System.out.println(result);
	System.out.println( "Total cost: " + transformedSolution.getTotalCost() );
        //AlgorithmTask.getInstance().publish(100, result, "");
        System.out.println( "Sending the flow units required " + Formatter.formatTimeUnit( getAlgorithm().getRuntime(), Formatter.TimeUnits.MilliSeconds ) );
        return df;
    }

}
