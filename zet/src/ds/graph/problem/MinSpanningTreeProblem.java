/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph.problem;


import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Edge;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
/**
 *
 * @author schwengf
 */
public class MinSpanningTreeProblem {
    
    private NetworkFlowModel graph;
    private IdentifiableIntegerMapping<Edge> distances;
    
    public MinSpanningTreeProblem(NetworkFlowModel graph,IdentifiableIntegerMapping<Edge> distances)
    {
        this.graph = graph;
        this.distances = distances;
    }
    
    public NetworkFlowModel getGraph()
    {
        return graph;
    }
    public IdentifiableIntegerMapping<Edge> getDistances()
    {
        return distances;
    }
}
