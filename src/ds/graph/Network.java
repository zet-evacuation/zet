/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
@XStreamAlias("network")
public class Network implements Graph, Cloneable {

    /**
     * The nodes of the network. Must not be null.
     */
    protected HidingSet<Node> nodes;    
    
    /**
     * The edges of the network. Must not be null.
     */
    protected HidingSet<Edge> edges;
    
    /**
     * Caches the edges incident to a node for all nodes in the graph.
     * Must not be null.
     */
    protected IdentifiableObjectMapping<Node, DependingListSequence> incidentEdges;
    
    /**
     * Caches the edges ending at a node for all nodes in the graph.
     * Must not be null.
     */
    protected IdentifiableObjectMapping<Node, DependingListSequence> incomingEdges;
    
    /**
     * Caches the edges starting at a node for all nodes in the graph.
     * Must not be null.
     */
    protected IdentifiableObjectMapping<Node, DependingListSequence> outgoingEdges;
    
    /**
     * Caches the number of edges incident to a node for all nodes in the graph.
     * Must not be null.
     */
    protected IdentifiableIntegerMapping<Node> degree;
    
    /**
     * Caches the number of edges ending at a node for all nodes in the graph.
     * Must not be null.
     */
    protected IdentifiableIntegerMapping<Node> indegree;
    
    /**
     * Caches the number of edges starting at a node for all nodes in the graph.
     * Must not be null.
     */
    protected IdentifiableIntegerMapping<Node> outdegree;

    /**
     * Creates a new Network with the specified capacities for edges and nodes.
     * Runtime O(max(initialNodeCapacity, initialEdgeCapacity)).
     * @param initialNodeCapacity the number of nodes that can belong to the 
     * graph.
     * @param initialEdgeCapacity the number of edges that can belong to the 
     * graph.
     */
    public Network(int initialNodeCapacity, int initialEdgeCapacity) {
        edges = new HidingSet<Edge>(Edge.class, initialEdgeCapacity);
        nodes = new HidingSet<Node>(Node.class, initialNodeCapacity);
        for (int i = 0; i < initialNodeCapacity; i++) {
            nodes.add(new Node(i));
        }
        incidentEdges = new IdentifiableObjectMapping<Node, DependingListSequence>
                (initialNodeCapacity, DependingListSequence.class);
        
        incomingEdges = new IdentifiableObjectMapping<Node, DependingListSequence>
                (initialNodeCapacity, DependingListSequence.class);
        
        outgoingEdges = new IdentifiableObjectMapping<Node, DependingListSequence>
                (initialNodeCapacity, DependingListSequence.class);
        for (Node node : nodes) {
            incidentEdges.set(node, new DependingListSequence<Edge>(edges));
            incomingEdges.set(node, new DependingListSequence<Edge>(edges));
            outgoingEdges.set(node, new DependingListSequence<Edge>(edges));
        }        
        degree = new IdentifiableIntegerMapping<Node>(initialNodeCapacity);
        indegree = new IdentifiableIntegerMapping<Node>(initialNodeCapacity);
        outdegree = new IdentifiableIntegerMapping<Node>(initialNodeCapacity);
    }

    /**
     * Checks whether the graph is directed. Runtime O(1).
     * @return <code>true</code>.
     */
    public boolean isDirected() {
        return true;
    }

    /**
     * Returns an {@link HidingSet} containing all the edges of
     * this graph. Runtime O(1).
     * @return an {@link HidingSet} containing all the edges of
     * this graph.
     */
    public IdentifiableCollection<Edge> edges() {
        return edges;
    }

    /**
     * Returns an {@link HidingSet} containing all the nodes of
     * this graph. Runtime O(1).
     * @return an {@link HidingSet} containing all the nodes of
     * this graph.
     */
    public IdentifiableCollection<Node> nodes() {
        return nodes;
    }

    /**
     * Returns the number of edges in this graph. Runtime O(1).
     * @return the number of edges in this graph.
     */
    public int numberOfEdges() {
        return edges().size();
    }

