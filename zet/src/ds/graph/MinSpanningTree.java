/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph;

import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.graph.IdentifiableCollection;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author schwengf
 */
public class MinSpanningTree {
    
    private MinSpanningTreeProblem minspanprob;
    private IdentifiableCollection<Edge> Edges ;
    private IdentifiableIntegerMapping<Edge> transit;
    private IdentifiableIntegerMapping<Edge> capac;
    private int overalldist;
    
    public MinSpanningTree(MinSpanningTreeProblem minspanprob,IdentifiableCollection<Edge> Edges, IdentifiableIntegerMapping trans,IdentifiableIntegerMapping cap, int overalldist)
    {
        this.minspanprob = minspanprob;
        this.Edges = Edges;
        this.transit = trans;
        this.capac = cap;
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
    
    public IdentifiableIntegerMapping<Edge> getTransit()
    {
        return transit;
    }
    
    public IdentifiableIntegerMapping<Edge> getCapac()
    {
        return capac;
    }
    
}


