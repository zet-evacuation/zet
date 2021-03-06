/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import static org.junit.Assert.*;

import java.util.logging.Level;

import org.junit.Test;

import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.common.debug.Debug;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import zet.tasks.GraphAlgorithmEnumeration;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ShrinkerTest {

    @Test
    public void rectangleTest() {
        Debug.setUpLogging();
        Debug.setDefaultLogLevel(Level.FINEST);
        // convert
        int nodes = 10;
        performTest(new RectangleConverter(), nodes, 35);
    }

    @Test
    public void dijkstraTest() {
        Debug.setUpLogging();
        Debug.setDefaultLogLevel(Level.FINEST);
        // convert
        int nodes = 10;
        performTest(new GraphConverterAndShrinker(new RectangleConverter(), new ShortestPathTreeShrinker()), nodes, (nodes - 2) * 2 + 1);
    }

    @Test
    public void spanningTreeTest() {
        Debug.setUpLogging();
        Debug.setDefaultLogLevel(Level.FINEST);
        // convert
        int nodes = 10;

        performTest(new GraphConverterAndShrinker(new RectangleConverter(), new SpanningTreeShrinker()), nodes, (nodes - 2) * 2 + 1);
    }

    @Test
    public void greedySpannerTest() {
        Debug.setUpLogging();
        Debug.setDefaultLogLevel(Level.FINEST);
        // convert
        int nodes = 10;

        // Spanner of size 11, 10 inner edges are duplicated
        performTest(new GraphConverterAndShrinker(new RectangleConverter(), new GreedySpannerShrinker()), nodes, 21);
    }

    @Test
    public void steinerTreeTest() {
        Debug.setUpLogging();
        Debug.setDefaultLogLevel(Level.FINEST);
        // convert
        int nodes = 10;

        performTest(new GraphConverterAndShrinker(new RectangleConverter(), new SteinerTreeShrinker()), nodes, 7);
    }

    @Test
    public void clusterTest() {
        Debug.setUpLogging();
        Debug.setDefaultLogLevel(Level.FINEST);
        // convert
        int nodes = 10;

        // here we have non-determinism? number of edges varies from run to run...
        // performTest(new GraphConverterAndShrinker(new RectangleConverter(), new ClusterShrinker()), nodes, 24);
        InstanceGenerator ig = new InstanceGenerator();
        ig.setUpInstance();

        RectangleConverter conv = new RectangleConverter();
        conv.setProblem(ig.zControl.getProject().getBuildingPlan());
        conv.run();
    }

    @Test
    public void shortestPathGraphTest() {
        Debug.setUpLogging();
        Debug.setDefaultLogLevel(Level.FINEST);
        // convert
        int nodes = 10;

        performTest(new GraphConverterAndShrinker(new RectangleConverter(), new ShortestPathGraphShrinker()), nodes, 19);
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
        Debug.setDefaultLogLevel(Level.FINEST);
        // convert
        int nodes = 9;
        performTest(new GraphConverterAndShrinker(new RectangleConverter(), new RepeatedShortestPathsShrinker()), nodes, 25);
    }

    private void performTest(AbstractAlgorithm<BuildingPlan, NetworkFlowModel> conv, int targetNodes, int targetEdges) {
        InstanceGenerator ig = new InstanceGenerator();
        ig.setUpInstance();

        conv.setProblem(ig.zControl.getProject().getBuildingPlan());
        conv.run();
        assertEquals("Number of nodes", targetNodes, conv.getSolution().numberOfNodes());
        assertEquals("Number of edges", targetEdges, conv.getSolution().numberOfEdges());

        // concrete assignment
        ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment(ig.zControl.getProject().getCurrentAssignment(), 400);
        GraphAssignmentConverter cav = new GraphAssignmentConverter(conv.getSolution());
        cav.setProblem(concreteAssignment);
        cav.run();

        // call the graph algorithm
        AbstractAlgorithm<NetworkFlowModel, PathBasedFlowOverTime> gt = GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimized.createTask(cav.getSolution(), 600);
        gt.setProblem(cav.getSolution());
        gt.run();
    }
}
