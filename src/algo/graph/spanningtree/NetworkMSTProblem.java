
package algo.graph.spanningtree;

import org.zetool.graph.Edge;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DynamicNetwork;

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
