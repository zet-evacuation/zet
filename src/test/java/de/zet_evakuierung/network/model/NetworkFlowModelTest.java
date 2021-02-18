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
package de.zet_evakuierung.network.model;

import static java.lang.Double.doubleToLongBits;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import de.zet_evakuierung.network.model.NetworkFlowModel.AssignmentBuilder;
import de.zet_evakuierung.network.model.NetworkFlowModel.BasicBuilder;
import org.zetool.common.util.Level;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class NetworkFlowModelTest {

    /**
     * Empty raster instance used to initialize {@link BasicBuilder}.
     */
    private static final ZToGraphRasterContainer EMPTY_RASTER = new ZToGraphRasterContainer();

    /**
     * Empty model instance be used by copy constructors.
     */
    private static final NetworkFlowModel EMPTY_MODEL = new BasicBuilder(EMPTY_RASTER).build();

    /**
     * Basic instance containing source, sink, and intermediate node to be used as input for {@link AssignmentBuilder}.
     */
    private static final NetworkFlowModel SIMPLE_BASIC_MODEL;
    /**
     * The source node of the {@link #SIMPLE_BASIC_MODEL}.
     */
    private static final Node SOURCE;
    /**
     * The intermediate node of the {@link #SIMPLE_BASIC_MODEL}.
     */
    private static final Node INTERMEDIATE_NODE;
    /**
     * The sink node of the {@link #SIMPLE_BASIC_MODEL}.
     */
    private static final Node SINK;

    static {
        BasicBuilder builder = new BasicBuilder(EMPTY_RASTER);
        SOURCE = builder.newNode();
        INTERMEDIATE_NODE = builder.newNode();
        SINK = builder.newNode();
        Edge e1 = builder.newEdge(SOURCE, INTERMEDIATE_NODE);
        builder.getZToGraphMapping().setEdgeLevel(e1, Level.Equal);
        Edge e2 = builder.newEdge(INTERMEDIATE_NODE, SINK);
        builder.getZToGraphMapping().setEdgeLevel(e2, Level.Equal);
        builder.addSink(SINK);
        builder.addSource(SOURCE);
        SIMPLE_BASIC_MODEL = builder.build();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInitialization() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        assertThat(fixture.supersink.id(), is(equalTo(0)));
        assertThat(fixture.edges(), is(emptyIterable()));
    }

    @Test
    public void numberOfNodesUpdated() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        assertThat(fixture.numberOfNodes(), is(equalTo(1)));

        fixture.newNode();

        assertThat(fixture.numberOfNodes(), is(equalTo(2)));
    }

    @Test
    public void numberOfEdgesUpdated() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        assertThat(fixture.numberOfEdges(), is(equalTo(0)));

        Node a = fixture.newNode();
        Node b = fixture.newNode();
        fixture.newEdge(a, b);

        assertThat(fixture.numberOfEdges(), is(equalTo(1)));
    }

    @Test
    public void numberOfSinksUpdated() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        assertThat(fixture.numberOfSinks(), is(equalTo(0)));
        assertThat(fixture.numberOfEdges(), is(equalTo(0)));

        Node t = fixture.newNode();

        fixture.addSink(t);

        assertThat(fixture.numberOfSinks(), is(equalTo(1)));
        // after refactoring
