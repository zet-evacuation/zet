/*
 * SuccessiveEarliestArrivalAugmentingPathAlgorithm.java
 *
 */
package algo.graph.dynamicflow.eat;

import ds.graph.DynamicResidualNetwork;
import ds.graph.Node;
import ds.graph.flow.FlowOverTime;
import java.util.LinkedList;
import sandbox.Algorithm;

/**
 *
 * @author Martin Groß
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithm extends Algorithm<EarliestArrivalFlowProblem, FlowOverTime> {

    @Override
    protected FlowOverTime runAlgorithm(EarliestArrivalFlowProblem problem) {
        DynamicResidualNetwork drn = new DynamicResidualNetwork(problem.getNetwork(), problem.getEdgeCapacities(), problem.getNodeCapacities(), problem.getTransitTimes(), problem.getSources(), problem.getSupplies(), problem.getTimeHorizon());
        EarliestArrivalAugmentingPathProblem pathProblem = new EarliestArrivalAugmentingPathProblem(drn, drn.getSuperSource(), problem.getSink(), problem.getTimeHorizon());
        EarliestArrivalAugmentingPathAlgorithm pathAlgorithm = new EarliestArrivalAugmentingPathAlgorithm();
        pathAlgorithm.setProblem(pathProblem);
        pathAlgorithm.run();
        EarliestArrivalAugmentingPath path = pathAlgorithm.getSolution();
        int flowUnitsSent = 0;
        int flowUnitsTotal = 0;
        for (Node source : problem.getSources()) {
            flowUnitsTotal += problem.getSupplies().get(source);
        }
        LinkedList<EarliestArrivalAugmentingPath> paths = new LinkedList<EarliestArrivalAugmentingPath>();
        while (!path.isEmpty() && path.getCapacity() > 0) {
            flowUnitsSent += path.getCapacity();
            fireProgressEvent(flowUnitsSent * 1.0 / flowUnitsTotal);
            paths.add(path);
            drn.augmentPath(path);
            pathAlgorithm = new EarliestArrivalAugmentingPathAlgorithm();
            pathAlgorithm.setProblem(pathProblem);
            pathAlgorithm.run();
            path = pathAlgorithm.getSolution();
        }
        FlowOverTime flow = new FlowOverTime(drn, paths);
        return flow;
    }
}
