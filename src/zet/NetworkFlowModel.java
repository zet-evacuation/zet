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
 * NetworkFlowModel.java
 *
 */

package zet;

import ds.graph.*;
import java.util.LinkedList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import converter.ZToGraphMapping;

/**
 *
 */
@XStreamAlias("networkFlowModel")
public class NetworkFlowModel {
        
    protected Graph network;
    
    protected IdentifiableIntegerMapping<Edge> edgeCapacities;
    
    protected IdentifiableIntegerMapping<Node> nodeCapacities;
    
    protected IdentifiableIntegerMapping<Edge> transitTimes;
    
    protected IdentifiableIntegerMapping<Node> currentAssignment;
    
    protected LinkedList<Node> sources;
        
    protected ZToGraphMapping mapping;
    
    protected Node supersink;
    
    public NetworkFlowModel(){
        this.network = new Network(0, 0);
        this.edgeCapacities = new IdentifiableIntegerMapping<Edge>(0);
        this.nodeCapacities = new IdentifiableIntegerMapping<Node>(0);
        this.transitTimes = new IdentifiableIntegerMapping<Edge>(0);
    }
    /*
    public FlowProblemInstance getFlowProblemInstance() {
        return new FlowProblemInstance(network.getAsStaticNetwork(), edgeCapacities, nodeCapacities, transitTimes, currentAssignment);
    }*/

    public IdentifiableIntegerMapping<Node> getCurrentAssignment() {
        return currentAssignment;
    }

    public void setCurrentAssignment(IdentifiableIntegerMapping<Node> currentAssignment) {
        this.currentAssignment = currentAssignment;
    }

    public int getEdgeCapacity(Edge edge) {
        if (edgeCapacities.isDefinedFor(edge)) {
            return edgeCapacities.get(edge);
        } else {
            throw new IllegalArgumentException(Localization.getInstance (
			).getString ("ds.Graph.NoEdgeCapacityException"+edge+"."));
        }        
    }
    
    public void setEdgeCapacity(Edge edge, int value) {
        edgeCapacities.set(edge, value);
    }    
    
    public IdentifiableIntegerMapping<Edge> getEdgeCapacities() {
        return edgeCapacities;
    }

    public void setEdgeCapacities(IdentifiableIntegerMapping<Edge> edgeCapacities) {
        this.edgeCapacities = edgeCapacities;
    }

    /**
     * Sets a linked list of nodes as sources of the network.
     * @param sources a linked list of nodes as sources of the network.
     */
    public void setSources(LinkedList<Node> sources){
    	this.sources = sources;
    }
    
    /**
     * Returns a linked list containing the sources.
     * May be consistent with the current assignment or not.
     * @return a linked list containing the sources.
     */
    public LinkedList<Node> getSources(){
    	return sources;
    }
    
    /**
     * Returns a linked list containing the super sink.
     * @return a linked list containing the super sink
     */
    public LinkedList<Node> getSinks(){
    	LinkedList<Node> sinks = new LinkedList<Node>();
    	sinks.add(supersink);
    	return sinks;
    }

    public ZToGraphMapping getZToGraphMapping() {
        return mapping;
    }

    public void setZToGraphMapping(ZToGraphMapping mapping) {
        this.mapping = mapping;
    }

    public Graph getGraph() {
        return network;
    }
    
    public Network getNetwork(){
    	return network.getAsStaticNetwork();
    }
    
    public DynamicNetwork getDynamicNetwork(){
    	if (network instanceof DynamicNetwork)
    		return (DynamicNetwork)network;
    	else throw new RuntimeException(Localization.getInstance (
		).getString ("ds.Graph.NoDynamicGraphException"));

    }

    public void setNetwork(Graph network) {
        this.network = network;
    }
    
    public int getNodeCapacity(Node node) {
        if (nodeCapacities.isDefinedFor(node)) {
            return nodeCapacities.get(node);
        } else {
            throw new IllegalArgumentException(Localization.getInstance (
			).getString ("ds.Graph.NoNodeCapacityException"+ node + "."));
        }        
    }
    
    public void setNodeCapacity(Node node, int value) {
        nodeCapacities.set(node, value);
    }

    public IdentifiableIntegerMapping<Node> getNodeCapacities() {
        return nodeCapacities;
    }

    public void setNodeCapacities(IdentifiableIntegerMapping<Node> nodeCapacities) {
        this.nodeCapacities = nodeCapacities;
    }
    
    public int getTransitTime(Edge edge) {
        if (transitTimes.isDefinedFor(edge)) {
            return transitTimes.get(edge);
        } else {
            throw new IllegalArgumentException(Localization.getInstance (
			).getString ("ds.Graph.NoTransitTimeException" + edge + "."));
        }
    }
    
    public void setTransitTime(Edge edge, int value) {
        transitTimes.set(edge, value);
    }

    public IdentifiableIntegerMapping<Edge> getTransitTimes() {
        return transitTimes;
    }

    public void setTransitTimes(IdentifiableIntegerMapping<Edge> transitTimes) {
        this.transitTimes = transitTimes;
    }

    public Node getSupersink() {
        return supersink;
    }

    public void setSupersink(Node supersink) {
        this.supersink = supersink;
    }   

}
