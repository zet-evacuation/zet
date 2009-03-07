/*
 * OppositeNodeIterator.java
 *
 */

package ds.graph;

import java.util.HashMap;
import java.util.Iterator;

import localization.Localization;

/**
 * The iterator belong to the collection class above. Required for iterating
 * over the set of adjacent nodes obtained by the class above.
 */        
public class OppositeNodeIterator implements Iterator<Node> {

    /**
     * The base node for which the adjacent nodes are to be iterated.
     */        
    protected Node node;

    /**
     * An iterator iterating over all edges leading to adjacent nodes. 
     */
    protected Iterator<Edge> edgeIterator;

    /**
     * The node to be returned when <code>next</code> is called the first
     * time.
     */
    protected transient Node next;

    /**
     * A map for marking nodes which have already been returned by this
     * iterator. This is required for preventing a node to be returned 
     * twice.
     */
    protected transient HashMap<Node, Boolean> visited;

    /**
     * Creates a new <code>OppositeNodeIterator</code> iterating over all
     * nodes that are opposite to the specified node with regard to the
     * specified collection of edges. Runtime O(1).
     * @param node the base node for which the adjacent nodes are to be 
     * returned.
     * @param edgeIterator the edges incident to <code>node</code>.
     */        
    protected OppositeNodeIterator(Node node, Iterator<Edge> edgeIterator) {
        this.node = node;
        this.edgeIterator = edgeIterator;
        this.visited = new HashMap<Node, Boolean>();
    }

    /**
     * Checks whether there is a node adjacent to the base node which has
     * not been returned by a call to <code>next</code> yet.
     * @return whether there is a node adjacent to the base node which has
     * not been returned by a call to <code>next</code> yet.
     */
    public boolean hasNext() {
        if (next != null) {
            return true;
        } else {
            if (edgeIterator.hasNext()) {
                do {
                    next = edgeIterator.next().opposite(node);
                } while (visited.containsKey(next) && edgeIterator.hasNext());
                return !visited.containsKey(next);
            } else {
                return false;
            }
        }
    }

    /**
     * Returns a node adjacent to the base node which has
     * not been returned by a call to <code>next</code> yet, or <code>null
     * </code> if no such node exists. Runtime O(1).
     * @return  a node adjacent to the base node which has
     * not been returned by a call to <code>next</code> yet, or <code>null
     * </code> if no such node exists.
     */        
    public Node next() {
        if (next == null) {
            hasNext();
        }
        Node result = next;
        visited.put(result, Boolean.TRUE);
        next = null;
        return result;
    }

    /**
     * This operation is not supported by this iterator.
     * @exception UnsupportedOperationException if this method is called.
     */
    public void remove() {
        throw new UnsupportedOperationException(Localization.getInstance (
		).getString ("ds.graph.NotSupportedException"));
    }
}
