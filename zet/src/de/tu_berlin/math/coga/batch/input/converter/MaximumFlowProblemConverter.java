/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.batch.input.converter;

import de.tu_berlin.math.coga.algorithm.flowovertime.maxflow.MaximumFlowOverTimeProblem;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.Edge;
import ds.graph.network.Network;
import ds.graph.problem.MaximumFlowProblem;
import ds.graph.problem.RawMaximumFlowProblem;
import ds.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author Martin
 */
public class MaximumFlowProblemConverter extends Algorithm<RawMaximumFlowProblem,MaximumFlowProblem> {

    @Override
    protected MaximumFlowProblem runAlgorithm(RawMaximumFlowProblem problem) {
        Network network = new Network(problem.getNumberOfNodes(), problem.getNumberOfEdges());
        IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<>(problem.getNumberOfEdges());
        for (int nodeIndex = 0; nodeIndex < problem.getNumberOfNodes(); nodeIndex++) {
            int nextStartIndex = ((nodeIndex+1 < problem.getNumberOfNodes())? problem.getNodeStartIndices()[nodeIndex+1] : problem.getNumberOfNodes());
            for (int edgeOfNodeIndex = problem.getNodeStartIndices()[nodeIndex]; edgeOfNodeIndex < nextStartIndex; edgeOfNodeIndex++) {
                Edge edge = network.createAndSetEdge(network.getNode(nodeIndex), network.getNode(problem.getEdgeEndIDs()[edgeOfNodeIndex]));
                capacities.set(edge, problem.getEdgeCapacities()[edge.id()]);
            }
        }        
        MaximumFlowProblem solution = new MaximumFlowProblem(network, capacities, network.getNode(problem.getSourceID()), network.getNode(problem.getSinkID()));
        return solution;
    }    
}
