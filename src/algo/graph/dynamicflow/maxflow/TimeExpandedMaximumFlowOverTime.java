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

import algo.graph.staticflow.maxflow.DischargingGlobalGapHighestLabelPreflowPushAlgorithm;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.DynamicPath;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.NetworkFlowModel;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlow;
import ds.graph.StaticPath;
import ds.graph.flow.StaticPathFlow;
import ds.graph.TimeExpandedNetwork;
import java.util.LinkedList;

import util.DebugFlags;
import algo.graph.staticflow.maxflow.MaximumFlowProblem;
import algo.graph.util.PathComposition;
import algo.graph.util.PathDecomposition;
//import statistic.graph.FlowStatisticsCalculator;
import ds.graph.flow.MaximumFlow;
import sandbox.Algorithm;
import tasks.AlgorithmTask;

/**
 *
 */
public class TimeExpandedMaximumFlowOverTime implements Runnable {
    
    protected Network network;
    protected IdentifiableIntegerMapping<Edge> capacities;
    protected IdentifiableIntegerMapping<Edge> transitTimes;
    protected LinkedList<Node> sources;
    protected LinkedList<Node> sinks;
    protected int timeHorizon;
    
    protected PathBasedFlowOverTime dynamicFlow;
    protected int value;

    public TimeExpandedMaximumFlowOverTime(Network network, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Edge> transitTimes, LinkedList<Node> sources, LinkedList<Node> sinks, int timeHorizon) {
        this.network = network;
        this.capacities = capacities;
        this.transitTimes = transitTimes;
        this.sources = sources;
        this.sinks = sinks;
        this.timeHorizon = timeHorizon;
    }

    public PathBasedFlowOverTime getDynamicFlow() {
        return dynamicFlow;
    }   
    
    public int getValueOfMaximumFlowOverTime() {
        return value;
    }
    
