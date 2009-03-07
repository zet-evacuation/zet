/*
 * ExitAssignments.java
 *
 */

package algo.graph;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import algo.graph.shortestpath.Dijkstra;
import algo.graph.staticflow.mincost.SuccessiveShortestPath;
import algo.graph.util.PathDecomposition;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.MinHeap;
import ds.graph.Network;
import ds.graph.NetworkFlowModel;
import ds.graph.Node;
import ds.graph.MinHeap.Element;
import ds.graph.flow.FlowOverTime;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.flow.PathBasedFlow;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.flow.StaticPathFlow;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class ExitAssignments {
    
    protected IdentifiableObjectMapping<Node, MinHeap> exitDistances;
    protected IdentifiableObjectMapping<Node, Node> shortestPathExits;
    protected IdentifiableObjectMapping<Node, List> minimumCostExits;
    protected IdentifiableObjectMapping<Node, List> eatExits;
    
    public void shortestPaths(NetworkFlowModel model) {         
        
        
        long start = System.currentTimeMillis();
        
        Network network = model.getNetwork();
        shortestPathExits = new IdentifiableObjectMapping<Node, Node>(network.numberOfNodes(), Node.class);
        minimumCostExits = new IdentifiableObjectMapping<Node, List>(network.numberOfNodes(), List.class);
        eatExits = new IdentifiableObjectMapping<Node, List>(network.numberOfNodes(), List.class);
        IdentifiableCollection<Node> sinks = network.predecessorNodes(model.getSupersink());
        System.out.println(sinks);
        Dijkstra dijkstra = new Dijkstra(network, model.getTransitTimes(), null, true);
        exitDistances = new IdentifiableObjectMapping<Node, MinHeap>(network.nodes(), MinHeap.class);
        for (Node sink : sinks) {
            dijkstra.setSource(sink);
            dijkstra.run();
            for (Node source : model.getSources()) {
                if (!exitDistances.isDefinedFor(source)) {
                    exitDistances.set(source, new MinHeap());
                }
                System.out.println(source + " <- "  + sink + "(" + dijkstra.getDistance(source) + ")");
                exitDistances.get(source).insert(sink, dijkstra.getDistance(source));
            }
        }
        for (Node source : model.getSources()) {
            //System.out.println(source);
            //System.out.println(exitDistances.get(source).isEmpty());
            shortestPathExits.set(source, (Node) exitDistances.get(source).extractMin().getObject());
        }
        
        System.out.println("Kürzeste Wege Zuweisung berechnet in " + (System.currentTimeMillis() - start) + " ms:");
        System.out.println(shortestPathExits);
        
        start = System.currentTimeMillis();
        
        Network reducedNetwork = new Network(sinks.size() + model.getSources().size(), sinks.size() * model.getSources().size());
        IdentifiableIntegerMapping<Edge> reducedTransitTimes = new IdentifiableIntegerMapping<Edge>(sinks.size() * model.getSources().size());
        IdentifiableIntegerMapping<Edge> reducedCapacities = new IdentifiableIntegerMapping<Edge>(sinks.size() * model.getSources().size());
        IdentifiableIntegerMapping<Node> reducedBalances = new IdentifiableIntegerMapping<Node>(sinks.size() + model.getSources().size());
        int index = 0;
        List<Node> reducedSources = new LinkedList<Node>();
        List<Node> reducedSinks = new LinkedList<Node>();
        for (Node source : model.getSources()) {
            while (!exitDistances.get(source).isEmpty()) {
                MinHeap<Node, Integer>.Element element = (Element) exitDistances.get(source).extractMin();
                int sinkIndex = -1;
                for (Node sink : sinks) {
                    sinkIndex++;
                    if (sink == element.getObject()) {
                        break;
                    }
                }
                Edge edge = reducedNetwork.createAndSetEdge(reducedNetwork.getNode(index), reducedNetwork.getNode(model.getSources().size() + sinkIndex));
                reducedTransitTimes.set(edge, element.getPriority());
                reducedCapacities.set(edge, Integer.MAX_VALUE);
            }
            reducedBalances.set(reducedNetwork.getNode(index), model.getCurrentAssignment().get(source));
            reducedSources.add(reducedNetwork.getNode(index));
            index++;
        }
        int sinkIndex = 0;
        int totalCapacities = 0;
        for (Node sink : sinks) {
            for (Edge edge : network.incomingEdges(sink)) {
                totalCapacities += model.getEdgeCapacity(edge);
            }        
        }
        int totalSupplies = 0;
        for (Node source : model.getSources()) {
            totalSupplies += model.getCurrentAssignment().get(source);
        }        
        for (Node sink : sinks) {
            int capacity = 0;
            for (Edge edge : network.incomingEdges(sink)) {
                capacity += model.getEdgeCapacity(edge);
            }
            reducedBalances.set(reducedNetwork.getNode(index + sinkIndex), (int) -Math.ceil(capacity * 1.0 / totalCapacities * totalSupplies));
            reducedSinks.add(reducedNetwork.getNode(index + sinkIndex));
            sinkIndex++;
        }                    
        SuccessiveShortestPath ssp = new SuccessiveShortestPath(reducedNetwork, reducedBalances, reducedCapacities, reducedTransitTimes, true);
        ssp.run();
        PathBasedFlow pathDecomposition = PathDecomposition.calculatePathDecomposition(reducedNetwork, reducedBalances, reducedSources, reducedSinks, ssp.getFlow());
        LinkedList<Node> sinks2 = new LinkedList<Node>();
        for (Node sink : sinks) {
            sinks2.add(sink);
        }
        for (StaticPathFlow path : pathDecomposition) {
            Edge edge = path.firstEdge();
            Node source = model.getSources().get(edge.start().id());
            Node sink = sinks2.get(edge.end().id() - model.getSources().size());
            if (!minimumCostExits.isDefinedFor(source)) {
                minimumCostExits.set(source, new LinkedList());
            }
            minimumCostExits.get(source).add(sink);
        }
        System.out.println("Minimale Fahrzeiten Zuweisung berechnet in " + (System.currentTimeMillis() - start) + " ms:");
        System.out.println(minimumCostExits);
        
        start = System.currentTimeMillis();
        
        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), 0, model.getTransitTimes(), model.getCurrentAssignment());            
        LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();
        
        start = System.currentTimeMillis();
        problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), estimator.getSolution().getUpperBound(), model.getTransitTimes(), model.getCurrentAssignment());
        SEAAPAlgorithm algo = new SEAAPAlgorithm();
        algo.setProblem(problem);
        algo.run();
        
        FlowOverTime eat = algo.getSolution();
        PathBasedFlowOverTime eatPaths = eat.getPathBased();
        for (FlowOverTimePath path : eatPaths) {
            Node source = path.firstEdge().start();
            Node sink = path.lastEdge().start();
            if (!eatExits.isDefinedFor(source)) {
                eatExits.set(source, new LinkedList());
            }
            eatExits.get(source).add(sink);            
        }
        
        System.out.println("EAT Zuweisung berechnet in " + (System.currentTimeMillis() - start) + " ms:");
        System.out.println(eatExits);
        //System.out.println(shortestPathExits);
    }
}
