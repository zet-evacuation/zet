/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import algo.graph.staticflow.maxflow.DischargingGlobalGapHighestLabelPreflowPushAlgorithm;
import algo.graph.staticflow.maxflow.MaximumFlowProblem;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.NetworkFlowModel;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import sandbox.Algorithm;

/**
 *
 * @author Joscha
 */
public class ExitCapacityEstimator {
    
    public ExitCapacityEstimator(){
        
    }
    
    public int estimateCapacityByMaximumFlow(NetworkFlowModel model, Node sink) {
        IdentifiableCollection<Node> sinks = model.getNetwork().predecessorNodes(model.getSupersink());
        IdentifiableIntegerMapping<Edge> newCapacities = new IdentifiableIntegerMapping<Edge>(model.getEdgeCapacities());
        for (Node s : sinks) {
            for (Edge edge : model.getNetwork().outgoingEdges(s)) {
                //if (sinks.contains(edge.start())) {
                newCapacities.set(edge, 0);
            //}
            }
        }
        MaximumFlowProblem problem = new MaximumFlowProblem(model.getNetwork(), newCapacities, model.getSources(), sink);
        Algorithm<MaximumFlowProblem, MaximumFlow> algorithm = new DischargingGlobalGapHighestLabelPreflowPushAlgorithm();
        algorithm.setProblem(problem);
        algorithm.run();
        return algorithm.getSolution().getFlowValue();
    }
    
    public int estimateCapacityByIncomingEdges(NetworkFlowModel model, Node sink) {
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

}
