/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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
import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import batch.tasks.AlgorithmTask;
import ds.NetworkFlowModel;
import ds.graph.flow.FlowOverTime;
import ds.graph.flow.PathBasedFlowOverTime;

public class SuccessiveEarliestArrivalAugmentingPathAlgorithmTask3 extends Transformation<NetworkFlowModel, EarliestArrivalFlowProblem, FlowOverTime, PathBasedFlowOverTime> {

    public SuccessiveEarliestArrivalAugmentingPathAlgorithmTask3() {
        setAlgorithm(new SEAAPAlgorithm());
    }

    @Override
    protected EarliestArrivalFlowProblem transformProblem(NetworkFlowModel originalProblem) {
        System.out.println("EAT Task Begins");
        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(originalProblem.getEdgeCapacities(), originalProblem.getNetwork(), originalProblem.getNodeCapacities(), originalProblem.getSupersink(), originalProblem.getSources(), 0, originalProblem.getTransitTimes(), originalProblem.getCurrentAssignment());
        LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();
        System.out.println(estimator.getSolution());
        problem = new EarliestArrivalFlowProblem(originalProblem.getEdgeCapacities(), originalProblem.getNetwork(), originalProblem.getNodeCapacities(), originalProblem.getSupersink(), originalProblem.getSources(), estimator.getSolution().getUpperBound(), originalProblem.getTransitTimes(), originalProblem.getCurrentAssignment());
        return problem;
    }

    @Override
    protected PathBasedFlowOverTime transformSolution(FlowOverTime transformedSolution) {
        PathBasedFlowOverTime df = transformedSolution.getPathBased();
        String result = String.format("Sent %1$s of %2$s flow units in %3$s time units successfully.", transformedSolution.getFlowAmount(), getAlgorithm().getProblem().getTotalSupplies(), transformedSolution.getTimeHorizon());
        System.out.println(result);
        AlgorithmTask.getInstance().publish(100, result, "");
        System.out.println(String.format("Sending the flow units required %1$s ms.", getAlgorithm().getRuntime() / 1000000));
        return df;
    }

    //@Override
    //protected PathBasedFlowOverTime runAlgorithm(NetworkFlowModel model) {
        /*
        System.out.println("EAT Task Begins");
        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), 0, model.getTransitTimes(), model.getCurrentAssignment());
        LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();
        System.out.println(estimator.getSolution());
        problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), estimator.getSolution().getUpperBound(), model.getTransitTimes(), model.getCurrentAssignment());
        //problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), estimator.getSolution().getUpperBound(), model.getTransitTimes(), model.getCurrentAssignment());
        */
        // System.out.println("Creating Algorithm Instance");
        //SEAAPAlgorithm algo = new SEAAPAlgorithm();
        //algo.addAlgorithmListener(AlgorithmTask.getInstance());
        //if (listener != null) algo.addAlgorithmListener(listener);
        //algo.setProblem(problem);
        //System.out.println("Calling Algorithm Instance");
        //algo.run();

    //}
}
