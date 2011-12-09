/*
 * Dijkstra.java
 *
 */

package algo.graph.shortestpath;

import ds.graph.network.DynamicNetwork;
import ds.graph.Edge;
import de.tu_berlin.math.coga.datastructure.priorityQueue.MinHeap;
import ds.graph.Node;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Martin Gro�
 */
public class DijkstraWithRationalDistances {
    
    private Map<Edge,Double> costs;
    private DynamicNetwork graph;
    private Node source;
    
    private Map<Node,Double> distances;
    private Map<Node,Edge> edges;
    private Map<Node,Node> nodes;
    
    public DijkstraWithRationalDistances() {
    }

    public DijkstraWithRationalDistances(DynamicNetwork graph, Map<Edge,Double> costs, Node source) {
        this.costs = costs;
        this.graph = graph;
        this.source = source;
    }    
    
    public Map<Node,Double> getDistances() {
        if (distances == null) {
            throw new IllegalStateException("run has not been called yet");
        }
        return distances;
    }
    
    public double getDistance(Node node) {
        if (distances == null) {
            throw new IllegalStateException("run has not been called yet");
        }
        return distances.get(node);
    }    
    
    public Map<Node,Edge> getLastEdges() {
        if (edges == null) {
            throw new IllegalStateException("run has not been called yet");
        }
        return edges;        
    }
    
    public Edge getLastEdge(Node node) {
        if (edges == null) {
            throw new IllegalStateException("run has not been called yet");
        }        
        return edges.get(node);
    }

    public Map<Node,Node> getPredecessors() {
        if (nodes == null) {
            throw new IllegalStateException("run has not been called yet");
        }
        return nodes;        
    }
    
    public Node getPredecessor(Node node) {
        if (nodes == null) {
            throw new IllegalStateException("run has not been called yet");
        }        
        return nodes.get(node);
    }    
    
    public DynamicNetwork getShortestPathTree() {
        return new DynamicNetwork(graph.nodes(),edges.values());
    }
    
    public DynamicNetwork getShortestPathGraph() {
        DynamicNetwork g = new DynamicNetwork();
        g.setNodes(graph.nodes());
        List<Edge> e = new LinkedList<Edge>(edges.values());
        Map<Edge,Boolean> chosen = new HashMap<Edge,Boolean>();
        for (Edge edge : edges.values()) {
            chosen.put(edge,true);
        }        
        for (int i=0; i<graph.numberOfEdges(); i++) {
            Edge edge = graph.getEdge(i);
            if (chosen.containsKey(edge)) continue;
            if (distances.get(edge.start())+costs.get(edge) == distances.get(edge.end())) {
                e.add(edge);
            }
        }
        g.setEdges(e);
        return g;
    }
    
    public boolean isInitialized() {
        return graph != null && source != null;
    }
    
    public void run() {
        if (graph == null) { throw new IllegalStateException("graph is null"); }
        if (source == null) { throw new IllegalStateException("source is null"); }
        if (distances != null) return;
        distances = new HashMap<Node,Double>(graph.numberOfNodes());
        edges = new HashMap<Node,Edge>(graph.numberOfNodes());
        nodes = new HashMap<Node,Node>(graph.numberOfNodes());
        MinHeap<Node,Double> queue = new MinHeap<Node,Double>(graph.numberOfNodes());
        for (int v=0; v<graph.numberOfNodes(); v++) {
            distances.put(graph.getNode(v),Double.POSITIVE_INFINITY);
            queue.insert(graph.getNode(v),Double.POSITIVE_INFINITY);
        }
        distances.put(source,0.0);
        queue.decreasePriority(source,0.0);
        while (!queue.isEmpty()) {
            MinHeap<Node,Double>.Element min = queue.extractMin();
            Node v = min.getObject();
            Double pv = min.getPriority();
            distances.put(v,pv);
            //List<Edge> incidentEdges = graph.outgoingEdges(v);
            List<Edge> incidentEdges = graph.incomingEdges(v);
            for (Edge edge : incidentEdges) {
                Node w = edge.opposite(v);
                if (queue.contains(w) && queue.priority(w) > pv + costs.get(edge)) {
                    queue.decreasePriority(w,pv+costs.get(edge));
                    edges.put(w,edge);
                    nodes.put(w,v);
                }
            }
        }
    }

    public DynamicNetwork getGraph() {
        return graph;
    }

    public void setGraph(DynamicNetwork graph) {
        if (graph != this.graph) {
            this.graph = graph;
            distances = null;
            edges = null;
            nodes = null;
        }
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        if (source != this.source) {
            this.source = source;
            distances = null;
            edges = null;
            nodes = null;
        }
    }
    
}
