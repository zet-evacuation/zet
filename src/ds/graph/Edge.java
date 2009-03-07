/*
 * Edge.java
 *
 */
package ds.graph;

import localization.Localization;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * The <code>Edge</code> class represents a edge in a graph.
 * An edge is identifiable, e.g. the class implements the Interface 
 * {@link Identifiable}.
 * This means that every edge has an ID that can be used for storing edges
 * in for example {@link ArraySet}s.
 * The ID must be set at the creation of a new edge.
 * 
 * A edge consists of two nodes of the Type {@link Node}: 
 * a startnode and endnode. Thus edges are by default directed.
 */
//@XStreamAlias("edge") - Avoid duplicate edge tags (ds.z.Edge also exists)
public class Edge implements Identifiable {

    /**
     * The ID of this edge.
     */
    /* Don't rename this field to "id" because XStream will mess up the attribute names
     * in the XML file as it uses the "id" attribute itself. Even using @XStreamAlias("edgeID")
     * here, hasn't solved the problem. */
    @XStreamAsAttribute
    private int edgeID;
    /**
     * The startnode of this edge.
     */
    private Node start;
    /**
     * The endnode of this edge.
     */
    private Node end;

    /**
     * Constructs a new <code>Edge</code> object with a given given start-
     * and endnode and given ID. Runtime O(1).
     * @param edgeID the ID of the new edge.
     * @param start the startnode of the new  edge.
     * @param end the endnode of the new  edge.
     * @exception NullPointerException if <code>start</code> or 
     * <code>end</code> is null.
     */
    public Edge(int id, Node start, Node end) {
        if (start == null || end == null) {
            throw new NullPointerException(Localization.getInstance().getString("ds.graph.StartEndNodeIsNullException"));
        }
        this.edgeID = id;
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the ID of this edge. Runtime O(1).
     * @return the ID of this edge.
     */
    public int id() {
        return edgeID;
    }

    /**
     * Returns the startnode of this edge. Runtime O(1).
     * @return the startnode of this edge.
     */
    public Node start() {
        return start;
    }

    /**
     * Returns the endnode of this edge. Runtime O(1).
     * @return the endnode of this edge.
     */
    public Node end() {
        return end;
    }

    /**
     * Given a node <code>node</code>, this method returns the other node
     * (the node that is not <code>node</code>).
     * @param node the node this method shall give the opposite of.
     * @return the opposite node to <code>node</code>.
     */
    public Node opposite(Node node) {
        if (node == start && node != end) {
            return end;
        } else if (node != start && node == end) {
            return start;
        } else if (node == start && node == end) {
            return null;
        } else {
            throw new IllegalArgumentException("node=" + node);
        }
    }

    /**
     * Returns a String containing the IDs of start- and endnode of this edge.
     * @return a String containing the IDs of start- and endnode of this edge.
     */
    public String nodesToString() {
        return String.format("(%1$s,%2$s)", start.id(), end.id());
    }

    /**
     * Returns a String containing the ID of this edge.
     * @return a String containing the ID of this edge.
     */
    @Override
    public String toString() {
        return String.format("%1$s = (%2$s,%3$s)", edgeID, start, end);
    }

    /**
     * Returns the hash code of this edge.
     * The hash code is identical to the ID of this edge.
     * @return the hash code of this edge.
     */
    @Override
    public int hashCode() {
        return this.edgeID;
    }

    /**
     * Returns whether an object is equal to this edge.
     * The result is true if and only if the argument is not null and is an 
     * <code>Edge></code> object having the same ID as this edge.
     * @param o the object to compare.
     * @return <code>true</code> if the given object represents an
     * <code>Edge</code> equivalent to this edge, <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Edge)) {
            return false;
        } else {
            Edge e = (Edge) o;
            return (e.id() == this.edgeID);
        }
    }

    /**
     * Returns a new <code>Edge</code> with the same ID as this edge.
     * @return a clone of this edge (a edge with the same edgeID, not the same object).
     */
    @Override
    public Edge clone() {
        return new Edge(this.edgeID, this.start, this.end);
    }
}