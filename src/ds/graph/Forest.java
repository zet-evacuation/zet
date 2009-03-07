/*
 * Network.java
 *
 */
package ds.graph;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * The <code>Network</class> provides an implementation of a directed graph
 * optimized for use by flow algorithms. Examples of these optimizations 
 * include use of array based data structures for edges and nodes in order to
 * provide fast access, as well as the possiblity to hide edges and nodes (which
 * is useful for residual networks, for instance).
 */
@XStreamAlias("forest")
public class Forest extends Network {

    protected IdentifiableObjectMapping<Node, Edge> precedingEdges;
    
    /**
     * Creates a new Forest with the specified capacities for edges and nodes.
     * Runtime O(max(initialNodeCapacity, initialEdgeCapacity)).
     * @param initialNodeCapacity the number of nodes that can belong to the 
     * graph.
     * @param initialEdgeCapacity the number of edges that can belong to the 
     * graph.
     */
    public Forest(int initialNodeCapacity, int initialEdgeCapacity) {
        super(initialNodeCapacity, initialEdgeCapacity);
    }
    
    public Forest(IdentifiableCollection<Node> nodes, IdentifiableObjectMapping<Node, Edge> precedingEdges) {
        super(nodes.size(), nodes.size() - 1);
        this.precedingEdges = new IdentifiableObjectMapping<Node, Edge>(nodes.size(), Edge.class);
        for (Node node : nodes) {
            if (precedingEdges.get(node) == null) {
                continue;
            }
            Edge edge = createAndSetEdge(precedingEdges.get(node).opposite(node), node);
            this.precedingEdges.set(node, edge);
        }
    }
    
    public Path getPathToRoot(Node node) {
        Path result = new StaticPath();
        Edge edge;
        while ((edge = precedingEdges.get(node)) != null) {
            result.addFirstEdge(edge);
            node = edge.opposite(node);
        }
        return result;
    } 
}
