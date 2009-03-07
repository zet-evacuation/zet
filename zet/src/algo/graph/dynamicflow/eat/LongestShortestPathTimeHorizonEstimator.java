/*
 * LongestShortestPathTimeHorizonEstimator.java
 *
 */

package algo.graph.dynamicflow.eat;

import sandbox.*;
import algo.graph.shortestpath.Dijkstra;
import ds.graph.Node;

/**
 *
 * @author Martin Groß
 */
public class LongestShortestPathTimeHorizonEstimator extends Algorithm<EarliestArrivalFlowProblem, TimeHorizonBounds> {

    @Override
    protected TimeHorizonBounds runAlgorithm(EarliestArrivalFlowProblem problem) {
        int longest = 0;
        for (Node source : problem.getSources()) {
            Dijkstra dijkstra = new Dijkstra(problem.getNetwork(), problem.getTransitTimes(), source);
            dijkstra.run();
            if (dijkstra.getDistance(problem.getSink()) > longest) {
                longest = dijkstra.getDistance(problem.getSink());
            }
        }
        int supply = Math.abs(problem.getSupplies().get(problem.getSink()));
        return new TimeHorizonBounds(longest + 1, longest + supply + 1);
    }

}
