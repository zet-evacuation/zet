/*
 * NetworkHook.java
 *
 * Created on 29. November 2007, 19:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ds.graph;

/**
 *
 * @author mouk
 * This class is responsible for granting access to the protected field of Network for testing porpsoes.
 */
public class NetworkHook extends Network
{
    
    public NetworkHook(int initialNodeCapacity, int initialEdgeCapacity)
    {
        super(initialNodeCapacity, initialEdgeCapacity);     
    }
    
    public void setIncidentEdges(IdentifiableObjectMapping<Node, DependingListSequence> value)
    {
        incidentEdges = value;
    }
    
    public void setincomingEdges(IdentifiableObjectMapping<Node, DependingListSequence> value)
    {
        incomingEdges = value;
    }
     public void setOutgoingEdges(IdentifiableObjectMapping<Node, DependingListSequence> value)
    {
        outgoingEdges = value;
    }
    

    public void setDegree(IdentifiableIntegerMapping<Node> degree) 
    {
        this.degree = degree;
    }

    public void setIndegree(IdentifiableIntegerMapping<Node> indegree) 
    {
        this.indegree = indegree;
    }

    public void setOutdegree(IdentifiableIntegerMapping<Node> outdegree) 
    {
        this.outdegree = outdegree;
    }
}
