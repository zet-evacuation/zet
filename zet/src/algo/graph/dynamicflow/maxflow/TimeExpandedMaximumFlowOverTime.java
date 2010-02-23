/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * TimeExpandedMaximumFlowOverTime.java
 *
 */
package algo.graph.dynamicflow.maxflow;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import algo.graph.staticflow.maxflow.DischargingGlobalGapHighestLabelPreflowPushAlgorithm;
import algo.graph.util.PathDecomposition;
import ds.graph.DynamicPath;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.StaticPath;
import ds.graph.TimeExpandedNetwork;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.flow.MaximumFlow;
import ds.graph.flow.PathBasedFlow;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.flow.StaticPathFlow;
import ds.graph.problem.MaximumFlowProblem;
import java.util.LinkedList;

/**
 * Calculates a maximum flow over time by reducing it to the maximum flow 
 * problem using a time expanded network.
 * @author Martin Groß
 */
public class TimeExpandedMaximumFlowOverTime extends Algorithm<MaximumFlowOverTimeProblem, PathBasedFlowOverTime> {

    @Override
    protected PathBasedFlowOverTime runAlgorithm(MaximumFlowOverTimeProblem problem) {
        if (problem.getSources().size() == 0 || problem.getSinks().size() == 0) {
            System.out.println("TimeExpandedMaximumFlowOverTime: The problem is invalid - this should not happen!");
            return new PathBasedFlowOverTime();
        }
        int v = 0;        
        for (Node source : problem.getSources()) {
            for (Edge edge : problem.getNetwork().outgoingEdges(source)) {
                v += problem.getCapacities().get(edge) * problem.getTimeHorizon();
            }
        }
        TimeExpandedNetwork ten = new TimeExpandedNetwork(problem.getNetwork(), problem.getCapacities(), problem.getTransitTimes(), problem.getTimeHorizon(), problem.getSources(), problem.getSinks(), v, false);
        MaximumFlowProblem maximumFlowProblem = new MaximumFlowProblem(ten, ten.capacities(), ten.sources(), ten.sinks());
        Algorithm<MaximumFlowProblem, MaximumFlow> algorithm = new DischargingGlobalGapHighestLabelPreflowPushAlgorithm();
        algorithm.setProblem(maximumFlowProblem);
        algorithm.run();

        PathBasedFlow decomposedFlow = PathDecomposition.calculatePathDecomposition(ten, ten.sources(), ten.sinks(), algorithm.getSolution());
        PathBasedFlowOverTime dynamicFlow = new PathBasedFlowOverTime();
        for (StaticPathFlow staticPathFlow : decomposedFlow) {
            if (staticPathFlow.getAmount() == 0) {
                System.out.println("TimeExpandedMaximumFlowOverTime: There is a flow path with zero units - this should not happen!");
                continue;
            }
            StaticPath staticPath = staticPathFlow.getPath();
            DynamicPath dynamicPath = ten.translatePath(staticPath);
            FlowOverTimePath dynamicPathFlow = new FlowOverTimePath(dynamicPath, staticPathFlow.getAmount(), staticPathFlow.getAmount());
            dynamicFlow.addPathFlow(dynamicPathFlow);
        }
        return dynamicFlow;
    }
}
