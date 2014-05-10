/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.spanningtree;

import ds.graph.network.DynamicNetwork;
import ds.graph.Edge;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author schwengf
 */
public class NetworkMSTProblem {
    
    private DynamicNetwork graph;
    private IdentifiableIntegerMapping<Edge> distances;
    
    public NetworkMSTProblem(DynamicNetwork graph,IdentifiableIntegerMapping<Edge> distances)
    {
        this.graph = graph;
        this.distances = distances;
    }
    
    public DynamicNetwork getGraph()
    {
        return graph;
    }
    public IdentifiableIntegerMapping<Edge> getDistances()
    {
        return distances;
    }
    
}
