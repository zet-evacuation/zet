/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow;

import algo.graph.traverse.BFS;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.StaticPath;
import ds.graph.flow.MaximumFlow;
import ds.graph.network.NetworkInterface;
import ds.graph.network.ResidualNetwork;
import ds.graph.network.ResidualNetworkExtended;
import ds.graph.problem.MaximumFlowProblem;
import de.tu_berlin.coga.container.mapping.IdentifiableBooleanMapping;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FordFulkerson extends Algorithm<MaximumFlowProblem, MaximumFlow> {
	protected ResidualNetwork residualNetwork;
	protected long pushes = 0;
	protected int flow = 0;
	protected int augmentations = 0;
	protected Node source;
	protected Node sink;
	boolean verbose = true;
	boolean useLower = true;
	private IdentifiableIntegerMapping<Edge> lowerCapacities;

	@Override
	protected MaximumFlow runAlgorithm( MaximumFlowProblem problem ) {
		if( residualNetwork == null ) // only initialize in the first run!
			initializeDatastructures();
		else {
			residualNetwork.update(); // second round
			cut = null;
		}

		int maxPossibleFlow = 0;
		for( Edge e : residualNetwork.outgoingEdges( source ) )
			maxPossibleFlow += residualNetwork.residualCapacities().get( e );

		int maxPossibleFlow2 = 0;
		for( Edge e : residualNetwork.incomingEdges( sink ) )
			if( residualNetwork.residualCapacities().get( e ) == Integer.MAX_VALUE ) {
				maxPossibleFlow2= Integer.MAX_VALUE;
				break;
			} else
				maxPossibleFlow2 += residualNetwork.residualCapacities().get( e );

		if( maxPossibleFlow2 < maxPossibleFlow )
			maxPossibleFlow = maxPossibleFlow2;

		int value = 0;
		do {
			StaticPath p = findPath();
			value = residualCapacity( p );
			augmentFlow( p, value );
			fireProgressEvent( value < Integer.MAX_VALUE ? (double)flow/maxPossibleFlow : 1 );
		} while( value > 0 && value < Integer.MAX_VALUE ); //while( augmentFlow() != 0 )

		return new MaximumFlow( getProblem(), residualNetwork.flow() );
	}

	private void initializeDatastructures() {
		if( useLower ) {
			residualNetwork = new ResidualNetworkExtended( getProblem().getNetwork(), getProblem().getCapacities() );
			((ResidualNetworkExtended)residualNetwork).setLower( lowerCapacities );
		} else
			residualNetwork = new ResidualNetwork( getProblem().getNetwork(), getProblem().getCapacities() );
		source = getProblem().getSource();
		sink = getProblem().getSink();
	}

	protected StaticPath findPath() {
		BFS bfs = new BFS( residualNetwork );
		bfs.run( source, sink );

		StaticPath path = new StaticPath();

		Node current = sink;
		do {
			final Edge e = bfs.predecedingEdge( current );
			if( e == null )
				return path;
			path.addFirstEdge( e );
			current = e.start();
		} while( !current.equals( source ) );
		return path;
	}

	private int residualCapacity( StaticPath path ) {
		if( path.length() == 0 )
			return 0;
		int min = Integer.MAX_VALUE;
		for( Edge e : path )
			min = Math.min( min, residualNetwork.residualCapacities().get( e ) );
		return min;
	}

	public void augmentFlow( StaticPath path, int value ) {
		Stack<Edge> s = new Stack<>();

		for( Edge e : path ) {
			residualNetwork.augmentFlow( e, value );
			pushes++;
			s.push( e );
		}

		System.out.println( "Auzgmented on " );
		while( !s.empty() ) {
			System.out.print( s.pop() );
		}
		System.out.println( " by " + value );

		flow += value;
		augmentations++;
	}

	public int getFlow() {
		return flow;
	}

	public int getAugmentations() {
		return augmentations;
	}

	public long getPushes() {
		return pushes;
	}

	IdentifiableBooleanMapping<Node> contained;
	Set<Node> cut;
	public Set<Node> computeCutNodes() {
		if( contained == null )
			contained = new IdentifiableBooleanMapping<>( residualNetwork.nodeCount() );
		for( Node n : getProblem().getNetwork() ) {
			contained.set( n, false );
		}
		BFS bfs = new BFS( residualNetwork );
		Set<Node> reachable = bfs.getReachableNodes( source );
		for( Node n : reachable ) {
			contained.set( n, true );
		}
		cut = reachable;
		return reachable;
	}

	LinkedList<Edge> cutOutgoing = new LinkedList<>();
	LinkedList<Edge> cutIncoming = new LinkedList<>();

	public void computeCutEdges() {
		if( cut == null ) {
			cut = computeCutNodes();
			cutOutgoing.clear();
			cutIncoming.clear();
		}

		for( Node n : cut ) {
			//for( Edge e : getProblem().getNetwork().outgoingEdges( n ) ) {
			for( Edge e : getProblem().getNetwork().outgoingEdges( n ) ) {
				// find outgoing edges
				if( !contained.get( e.end() ) && !cutOutgoing.contains( e ) )
					cutOutgoing.add( e );
			}
			for( Edge e : getProblem().getNetwork().incomingEdges( n ) ) {
				if( !contained.get( e.start() ) && !cutIncoming.contains( e ) ) {
					cutIncoming.add( e );
				}
			}
		}
	}

	public boolean isInCut( Node n ) {
		return contained.get( n );
	}

	public Set<Node> getCut() {
		return Collections.unmodifiableSet( cut );
	}

	public List<Edge> getOutgoingCut() {
		return Collections.unmodifiableList( cutOutgoing );
	}

	public List<Edge> getIncomingCut() {
		return Collections.unmodifiableList( cutIncoming );
	}

	public void setLowerCapacities( IdentifiableIntegerMapping<Edge> lowerCapacities ) {
		this.lowerCapacities = lowerCapacities;
	}
}
