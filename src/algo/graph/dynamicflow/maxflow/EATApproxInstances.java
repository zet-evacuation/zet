/**
 * EATApproxInstances.java
 * Created: 05.12.2011, 12:54:29
 */
package algo.graph.dynamicflow.maxflow;

import de.tu_berlin.math.coga.graph.generator.RMFGEN;
import de.tu_berlin.math.coga.rndutils.distribution.discrete.UniformDistribution;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.ArrayList;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EATApproxInstances {
	Network network;
	IdentifiableIntegerMapping<Edge> capacities;
	IdentifiableIntegerMapping<Edge> transitTime;
	IdentifiableIntegerMapping<Node> supplies;
	ArrayList<Node> sources;
	ArrayList<Node> sinks;
	int timeHorizon;
	int totalSupply;
	
	public EATApproxInstances() {

		UniformDistribution dist = new UniformDistribution( 1, 20 );
		RMFGEN gen = new RMFGEN();
		gen.setDistribution( dist );
		
		int a = 16;
		int b = 4;
		
		gen.generateCompleteGraph( a, b );
		
		
		network = gen.getGraph();
	
		capacities = gen.getCapacities();
		
		transitTime = new IdentifiableIntegerMapping<>( network.getEdgeCapacity() );
		for( int i = 0; i < network.getEdgeCapacity(); ++i )
			transitTime.add( network.getEdge( i ), 0 );
		
		supplies = new IdentifiableIntegerMapping<>( network.getNodeCapacity() );
		for( int i = 0; i < network.getNodeCapacity(); ++i )
			supplies.set( network.getNode( i ), 0 );
		
		sources = new ArrayList<>( a*a );
		
		sinks = new ArrayList<>( a*a );

		totalSupply = 0;
		for( int i = 0; i < a*a; ++i ) {
			int supply = dist.getNextRandom()*10;
			totalSupply += supply;
			supplies.set( network.getNode( i ), supply );
			supplies.set( network.getNode( a*a*b -(i+1) ), -supply );
			sources.add( network.getNode( i ) );
			sinks.add( network.getNode( a*a*b - (i+1) ) );
		}
		
		timeHorizon = 4;
		
		System.out.println( "Total supply: " + totalSupply );
	}
	
	
	
	public static void main( String[] arguments ) {
		Network n = new Network(4, 3);
		int M = 100;
		
		int timeHorizon = 1;
		
		n.createAndSetEdge( n.getNode( 0 ), n.getNode( 2 ) );
		n.createAndSetEdge( n.getNode( 0 ), n.getNode( 3 ) );
		n.createAndSetEdge( n.getNode( 1 ), n.getNode( 3 ) );
		
		IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<>( 3 );
		capacities.set( n.getEdge( 0 ), 1 );
		capacities.set( n.getEdge( 1 ), M-1 );
		capacities.set( n.getEdge( 2 ), 1 );
		
		IdentifiableIntegerMapping<Edge> transitTime = new IdentifiableIntegerMapping<>( 3 );
		transitTime.set( n.getEdge( 0 ), 0 );
		transitTime.set( n.getEdge( 1 ), 0 );
		transitTime.set( n.getEdge( 2 ), 0 );
		
		IdentifiableIntegerMapping<Node> supplies = new IdentifiableIntegerMapping<>( 4 );
		supplies.set( n.getNode( 0 ), M );
		supplies.set( n.getNode( 1 ), M );
		supplies.set( n.getNode( 2 ), -M );
		supplies.set( n.getNode( 3 ), -M );
		
		System.out.println( n.toString() );
		
		ArrayList<Node> sources = new ArrayList<>(2);
		sources.add( n.getNode( 0 ) );
		sources.add( n.getNode( 1 ) );
		
		ArrayList<Node> sinks = new ArrayList<>(2);
		sinks.add( n.getNode( 2 ) );
		sinks.add( n.getNode( 3 ) );

		//MaximumFlowOverTimeProblem p = new MaximumFlowOverTimeProblem(n, capacities, transitTime, sources, sinks, timeHorizon );
		
		ArrayList<Integer> flowValues = new ArrayList<>();
MaximumFlowOverTimeProblem p;
				
		LimitedMaxFlowOverTime lmfot;
		int T = 1;
		int totalSupply = 2*M;
		do {
			System.out.println( "T = " + T );
			timeHorizon = T++;
			
			p = new MaximumFlowOverTimeProblem( n, capacities, transitTime, sources, sinks, timeHorizon );		
			lmfot = new LimitedMaxFlowOverTime( p, supplies );		
			lmfot.runAlgorithm();
			flowValues.add( (int)lmfot.hiprf );
			
			System.out.println();
		} while( lmfot.getFlow() != totalSupply );

			p = new MaximumFlowOverTimeProblem( n, capacities, transitTime, sources, sinks, T );		
		EATApprox eata = new EATApprox( p, supplies );
//		
		eata.runAlgorithm();
//

		System.out.println( flowValues.toString() );
		System.out.println( eata.flowCurve.toString() );
		
//		EATApproxInstances eai = new EATApproxInstances();
//
//		ArrayList<Integer> flowValues = new ArrayList<>();
//		
//		LimitedMaxFlowOverTime lmfot;
//		int T = 1;
//		do {
//			System.out.println( "T = " + T );
//			eai.timeHorizon = T++;
//			
//			MaximumFlowOverTimeProblem p = new MaximumFlowOverTimeProblem(eai.network, eai.capacities, eai.transitTime, eai.sources, eai.sinks, eai.timeHorizon );		
//			lmfot = new LimitedMaxFlowOverTime( p, eai.supplies );		
//			lmfot.runAlgorithm();
//			flowValues.add( (int)lmfot.hiprf );
//			
//			System.out.println();
//		} while( lmfot.getFlow() != eai.totalSupply );
//		
//		//eai = new EATApproxInstances();
//		eai.timeHorizon = T;
//		MaximumFlowOverTimeProblem p = new MaximumFlowOverTimeProblem(eai.network, eai.capacities, eai.transitTime, eai.sources, eai.sinks, eai.timeHorizon );
//		
//		//LimitedMaxFlowOverTime lmfot = new LimitedMaxFlowOverTime( p, supplies );
//		
//		//lmfot.runAlgorithm();
//		
//		EATApprox eata = new EATApprox( p, eai.supplies );
//		
//		eata.runAlgorithm();
//
//		System.out.println( flowValues.toString() );
//		System.out.println( eata.flowCurve.toString() );

	}
}
