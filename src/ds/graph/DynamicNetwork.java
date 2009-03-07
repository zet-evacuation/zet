/*
 * DynamicGraph.java
 *
 */

package ds.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import localization.Localization;

/**
 *
 */
public class DynamicNetwork implements Graph {
    
    protected ListSequence<Node> nodes;
    protected ListSequence<Edge> edges;
    
    protected transient Map<Node, ListSequence<Edge>> incomingEdges;
    protected transient Map<Node, ListSequence<Edge>> outgoingEdges;
    
    public DynamicNetwork() {
        nodes = new ListSequence<Node>();
        edges = new ListSequence<Edge>();
        incomingEdges = new HashMap<Node,ListSequence<Edge>>();
        outgoingEdges = new HashMap<Node,ListSequence<Edge>>();
    }
    
    public DynamicNetwork(DynamicNetwork graph) {
        this();
        setNodes(graph.nodes());
        setEdges(graph.edges());
    }
    
    public DynamicNetwork(Iterable<Node> nodes, Iterable<Edge> edges) {
        this();
        setNodes(nodes);
        setEdges(edges);
    }
    
    public boolean directed() {
        return true;
    }
    
    public void addEdge(Edge edge) {
        edges.add(edge);
        incomingEdges.get(edge.end()).add(edge);
        outgoingEdges.get(edge.start()).add(edge);
    }
    
    public void addEdges(Iterable<Edge> edges) {
        for (Edge edge : edges) {
            addEdge(edge);
        }
    }
    
    public void addNode(Node node) {
        nodes.add(node);
        incomingEdges.put(node,new ListSequence<Edge>());
        outgoingEdges.put(node,new ListSequence<Edge>());
    }
    
    public void addNodes(Iterable<Node> nodes) {
        for (Node node : nodes) {
            addNode(node);
        }
    }
    
    public boolean contains(Node node) {
        return incomingEdges.containsKey(node);
    }
    
    public boolean contains(Edge edge) {
        return edges.contains(edge);
    }
    
    public List<Edge> extractEdges() {
        List<Edge> edges = this.edges;
        setEdges(new ArrayList<Edge>());
        return edges;
    }
    
    public Edge getEdge(int index) {
        return edges.get(index);
    }
    
    public Edge getEdge(Node source, Node target) {
        for (Edge edge : outgoingEdges.get(source)) {
            if (edge.start() == source && edge.end() == target) {
                return edge;
            }
        }
        return null;
    }
    
    public Node getNode(int index) {
        return nodes.get(index);
    }
    
    /*
    public ListSequence<Edge> adjEdges(Node node) {
        return outgoingEdges(node);
    }
    */
    public ListSequence<Edge> edges() {
        return edges;
    }
    
    public ListSequence<Edge> incomingEdges(Node node) {
        return incomingEdges.get(node);
    }

    public ListSequence<Edge> incidentEdges(Node node) {
        ListSequence<Edge> incidentEdges = new ListSequence<Edge>();
        incidentEdges.addAll(incomingEdges(node));
        incidentEdges.addAll(outgoingEdges(node));
        return incidentEdges;
    }    

    public ListSequence<Edge> outgoingEdges(Node node) {
        return outgoingEdges.get(node);
    }    
    
    public ListSequence<Node> nodes() {
        return nodes;
    }
    
    public int degree(Node node) {
        return indegree(node)+outdegree(node);
    }
    
    public int indegree(Node node) {
        return incomingEdges(node).size();
    }
    
    public int outdegree(Node node) {
        return outgoingEdges(node).size();
    }
    
    public int numberOfEdges() {
        return edges.size();
    }
    
    public int numberOfNodes() {
        return nodes.size();
    }
    
    public Node opposite(Edge edge, Node node) {
        if (node == edge.start()) {
            return edge.end();
        } else if (node == edge.end()) {
            return edge.start();
        } else {
            throw new IllegalArgumentException(Localization.getInstance (
			).getString ("ds.graph.NotIncidentException"+node+", "+edge));
        }
    }
    
    public Node opposite(Node node, Edge edge) {
        return opposite(edge,node);
    }
    /*    
    public ListSequence<Edge> reachableFromEdges(Node node) {
        ListSequence<Edge> result = new ListSequence<Edge>();
        for (Node n : reachableFromNodes(node)) {
            result.addAll(incomingEdges.get(n));
        }
        return result;
    }

    public ListSequence<Node> reachableFromNodes(Node node) {
        DFS dfs = new DFS(this);
        dfs.run(node,true);
        ListSequence<Node> reachableNodes = new ListSequence<Node>();
        for (Node n : nodes) {
            if (dfs.state(n) == DFS.State.DONE) reachableNodes.add(n);
        }
        return reachableNodes;
    }
    */
    public void removeAllEdges() {
        for (Node node : nodes) {
            incomingEdges.get(node).clear();
            outgoingEdges.get(node).clear();
        }
        edges.clear();
    }
    
