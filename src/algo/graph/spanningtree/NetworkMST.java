/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.spanningtree;

import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.graph.Edge;

/**
 *
 * @author schwengf
 */
public class NetworkMST {
    
    
    private NetworkMSTProblem networkprob;
    private IdentifiableCollection<Edge> Edges ;
    private int overalldist;
    
    public NetworkMST(NetworkMSTProblem networkprob,IdentifiableCollection<Edge> Edges, int overalldist)
    {
        this.networkprob = networkprob;
        this.Edges = Edges;
        this.overalldist = overalldist;
    }
    public NetworkMST()
    {
        
    }    
    public NetworkMSTProblem getProblem()
    {
        return networkprob;
    }
    public IdentifiableCollection<Edge> getEdges()
    {
        return Edges;
    }
    


    
}
