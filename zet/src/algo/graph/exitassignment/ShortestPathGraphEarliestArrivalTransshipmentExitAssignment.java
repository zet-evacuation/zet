/*
 * MinimumCostTransshipmentExitAssignment.java
 *
 */
package algo.graph.exitassignment;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SEAAPAlgorithm;
import algo.graph.dynamicflow.eat.TimeHorizonBounds;
import algo.graph.shortestpath.Dijkstra;
import algo.graph.staticflow.maxflow.DischargingGlobalGapHighestLabelPreflowPushAlgorithm;
import algo.graph.staticflow.maxflow.MaximumFlowProblem;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.NetworkFlowModel;
import ds.graph.Node;
import ds.graph.Path;
import ds.graph.flow.FlowOverTime;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.flow.MaximumFlow;
import ds.graph.flow.PathBasedFlowOverTime;
import sandbox.Algorithm;

/**
 *
 * @author Martin Groß
 */
public class ShortestPathGraphEarliestArrivalTransshipmentExitAssignment extends Algorithm<NetworkFlowModel, ExitAssignment> implements Assignable {

    @Override
    protected ExitAssignment runAlgorithm(NetworkFlowModel model) {
        ExitAssignment solution = new ExitAssignment(model.getNetwork().nodes());

        Network network = model.getNetwork();
        IdentifiableCollection<Node> sinks = network.predecessorNodes(model.getSupersink());

        Dijkstra dijkstra = new Dijkstra(network, model.getTransitTimes(), null);
        IdentifiableObjectMapping<Edge, Boolean> shortestEdges = new IdentifiableObjectMapping<Edge, Boolean>(model.getNetwork().edges(), Boolean.class);
        for (Node source : model.getSources()) {
            dijkstra.setSource(source);
            dijkstra.run();
            Forest shortestPathTree = dijkstra.getShortestPathTree();
            for (Node sink : sinks) {
                Path path = shortestPathTree.getPathToRoot(sink);
                for (Edge edge : path) {
                    shortestEdges.set(network.getEdge(edge.start(), edge.end()), true);
                }
            }
        }
        for (Edge edge : network.incomingEdges(model.getSupersink())) {
            shortestEdges.set(edge, true);
        }
        IdentifiableIntegerMapping<Edge> reducedCapacities = new IdentifiableIntegerMapping<Edge>(model.getEdgeCapacities());
        for (Edge edge : network.edges()) {
            if (!shortestEdges.isDefinedFor(edge)) {
                reducedCapacities.set(edge, 0);
            }
        }

        EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(reducedCapacities, model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), 0, model.getTransitTimes(), model.getCurrentAssignment());            
        Algorithm<EarliestArrivalFlowProblem, TimeHorizonBounds> estimator = new LongestShortestPathTimeHorizonEstimator();
        estimator.setProblem(problem);
        estimator.run();
        
        problem = new EarliestArrivalFlowProblem(reducedCapacities, model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), estimator.getSolution().getUpperBound(), model.getTransitTimes(), model.getCurrentAssignment());       
        Algorithm<EarliestArrivalFlowProblem, FlowOverTime> algorithm = new SEAAPAlgorithm();
        algorithm.setProblem(problem);
        algorithm.run();
        PathBasedFlowOverTime paths = algorithm.getSolution().getPathBased();
        for (FlowOverTimePath path : paths) {
            Node start = path.firstEdge().start();
            Node exit = path.lastEdge().start();
            for (int i = 0; i < path.getRate(); i++) {
                solution.assignIndividualToExit(start, exit);
            }
        }        
        return solution;
    }

    protected int estimateCapacityByIncomingEdges(NetworkFlowModel model, Node sink) {
        IdentifiableCollection<Node> sinks = model.getNetwork().predecessorNodes(model.getSupersink());
        int result = 0;
        for (Edge edge : model.getNetwork().incomingEdges(sink)) {
            if (sinks.contains(edge.start())) {
                continue;
            }
            result += model.getEdgeCapacity(edge);
        }
        return result;
    }

    protected int estimateCapacityByMaximumFlow(NetworkFlowModel model, Node sink) {
        IdentifiableCollection<Node> sinks = model.getNetwork().predecessorNodes(model.getSupersink());
        IdentifiableIntegerMapping<Edge> newCapacities = new IdentifiableIntegerMapping<Edge>(model.getEdgeCapacities());
        for (Node s : sinks) {
            for (Edge edge : model.getNetwork().outgoingEdges(s)) {
                newCapacities.set(edge, 0);
            }
        }
        MaximumFlowProblem problem = new MaximumFlowProblem(model.getNetwork(), newCapacities, model.getSources(), sink);
        Algorithm<MaximumFlowProblem, MaximumFlow> algorithm = new DischargingGlobalGapHighestLabelPreflowPushAlgorithm();
        algorithm.setProblem(problem);
        algorithm.run();
        return algorithm.getSolution().getFlowValue();
    }

    /**
     * Returns the calculated exit assignment.
     * @return the calculated exit assignment.
     */
    public ExitAssignment getExitAssignment() {
        return getSolution();
    }
}
