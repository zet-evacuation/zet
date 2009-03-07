/*
 * BFS.java
 *
 */
package algo.graph.traverse;

import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Martin Groß
 */
public class BFS {

    private Graph graph;
    private IdentifiableIntegerMapping<Node> distances;
    private IdentifiableObjectMapping<Node, Edge> predecedingEdges;
    private IdentifiableObjectMapping<Node, Boolean> visited;

    public BFS(Graph graph) {
        this.graph = graph;
        distances = new IdentifiableIntegerMapping<Node>(graph.numberOfNodes());
        predecedingEdges = new IdentifiableObjectMapping<Node, Edge>(graph.numberOfNodes(), Edge.class);
        visited = new IdentifiableObjectMapping<Node, Boolean>(graph.numberOfNodes(), Boolean.class);
        for (Node node : graph.nodes()) {
            distances.set(node, Integer.MAX_VALUE);
            predecedingEdges.set(node, null);
            visited.set(node, false);
        }
    }

    public int distance(Node node) {
        return distances.get(node);
    }

    public Edge predecedingEdge(Node node) {
        return predecedingEdges.get(node);
    }

    public void run(Node start) {
        run(start, null, false, false);
    }

    public void run(Node start, Node end) {
        run(start, end, false, false);
    }

    public void run(Node start, Node end, boolean longest, boolean reverse) {
        List<Node> sources = new LinkedList<Node>();
        sources.add(start);
        List<Node> sinks = new LinkedList<Node>();
        sinks.add(end);
        run(sources, sinks, false, false);
    }    
    
    public void run(Iterable<Node> sources, List<Node> sinks, boolean longest, boolean reverse) {
        Queue<Node> queue = new LinkedList<Node>();
        for (Node source : sources) {
            queue.offer(source);
            distances.set(source, 0);
            visited.set(source, true);
        }
        while (!queue.isEmpty()) {
            Node v = queue.poll();
            if (sinks != null && sinks.contains(v)) {
                return;
            }
            Iterable<Edge> edges;
            if (reverse) {
                edges = graph.incomingEdges(v);
            } else {
                edges = graph.outgoingEdges(v);
            }
            for (Edge edge : edges) {
                Node w = edge.opposite(v);
                if (!visited.get(w)) {
                    queue.offer(w);
                    distances.set(w, distances.get(v) + 1);
                    predecedingEdges.set(w, edge);
                    visited.set(w, true);
                } else if (longest && distances.get(w) < distances.get(v) + 1) {
                    queue.offer(w);
                    distances.set(w, distances.get(v) + 1);
                    predecedingEdges.set(w, edge);
                }
            }
        }
    }
}
