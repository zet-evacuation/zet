/**
 * ShrinkerTest.java
 * Created: Oct 23, 2012, 12:39:05 PM
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.common.debug.Debug;
import de.tu_berlin.coga.netflow.ds.flow.PathBasedFlowOverTime;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;
import java.util.logging.Level;
import static org.junit.Assert.*;
import org.junit.Test;
import zet.tasks.GraphAlgorithmEnumeration;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ShrinkerTest {

	@Test
	public void rectangleTest() {
		Debug.setUpLogging();
		Debug.setDefaultLogLevel( Level.FINEST );
		// convert
		int nodes = 10;
		performTest( new RectangleConverter(), nodes, 35 );
	}


	@Test
	public void dijkstraTest() {
		Debug.setUpLogging();
		Debug.setDefaultLogLevel( Level.FINEST );
		// convert
		int nodes = 10;
		performTest( new GraphConverterAndShrinker( new RectangleConverter(), new ShortestPathTreeShrinker() ), nodes, (nodes - 2) * 2 + 1 );
	}

	@Test
	public void spanningTreeTest() {
		Debug.setUpLogging();
		Debug.setDefaultLogLevel( Level.FINEST );
		// convert
		int nodes = 10;

		performTest( new GraphConverterAndShrinker( new RectangleConverter(), new SpanningTreeShrinker() ), nodes, (nodes - 2) * 2 + 1 );
	}

	@Test
	public void greedySpannerTest() {
		Debug.setUpLogging();
		Debug.setDefaultLogLevel( Level.FINEST );
		// convert
		int nodes = 10;

		performTest( new GraphConverterAndShrinker( new RectangleConverter(), new GreedySpannerShrinker() ), nodes, 33 );
	}

	@Test
	public void steinerTreeTest() {
		Debug.setUpLogging();
		Debug.setDefaultLogLevel( Level.FINEST );
		// convert
		int nodes = 10;

		performTest( new GraphConverterAndShrinker( new RectangleConverter(), new SteinerTreeShrinker() ), nodes, (nodes - 2) * 2 + 1 );
	}

	@Test
	public void clusterTest() {
		Debug.setUpLogging();
		Debug.setDefaultLogLevel( Level.FINEST );
		// convert
		int nodes = 10;

		// here we have non-determinism? number of edges varies from run to run...
		performTest( new GraphConverterAndShrinker( new RectangleConverter(), new ClusterShrinker() ), nodes, 21 );
	}

	@Test
	public void shortestPathGraphTest() {
		Debug.setUpLogging();
		Debug.setDefaultLogLevel( Level.FINEST );
		// convert
		int nodes = 10;

		performTest( new GraphConverterAndShrinker( new RectangleConverter(), new ShortestPathGraphShrinker() ), nodes, 35 );
	}

//	@Test
//	public void AllPairsShortestPathTest() {
//		Debug.setUpLogging();
//		Debug.setDefaultLogLevel( Level.FINEST );
//		// convert
//		int nodes = 10;
//
//		performTest( new GraphConverterAndShrinker( new RectangleConverter(), new APSPGraphShrinker() ), nodes, (nodes - 2) * 2 + 1 );
//	}

	@Test
	public void repeatedShortestPathsTest() {
		Debug.setUpLogging();
		Debug.setDefaultLogLevel( Level.FINEST );
		// convert
		int nodes = 10;
		performTest( new GraphConverterAndShrinker( new RectangleConverter(), new RepeatedShortestPathsShrinker() ), nodes, 35 );

	}

	private void performTest( Algorithm<BuildingPlan, NetworkFlowModel> conv, int targetNodes, int targetEdges ) {
		InstanceGenerator ig = new InstanceGenerator();
		ig.setUpInstance();

		conv.setProblem( ig.zControl.getProject().getBuildingPlan() );
		conv.run();
		assertEquals( "Number of nodes", targetNodes, conv.getSolution().numberOfNodes() );
		assertEquals( "Number of edges", targetEdges, conv.getSolution().numberOfEdges() );

		// concrete assignment
		ConcreteAssignment concreteAssignment = ig.zControl.getProject().getCurrentAssignment().createConcreteAssignment( 400 );
		GraphAssignmentConverter cav = new GraphAssignmentConverter( conv.getSolution() );
		cav.setProblem( concreteAssignment );
		cav.run();

		// call the graph algorithm
		Algorithm<NetworkFlowModel, PathBasedFlowOverTime> gt = GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimized.createTask( cav.getSolution(), 600 );
		gt.setProblem( cav.getSolution() );
		gt.run();
	}
}