/**
 * EATApprox.java
 * Created: 02.12.2011, 15:35:02
 */
package algo.graph.dynamicflow.maxflow;

import algo.graph.staticflow.maxflow.EdmondsKarp;
import algo.graph.staticflow.maxflow.PushRelabel;
import algo.graph.staticflow.maxflow.PushRelabelHighestLabel;
import algo.graph.staticflow.maxflow.PushRelabelHighestLabelGlobalRelabelling;
import algo.graph.staticflow.maxflow.PushRelabelHighestLabelNeu;
import de.tu_berlin.math.coga.common.util.Formatter;
import de.tu_berlin.math.coga.common.util.Formatter.TimeUnits;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.problem.MaximumFlowProblem;
import java.util.ArrayList;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EATApprox {
		MaximumFlowOverTimeProblem problem;
	IdentifiableIntegerMapping<Node> supplies;
	ArrayList<Integer> flowCurve = new ArrayList<>();

		public EATApprox( MaximumFlowOverTimeProblem problem, IdentifiableIntegerMapping<Node> supplies ) {
		this.problem = problem;
		this.supplies = supplies;
	}
	
	protected void runAlgorithm() {
		if( problem.getSources().isEmpty() || problem.getSinks().isEmpty() ) {
			System.out.println( "TimeExpandedMaximumFlowOverTime: The problem is invalid - this should not happen!" );
			//return new PathBasedFlowOverTime();
			throw new IllegalArgumentException( "empty problem" );
		}
		
		// check for zero transit times
		if( problem.getTransitTimes().maximum() > 0 )
			throw new IllegalArgumentException( "Not-Zero transit times!" );

		// run the algorithm
		int sinkCount = problem.getSinks().size();
		int sourceCount = problem.getSources().size();
		
		int sumOfSupplies = 0;

		int lastFlow = -1;
		int totalFlow = 0;
		
		do {
			Network n = new Network( problem.getNetwork().numberOfNodes() + 2, problem.getNetwork().numberOfEdges() + sinkCount + sourceCount );
			IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<>( n.getEdgeCapacity() );

			int superSource = problem.getNetwork().numberOfNodes();
			int superSink = problem.getNetwork().numberOfNodes()+1;

			System.out.println( supplies.toString() );

			for( Edge e : problem.getNetwork().edges() ) {
				Edge newEdge = n.createAndSetEdge( e.start(), e.end() );
				//n.setEdgeCapacity( problem.getCapacities().get( e ) );
				capacities.set( newEdge, problem.getCapacities().get( e ) );
			}

			for( Node s : problem.getSources() ) {
				Edge newEdge = n.createAndSetEdge( n.getNode( superSource ), n.getNode( s.id() ) );
				capacities.set( newEdge, supplies.get( s ) );
			}

			for( Node s : problem.getSinks() ) {
				Edge newEdge = n.createAndSetEdge( n.getNode( s.id() ), n.getNode( superSink ) );
				capacities.set( newEdge, supplies.get( s ) * -1 );
			}

			//System.out.println( n.toString() );
			//System.out.println( capacities.toString() );

			MaximumFlowProblem maximumFlowProblem = new MaximumFlowProblem(n, capacities, n.getNode( superSource ), n.getNode( superSink ) );

			//PushRelabel hipr = new PushRelabelHighestLabelGlobalRelabelling();
			PushRelabel hipr = new PushRelabelHighestLabelNeu();
			//PushRelabel hipr = new PushRelabelHighestLabel();

//		EdmondsKarp ek = new EdmondsKarp();
//		ek.setProblem( maximumFlowProblem );
//		ek.run();
//		System.out.println( ek.getSolution().getFlowValue() );

			
			hipr.setProblem( maximumFlowProblem );
			long start = System.nanoTime();
			hipr.run();
			long end = System.nanoTime();
			MaximumFlow mf = hipr.getSolution();

			System.out.println( "Flow value: " + mf.getFlowValue() );
			System.out.println( "Sink-Arrival value: " + hipr.getFlowValue() );
			long hiprf = hipr.getFlowValue();
			lastFlow = mf.getFlowValue();

			//System.out.println( "Pushes: " + hipr.getPushes() + ", Relabels: " + hipr.getRelabels() );
			//System.out.println( "Global relabels: " + hipr.getGlobalRelabels() );
			//System.out.println( "Gaps : " + hipr.getGaps() + " Gap nodes: " + hipr.getGapNodes() );
			System.out.println( Formatter.formatTimeUnit( end-start, TimeUnits.NanoSeconds ) );


			//System.out.println( "Fluss auf 12: " + mf.get( n.getEdge( 12 ) ) );
			//System.out.println( "Fluss auf 13: " + mf.get( n.getEdge( 13 ) ) );

			// reduce capacities
			for( Edge e : n.outgoingEdges( n.getNode( superSource ) ) ) {
				supplies.decrease( e.end(), mf.get( e ) );
			}

			for( Edge e : n.incomingEdges( n.getNode( superSink ) ) ) {
				supplies.increase( e.start(), mf.get( e ) );
			}

			sumOfSupplies = 0;
			for( Node s : problem.getSources() ) {
				sumOfSupplies += supplies.get( s );
			}

			//System.out.println( supplies.toString() );
			System.out.println( "Left over supplies: " + sumOfSupplies );
			//System.out.println( n.toString() );
			totalFlow += lastFlow;
			if( lastFlow != 0 )
				flowCurve.add( totalFlow );
		} while( sumOfSupplies != 0 && lastFlow != 0 );
		
		
	}
	
	public static void main( String[] args ) {
		
		Network n = new Network(4, 3);
		
		int timeHorizon = 2;
		
		n.createAndSetEdge( n.getNode( 0 ), n.getNode( 2 ) );
		n.createAndSetEdge( n.getNode( 0 ), n.getNode( 3 ) );
		n.createAndSetEdge( n.getNode( 1 ), n.getNode( 3 ) );
		
		IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<>( 3 );
		capacities.set( n.getEdge( 0 ), 1 );
		capacities.set( n.getEdge( 1 ), 1 );
		capacities.set( n.getEdge( 2 ), 1 );
		
		IdentifiableIntegerMapping<Edge> transitTime = new IdentifiableIntegerMapping<>( 3 );
		transitTime.set( n.getEdge( 0 ), 0 );
		transitTime.set( n.getEdge( 1 ), 0 );
		transitTime.set( n.getEdge( 2 ), 0 );
		
		IdentifiableIntegerMapping<Node> supplies = new IdentifiableIntegerMapping<>( 4 );
		supplies.set( n.getNode( 0 ), 4 );
		supplies.set( n.getNode( 1 ), 2 );
		supplies.set( n.getNode( 2 ), -4 );
		supplies.set( n.getNode( 3 ), -2 );
		
		
		System.out.println( n.toString() );
		
		ArrayList<Node> sources = new ArrayList<>(2);
		sources.add( n.getNode( 0 ) );
		sources.add( n.getNode( 1 ) );
		
		ArrayList<Node> sinks = new ArrayList<>(2);
		sinks.add( n.getNode( 2 ) );
		sinks.add( n.getNode( 3 ) );

		MaximumFlowOverTimeProblem p = new MaximumFlowOverTimeProblem(n, capacities, transitTime, sources, sinks, timeHorizon );
		
		//LimitedMaxFlowOverTime lmfot = new LimitedMaxFlowOverTime( p, supplies );
		
		//lmfot.runAlgorithm();
		
		EATApprox eata = new EATApprox( p, supplies );
		
		eata.runAlgorithm();
		
	}

}
