/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
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
    
    public NetworkFlowModel getNetworkFlowModel()
    {
        return graph;
    }
    public IdentifiableIntegerMapping<Edge> getDistances()
    {
        return distances;
    }
}
