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
 * TimeExpandedMaximumFlowOverTime.java
 *
 */
package algo.graph.dynamicflow.maxflow;

import algo.graph.Algorithm;
import algo.graph.DebugFlags;
import algo.graph.staticflow.maxflow.DischargingGlobalGapHighestLabelPreflowPushAlgorithm;
import algo.graph.util.PathComposition;
import algo.graph.util.PathDecomposition;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.DynamicPath;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.problem.MaximumFlowProblem;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlow;
import ds.graph.StaticPath;
import ds.graph.flow.StaticPathFlow;
import ds.graph.TimeExpandedNetwork;
import ds.graph.flow.MaximumFlow;
import java.util.LinkedList;

import tasks.AlgorithmTask;

/**
 *
 * @author Martin Groß
 */
public class TimeExpandedMaximumFlowOverTime extends Algorithm<MaximumFlowOverTimeProblem, PathBasedFlowOverTime> {

    public TimeExpandedMaximumFlowOverTime(Network network, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Edge> transitTimes, LinkedList<Node> sources, LinkedList<Node> sinks, int timeHorizon) {
        setProblem(new MaximumFlowOverTimeProblem(network, capacities, transitTimes, sources, sinks, timeHorizon));
    }

    @Override
    protected PathBasedFlowOverTime runAlgorithm(MaximumFlowOverTimeProblem problem) {
        int v = 0;
        if (problem.getSources().size() == 0 || problem.getSinks().size() == 0) {
            if (DebugFlags.MEL) {
                System.out.println("No individuals - no flow.");
            }
            return new PathBasedFlowOverTime();
        }
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
                continue;
            }
            StaticPath staticPath = staticPathFlow.getPath();
            DynamicPath dynamicPath = ten.translatePath(staticPath);
            FlowOverTimePath dynamicPathFlow = new FlowOverTimePath(dynamicPath, staticPathFlow.getAmount(), staticPathFlow.getAmount());
            dynamicFlow.addPathFlow(dynamicPathFlow);
        }
        if (DebugFlags.TEMFOT) {
            System.out.println("The maximum flow over time has the following value:");
            System.out.println(algorithm.getSolution().getFlowValue());
            AlgorithmTask.getInstance().publish(100, "TimeExpandedMaximumFlowOverTime", "The maximal flow value is: " + algorithm.getSolution().getFlowValue());
            System.out.println("It consists of the following dynamic path flows:");
            System.out.println(dynamicFlow);
        }
        return dynamicFlow;
    }
}