//        assertThat(fixture.numberOfEdges(), is(equalTo(0)));
    }

    @Test
    public void edgeByIndexReturned() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Edge edge = fixture.newEdge(fixture.newNode(), fixture.newNode());

        assertThat(fixture.getEdge(0), is(sameInstance(edge)));
    }

    @Test
    public void edgeByNodesReturned() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Node a = fixture.newNode();
        Node b = fixture.newNode();
        Edge edge = fixture.newEdge(a, b);

        assertThat(fixture.getEdge(a, b), is(sameInstance(edge)));
    }

    @Test
    public void edgeCapacitySet() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Edge edge = fixture.newEdge(fixture.newNode(), fixture.newNode());
        fixture.setEdgeCapacity(edge, 3);

        assertThat(fixture.getEdgeCapacity(edge), is(equalTo(3)));
    }

    @Test
    public void edgeTransitTimeSet() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Edge edge = fixture.newEdge(fixture.newNode(), fixture.newNode());
        fixture.setExactTransitTime(edge, Math.PI);

        assertThat(fixture.getExactTransitTime(edge), is(closeTo(3.1416, 0.001)));
    }

    @Test
    public void testEmptyBuild() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        NetworkFlowModel model = fixture.build();
        assertThat(model.getSupersink().id(), is(equalTo(0)));
        assertThat(model, contains(new Node(0)));
        assertThat(model.edges(), is(emptyIterable()));
        assertThat(model.getNodeCapacity(model.getSupersink()), is(equalTo(Integer.MAX_VALUE)));
    }

    @Test
    public void newNodes() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        fixture.newNode();

        NetworkFlowModel model = fixture.build();
        assertThat(model.getSupersink().id(), is(equalTo(0)));
        assertThat(model, contains(new Node(0), new Node(1)));
        assertThat(model.edges(), is(emptyIterable()));
    }

    @Test
    public void newNodeFailsAfterBuildWithRaster() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);
        fixture.build();

        thrown.expect(IllegalStateException.class);
        fixture.newNode();
    }

    @Test
    public void newNodeFailsAfterBuildWithModel() {
        BasicBuilder fixture = BasicBuilder.forExtension(EMPTY_MODEL);
        fixture.build();

        thrown.expect(IllegalStateException.class);
        fixture.newNode();
    }

    @Test
    public void increaseNodeCapacity() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Node newNode = fixture.newNode();
        fixture.increaseNodeCapacity(newNode, 1);

        NetworkFlowModel model = fixture.build();
        assertThat(model.getNodeCapacity(newNode), is(equalTo(1)));
    }

    @Test
    public void increaseNodeCapacityOverflowThrows() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Node newNode = fixture.newNode();
        fixture.setNodeCapacity(newNode, Integer.MAX_VALUE);

        thrown.expect(ArithmeticException.class);

        fixture.increaseNodeCapacity(newNode, 1);
    }

    @Test
    public void newEdgesHaveDefaultValues() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Node a = fixture.newNode();
        Node b = fixture.newNode();
        Edge edge = fixture.newEdge(a, b);

        fixture.getZToGraphMapping().setEdgeLevel(edge, Level.Equal);

        NetworkFlowModel model = fixture.build();
        assertThat(model.getSupersink().id(), is(equalTo(0)));
        assertThat(model, contains(new Node(0), new Node(1), new Node(2)));
        assertThat(model.edges(), contains(new Edge(0, a, b), new Edge(1, b, a)));
        assertThat(model.getEdge(0), is(sameInstance(edge)));
        assertThat(model.getEdgeCapacity(edge), is(equalTo(1)));
        assertThat(model.getTransitTime(edge), is(equalTo(0)));
    }

    @Test
    public void transitTimeRounding() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Node a = fixture.newNode();
        Node b = fixture.newNode();
        Edge roundDown = fixture.newEdge(a, b);
        Edge roundUp = fixture.newEdge(b, a);

        double roundDownTime = 1.3;
        double roundUpTime = 1.9;
        fixture.setExactTransitTime(roundDown, roundDownTime);
        fixture.setExactTransitTime(roundUp, roundUpTime);

        fixture.getZToGraphMapping().setEdgeLevel(roundUp, Level.Equal);
        fixture.getZToGraphMapping().setEdgeLevel(roundDown, Level.Equal);

        NetworkFlowModel model = fixture.build();
        assertThat(model.getTransitTime(roundDown), is(equalTo(1)));
        assertThat(model.getTransitTime(roundUp), is(equalTo(2)));
        assertThat(doubleToLongBits(model.getExactTransitTime(roundDown)), is(equalTo(doubleToLongBits(roundDownTime))));
        assertThat(doubleToLongBits(model.getExactTransitTime(roundUp)), is(equalTo(doubleToLongBits(roundUpTime))));
    }

    @Test
    public void reverseEdges() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Node a = fixture.newNode();
        Node b = fixture.newNode();
        Edge edge = fixture.newEdge(a, b);

        fixture.getZToGraphMapping().setEdgeLevel(edge, Level.Equal);

        NetworkFlowModel model = fixture.build();
        assertThat(model.edges(), contains(new Edge(0, a, b), new Edge(1, b, a)));
        assertThat(model.getEdge(0), is(sameInstance(edge)));
        assertThat(model.getEdgeCapacity(edge), is(equalTo(1)));
        assertThat(model.getTransitTime(edge), is(equalTo(0)));

        Edge reverse = model.getEdge(1);
        assertThat(model.getEdgeCapacity(reverse), is(equalTo(1)));
        assertThat(model.getTransitTime(reverse), is(equalTo(0)));
    }

    @Test
    public void sinksAreConnected() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Node sink = fixture.newNode();
        fixture.addSink(sink);

        NetworkFlowModel model = fixture.build();
        assertThat(model.getSupersink().id(), is(equalTo(0)));
        assertThat(model, contains(new Node(0), new Node(1)));
        assertThat(model.edges(), contains(new Edge(0, sink, model.getSupersink())));

        Edge edge = model.getEdge(0);
        assertThat(model.getEdgeCapacity(edge), is(equalTo(Integer.MAX_VALUE)));
        assertThat(model.getTransitTime(edge), is(equalTo(0)));
    }

    @Test
    public void divide() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Edge edge1 = fixture.newEdge(fixture.newNode(), fixture.newNode());
        Edge edge2 = fixture.newEdge(fixture.newNode(), fixture.newNode());
        fixture.setExactTransitTime(edge1, 10);
        fixture.setExactTransitTime(edge2, 8);

        fixture.divide(edge1, 0.5);
        fixture.divide(edge2, 4);

        assertThat(fixture.getExactTransitTime(edge1), is(closeTo(20, 0.001)));
        assertThat(fixture.getExactTransitTime(edge2), is(closeTo(2, 0.001)));
    }

    @Test
    public void numberOfSinksModel() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        fixture.addSink(fixture.newNode());
        fixture.addSink(fixture.newNode());

        NetworkFlowModel result = fixture.build();

        assertThat(result.numberOfSinks(), is(equalTo(2)));
    }

    @Test
    public void numberOfNodesModel() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        fixture.newNode();
        fixture.newNode();

        NetworkFlowModel result = fixture.build();

        assertThat(result.numberOfNodes(), is(equalTo(3)));
    }

    @Test
    public void numberOfEdgesModel() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Node a = fixture.newNode();
        Node b = fixture.newNode();
        Edge edge = fixture.newEdge(a, b);
        fixture.getZToGraphMapping().setEdgeLevel(edge, Level.Equal);
        fixture.addSink(a);

        NetworkFlowModel result = fixture.build();

        assertThat(result.numberOfEdges(), is(equalTo(3)));
    }

    @Test
    public void edgeByNodesModelReturned() {
        BasicBuilder fixture = new BasicBuilder(EMPTY_RASTER);

        Node a = fixture.newNode();
        Node b = fixture.newNode();
        Edge edge = fixture.newEdge(a, b);
        fixture.getZToGraphMapping().setEdgeLevel(edge, Level.Equal);

        NetworkFlowModel model = fixture.build();

        assertThat(model.getEdge(a, b), is(sameInstance(edge)));
    }

    @Test
    public void testCopy() {
        BasicBuilder fixture = BasicBuilder.forExtension(SIMPLE_BASIC_MODEL);

        NetworkFlowModel model = fixture.build();

        assertThat(fixture.supersink.id(), is(equalTo(0)));
        assertThat(model.graph().nodeCount(), is(equalTo(4)));
        assertThat(model.graph().edgeCount(), is(equalTo(5)));
    }

    @Test
    public void assignmentRemoved() {
        // Initialize a model with assignment to start with
        AssignmentBuilder builder = new AssignmentBuilder(SIMPLE_BASIC_MODEL);
        builder.setNodeAssignment(SOURCE, 3);
        NetworkFlowModel modelWithAssignment = builder.build();

        // A virtual node and edge is created
        assertThat(modelWithAssignment.graph().nodeCount(), is(equalTo(5)));
        assertThat(modelWithAssignment.graph().edgeCount(), is(equalTo(6)));

        BasicBuilder fixture = BasicBuilder.forExtension(modelWithAssignment);

        // The resulting model does not contain the virtual node and edge
        NetworkFlowModel modelWithoutAssignment = fixture.build();
        assertThat(modelWithoutAssignment.graph().nodeCount(), is(equalTo(4)));
        assertThat(modelWithoutAssignment.graph().edgeCount(), is(equalTo(5)));
    }

    @Test
    public void edgesCleared() {
        NetworkFlowModel model = BasicBuilder.fromNodes(SIMPLE_BASIC_MODEL).build();
        assertThat(model.getSupersink().id(), is(equalTo(0)));
        assertThat(model.graph().nodeCount(), is(equalTo(4)));
        assertThat(model.graph().edgeCount(), is(equalTo(0)));
    }

    /**
     * When starting with clean builder without edges, no failure in calculating edge size must happen.
     */
    @Test
    public void regressionNoEdgesWithSinks() {
        BasicBuilder.fromNodes(SIMPLE_BASIC_MODEL).build();
    }

    @Test
    public void assignmentEmptyForModelNodes() {
        IdentifiableIntegerMapping<Node> assignment = SIMPLE_BASIC_MODEL.currentAssignment();

        assertThat(assignment.get(SIMPLE_BASIC_MODEL.getSupersink()), is(equalTo(0)));
        assertThat(assignment.get(SOURCE), is(equalTo(0)));
        assertThat(assignment.get(INTERMEDIATE_NODE), is(equalTo(0)));
        assertThat(assignment.get(SINK), is(equalTo(0)));
    }

    @Test
    public void assignmentSet() {
        AssignmentBuilder fixture = new AssignmentBuilder(SIMPLE_BASIC_MODEL);

        fixture.setNodeAssignment(SOURCE, 3);

        NetworkFlowModel model = fixture.build();

        assertSingleAssignment(model, 3, 0);
    }

    @Test
    public void assignmentSetMultiple() {
        AssignmentBuilder fixture = new AssignmentBuilder(SIMPLE_BASIC_MODEL);

        fixture.setNodeAssignment(SOURCE, 3);
        fixture.setNodeAssignment(SOURCE, 2);

        NetworkFlowModel model = fixture.build();

        assertSingleAssignment(model, 2, 0);
    }

    @Test
    public void assignmentIncreaseOnly() {
        AssignmentBuilder fixture = new AssignmentBuilder(SIMPLE_BASIC_MODEL);

        fixture.increaseNodeAssignment(SOURCE);

        NetworkFlowModel model = fixture.build();

        assertSingleAssignment(model, 1, 0);
    }

    @Test
    public void assignmentSetAndIncrease() {
        AssignmentBuilder fixture = new AssignmentBuilder(SIMPLE_BASIC_MODEL);

        fixture.setNodeAssignment(SOURCE, 2);
        fixture.increaseNodeAssignment(SOURCE);

        NetworkFlowModel model = fixture.build();

        assertSingleAssignment(model, 3, 0);
    }

    @Test
    public void assignmentWithTransitTime() {
        AssignmentBuilder fixture = new AssignmentBuilder(SIMPLE_BASIC_MODEL);

        fixture.increaseNodeAssignment(SOURCE, 3.5);

        NetworkFlowModel model = fixture.build();

        assertSingleAssignment(model, 1, 4);
    }

    /**
     * Asserts that the simple model with a single assignment is valid. The {@link NetworkFlowModel model} is expected
     * to contain five nodes and six edges, e.g. it consists of the {@link #SIMPLE_BASIC_MODEL simple model} and a
     * single assignment node.
     *
     * @param model the model instance
     * @param expectedAssignment the expected value
     * @param expectedTransitTime the expected rounded transit time
     */
    private void assertSingleAssignment(NetworkFlowModel model, int expectedAssignment, int expectedTransitTime) {
        // Check virtual source node has been added
        assertThat(model.graph().nodeCount(), is(equalTo(5)));
        assertThat(model.graph().edgeCount(), is(equalTo(6)));

        // Check assignment
        Node actualSource = model.getNode(4);
        assertThat(model.currentAssignment().get(actualSource), is(equalTo(expectedAssignment)));

        // Check edge properties
        Edge sourceEdge = model.getEdge(5);
        assertThat(model.getEdgeCapacity(sourceEdge), is(equalTo(Integer.MAX_VALUE)));
        assertThat(model.getTransitTime(sourceEdge), is(equalTo(expectedTransitTime)));

        // Assert other parts of the network remain unchanged
        assertSimpleModel();
        assertSimpleModelAssignment(model.currentAssignment());
    }

    /**
     * Asserts the properties of the simple model.
     */
    private void assertSimpleModel() {
        assertThat(SIMPLE_BASIC_MODEL.graph().nodeCount(), is(equalTo(4)));
        assertThat(SIMPLE_BASIC_MODEL.graph().edgeCount(), is(equalTo(5)));
    }

    /**
     * Asserts that no actual supply is defined for the model nodes.
     *
     * @param assignment the final assignment
     */
    private void assertSimpleModelAssignment(IdentifiableIntegerMapping<Node> assignment) {
//        assertThat(assignment.get(SIMPLE_MODEL.getSupersink()), is(equalTo(0)));
        assertThat(assignment.get(SOURCE), is(equalTo(0)));
        assertThat(assignment.get(INTERMEDIATE_NODE), is(equalTo(0)));
        assertThat(assignment.get(SINK), is(equalTo(0)));
    }

    @Test
    public void assignmentClearedForSources() {
        AssignmentBuilder firstBuilder = new AssignmentBuilder(SIMPLE_BASIC_MODEL);

        firstBuilder.setNodeAssignment(SOURCE, 2);

        NetworkFlowModel modelWithAssignment = firstBuilder.build();

        AssignmentBuilder secondBuilder = new AssignmentBuilder(modelWithAssignment);

        NetworkFlowModel modelWithoutAssignment = secondBuilder.build();

        assertThat(modelWithoutAssignment.graph().nodeCount(), is(equalTo(4)));
        assertThat(modelWithoutAssignment.graph().edgeCount(), is(equalTo(5)));

//        assertSingleAssignment(modelWithoutAssignment, 0, 0);
    }
}