    /**
     * Returns the number of nodes in this graph. Runtime O(1).
     * @return the number of nodes in this graph.
     */
    public int numberOfNodes() {
        return nodes().size();
    }

    /**
     * Returns an {@link DependingListSequence} containing all the edges incident to
     * the specified node. Runtime O(1).
     * @return an {@link DependingListSequence} containing all the edges incident to
     * the specified node.
     */
    public IdentifiableCollection<Edge> incidentEdges(Node node) {
        return incidentEdges.get(node);
    }

    /**
     * Returns an {@link DependingListSequence} containing all the edges ending at
     * the specified node. Runtime O(1).
     * @return an {@link DependingListSequence} containing all the edges ending at
     * the specified node.
     */
    public IdentifiableCollection<Edge> incomingEdges(Node node) {
        return incomingEdges.get(node);
    }

    /**
     * Returns an {@link DependingListSequence} containing all the edges starting at
     * the specified node. Runtime O(1).
     * @return an {@link DependingListSequence} containing all the edges starting at
     * the specified node.
     */
    public IdentifiableCollection<Edge> outgoingEdges(Node node) {
        return outgoingEdges.get(node);
    }

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes adjacent to
     * the specified node. Runtime O(1).
     * @return an {@link IdentifiableCollection} containing all the nodes adjacent to
     * the specified node.
     */
    public IdentifiableCollection<Node> adjacentNodes(Node node) {
        return new OppositeNodeCollection(node, incidentEdges.get(node));
    }

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge ending at the specified node. Runtime O(1).
     * @return an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge ending at the specified node.
     */
    public IdentifiableCollection<Node> predecessorNodes(Node node) {
        return new OppositeNodeCollection(node, incomingEdges.get(node));
    }

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge starting at the specified node. Runtime O(1).
     * @return an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge starting at the specified node.
     */
    public IdentifiableCollection<Node> successorNodes(Node node) {
        return new OppositeNodeCollection(node, outgoingEdges.get(node)); 
    }

    /**
     * Returns the degree of the specified node, i.e. the number of edges 
     * incident to it. Runtime O(1).
     * @param node the node for which the degree is to be returned.
     * @return the degree of the specified node.
     */
    public int degree(Node node) {
        return degree.get(node);
    }

    /**
     * Returns the indegree of the specified node, i.e. the number of edges 
     * ending at it. Runtime O(1).
     * @param node the node for which the indegree is to be returned.
     * @return the indegree of the specified node.
     */
    public int indegree(Node node) {
        return indegree.get(node);
    }

    /**
     * Returns the outdegree of the specified node, i.e. the number of edges 
     * starting at it. Runtime O(1).
     * @param node the node for which the outdegree is to be returned.
     * @return the outdegree of the specified node.
     */
    public int outdegree(Node node) {
        return outdegree.get(node);
    }

    /**
     * Checks whether the specified edge is contained in this graph. Runtime 
     * O(1).
     * @param edge the edge to be checked.
     * @return <code>true</code> if the edge is contained in this graph, 
     * <code>false</code> otherwise.
     */
    public boolean contains(Edge edge) {
        return edges.contains(edge);
    }

    /**
     * Checks whether the specified node is contained in this graph. Runtime 
     * O(1).
     * @param node the node to be checked.
     * @return <code>true</code> if the node is contained in this graph, 
     * <code>false</code> otherwise.
     */
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    /**
     * Returns the edge with the specified id or <code>null</code> if the graph
     * does not contain an edge with the specified id. Runtime O(1).
     * @param id the id of the edge to be returned.
     * @return the edge with the specified id or <code>null</code> if the graph
     * does not contain an edge with the specified id.
     */
    public Edge getEdge(int id) {
        return edges.get(id);
    }

