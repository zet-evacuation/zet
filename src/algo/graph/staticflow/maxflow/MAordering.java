/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algo.graph.staticflow.maxflow;


import ds.graph.Node;
import ds.graph.DoubleMap;
import java.util.List;
import java.util.ArrayList;
import ds.graph.Edge;
import java.util.HashMap;
import ds.graph.ResidualGraph;

/**
 *
 * @author Sebastian Schenker
 */

public class MAordering {

    private ResidualGraph graph;
    private DoubleMap<Edge> capacities;
    private Node source, sink;
    private DoubleMap<Node> demands;
    private List<Node> ordering;
    private HashMap<Node,List<Edge>> nodelist;
    private List<Node> VminusW;
    private int iterations;
    
    public MAordering(ResidualGraph resGraph, Node s, Node t, DoubleMap<Edge> cap) {
        graph = resGraph; 
        capacities = cap;
        source = s;
        sink = t;
        demands = new DoubleMap<Node>(resGraph.numberOfNodes());
        VminusW = new ArrayList<Node>(resGraph.numberOfNodes());
        nodelist = new HashMap<Node,List<Edge>>(resGraph.numberOfNodes());
        for(Node n : resGraph.nodes()) {
            demands.set(n, 0.0);
            nodelist.put(n, new ArrayList<Edge>());
            
            if(n != source) 
                VminusW.add(n);
        }
        
        ordering = new ArrayList<Node>(resGraph.numberOfNodes());
        iterations = 0;
    }
    
    
    
    public List<Node> getMAordering() {
        return ordering;
    }
    
    public List<Edge> getEdgeList(Node n) {
        return nodelist.get(n);
    }
    
    public int getIterations() {
        return iterations;
    }
    
    public Double getDemand(Node n) {
        return demands.get(n);
    }
    
    public void computeMAordering() {
        Node currentNode = source;
        ordering.add(source);
        Node endNode,maxNode;
        double maxDemandValue,value;
        while(currentNode != sink) {
            for(Edge e : graph.outgoingEdges(currentNode)) {
               endNode = e.end();
               if(VminusW.contains(endNode)) {
                   demands.increase(endNode, capacities.get(e));
                   nodelist.get(endNode).add(e);
                   
               }
            }
            maxNode = null;
            maxDemandValue = -1.0;
            for(Node n : VminusW) {
                if((value=demands.get(n)) > maxDemandValue) {
                    maxDemandValue = value;
                    maxNode = n;
                }
            }
                
            currentNode = maxNode;
            ++iterations;
            ordering.add(currentNode);
            VminusW.remove(currentNode);
        }
        }
    
    
}
