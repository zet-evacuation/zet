/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.algorithm.shortestpath;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.Node;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.container.mapping.IdentifiableObjectMapping;

/**
 *
 * @author gross
 */
public class MooreBellmanFord extends Algorithm<ShortestPathProblem, ShortestPaths> {

    @Override
    protected ShortestPaths runAlgorithm(ShortestPathProblem problem) {
        IdentifiableIntegerMapping<Edge> costs = problem.getCosts();
        Graph graph = problem.getGraph();
        Node source = problem.getSource();
        IdentifiableIntegerMapping<Node> distances = new IdentifiableIntegerMapping<>(graph.numberOfNodes());
        IdentifiableObjectMapping<Node, Edge> edges = new IdentifiableObjectMapping<>(graph.numberOfNodes(), Edge.class);
        IdentifiableObjectMapping<Node, Node> nodes = new IdentifiableObjectMapping<>(graph.numberOfNodes(), Node.class);
        for (Node node : graph.nodes()) {
            distances.set(node, Integer.MAX_VALUE);
        }
        distances.set(source, 0);
        for (int i = 0; i < graph.numberOfNodes(); i++) {
            for (Edge e : graph.edges()) {
                Node v = e.start();
                Node w = e.end();
                long dw = distances.get(w);
                long dv = distances.get(v);
                if (dw > dv + costs.get(e)) {
                    distances.set(w, distances.get(v) + costs.get(e));
                    edges.set(w, e);
                    nodes.set(w, v);
                }
            }
        }
        return new ShortestPaths(graph, distances);
    }
}
