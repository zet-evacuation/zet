package de.tu_berlin.math.coga.batch.input.converter;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.netflow.ds.network.Network;
import de.tu_berlin.coga.netflow.classic.problems.MinimumCostFlowProblem;
import ds.graph.problem.RawMinimumCostFlowProblem;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;

/**
 * Converter that transforms memory-efficient raw minimum cost flow problems
 * into more verbose minimum cost flow problems that are based on full graphs.
 *
 * @author Martin Groß
 */
public class RawToFullMinimumFlowProblemConverter extends Algorithm<RawMinimumCostFlowProblem, MinimumCostFlowProblem> {

    /**
     * {@inheritDoc}
     */
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
                Edge edge = network.createAndSetEdge(node, network.getNode(edgeEndIDs[edgeIndex]));                
                capacities.set(edge, problem.getEdgeCapacities()[edgeIndex]);
                costs.set(edge, problem.getEdgeCosts()[edgeIndex]);
            }
        }
        return new MinimumCostFlowProblem(network, capacities, costs, supplies);
    }
    
}
