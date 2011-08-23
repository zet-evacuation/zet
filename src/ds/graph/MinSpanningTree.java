/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph;

import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.IdentifiableCollection;
import ds.graph.problem.MinSpanningTreeProblem;

/**
 *
 * @author schwengf
 */
public class MinSpanningTree {
    
    private MinSpanningTreeProblem minspanprob;
    private IdentifiableCollection<Edge> Edges ;
    private int overalldist;
    
    public MinSpanningTree(MinSpanningTreeProblem minspanprob,IdentifiableCollection<Edge> Edges, int overalldist)
    {
        this.minspanprob = minspanprob;
        this.Edges = Edges;
        this.overalldist = overalldist;
    }
    public MinSpanningTree()
    {
        
    }
    
    public MinSpanningTreeProblem getProblem()
    {
        return minspanprob;
    }
    public IdentifiableCollection<Edge> getEdges()
    {
        return Edges;
    }
    
}


