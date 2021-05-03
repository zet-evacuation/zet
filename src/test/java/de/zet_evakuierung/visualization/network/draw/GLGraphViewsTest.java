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
package de.zet_evakuierung.visualization.network.draw;

import static de.zet_evakuierung.visualization.ModelContainerTestUtils.MockHierarchyBuilder.hierarchyMocks;
import static de.zet_evakuierung.visualization.ModelContainerTestUtils.mockNodeFloorMapping;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.zetool.opengl.framework.util.GLContextAwareThread.createWithGLContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.zet_evakuierung.visualization.ModelContainerTestUtils;
import de.zet_evakuierung.visualization.network.GraphVisualizationData;
import de.zet_evakuierung.visualization.network.GraphVisualizationProperties;
import de.zet_evakuierung.visualization.network.model.GLFlowEdgeModel;
import de.zet_evakuierung.visualization.network.model.GLGraphFloorModel;
import de.zet_evakuierung.visualization.network.model.GLNodeModel;
import de.zet_evakuierung.visualization.network.model.GraphVisualizationModelContainer;
import de.zet_evakuierung.visualization.network.model.NetworkVisualizationModel;
import ds.GraphVisualizationResults;
import org.junit.Test;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.collection.ListSequence;
import org.zetool.container.mapping.IdentifiableCloneable;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLGraphViewsTest {

    @Test
    public void testEmpty() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        setUpFloors(baseMocks, 0);

        GLGraphViews result = baseMocks.createResult();

        assertThat(result.getView(), is(not(nullValue())));
        assertCounts(result.getView(), 0, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Tests creation of an empty floor.
     */
    @Test
    public void testFactoryFloors() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        setUpFloors(baseMocks, 1);

        GLGraphViews result = createWithGLContext(baseMocks::createResult);

        assertCounts(result.getView(), 1, List.of(0), Collections.emptyList());
    }

    /**
     * Tests creation of nodes in a floor.
     */
    @Test
    public void testFactoryNodes() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        List<GLGraphFloorModel> mockFloors = setUpFloors(baseMocks, 1);
        List<Node> mockNodes = setUpNodes(baseMocks, mockFloors, 2);
        setUpEdges(baseMocks, mockNodes, 0, 0);

        GLGraphViews result = createWithGLContext(baseMocks::createResult);

        assertCounts(result.getView(), 1, List.of(2), List.of(0, 0));
    }

    /**
     * Tests that no node view is created for the supersink.
     */
    @Test
    public void superSinkExcluded() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        // Set up one floor with three nodes, one of which is the supersink; no edges
        int floorCount = 1;
        int superSink = 1;
        int nodeCount = 3;
        List<GLGraphFloorModel> mockFloors = setUpFloors(baseMocks, floorCount);
        List<Node> mockNodes = setUpNodes(baseMocks, mockFloors, nodeCount);
        setUpEdges(baseMocks, mockNodes, 0, 0, 0);

        when(baseMocks.visualizationData.getSupersink()).thenReturn(mockNodes.get(superSink));

        GLGraphViews result = createWithGLContext(baseMocks::createResult);

        assertCounts(result.getView(), floorCount, List.of(nodeCount - 1), List.of(0, 0));

        List<Integer> nodeIds = StreamSupport.stream(Spliterators
                .spliteratorUnknownSize(result.nodeViews().iterator(), Spliterator.ORDERED), false)
                .map(GLNode::getModel)
                .map(GLNodeModel::getNumber)
                .collect(toList());
        assertThat(nodeIds, containsInAnyOrder(0, 2));
    }

    /**
     * Tests creation of edges adjacent to nodes.
     */
    public void testFactoryEdges() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        List<GLGraphFloorModel> mockFloors = setUpFloors(baseMocks, 1);
        List<Node> mockNodes = setUpNodes(baseMocks, mockFloors, 2);
        setUpEdges(baseMocks, mockNodes, 0, 0);

        GLGraphViews result = createWithGLContext(baseMocks::createResult);

        assertCounts(result.getView(), 1, List.of(2), List.of(0, 0));
    }

    @Test
    public void nodesNotConnectedToSupersink() {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        // Set up a single floor with three nodes connected by 2 edges
        int superSink = 1;
        int floorCount = 1;
        List<GLGraphFloorModel> mockFloors = setUpFloors(baseMocks, floorCount);
        int nodeCount = 3;
        List<Node> nodes = setUpNodes(baseMocks, mockFloors, nodeCount);
        when(baseMocks.visualizationData.getSupersink()).thenReturn(nodes.get(superSink));
        // Connect nodes as circle 0 to supersink (1), supersink (1) to 2, 2 to 0. The last edge remains
        List<Edge> edges = setUpEdges(baseMocks, nodes, 1, 1, 1);
        when(edges.get(0).start()).thenReturn(nodes.get(0));
        when(edges.get(0).end()).thenReturn(nodes.get(1));
        when(edges.get(1).start()).thenReturn(nodes.get(1));
        when(edges.get(1).end()).thenReturn(nodes.get(2));
        when(edges.get(2).start()).thenReturn(nodes.get(2));
        when(edges.get(2).end()).thenReturn(nodes.get(0));

        when(baseMocks.visualizationModel.superSinkID()).thenReturn(superSink);

        GLGraphViews result = createWithGLContext(baseMocks::createResult);

        // We have only one edge on two nodes:
        // (0,1) is removed because connected to supersink
        // (1,2) removed because 1 is supersink
        assertCounts(result.getView(), floorCount, List.of(nodeCount - 1), List.of(0, 1));
    }

    @Test
    public void edgesBetweenFloorsNotIncluded() {
        Function<FactoryBaseMocks, GLGraphViews> fixtureFactory
                = baseMocks -> createWithGLContext(() -> baseMocks.createResult(false));

        testInterFloorEdgeCreation(fixtureFactory, List.of(1, 0, 1, 0));
    }

    @Test
    public void edgesBetweenFloorsIncludedWhenParameterSet() {
        Function<FactoryBaseMocks, GLGraphViews> fixtureFactory
                = baseMocks -> createWithGLContext(() -> baseMocks.createResult(true));

        testInterFloorEdgeCreation(fixtureFactory, List.of(2, 0, 1, 1));
    }

    /**
     * Creates a mocked input and tests the created edges. The instance has two floors with two nodes each and to edges
     * connecting nodes on the same floor and two edges connecting nodes on different floors.
     * <p>
     * The first node contains nodes 0 and 1. The second floor contains nodes 2 and 3.
     * <ol>
     * <li>edge 0: (0,1) on floor 0</li>
     * <li>edge 1: (0,2) between floors 0 and 1</li>
     * <li>edge 2: (2, 3) on floor 1</li>
     * <li>edge 3: (3, 0) betwen floors 1 and 0</li>
     * </ol>
     * </p>
     *
     * @param fixtureFactory creates a container fixture from {@link FactoryBaseMocks} with mocks
     * @param exptectedEdgeIndices the indices of the mock edges for which creation of model edges is expected
     * @return the created fixture to allow further assertions of the result
     */
    private GLGraphViews testInterFloorEdgeCreation(
            Function<FactoryBaseMocks, GLGraphViews> fixtureFactory,
            List<Integer> exptectedEdgeIndices) {
        FactoryBaseMocks baseMocks = new FactoryBaseMocks();

        // Set up a single floor with three nodes connected by 2 edges
        int floorCount = 2;
        List<GLGraphFloorModel> floorMocks = setUpFloors(baseMocks, floorCount);
        List<Node> nodes = setUpNodes(baseMocks, floorMocks, 2, 2);

        // Connect the nodes on a floor and the two nodes on different floors (different direction)
        List<Edge> edges = setUpEdges(baseMocks, nodes, 2, 0, 1, 1);
        when(edges.get(0).start()).thenReturn(nodes.get(0));
        when(edges.get(0).end()).thenReturn(nodes.get(1));
        when(edges.get(1).start()).thenReturn(nodes.get(0));
        when(edges.get(1).end()).thenReturn(nodes.get(2));
        when(edges.get(2).start()).thenReturn(nodes.get(2));
        when(edges.get(2).end()).thenReturn(nodes.get(3));
        when(edges.get(3).start()).thenReturn(nodes.get(3));
        when(edges.get(3).end()).thenReturn(nodes.get(0));

        when(baseMocks.visualizationModel.superSinkID()).thenReturn(Integer.MIN_VALUE);

        GLGraphViews result = fixtureFactory.apply(baseMocks);

        assertCounts(result.getView(), floorCount, List.of(2, 2), exptectedEdgeIndices);

        return result;
    }

    /**
     * Sets up the mocks for floors models.
     * <p>
     * Creates mocked instances and sets up the graph model to return as many as required.</p>
     * <p>
     * Initializes the mocks also to be prepared for floor view object creation.</p>
     *
     * @param baseMocks the base mocks that are set up
     * @param floorCount the number of floor mocks to be created
     */
    private List<GLGraphFloorModel> setUpFloors(FactoryBaseMocks baseMocks, int floorCount) {
        when(baseMocks.graphModel.getFloorCount()).thenReturn(floorCount);
        List<GLGraphFloorModel> floorMocks = new ArrayList<>(floorCount);
        for (int i = 0; i < floorCount; ++i) {
            GLGraphFloorModel mockFloor = mock(GLGraphFloorModel.class);
            floorMocks.add(mockFloor);
            when(baseMocks.graphModel.getFloorModel(i)).thenReturn(mockFloor);
        }

        return floorMocks;
    }

    /**
     * Sets up the mocks for node models. Creates {@link Node} instances and {@link GLNodeModel} mocks corresponding to
     * each other.
     * <p>
     * Sets up the {@link GraphVisualizationResults graph results} mock to return the respective list of
     * {@link Node nodes} for each floor and the model to retrieve the {@link GLNodeModel} for its corresponding
     * node.</p>
     *
     * @param baseMocks the base mocks that are set up
     * @param floorMocks list of floor mocks that have been mocked already
     * @param nodesOnFloor number of nodes for each floor; must have the same size as {@code floorMocks}
     * @return a list of all created nodes, ordered by floor
     */
    private static List<Node> setUpNodes(FactoryBaseMocks baseMocks, List<GLGraphFloorModel> floorMocks,
            int... nodesOnFloor) {

        assertThat(floorMocks.size(), is(equalTo(nodesOnFloor.length)));

        List<Node> nodes = IntStream.range(0, Arrays.stream(nodesOnFloor).sum()).mapToObj(Node::new).collect(toList());

        mockNodeFloorMapping(baseMocks.visualizationData, nodes, nodesOnFloor);

        // Set up a hacked injection of the real objects in the mock factory
        AtomicInteger count = new AtomicInteger();
        BiFunction<Class<? extends Node>, Integer, Iterable<Node>> mockListFactory
                = (Class<? extends Node> unused, Integer nodeOnFloor) -> {
                    List<Node> result = nodes.subList(count.get(), count.get() + nodeOnFloor);
                    count.addAndGet(nodeOnFloor);
                    return result;
                };

        Function<Integer, Iterable<Node>> nodeMockSupplier
                = i -> baseMocks.visualizationData.getNodesOnLayer(i);
        Function<Node, GLNodeModel> internalMockFunction
                = nodeMock -> baseMocks.graphModel.getNodeModel(nodeMock);
        hierarchyMocks(Node.class, GLNodeModel.class)
                .forParentCount(floorMocks.size())
                .withModelMockAccessor(nodeMockSupplier)
                .withMockListFactory(mockListFactory)
                .withViewModelMockAccessor(internalMockFunction)
                .withChildrenInParent(nodesOnFloor)
                .build();

        // Set up the node model instances to know their id
        for (int i = 0; i < nodes.size(); ++i) {
            GLNodeModel nodeModel = internalMockFunction.apply(nodes.get(i));
            when(nodeModel.getNumber()).thenReturn(i);
        }

        return nodes;
    }

    /**
     * Sets up outgoing edges for nodes.
     *
     * @param baseMocks the base mocks that are set up
     * @param nodeMock a list of nodes for which outgoing edges are defined
     * @param incidentEdges the number of edges to be created for each node
     */
    private List<Edge> setUpEdges(FactoryBaseMocks baseMocks, List<Node> nodeMock, int... incidentEdges) {
        assertThat("mock initialization sanity check", nodeMock, hasSize(incidentEdges.length));

        Function<Integer, Iterable<Edge>> edgeMockSupplier
                = i -> baseMocks.network.outgoingEdges(nodeMock.get(i));
        Function<Edge, GLFlowEdgeModel> internalMockFunction
                = edgeMock -> baseMocks.graphModel.getEdgeModel(edgeMock);

        return hierarchyMocks(Edge.class, GLFlowEdgeModel.class)
                .forParentCount(nodeMock.size())
                .withModelMockAccessor(edgeMockSupplier)
                .withMockListFactory(GLGraphViewsTest::createMockIdentifiableCollection)
                .withViewModelMockAccessor(internalMockFunction)
                .withChildrenInParent(incidentEdges)
                .build();
    }

    /**
     * Creates a number of {@link IdentifiableCloneable identifable} objects like {@link Node} or {@link Edge} in an
     * {@link IdentifiableCollection}. To be uses as
     * {@link ModelContainerTestUtils.MockHierarchyBuilder mockListFactory}.
     *
     * @param <T> the type of mocks that is created
     * @param mockType class type of the mocks
     * @param count the number of mocks that is created
     * @return the list of created mocks
     */
    private static <T extends IdentifiableCloneable> IdentifiableCollection<T> createMockIdentifiableCollection(
            Class<? extends T> mockType, int count) {
        return new ListSequence<>(Stream.generate(() -> mock(mockType)).limit(count).collect(toList()));
    }

    /**
     * Asserts the numbers of {@code floor}s, {@code node}s, and {@code edge}s in a created model object.
     *
     * @param resultView that is asserted
     * @param floorCount the expected number of floors
     * @param nodeCounts the expected number of nodes per floor
     * @param edgeCounts the expected number of edges outgoing for each node
     */
    private static void assertCounts(GLFlowGraph resultView, int floorCount, List<Integer> nodeCounts,
            List<Integer> edgeCounts) {
        assertThat(resultView, is(iterableWithSize(floorCount)));
        int currentFloor = -1;
        int currentNode = -1;
        for (GLGraphFloor floor : resultView) {
            currentFloor++;
            assertThat(floor, is(iterableWithSize(nodeCounts.get(currentFloor))));
            for (GLNode node : floor) {
                currentNode++;
                assertThat(node, is(iterableWithSize(edgeCounts.get(currentNode))));
            }

        }
    }

    /**
     * Structure containing two important mocks to initialize the factory with.
     */
    private static class FactoryBaseMocks {

        final NetworkVisualizationModel visualizationModel;
        final GraphVisualizationModelContainer graphModel;
        final GraphVisualizationData visualizationData;
        final DirectedGraph network;

        public FactoryBaseMocks() {
            visualizationModel = mock(NetworkVisualizationModel.class);
            graphModel = mock(GraphVisualizationModelContainer.class);
            visualizationData = mock(GraphVisualizationData.class);
            network = mock(DirectedGraph.class);
            when(visualizationData.getNetwork()).thenReturn(network);
        }

        /**
         * Creates the result object using the factory with the mocks. Must only be called once all mocks have been set
         * up.
         *
         * @return the created instance
         */
        private GLGraphViews createResult() {
            return createResult(false);
        }

        /**
         * Creates the result object using the factory with the mocks. Must only be called once all mocks have been set
         * up.
         *
         * @param interFloorEdges determines wether or not view instances between floors should be created
         * @return the created instance
         */
        private GLGraphViews createResult(boolean interFloorEdges) {
            GraphVisualizationProperties properties = new GraphVisualizationProperties() {
            };
            return GLGraphViews.createInstance(visualizationModel, properties, visualizationData, graphModel,
                    interFloorEdges);
        }
    }
}
