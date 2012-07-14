/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow;

import algo.graph.traverse.BFS;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.StaticPath;
import ds.graph.flow.MaximumFlow;
import ds.graph.network.ResidualNetwork;
import ds.graph.problem.MaximumFlowProblem;
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

	@Override
	protected MaximumFlow runAlgorithm( MaximumFlowProblem problem ) {
		if( residualNetwork == null ) // only initialize in the first run!
			initializeDatastructures();

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
			fireProgressEvent( (double)flow/maxPossibleFlow );
		} while( value > 0 ); //while( augmentFlow() != 0 )

		return new MaximumFlow( getProblem(), residualNetwork.flow() );
	}

	private void initializeDatastructures() {
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
}