    public void removeAllNodes() {
        edges.clear();
        nodes.clear();
        incomingEdges.clear();
        outgoingEdges.clear();
    }
    /*
    public void removeCycles(Node startingFrom) {
        DFS dfs = new DFS(this);
        dfs.run(startingFrom,false);
        removeEdges(dfs.getBackEdges());
    }
    */
    public void removeEdge(Edge edge) {
        edges.remove(edge);
        incomingEdges.get(edge.end()).remove(edge);
        outgoingEdges.get(edge.start()).remove(edge);
    }
    
    public void removeEdges(Iterable<Edge> edges) {
        for (Edge edge : edges) {
            removeEdge(edge);
        }
    }
    
    public void removeLoops() {
        ListSequence<Edge> loops = new ListSequence<Edge>();
        for (Edge edge : edges) {
            if (edge.start() == edge.end()) loops.add(edge);
        }
        removeEdges(loops);
    }
    
    public void removeNode(Node node) {
        removeEdges(incidentEdges(node));
        nodes.remove(node);
    }
    
    public void removeNodes(Iterable<Node> nodes) {
        for (Node node : nodes) {
            removeNode(node);
        }
    }
    
    public void retainEdges(Collection<Edge> edges) {
        ListSequence<Edge> copy = edges().clone();
        for (Edge edge : edges) {
            copy.remove(edge);
        }        
        removeEdges(copy);
    }
    
    public void retainNodes(Iterable<Node> nodes) {
        ListSequence<Edge> edges = edges().clone();
        removeAllNodes();
        addNodes(nodes);        
        for (Edge edge : edges) {
            if (contains(edge.start()) && contains(edge.end())) {
                addEdge(edge);
            }
        }
    }
    
    public void setEdges(Iterable<Edge> edges) {
        removeAllEdges();
        addEdges(edges);
    }
    
    public void setNodes(Iterable<Node> nodes) {
        removeAllNodes();
        addNodes(nodes);
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("V = {");
        for (Node node : nodes) {
            builder.append(node + ",");
        }
        if (!nodes.isEmpty()) builder.deleteCharAt(builder.length()-1);
        builder.append("}\n");
        builder.append("E = {");
        int counter = 0;
        for (Edge edge : edges) {
            if (counter == 10) {
                counter = 0;
                builder.append("\n");
            }
            builder.append(edge + ",");
            counter++;
        }
        if (!edges.isEmpty()) builder.deleteCharAt(builder.length()-1);        
        builder.append("}");
        return builder.toString();
    }

    public String deepToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("V = {");
        for (Node node : nodes) {
            builder.append(node + ",");
        }
        if (!nodes.isEmpty()) builder.deleteCharAt(builder.length()-1);
        builder.append("}\n");
        builder.append("E = {");
        int counter = 0; 
        for (Edge edge : edges) {
            if (counter == 10) {
                counter = 0;
                builder.append("\n");
            }            
            builder.append(edge.nodesToString() + ",");
            counter++;
        }
        if (!edges.isEmpty()) builder.deleteCharAt(builder.length()-1);        
        builder.append("}");
        return builder.toString();
    }    
    
    public boolean isDirected() {
        return true;
    }

    public IdentifiableCollection<Node> adjacentNodes(Node node) {
        return new OppositeNodeCollection(node, incidentEdges(node));
    }

    public IdentifiableCollection<Node> predecessorNodes(Node node) {
        return new OppositeNodeCollection(node, incomingEdges.get(node));
    }

    public IdentifiableCollection<Node> successorNodes(Node node) {
        return new OppositeNodeCollection(node, outgoingEdges.get(node));
    }
    
    public void setNode(Node node) {
        addNode(node);
    }

    public void setEdge(Edge edge) {
        addEdge(edge);
    }

    public IdentifiableCollection<Edge> getEdges(Node start, Node end) {
        throw new UnsupportedOperationException(Localization.getInstance (
		).getString ("ds.graph.NotSupportedException"));
    }

    public boolean existsPath(Node start, Node end) {
        throw new UnsupportedOperationException(Localization.getInstance (
		).getString ("ds.graph.NotSupportedException"));
    }
    
    public Network getAsStaticNetwork() {
        Network network = new Network(numberOfNodes(), numberOfEdges());
        network.setNodes(nodes);
        network.setEdges(edges);
        return network;
    }

}
