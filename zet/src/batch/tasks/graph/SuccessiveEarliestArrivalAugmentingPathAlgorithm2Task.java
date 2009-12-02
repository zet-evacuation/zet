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
 * MaxFlowTask.java
 * 
 */
package batch.tasks.graph;

import ds.NetworkFlowModel;
import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SuccessiveEarliestArrivalAugmentingPathAlgorithm;
import batch.tasks.AlgorithmTask;
import batch.tasks.GraphAlgorithmTask;

/**
 *
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task extends GraphAlgorithmTask {

    public SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task(NetworkFlowModel model) {
        super(model);
    }

    @Override
    public void run() {
        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), 0, model.getTransitTimes(), model.getCurrentAssignment());
        LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();
        System.out.println(estimator.getSolution());
        problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), estimator.getSolution().getUpperBound(), model.getTransitTimes(), model.getCurrentAssignment());
        SuccessiveEarliestArrivalAugmentingPathAlgorithm algo = new SuccessiveEarliestArrivalAugmentingPathAlgorithm();
        algo.setProblem(problem);
        algo.run();
        df = algo.getSolution().getPathBased();
        String result = String.format("Sent %1$s of %2$s flow units in %3$s time units successfully.", algo.getSolution().getFlowAmount(), problem.getTotalSupplies(), algo.getSolution().getTimeHorizon());
        System.out.println(result);
        AlgorithmTask.getInstance().publish(100, result, "");
        System.out.println(String.format("Sending the flow units required %1$s ms.", algo.getRuntime() / 1000000));
    }
}
