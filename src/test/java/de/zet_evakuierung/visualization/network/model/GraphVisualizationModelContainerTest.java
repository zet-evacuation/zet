/*
 * zet evacuation tool copyright © 2007-21 zet evacuation team
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
package de.zet_evakuierung.visualization.network.model;

import static de.zet_evakuierung.visualization.ModelContainerTestUtils.assertObjectsFromIteratorAndMapAreEqual;
import static de.zet_evakuierung.visualization.ModelContainerTestUtils.assertPositions;
import static de.zet_evakuierung.visualization.ModelContainerTestUtils.createMockList;
import static de.zet_evakuierung.visualization.ModelContainerTestUtils.mockNodeFloorMapping;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import ds.GraphVisualizationResults;
import ds.graph.NodeRectangle;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;
import org.mockito.Mockito;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.collection.ListSequence;
import org.zetool.container.mapping.IdentifiableConstantMapping;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.container.mapping.TimeIntegerMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.graph.visualization.NodePositionMapping;
import org.zetool.math.vectormath.Vector3;
import org.zetool.netflow.ds.flow.EdgeBasedFlowOverTime;

/**
 * Tests that the {@link GraphVisualizationModelContainer.Builder builder} creates correct model containers.
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphVisualizationModelContainerTest {

    /**
     * Some pre-defined coordinates of objects; allows to create 3 test objects.
     */
    private static final List<Point2D> POSITIONS = List.of(new Point2D.Double(100, 200), new Point2D.Double(0, 0),
            new Point2D.Double(1000, 3000));

