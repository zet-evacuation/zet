/**
 * EATApprox.java
 * Created: 02.12.2011, 15:35:02
 */
package algo.graph.dynamicflow.eat;

import de.tu_berlin.math.coga.algorithm.flowovertime.maxflow.MaximumFlowOverTimeProblem;
import de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.PushRelabel;
import de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.PushRelabelHighestLabel;
import ds.graph.Edge;
import ds.mapping.IdentifiableIntegerMapping;
import ds.graph.network.AbstractNetwork;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.network.Network;
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
			AbstractNetwork n = new Network( problem.getNetwork().numberOfNodes() + 2, problem.getNetwork().numberOfEdges() + sinkCount + sourceCount );
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
			PushRelabel hipr = new PushRelabelHighestLabel();
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

			//System.out.println( "Flow value: " + mf.getFlowValue() );
			//System.out.println( "Sink-Arrival value: " + hipr.getFlowValue() );
			long hiprf = hipr.getFlowValue();
			lastFlow = mf.getFlowValue();

			//System.out.println( "Pushes: " + hipr.getPushes() + ", Relabels: " + hipr.getRelabels() );
			//System.out.println( "Global relabels: " + hipr.getGlobalRelabels() );
			//System.out.println( "Gaps : " + hipr.getGaps() + " Gap nodes: " + hipr.getGapNodes() );
			//System.out.println( Formatter.formatTimeUnit( end-start, TimeUnits.NanoSeconds ) );


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
}