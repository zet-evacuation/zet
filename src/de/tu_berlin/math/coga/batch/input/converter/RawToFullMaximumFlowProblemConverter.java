/*
 * RawToFullMaximumFlowProblemConverter.java
 * 
 */
package de.tu_berlin.math.coga.batch.input.converter;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.netflow.ds.network.Network;
import de.tu_berlin.coga.netflow.classic.problems.MaximumFlowProblem;
import ds.graph.problem.RawMaximumFlowProblem;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;

/**
 * Converter that transforms memory-efficient raw maximum flow problems into
 * more verbose maximum flow problems that are based on full graphs.
 *
 * @author Martin Groß
 */
public class RawToFullMaximumFlowProblemConverter extends Algorithm<RawMaximumFlowProblem,MaximumFlowProblem> {

    /**
     * {@InheritDoc}
     */
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
