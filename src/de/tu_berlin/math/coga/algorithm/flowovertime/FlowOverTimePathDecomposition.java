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
import java.util.ListIterator;
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
			
			// check feasibility
//			if( !network.isFeasible() )
//				throw new IllegalStateException( "Infeasible network!" );
			
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

		IdentifiableIntegerMapping distances = new IdentifiableIntegerMapping<>(network.numberOfNodes());
		IdentifiableObjectMapping<Node, Edge> preceedingEdges = new IdentifiableObjectMapping<>(network.numberOfEdges(), Edge.class);
		MinHeap<Node, Integer> queue = new MinHeap<>(network.numberOfNodes());
		for (int v = 0; v < network.numberOfNodes(); v++) {
				queue.insert(network.getNode(v), Integer.MAX_VALUE);
		}
		distances.set(network.superSource(), 0);
		queue.decreasePriority(network.superSource(), 0);
		
		network.getOutflow( 4 );		
		network.getInflow( 21 );
		
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
							// check here if something may get wrong!
							
							
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
		

		// new method
//		int previousTime = Integer.MIN_VALUE;
//		ListIterator<FlowOverTimeEdge> backIter = path.listIterator( path.size() );
//		while( backIter.hasPrevious() ) {
//			FlowOverTimeEdge edge = backIter.previous();
//			if (edge.getEdge().start() == network.superSource() && network.hasArtificialSuperSource()) {
//					superSourceFlow.decrease(edge.getEdge(), capacity);
//			} else {
//				// we try to decrease at edge.getTime().
//					if( previousTime == Integer.MIN_VALUE ) {
//						flow.get(edge.getEdge()).decrease(edge.getTime(), capacity);
//						previousTime = edge.getTime();
//					} else {
//						// the flow is sended by the next element on the path at time edge.getTime().
//						// maybe, we can send our flow a little bit later than edge.getTime(). Let's check that.
//						int possibleDelay = previousTime - edge.getTime();
//						for( int i = possibleDelay; i >= 0; --i ) {
//							// try to send at time edge.getTime() + i
//							if( flow.get( edge.getEdge()).get( edge.getTime() + i ) >= capacity ) { // this will be definitely true for i=0
//								if( i != 0 ) {
//									System.out.println( "Sending flow on " + edge.toString() + " " + i + " time steps later." );
//								}
//								flow.get(edge.getEdge()).decrease(edge.getTime() + i, capacity);
//								previousTime = edge.getTime()+i;
//								break;
//							}
//						}
//					}
//			}
//		}
		
		// old method
		for (FlowOverTimeEdge edge : path) {
				if (edge.getEdge().start() == network.superSource() && network.hasArtificialSuperSource()) {
						superSourceFlow.decrease(edge.getEdge(), capacity);
				} else {
						flow.get(edge.getEdge()).decrease(edge.getTime(), capacity);
				}
		}
		
		if (network.hasArtificialSuperSource()) {
				path.removeFirst();
		}
		
			// check feasibility
			//if( !network.isFeasible() )
			//	throw new IllegalStateException( "Infeasible network!" );
		
		
		
		// Set amount and rate
		path.setAmount( capacity );
		path.setRate( capacity );
		return path;
	}
}
