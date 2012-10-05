/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.algorithm.flowovertime;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.datastructure.priorityQueue.MinHeap;
import ds.graph.Edge;
import ds.graph.ImplicitTimeExpandedResidualNetwork;
import ds.graph.Node;
import ds.graph.flow.EdgeBasedFlowOverTime;
import ds.graph.flow.FlowOverTimeEdge;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.mapping.IdentifiableIntegerMapping;
import ds.mapping.IdentifiableObjectMapping;
import java.util.logging.Logger;

/**
 *
 * @author Martin Groß
 */
public class FlowOverTimePathDecomposition extends Algorithm<ImplicitTimeExpandedResidualNetwork, PathBasedFlowOverTime> {
	/** The logger of the main class. */
	private static final Logger log = Logger.getGlobal();

    private ImplicitTimeExpandedResidualNetwork network;    
    private EdgeBasedFlowOverTime flow;
    private IdentifiableIntegerMapping<Edge> superSourceFlow;
    
    @Override
    protected PathBasedFlowOverTime runAlgorithm(ImplicitTimeExpandedResidualNetwork network) {
			this.network = network;
			flow = new EdgeBasedFlowOverTime(network.flow().clone());
			superSourceFlow = network.superSourceFlow().clone();
			PathBasedFlowOverTime result = new PathBasedFlowOverTime();
			FlowOverTimePath path = calculateShortestPath();
			while (path != null) {
				result.addPathFlow( path );
				log.fine( "Path " + path + " found with capacity " + path.getRate() +". Value now: " + result.getValue() );
				path = calculateShortestPath();				
			}
			return result;
    }

	public FlowOverTimePath calculateShortestPath() {
	int sink = 0;
		for( Edge e : network.incomingEdges( network.getProblem().getSink() ) ) {
			for( int i = 0; i <= flow.get( e ).getLastTimeWithNonZeroValue(); ++i ) {
				sink += flow.get( e ).get( i );
			}
		}
		log.finer( "Sink input value:" + sink );

		
		Node tempNode = network.getNode( 12 );
		sink = 0;
		for( Edge e : network.incomingEdges( tempNode ) ) {
			if( network.isReverseEdge( e ) )
				continue;
			if( flow.get( e ) != null )
				for( int i = 0; i <= flow.get( e ).getLastTimeWithNonZeroValue(); ++i ) {
					sink += flow.get( e ).get( i );
				}
			else
					sink += superSourceFlow.get( e );
		}
		log.finer( "Incoming in 12: " + sink );
		sink = 0;
		for( Edge e : network.outgoingEdges( tempNode ) ) {
			if( network.isReverseEdge( e ) )
				continue;
			if( flow.get( e ) != null )
				for( int i = 0; i <= flow.get( e ).getLastTimeWithNonZeroValue(); ++i ) {
					sink += flow.get( e ).get( i );
				}
			else
					sink += superSourceFlow.get( e );
		}
		log.finer( "Outgoing in 12: " + sink );
		
		
		IdentifiableIntegerMapping distances = new IdentifiableIntegerMapping<>(network.numberOfNodes());
		IdentifiableObjectMapping<Node, Edge> preceedingEdges = new IdentifiableObjectMapping<>(network.numberOfEdges(), Edge.class);
		MinHeap<Node, Integer> queue = new MinHeap<>(network.numberOfNodes());
		for (int v = 0; v < network.numberOfNodes(); v++) {
				queue.insert(network.getNode(v), Integer.MAX_VALUE);
		}
		distances.set(network.superSource(), 0);
		queue.decreasePriority(network.superSource(), 0);
		while (!queue.isEmpty()) {
				MinHeap<Node, Integer>.Element min = queue.extractMin();
				Node node = min.getObject();
				Integer distance = min.getPriority();
				distances.set(node, distance);
				if (distance == Integer.MAX_VALUE) {
						continue;
				}
				for (Edge edge : network.outgoingEdges(node)) {                
						if (network.isReverseEdge(edge)) {
								continue;
						}
						int time;
						if (node == network.superSource() && network.hasArtificialSuperSource()) {
								time = (superSourceFlow.get(edge) > 0)? 0 : Integer.MAX_VALUE;
						} else {
								time = flow.get(edge).nextPositiveValue(distance);
						}
						if (time == Integer.MAX_VALUE) {
								continue;
						}                
						Node w = edge.opposite(node);
						if (queue.contains(w) && (long) queue.priority(w) > (long) time + (long) network.transitTime(edge)) {
								queue.decreasePriority(w, time + network.transitTime(edge));
								preceedingEdges.set(w, edge);
						}
				}
		}
		if (preceedingEdges.get(network.getProblem().getSink()) == null) {
				return null;
		}
		FlowOverTimePath path = new FlowOverTimePath();
		Node node = network.getProblem().getSink();
		int lastDistance = distances.get(node);        
		while (node != network.superSource()) {
				Edge edge = preceedingEdges.get(node);
				Node start = edge.opposite(node);
				int delay = lastDistance - network.transitTime(edge) - distances.get(start);
				path.addFirst(new FlowOverTimeEdge(edge, delay, distances.get(start) + delay));
				lastDistance = distances.get(start);
				node = start;            
		}
		int capacity = Integer.MAX_VALUE;
		for (FlowOverTimeEdge edge : path) {
				if (edge.getEdge().start() == network.superSource() && network.hasArtificialSuperSource()) {
						capacity = Math.min(superSourceFlow.get(edge.getEdge()), capacity);
				} else {
						capacity = Math.min(flow.get(edge.getEdge()).get(edge.getTime()), capacity);
				}
		}
		log.finer( "Capacity: " + capacity );
	
		
		for( Edge e : network.allEdges() ) {
			flow.get( e );
		}
		
		sink = 0;
		for( Edge e : network.incomingEdges( network.getProblem().getSink() ) ) {
			for( int i = 0; i <= flow.get( e ).getLastTimeWithNonZeroValue(); ++i ) {
				sink += flow.get( e ).get( i );
			}
			
		}
		log.finer( "Sink input value:" + sink );
		
		int cap;
		cap = 0;
		for( Edge e : network.incidentEdges( network.superSource() ) )
			cap += superSourceFlow.get( e );
		log.finer( "Flow out of super-source before: " + cap );
		
		
		
		for (FlowOverTimeEdge edge : path) {
				if (edge.getEdge().start() == network.superSource() && network.hasArtificialSuperSource()) {
						superSourceFlow.decrease(edge.getEdge(), capacity);
				} else {
						flow.get(edge.getEdge()).decrease(edge.getTime(), capacity);
				}
		}
		cap = 0;
		for( Edge e : network.incidentEdges( network.superSource() ) )
			cap += superSourceFlow.get( e );
		log.finer( "Flow out of super-source before: " + cap );
		
		if (network.hasArtificialSuperSource()) {
				path.removeFirst();
		}
		// Set amount and rate
		path.setAmount( capacity );
		path.setRate( capacity );
		return path;
	}
}