    public void run() {
        int v = 0;
        if (sources.size()==0 || sinks.size()==0){
        	if (DebugFlags.MEL){
        		System.out.println("No individuals - no flow.");
        	}
    		dynamicFlow = new PathBasedFlowOverTime();
    		return;
        }
        for (Node source : sources) {
            for (Edge edge : network.outgoingEdges(source)) {
                v += capacities.get(edge) * timeHorizon;
            }
        }
        int w = 0;
        for (Node sink : sinks) {
            for (Edge edge : network.incomingEdges(sink)) {
                //System.out.println("X " + w);
                w += capacities.get(edge) * timeHorizon;
            }
        }      
        //System.out.println(v + " " + w + " " + Math.min(v, w) + ": " + timeHorizon);
        TimeExpandedNetwork ten = new TimeExpandedNetwork(network, capacities, transitTimes, timeHorizon, sources, sinks, v, false);	
        //PreflowPush maxFlow = new PreflowPush(ten, ten.capacities(), ten.sources(), ten.sinks());        
        //maxFlow.run();
        MaximumFlowProblem problem = new MaximumFlowProblem(ten, ten.capacities(), ten.sources(), ten.sinks());
        
        ////PreflowPushAlgorithm algorithm = new PreflowPushAlgorithm();
        ////algorithm.setProblem(problem);
        ////algorithm.run();
        
        Algorithm<MaximumFlowProblem, MaximumFlow> algorithm = new DischargingGlobalGapHighestLabelPreflowPushAlgorithm();
        algorithm.setProblem(problem);
        algorithm.run();        
        
        //value = maxFlow.getValueOfMaximumFlow();
        value = algorithm.getSolution().getFlowValue();
        //PathBasedFlow decomposedFlow = PathDecomposition.calculatePathDecomposition(ten, ten.sources(), ten.sinks(), maxFlow.getFlow());
        PathBasedFlow decomposedFlow = PathDecomposition.calculatePathDecomposition(ten, ten.sources(), ten.sinks(), algorithm.getSolution());
        dynamicFlow = new PathBasedFlowOverTime();
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
            System.out.println(value);
            AlgorithmTask.getInstance().publish(100, "TimeExpandedMaximumFlowOverTime", "The maximal flow value is: " + value);
            System.out.println("It consists of the following dynamic path flows:");
            System.out.println(dynamicFlow);
        }        
    }
    
    public static void main (String args[])  {
        
        Network network = new Network(9, 10);
        Node source1 = network.getNode(0);
        Node source2 = network.getNode(1);
        Node source3 = network.getNode(4);
        Node sink1 = network.getNode(5);
        Node sink2 = network.getNode(6);
        Node sink3 = network.getNode(8);
        Node a = network.getNode(2);
        Node b = network.getNode(3);
        Node c = network.getNode(7);
        Edge e = new Edge(0, source1, a);
        Edge f = new Edge(1, source1, b);
        Edge g = new Edge(2, source2, a);
        Edge h = new Edge(3, source2, source3);
        Edge i = new Edge(4, a, b);
        Edge j = new Edge(5, a, source3);
        Edge k = new Edge(6, b, sink1);
        Edge l = new Edge(7, b, sink2);
        Edge m = new Edge(8, source3, c);
        Edge n = new Edge(9, sink2, sink3);
        network.setEdge(e);
        network.setEdge(f);
        network.setEdge(g);
        network.setEdge(h);
        network.setEdge(i);
        network.setEdge(j);
        network.setEdge(k);
        network.setEdge(l);
        network.setEdge(m);
        network.setEdge(n);
        IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges());
        capacities.set(e, 1);
        capacities.set(f, 1);
        capacities.set(g, 2);
        capacities.set(h, 2);
        capacities.set(i, 3);
        capacities.set(j, 3);
        capacities.set(k, 1);
        capacities.set(l, 2);
        capacities.set(m, 1);
        capacities.set(n, 1);
        IdentifiableIntegerMapping<Edge> transitTimes = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges());
        transitTimes.set(e, 1);
        transitTimes.set(f, 1);
        transitTimes.set(g, 2);
        transitTimes.set(h, 2);
        transitTimes.set(i, 3);
        transitTimes.set(j, 3);
        transitTimes.set(k, 1);
        transitTimes.set(l, 2);
        transitTimes.set(m, 1);
        transitTimes.set(n, 1);
        LinkedList<Node> sources = new LinkedList<Node>();
        sources.add(source1);
        sources.add(source2);
        sources.add(source3);
        LinkedList<Node> sinks = new LinkedList<Node>();
        sinks.add(sink1);
        sinks.add(sink2);
        sinks.add(sink3);
        IdentifiableIntegerMapping<Node> supplies = new IdentifiableIntegerMapping<Node>(network.numberOfNodes());
        supplies.set(source1, 2);
        supplies.set(source2, 1);
        supplies.set(source3, 0);
        supplies.set(sink1, -1);
        supplies.set(sink2, -1);
        supplies.set(sink3, -1);        
        TimeExpandedMaximumFlowOverTime algo = new TimeExpandedMaximumFlowOverTime(network, capacities, transitTimes, sources, sinks, 10);
        algo.run();
        //MinimumMeanCycleCancelling algo2 = new MinimumMeanCycleCancelling(network, capacities, transitTimes, supplies);
        //algo2.run();
        System.out.println(network);
        System.out.println(algo.getDynamicFlow());
        PathComposition fc = new PathComposition(network, transitTimes, algo.getDynamicFlow());
        fc.run();
        System.out.println(fc.getEdgeFlows());
        NetworkFlowModel nfm = new NetworkFlowModel();
        nfm.setNetwork(network);
        nfm.setEdgeCapacities(capacities);
        nfm.setTransitTimes(transitTimes);
        nfm.setCurrentAssignment(supplies);
        // fs = new FlowStatisticsCalculator(nfm, algo.getDynamicFlow());
        //System.out.println(fs.getFlowRate(e));
        //System.out.println(algo.getValueOfMaximumFlowOverTime());
        //System.out.println(algo.getDynamicFlow());
    }    
}