    /**
     * Returns an edge starting at <code>start</code> and ending at 
     * <code>end</code>. If no such edge exists, <code>null</code> is returned. 
     * Runtime O(outdegree(start)).
     * @param start the start node of the edge to be returned.
     * @param end the end node of the edge to be returned.
     * @return an edge starting at <code>start</code> and ending at 
     * <code>end</code>.
     */
    public Edge getEdge(Node start, Node end) {
        for (Edge edge : outgoingEdges(start)) {
            if (edge.end().equals(end)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Returns an {@link ListSequence} containing all edges starting at 
     * <code>start</code> and ending at 
     * <code>end</code>. If no such edge exists, an empty list is returned. 
     * Runtime O(outdegree(start)).
     * @param start the start node of the edges to be returned.
     * @param end the end node of the edges to be returned.
     * @return an {@link ListSequence} containing all edges starting at 
     * <code>start</code> and ending at <code>end</code>.
     */
    public IdentifiableCollection<Edge> getEdges(Node start, Node end) {
        ListSequence<Edge> result = new ListSequence<Edge>();
        for (Edge edge : outgoingEdges(start)) {
            if (edge.end().equals(end)) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Returns the node with the specified id or <code>null</code> if the graph
     * does not contain a node with the specified id. Runtime O(1).
     * @param id the id of the node to be returned.
     * @return the node with the specified id or <code>null</code> if the graph
     * does not contain a node with the specified id.
     */
    public Node getNode(int id) {
        return nodes.get(id);
    }
    
    /**
     * Returns the number of edges that can be contained in the graph.
     * Runtime O(1).
     * @return the number of edges that can be contained in the graph.
     */
    public int getEdgeCapacity() {
        return edges.getCapacity();
    }

    /**
     * Allocates space for edges to be contained in the graph. 
     * Runtime O(newCapacity).
     * @param newCapacity the number of edges that can be contained by the 
     * graph.
     */
    public void setEdgeCapacity(int newCapacity) {
        if (getEdgeCapacity() != newCapacity) {
            edges.setCapacity(newCapacity);
        }
    }

    /**
     * Returns the number of nodes that can be contained in the graph. 
     * Runtime O(1).
     * @return the number of nodes that can be contained in the graph.
     */    
    public int getNodeCapacity() {
        return nodes.getCapacity();
    }

    /**
     * Allocates space for nodes to be contained in the graph. 
     * Runtime O(newCapacity).
     * @param newCapacity the number of nodes that can be contained by the 
     * graph.
     */    
    public void setNodeCapacity(int newCapacity) {
        if (getNodeCapacity() != newCapacity) {
			int oldCapacity = getNodeCapacity();
            nodes.setCapacity(newCapacity);
			          
            incidentEdges.setDomainSize(newCapacity);
            incomingEdges.setDomainSize(newCapacity);
            outgoingEdges.setDomainSize(newCapacity);
            degree.setDomainSize(newCapacity);
            indegree.setDomainSize(newCapacity);
            outdegree.setDomainSize(newCapacity);
			
            for (int i = oldCapacity; i < newCapacity; i++) {
                setNode (new Node (i));
            }  
        }
    }

    /**
     * Checks whether the specified edge is hidden. Runtime O(1).
     * @param edge the edge to be tested.
     * @return <code>true</code> if the specified edge is hidden, <code>false
     * </code> otherwise. 
     */
    public boolean isHidden(Edge edge) {
        return edges.isHidden(edge);
    }

    /**
     * Sets the hidden state of the specified edge to the specified value. A 
     * hidden edge is treated as if it did not belong to the graph - the only
     * difference to it being actually deleted is that it can be restored very 
     * efficiently. This can be useful for residual networks amongst other 
     * things. Runtime O(1).
     * @param edge the edge for which the hidden state is to be set.
     * @param value the new value of the edge's hidden state.
     */    
    public void setHidden(Edge edge, boolean value) {
        if (isHidden(edge) != value) {
            edges.setHidden(edge, value);
            if (value) {
                degree.decrease(edge.start(), 1);
                degree.decrease(edge.end(), 1);
                outdegree.decrease(edge.start(), 1);
                indegree.decrease(edge.end(), 1);                
            } else {
                degree.increase(edge.start(), 1);
                degree.increase(edge.end(), 1);
                outdegree.increase(edge.start(), 1);
                indegree.increase(edge.end(), 1);                                
            }
        }
    }

    /**
     * Checks whether the specified node is hidden. Runtime O(1).
     * @param node the node to be tested.
     * @return <code>true</code> if the specified node is hidden, <code>false
     * </code> otherwise. 
     */    
    public boolean isHidden(Node node) {
        return nodes.isHidden(node);
    }

    /**
     * Sets the hidden state of the specified node to the specified value. A 
     * hidden node is treated as if it did not belong to the graph - the only
     * difference is to it being actually deleted is that it can be restored 
     * very efficiently. Hiding a node
     * causes all edges incident to it to be hidden as well.
     * Runtime O(degree(node)).
     * @param node the node for which the hidden state is to be set.
     * @param value the new value of the node's hidden state.
     */
    public void setHidden(Node node, boolean value) {
        if (isHidden(node) != value) {
            if (value) {
                for (Edge edge : incidentEdges(node)) {
                    setHidden(edge, value);
                }
            }
            nodes.setHidden(node, value);
        }
    }
    
    public void showAllEdges() {
        edges.showAll();
    }

    private int idOfLastCreatedEdge = -1; 
    
    /**
     * Creates a new directed edge between the specified start and end nodes and
     * adds it to the graph (provided the graph has enough space allocated for
     * an additional edge). Runtime O(1).
     * @param start the start node of the new edge.
     * @param end the end node of the new edge.
     * @return the new edge.
     */
    public Edge createAndSetEdge(Node start, Node end) {
        int id = idOfLastCreatedEdge + 1;
        int capacity = getEdgeCapacity();
        while (edges.getEvenIfHidden(id % capacity) != null || id == idOfLastCreatedEdge + 1 + capacity) {
            id++;
        }        
        if (edges.getEvenIfHidden(id % capacity) == null) {
            Edge edge = new Edge(id % capacity, start, end);
            setEdge(edge);
            idOfLastCreatedEdge = id % capacity;
            return edge;
        } else {
            throw new IllegalStateException(Localization.getInstance (
			).getString ("ds.Graph.NoCapacityException"));
        }       
    }

    /**
     * Adds the specified edge to the graph by setting it to it ID's correct 
     * position in the internal data structures. The correct position must be
     * empty. Runtime O(1).
     * @param edge the edge to be added to the graph.
     * @exception IllegalArgumentException if the specified position is not empty.
     */
    public void setEdge(Edge edge) {
        if (edges.get(edge.id()) == null) { 
            edges.add(edge);
            incidentEdges(edge.start()).add(edge);
            incidentEdges(edge.end()).add(edge);
            outgoingEdges(edge.start()).add(edge);
            incomingEdges(edge.end()).add(edge);
            degree.increase(edge.start(), 1);
            degree.increase(edge.end(), 1);
            outdegree.increase(edge.start(), 1);
            indegree.increase(edge.end(), 1);  
        } else if (edges.get(edge.id()).equals(edge)) {
        } else {
            throw new IllegalArgumentException("Edge position is already occupied");
        }
    }

    /**
     * Adds the specified edges to the graph by calling <code>setEdge</code> for
     * each edge. Runtime O(number of edges).
     * @param edges the edges to be added to the graph.
     */    
    public void setEdges(Iterable<Edge> edges) {
        for (Edge edge : edges) {
            setEdge(edge);
            if (edges instanceof HidingSet) { 
                setHidden(edge, ((HidingSet) edges).isHidden(edge));
            }            
        }
    }

    /**
     * Adds the specified node to the graph by setting it to it ID's correct 
     * position in the internal data structures. If this position was occupied
     * before it will be overwritten. Runtime O(1).
     * @param node the node to be added to the graph.
     */    
    public void setNode(Node node) {
        if (nodes.get(node.id()) == null) {
            incidentEdges.set(node, new DependingListSequence<Edge>(edges));
            incomingEdges.set(node, new DependingListSequence<Edge>(edges));
            outgoingEdges.set(node, new DependingListSequence<Edge>(edges));
            degree.set(node, 0);
            indegree.set(node, 0);
            outdegree.set(node, 0);
        }
        nodes.add(node);
    }

    /**
     * Adds the specified nodes to the graph by calling <code>setNode</code> for
     * each node. Runtime O(number of nodes).
     * @param nodes the nodes to be added to the graph.
     */        
    public void setNodes(Iterable<Node> nodes) {
        for (Node node : nodes) {
            setNode(node);
            if (nodes instanceof HidingSet) { 
                setHidden(node, ((HidingSet) nodes).isHidden(node));
            }
        }
    }

    /**
     * Returns a copy of this network. Runtime O(n + m).
     * @return a copy of this network.
     */
    @Override
    public Network clone() {
        Network clone = new Network(numberOfNodes(), numberOfEdges());
        clone.setNodes(nodes());
        clone.setEdges(edges());
        return clone;
    }
    
    /**
     * Compares the specified object to this object and returns whether the
     * specified object is equivalen to this one. An object is considered
     * equivalent to this network, if and only if it is a network with an 
     * equivalent node and edge set. Note that both the node and the edge set
     * must be completely equivalent (i.e. both the visible and hidden parts
     * must be equivalent). Runtime O(n + m).
     * @param o the object to compare with.
     * @return <code>true</code> if the specified object is a network equivalent
     * to this network, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Network) {
            Network n = (Network) o;
            return n.edges.equals(edges) && n.nodes.equals(nodes);
        } else {
            return false;
        } 
    }
    
    /**
     * Returns a hash code for this network. Runtime O(n + m).
     * @return the sum of the hash codes of the edge and node containers.
     */
    @Override
    public int hashCode() {
        return edges.hashCode() + nodes.hashCode();
    }
    
    /**
     * Returns a string representation of this network. The representation is
     * a list of all nodes and edges contained in this graph. The conversion of
     * nodes and edges to strings is done by the <code>toString</code> methods
     * of their classes. Runtime O(n + m). 
     * @return a string representation of this network
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("({");
        for (Node node : nodes()) {
            buffer.append(node.toString());
            buffer.append(", ");
        }
        if (numberOfNodes() > 0) buffer.delete(buffer.length()-2, buffer.length());
        buffer.append("}, {");
        int counter = 0;
        for (Edge edge : edges()) {
            if (counter == 10) {
                counter = 0;
                buffer.append("\n");
            }            
            buffer.append(edge.toString());
            buffer.append(", ");
            counter++;
        }
        if (numberOfEdges() > 0) buffer.delete(buffer.length()-2, buffer.length());        
        buffer.append("})");
        return buffer.toString();
    }    
    
    public String deepToString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("V = {");
        for (Node node : nodes()) {
            buffer.append(node.toString());
            buffer.append(", ");
        }
        if (numberOfNodes() > 0) buffer.delete(buffer.length()-2, buffer.length());
        buffer.append("\nE= {");
        int counter = 0;
        for (Edge edge : edges()) {
            if (counter == 10) {
                counter = 0;
                buffer.append("\n");
            }            
            buffer.append(edge.nodesToString());
            buffer.append(", ");
            counter++;
        }
        if (numberOfEdges() > 0) buffer.delete(buffer.length()-2, buffer.length());        
        buffer.append("}\n");
        return buffer.toString();
    }    
    
    /**
     * Checks whether a directed path between the specified start and end nodes
     * exists by performing a breadth first search. Runtime O(n + m).
     * @param start the start node of the path to be checked.
     * @param end the end node of the path to be checked.
     * @return <code>true</code> if a directed path between the start node and 
     * the end node exists, <code>false</code> otherwise.
     */
    public boolean existsPath(Node start, Node end) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Network createReverseNetwork() {
        Network result = new Network(numberOfNodes(), numberOfEdges());
        for (Edge edge : edges) {
            result.createAndSetEdge(edge.end(), edge.start());
        }
        return result;
    }
    
    /**
     * Returns the network as a <code>Network</code> object.
     * As this graph is already static, the object itself is returned.
     * @return this object
     */
	@Override
	public Network getAsStaticNetwork() {
		return this;
	}
}
