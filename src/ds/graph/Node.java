/*
 * Node.java
 *
 */

package ds.graph;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * The <code>Node</code> class represents a node in a graph. A node is 
 * identifiable, e.g. the class implements the Interface 
 * {@link Identifiable}. 
 * This means that every node has a ID that can be used for storing nodes in
 * for example {@link ArraySet}s. 
 * The ID must be set at creation of new nodes.
 */
@XStreamAlias("node")
public class Node implements Identifiable {
    
    /**
     * The ID of this node. Must be set at creation of the node.
     */ 
    @XStreamAsAttribute
    private final int nodeID;
     
    /**
     * Constructs a new <code>Node</code> object with a given ID. Runtime O(1).
     * @param nodeID the ID of the new node.
     */
    public Node(int id) {
        this.nodeID = id;
    }
    
    /**
     * Returns the ID of this node. Runtime O(1).
     * @return the ID of this node.
     */
    public int id() {
        return nodeID;
    }
    
    /**
     * Returns the ID of this node as a string.
     * @return a String containing the ID of this node.
     */
    @Override
    public String toString(){
        return String.valueOf(nodeID);
    }
    
    /**
     * Returns a new <code>Node</code> with the same ID as this node.
     * @return a clone of this node (a node with the same nodeID, not the same object).
     */
    @Override
    public Node clone(){
        return new Node(this.nodeID);
    }
        
    /**
     * Returns the hash code of this node. 
     * The hash code is identical to the ID of this node.
     * @return the hash code of this node.
     */
    @Override
    public int hashCode(){
        return nodeID;
    }    

    /**
     * Returns whether an object is equal to this node.
     * The result is true if and only if the argument is not null and is a 
     * <code>Node</code> object having the same ID as this node.
     * @param o object to compare.
     * @return <code>true</code> if the given object represents a
     * <code>Node</code> equivalent to this node, <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o){
        if (o == null || !(o instanceof Node)) {
            return false;
        } else {
            Node n = (Node)o;
            return n.id() == this.nodeID;
        }
    }
    
}
