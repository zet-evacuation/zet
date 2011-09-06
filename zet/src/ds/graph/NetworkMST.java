/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph;

import ds.graph.problem.NetworkMSTProblem;

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
