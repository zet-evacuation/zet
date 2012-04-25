package de.tu_berlin.math.coga.batch.input.converter;


import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.Edge;
//import ds.graph.IdentifiableIntegerMapping;
//import ds.graph.Network;
import ds.graph.Node;
import ds.graph.network.Network;
import ds.graph.problem.MinimumCostFlowProblem;
import ds.graph.problem.RawMinimumCostFlowProblem;
import ds.mapping.IdentifiableIntegerMapping;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gross
 */
public class MinimumFlowProblemConverter extends Algorithm<RawMinimumCostFlowProblem, MinimumCostFlowProblem> {

    @Override
    protected MinimumCostFlowProblem runAlgorithm(RawMinimumCostFlowProblem problem) {
        Network network = new Network(problem.getNumberOfNodes(), problem.getNumberOfEdges());
        IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<>(problem.getNumberOfEdges());
        IdentifiableIntegerMapping<Edge> costs = new IdentifiableIntegerMapping<>(problem.getNumberOfEdges());
        IdentifiableIntegerMapping<Node> supplies = new IdentifiableIntegerMapping<>(problem.getNumberOfNodes());
        int[] nodeStartIndices = problem.getNodeStartIndices();
        int[] edgeEndIDs = problem.getEdgeEndIDs();
        for (int nodeIndex = 0; nodeIndex < problem.getNumberOfNodes(); nodeIndex++) {
            Node node = network.getNode(nodeIndex);
            supplies.set(node, problem.getSupply()[nodeIndex]);
            int nextNodeStartIndex = (nodeIndex == problem.getNumberOfNodes() - 1)? problem.getNumberOfEdges() : nodeStartIndices[nodeIndex+1];
            for (int edgeIndex = nodeStartIndices[nodeIndex]; edgeIndex < nextNodeStartIndex; edgeIndex++) {
                //if (network.existsEdge(node, network.getNode(edgeEndIDs[edgeIndex]))) parallelEdges;
                Edge edge = network.createAndSetEdge(node, network.getNode(edgeEndIDs[edgeIndex]));                
                capacities.set(edge, problem.getEdgeCapacities()[edgeIndex]);
                costs.set(edge, problem.getEdgeCosts()[edgeIndex]);
            }
        }
        return new MinimumCostFlowProblem(network, capacities, costs, supplies);
    }
    
}
