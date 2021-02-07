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
import java.util.Optional;
import java.util.stream.IntStream;

import de.zet_evakuierung.visualization.network.GraphVisualizationData;
import ds.graph.NodeRectangle;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;
import org.mockito.Mockito;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.collection.ListSequence;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.math.vectormath.Vector3;

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

    @Test
    public void builderInitialization() {
        assertThrows(NullPointerException.class,
                () -> new GraphVisualizationModelContainer.Builder(null, mock(NetworkVisualizationModel.class)));
        assertThrows(NullPointerException.class,
                () -> new GraphVisualizationModelContainer.Builder(mock(GraphVisualizationData.class), null));
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
    private static void setUpFloors(BuilderBaseMocks baseMocks, int floorCount) {
        when(baseMocks.visualizationData.getLayerCount()).thenReturn(floorCount);
        IntStream.range(0, floorCount)
                .forEach(i -> when(baseMocks.visualizationData.getNodesOnLayer(i)).thenReturn(new ArrayList<>()));
    }

    /**
     * Sets up the mocks for Nodes. Prepares the mocked data in the {@link GraphVisualizationData visualization
     * data} to prepare simple or empty values for each node. These values are required to instantiate
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

        IdentifiableCollection<Edge> emptyCollection = new ListSequence<>();
        when(baseMocks.network.outgoingEdges(any())).thenReturn(emptyCollection);

        mockNodeFloorMapping(baseMocks.visualizationData, nodes, nodesOnFloor);

        for (int i = 0; i < nodes.size(); ++i) {
            when(baseMocks.visualizationData.getNodeRectangle(any())).thenReturn(Optional.of(new NodeRectangle(1, 2, 3, 4)));
            when(baseMocks.visualizationData.getPosition(any())).thenReturn(new Vector3());
            when(baseMocks.visualizationData.getCapacity(any(Node.class))).thenReturn(0);
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

        when(baseMocks.visualizationData.getTransitTime(any())).thenReturn(3);
        when(baseMocks.visualizationData.getLastFlowTime(any())).thenReturn(0);
        when(baseMocks.visualizationData.getCapacity(any(Edge.class))).thenReturn(3);

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
                baseMocks.visualizationData, baseMocks.visualizationModel).build();
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

        final GraphVisualizationData visualizationData;
        final NetworkVisualizationModel visualizationModel;
        final DirectedGraph network;

        public BuilderBaseMocks() {
            visualizationData = mock(GraphVisualizationData.class);
            visualizationModel = mock(NetworkVisualizationModel.class);
            network = mock(DirectedGraph.class);
            when(visualizationData.getNetwork()).thenReturn(network);
        }

        /**
         * Sets up interactions that happen always during build.
         */
        private void verifyGeneral() {
            verify(visualizationData, atLeastOnce()).getFloorCount();
            verify(visualizationData, atLeastOnce()).getNetwork();
        }

        /**
         * Verifies ase mock interactions for floors. Not necessary if no floors are mocked.
         *
         * @param floorCount the number of floors, 0 or greater
         */
        private void verifyFloors(int floorCount) {
            for (int i = 0; i < floorCount; ++i) {
                verify(visualizationData, atLeastOnce()).getNodesOnFloor(i);
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
            verify(visualizationData, atLeastOnce()).getNodeRectangle(any());
            verify(visualizationData, atLeastOnce()).getPosition(any());
            verify(visualizationData, atLeastOnce()).getCapacity(any(Node.class));
            verify(visualizationData, atLeastOnce()).getFloor(any());

            for (Node node : nodes) {
                if (node == superSink) {
                    continue;
                }
                // This is the only verification for the class under test
                verify(network, atLeastOnce()).outgoingEdges(node);

                verify(visualizationData, atLeastOnce()).isSink(node);
                verify(visualizationData, atLeastOnce()).isSource(node);
                verify(visualizationData, atLeastOnce()).isDeletedSource(node);
            }

            verify(visualizationModel, atLeastOnce()).nodeProgress();
        }

        /**
         * Verifies base mock interactions for edges. Verifies interactions in the created {@link GLEdgeControl edge}
         * instances.
         */
        private void verifyEdges() {
            // Verifications in the created edge instances
            verify(visualizationData, atLeastOnce()).getMaximumFlowValue();
            verify(visualizationData, atLeastOnce()).getLastFlowTime(any());
            verify(visualizationData, atLeastOnce()).getTransitTime(any());
            verify(visualizationData, atLeastOnce()).getCapacity(any(Edge.class));
        }

        void verifyNoMoreInteractions() {
            Mockito.verifyNoMoreInteractions(visualizationData);
            Mockito.verifyNoMoreInteractions(visualizationModel);
            Mockito.verifyNoMoreInteractions(network);
        }

    }

}