//    @Test
    public void builderInitialization() {
        assertThrows(NullPointerException.class,
                () -> new GraphVisualizationModelContainer.Builder(null, mock(NetworkVisualizationModel.class)));
        assertThrows(NullPointerException.class,
                () -> new GraphVisualizationModelContainer.Builder(mock(GraphVisualizationResults.class), null));
    }

    /**
     * Asserts that a builder with an empty {@link DirectedGraph network} produces an empty output model.
     */
    @Test
    public void emptyModel() {
        BuilderBaseMocks baseMocks = new BuilderBaseMocks();

        setUpFloors(baseMocks, 0);

        GraphVisualizationModelContainer fixture = buildFixture(baseMocks);

        baseMocks.verifyGeneral();
        baseMocks.verifyNoMoreInteractions();

        assertThat(fixture.getGraphModel(), is(not(nullValue())));
        assertCounts(fixture, 0, 0, 0);
    }

    @Test
    public void multipleEmptyFloors() {
        BuilderBaseMocks baseMocks = new BuilderBaseMocks();

        int floorCount = 3;
        setUpFloors(baseMocks, floorCount);

        GraphVisualizationModelContainer fixture = buildFixture(baseMocks);

        baseMocks.verifyGeneral();
        baseMocks.verifyFloors(floorCount);
        baseMocks.verifyNoMoreInteractions();

        assertCounts(fixture, 3, 0, 0);

        // Assert each floor object
        List<Integer> floorIds = IntStream.range(0, floorCount).boxed().collect(toList());
        assertObjectsFromIteratorAndMapAreEqual(fixture.floors(), floorIds, fixture::getFloorModel);
        assertPositions(fixture::getFloorModel, floorIds, Collections.nCopies(floorCount, new Point2D.Double(0, 0)));
    }

    /**
     * Tests that nodes are placed on correct floors.
     */
    @Test
    public void nodesOnCorrectFloors() {
        BuilderBaseMocks baseMocks = new BuilderBaseMocks();

        // Set up two floors with one and two nodess, respectively
        int floorCount = 2;
        setUpFloors(baseMocks, floorCount);
        List<Node> nodes = setUpNodes(baseMocks, floorCount, 1, 2);

        GraphVisualizationModelContainer fixture = buildFixture(baseMocks);

        baseMocks.verifyGeneral();
        baseMocks.verifyFloors(floorCount);
        baseMocks.verifyNodes(nodes, null);
        baseMocks.verifyNoMoreInteractions();

        assertCounts(fixture, floorCount, 3, 0);
        assertObjectsFromIteratorAndMapAreEqual(fixture.nodes(), nodes, fixture::getNodeModel);
        assertPositions(fixture::getNodeModel, nodes, Collections.nCopies(3, new Point2D.Double(0, 0)));
    }

    @Test
    public void nodesConnected() {
        BuilderBaseMocks baseMocks = new BuilderBaseMocks();

        // Set up a single floor with three nodes connected by 2 edges
        int floorCount = 1;
        setUpFloors(baseMocks, floorCount);
        int nodeCount = 3;
        List<Node> nodes = setUpNodes(baseMocks, 1, nodeCount);
        int edgeCount = nodeCount - 1;
        List<Edge> edges = setUpEdges(baseMocks, nodes, nodes.subList(1, nodeCount), List.of(), List.of());

        when(baseMocks.visualizationModel.superSinkID()).thenReturn(Integer.MIN_VALUE);

        GraphVisualizationModelContainer fixture = buildFixture(baseMocks);

        baseMocks.verifyGeneral();
        baseMocks.verifyFloors(floorCount);
        baseMocks.verifyNodes(nodes, null);
        baseMocks.verifyEdges();
        baseMocks.verifyNoMoreInteractions();

        assertCounts(fixture, floorCount, nodeCount, edgeCount);
        assertObjectsFromIteratorAndMapAreEqual(fixture.edges(), edges, fixture::getEdgeModel);
    }

    /**
     * Sets up the mocks for floors.
     *
     * @param baseMocks the base mocks that are set up
     * @param names the floor names, if none present the result is empty
     */
    private static List<String> setUpFloors(BuilderBaseMocks baseMocks, int floorCount) {
        List<String> floors = List.of();

        ArrayList<ArrayList<Node>> resultList = new ArrayList<>(floorCount);
        IntStream.range(0, floorCount).forEach(i -> resultList.add(new ArrayList<>()));
        when(baseMocks.visualizationResults.getFloorToNodeMapping()).thenReturn(resultList);

        return floors;
    }

    /**
     * Sets up the mocks for Nodes. Prepares the mocked data in the {@link GraphVisualizationResults visualization
     * results} to prepare simple or empty values for each node. These values are required to instantiate
     * {@link GLNodeModel node model} objects.
     *
     * @param baseMocks the base mocks that are set up
     * @param floorCount the number of floors
     * @param nodesOnFloor number of nodes for each floors; must have the same size as {@code floors}
     * @return a list of all nodess, ordered by floor
     */
    private static List<Node> setUpNodes(BuilderBaseMocks baseMocks, int floorCount, int... nodesOnFloor) {
        assertThat(floorCount, is(equalTo(nodesOnFloor.length)));

        List<Node> nodes = IntStream.range(0, Arrays.stream(nodesOnFloor).sum()).mapToObj(Node::new).collect(toList());

        IdentifiableObjectMapping<Node, NodeRectangle> nodeRectangles = mock(IdentifiableObjectMapping.class);
        NodePositionMapping<Vector3> nodePositionMapping = mock(NodePositionMapping.class);
        IdentifiableIntegerMapping<Node> nodeCapacities = mock(IdentifiableIntegerMapping.class);
        when(nodeRectangles.get(any())).thenReturn(new NodeRectangle(1, 2, 3, 4));
        when(nodePositionMapping.get(any())).thenReturn(new Vector3());
        when(nodeCapacities.get(any())).thenReturn(0);

        IdentifiableCollection<Edge> emptyCollection = new ListSequence<>();
        when(baseMocks.network.outgoingEdges(any())).thenReturn(emptyCollection);

        mockNodeFloorMapping(baseMocks.visualizationResults, nodes, nodesOnFloor);

        for (int i = 0; i < nodes.size(); ++i) {
            when(baseMocks.visualizationResults.getNodeRectangles()).thenReturn(nodeRectangles);
            when(baseMocks.visualizationResults.getNodePositionMapping()).thenReturn(nodePositionMapping);
            when(baseMocks.visualizationResults.getNodeCapacities()).thenReturn(nodeCapacities);
        }

        return nodes;
    }

    /**
     * Sets up edge mocks for a nodes. Defines all properties required to instantiate
     * {@link de.zet_evakuierung.visualization.network.draw.GLFlowEdge edge model} instances.
     *
     * @param baseMocks the base mocks that are set up
     * @param node the nodes for which the edges are created
     * @param to the number of edge mocks created
     * @return the list of created edges
     */
    private static List<Edge> setUpEdges(BuilderBaseMocks baseMocks, List<Node> nodes, List<Node>... to) {
        assertThat(nodes.size(), is(equalTo(to.length)));

        List<List<Edge>> edgesByNode = Arrays.stream(to)
                .map(targetList -> createMockList(Edge.class, targetList.size()))
                .collect(toList());

        List<Edge> edges = edgesByNode.stream().flatMap(List::stream).collect(toList());

        for (int i = 0; i < nodes.size(); ++i) {
            Node node = nodes.get(i);
            List<Edge> outgoingEdges = edgesByNode.get(i);
            when(baseMocks.network.outDegree(node)).thenReturn(outgoingEdges.size());
            when(baseMocks.network.outgoingEdges(node)).thenReturn(new ListSequence<>(outgoingEdges));
            for (int j = 0; j < outgoingEdges.size(); ++j) {
                Edge edge = outgoingEdges.get(j);
                when(edge.start()).thenReturn(node);
                when(edge.end()).thenReturn(to[i].get(j));
            }
        }

        IdentifiableIntegerMapping<Edge> mockTransitTimes = new IdentifiableConstantMapping<>(3);
        when(baseMocks.visualizationResults.getTransitTimes()).thenReturn(mockTransitTimes);

        IdentifiableIntegerMapping<Edge> mockEdgeCapacities = new IdentifiableConstantMapping<>(3);
        when(baseMocks.visualizationResults.getEdgeCapacities()).thenReturn(mockEdgeCapacities);

        // calculate flow on the edge
        EdgeBasedFlowOverTime mockFlowOverTime = mock(EdgeBasedFlowOverTime.class);
        when(baseMocks.visualizationResults.getFlow()).thenReturn(mockFlowOverTime);

        TimeIntegerMapping mockTimeIntegerMapping = mock(TimeIntegerMapping.class);
        when(mockTimeIntegerMapping.getLastTimeWithNonZeroValue()).thenReturn(0);
        edges.forEach(edge -> when(mockFlowOverTime.get(edge)).thenReturn(mockTimeIntegerMapping));

        return edges;
    }

    /**
     * Builds the graph model by executing its builder. Must only be called once all mocks have been set up.
     *
     * @param baseMocks the base mocks
     * @return the built instance
     */
    private static GraphVisualizationModelContainer buildFixture(BuilderBaseMocks baseMocks) {
        return new GraphVisualizationModelContainer.Builder(
                baseMocks.visualizationResults, baseMocks.visualizationModel).build();
    }

    /**
     * Asserts the numbers of {@code floor}s, {@code node}s, and {@code edge}s in a created model object.
     *
     * @param fixture the model object that is asserted
     * @param floorCount the expected number of floors
     * @param nodeCount the expected number of nodes
     * @param edgeCount the expected number of edges
     */
    private static void assertCounts(GraphVisualizationModelContainer fixture, int floorCount, int nodeCount,
            int edgeCount) {
        assertThat("floor count", fixture.getFloorCount(), is(equalTo(floorCount)));
        assertThat("floor size", fixture.floors(), is(iterableWithSize(floorCount)));
        assertThat("node size", fixture.nodes(), is(iterableWithSize(nodeCount)));
        assertThat("edge size", fixture.edges(), is(iterableWithSize(edgeCount)));
    }

    /**
     * Structure containing important mocks to initialize the builder with.
     */
    private static class BuilderBaseMocks {

        final GraphVisualizationResults visualizationResults;
        final NetworkVisualizationModel visualizationModel;
        final DirectedGraph network;

        public BuilderBaseMocks() {
            visualizationResults = mock(GraphVisualizationResults.class);
            visualizationModel = mock(NetworkVisualizationModel.class);
            network = mock(DirectedGraph.class);
            when(visualizationResults.getNetwork()).thenReturn(network);
        }

        /**
         * Sets up interactions that happen always during build.
         */
        private void verifyGeneral() {
            verify(visualizationResults, atLeastOnce()).getFloorToNodeMapping();
            verify(visualizationResults, atLeastOnce()).getNetwork();
        }

        /**
         * Verifies ase mock interactions for floors. Not necessary if no floors are mocked.
         *
         * @param floorCount the number of floors, 0 or greater
         */
        private void verifyFloors(int floorCount) {
            for (int i = 0; i < floorCount; ++i) {
                verify(visualizationResults, atLeastOnce()).getNodesOnFloor(i);
            }
        }

        /**
         * Verifies base mock interactions for nodes. Verifies both, interactions happening in the
         * {@link GraphVisualizationModelContainer class under test} as well as in created {@link GLNodeModel node}
         * instances.
         *
         * If a super sink is present, the interactions for it are not verified (as it is excluded in the builder).
         *
         * @param nodes the node mocks used in the test
         * @param superSink the super sink node, can be {@code null}
         */
        private void verifyNodes(List<Node> nodes, @Nullable Node superSink) {
            verify(visualizationResults, atLeastOnce()).getNodeRectangles();
            verify(visualizationResults, atLeastOnce()).getNodePositionMapping();
            verify(visualizationResults, atLeastOnce()).getNodeCapacities();
            verify(visualizationResults, atLeastOnce()).getNodeToFloorMapping();

            for (Node node : nodes) {
                if (node == superSink) {
                    continue;
                }
                // This is the only verification for the class under test
                verify(network, atLeastOnce()).outgoingEdges(node);

                verify(visualizationResults, atLeastOnce()).isEvacuationNode(node);
                verify(visualizationResults, atLeastOnce()).isSourceNode(node);
                verify(visualizationResults, atLeastOnce()).isDeletedSourceNode(node);
            }

            verify(visualizationModel, atLeastOnce()).nodeProgress();
        }

        /**
         * Verifies base mock interactions for edges. Verifies interactions in the created {@link GLEdgeControl edge}
         * instances.
         */
        private void verifyEdges() {
            // Verifications in the created edge instances
            verify(visualizationResults, atLeastOnce()).getMaxFlowRate();
            verify(visualizationResults, atLeastOnce()).getTransitTimes();
            verify(visualizationResults, atLeastOnce()).getFlow();
            verify(visualizationResults, atLeastOnce()).getTransitTimes();
            verify(visualizationResults, atLeastOnce()).getEdgeCapacities();
        }

        void verifyNoMoreInteractions() {
            Mockito.verifyNoMoreInteractions(visualizationResults);
            Mockito.verifyNoMoreInteractions(visualizationModel);
            Mockito.verifyNoMoreInteractions(network);
        }

    }

}
