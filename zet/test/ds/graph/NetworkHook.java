/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
