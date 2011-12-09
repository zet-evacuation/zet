/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph;

import ds.mapping.IdentifiableIntegerMapping;
import ds.graph.problem.MinSpanningTreeProblem;

/**
 *
 * @author schwengf
 */
public class MinSteinerTree {
    
    private MinSpanningTreeProblem minspanprob;
    private IdentifiableIntegerMapping<Edge> shortestdist;
    private IdentifiableCollection<Edge> Edges ;
    private IdentifiableCollection<Node> nodes;
    private int overalldist;
    
    public MinSteinerTree(MinSpanningTreeProblem minspanprob,IdentifiableIntegerMapping<Edge> shortestdist, IdentifiableCollection<Edge> Edges, IdentifiableCollection<Node> nodes, int overalldist)
    {
        this.minspanprob = minspanprob;
        this.shortestdist = shortestdist;
        this.Edges = Edges;
        this.nodes = nodes;
        this.overalldist = overalldist;
    }
    public MinSteinerTree()
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
    public IdentifiableCollection<Node> getNodes()
    {
        return nodes;
    }
    public IdentifiableIntegerMapping<Edge> getdist()
    {
        return shortestdist;
    }
    
}
