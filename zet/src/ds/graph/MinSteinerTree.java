/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph;

import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.graph.Node;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import algo.graph.spanningtree.MinSpanningTreeProblem;

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
