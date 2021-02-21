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

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphMapping;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;

import ds.graph.NodeRectangle;
import org.zetool.coga.netflow.util.GraphInstanceChecker;
import org.zetool.common.debug.Debug;
import org.zetool.container.mapping.IdentifiableConstantMapping;
import org.zetool.container.mapping.IdentifiableDoubleMapping;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.DynamicNetwork;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;

/**
 *
 */
@XStreamAlias("networkFlowModel")
public class NetworkFlowModel implements Iterable<Node> {

    /**
     * The network backing up the network flow model.
     */
    private final DynamicNetwork network;
    /**
     * The special super sink node within the network.
     */
    private final Node supersink;
    private final List<Node> modelSinks;
    /**
     * The node capacities, defined for every node.
     */
    private final IdentifiableIntegerMapping<Node> nodeCapacities;
    /**
     * The edge capacities, defined for every edge.
     */
    private final IdentifiableIntegerMapping<Edge> edgeCapacities;
    /**
     * The transit times, defined for every edge.
     */
    private final IdentifiableIntegerMapping<Edge> transitTimes;
    private final IdentifiableDoubleMapping<Edge> exactTransitTimes;
    /**
     * The assignment of supplies a network model.
     */
    private final IdentifiableIntegerMapping<Node> currentAssignment;
    private final List<Node> sources;
    /**
     * Sources defined due to an assignment.
     */
    private final List<Node> virtualSources;
    private final ZToGraphMapping mapping;

    NetworkFlowModel(AbstractNetworkFlowModelBuilder builder) {
        this.network = Objects.requireNonNull(builder.network);
        this.supersink = Objects.requireNonNull(builder.supersink); // can probably be removed and replaced with getNode(0)
        this.sources = Objects.requireNonNull(builder.sources);
        this.virtualSources = Objects.requireNonNull(builder.getVirtualSources());
        this.modelSinks = Objects.requireNonNull(builder.sinks);
        this.nodeCapacities = Objects.requireNonNull(builder.nodeCapacities);
        this.edgeCapacities = Objects.requireNonNull(builder.edgeCapacities);
        this.transitTimes = Objects.requireNonNull(builder.transitTimes);
        this.exactTransitTimes = Objects.requireNonNull(builder.exactTransitTimes);
        this.currentAssignment = Objects.requireNonNull(builder.getAssignment());
        this.mapping = Objects.requireNonNull(builder.getZToGraphMapping());
    }

    public NetworkFlowModel(ZToGraphRasterContainer raster) {
        this.network = new DynamicNetwork();
        this.edgeCapacities = new IdentifiableIntegerMapping<>(0);
        this.nodeCapacities = new IdentifiableIntegerMapping<>(0);
        this.transitTimes = new IdentifiableIntegerMapping<>(0);
        this.exactTransitTimes = new IdentifiableDoubleMapping<>(0);
        this.currentAssignment = new IdentifiableIntegerMapping<>(0);
        this.sources = new LinkedList<>();
        this.modelSinks = new LinkedList<>();
        this.virtualSources = Collections.emptyList();
        supersink = new Node(0);
        network.setNode(supersink);
        mapping = new ZToGraphMapping(raster); // start with an empty mapping in case we have an empty model
        mapping.setNodeRectangle(supersink, new NodeRectangle(0, 0, 0, 0));
        mapping.setFloorForNode(supersink, 0);
    }

    // TODO: delete the constructor
    public NetworkFlowModel(NetworkFlowModel model) {
        // a constructor that copies the nodes. TODO: as static method
        this(model.mapping.getRaster());
        network.setNodes(model.network.nodes());
//        this.supersink = model.supersink;
//        sources = model.sources;
//        nodeCapacities = model.nodeCapacities;
//        currentAssignment = model.currentAssignment;
    }

    @Override
    public Iterator<Node> iterator() {
        return network.iterator();
    }

    /**
     * Returns the number of nodes of the graph model. Includes the super sink.
     *
     * @return
     */
    public int numberOfNodes() {
        return network.nodeCount();
    }

    /**
     * Returns the number of edges in the graph model. Includes the edges connecting the super sink.
     *
     * @return
     */
    public int numberOfEdges() {
        return network.edgeCount();
    }

    /**
     * Delegated method to the internal graph model.
     *
     * @param i
     * @return
     */
    public Node getNode(int i) {
        return network.getNode(i);
    }

    /**
     * Delegated method to the internal graph model.
     *
     * @param edge
     * @return
     */
    public boolean contains(Edge edge) {
        return network.contains(edge);
    }

    /**
     * Delegated method to the internal graph model.
     *
     * @param i
     * @return
     */
    public Edge getEdge(int i) {
        return network.getEdge(i);
    }

    /**
     * Delegated method to the internal graph model.
     *
     * @param lastNode
     * @param node
     * @return
     */
    public Edge getEdge(Node lastNode, Node node) {
        return network.getEdge(lastNode, node);
    }

    /**
     * Delegated method to the internal graph model.
     *
     * @return
     */
    public Iterable<Edge> edges() {
        return network.edges();
    }

    /**
     * Returns the graph model.
     *
     * @return
     */
    public DirectedGraph graph() {
        // TODO: remove this and use delegated methods, or remove delegated methods. Probably use interface of graph for this class?
        return network;
    }

    /**
     * Returns the model sources. This is the set of nodes that represent sources with respect to the evacuation model.
     * They are not necessarily equal or even overlap with the nodes with positive supply.
     * <p>
     * Sources are associated with assignment area within the evacuation model and indicate starting positions of
     * evacuees. The actual modelling of the network can require that nodes with positive supplies are connected with
     * sources. These nodes are returned by {@link #getVirtualSources() }
     *
     * @see #currentAssignment() .
     * @return a linked list containing the sources.
     */
    public List<Node> getSources() {
        return Collections.unmodifiableList(sources);
    }

    /**
     * Returns the set of nodes having an actual positive supply. The returned set of nodes form a superset of the model
     * sources.
     *
     * @see #getSources()
     * @return the set of nodes with positive supplies
     */
    public List<Node> getVirtualSources() {
        return Collections.unmodifiableList(virtualSources);
    }

    /**
     * Returns the number of sinks.
     *
     * @return
     */
    public int numberOfSinks() {
        return network.degree(getSupersink());
    }

    /**
     * Returns the sinks defined by the evacuation model. They are not actually sinks in the network model, as there is
     * only one {@link #getSinks() supersink}.
     *
     * @return the sinks as defined by the evacuation model
     */
    public List<Node> getModelSinks() {
        return modelSinks;
    }

    /**
     * Returns a linked list containing the super sink.
     *
     * @return a linked list containing the super sink
     */
    public List<Node> getSinks() {
        LinkedList<Node> sinks = new LinkedList<>();
        sinks.add(supersink);
        return Collections.unmodifiableList(sinks);
    }

    /**
     * Returns the super sink node.
     *
     * @return
     */
    public Node getSupersink() {
        return supersink;
    }

    public IdentifiableIntegerMapping<Node> nodeCapacities() {
        return nodeCapacities;
    }

    /**
     * Returns the capacity of a node. The capacity of a node represents the number of units that can be stored within
     * the node.
     *
     * @param node the node
     * @return
     */
    public int getNodeCapacity(Node node) {
        return nodeCapacities.get(node);
    }

    /**
     * Returns the edge capacities.
     *
     * @return
     */
    public IdentifiableIntegerMapping<Edge> edgeCapacities() {
        // TODO: return unmodifiable edge capacities
        return edgeCapacities;
    }

    /**
     * Returns the capacity of an edge. The capacity of an edge represents the number of units that can enter the unit
     * at every time.
     *
     * @param edge the edge
     * @return
     */
    public int getEdgeCapacity(Edge edge) {
        return edgeCapacities.get(edge);
    }

    /**
     * Returns the transit times.
     *
     * @return
     */
    public IdentifiableIntegerMapping<Edge> transitTimes() {
        // TODO: return unmodifiable transit times
        return transitTimes;
    }

    /**
     * Retuns the exact transit time of an edge before . The transit time is the necessary time required to travel along
     * an edge from beginning to end. The transit time is rounded.
     *
     * @see NetworkFlowModel#getExactTransitTime(org.zetool.graph.Edge)
     * @param edge the edge
     * @return
     */
    public int getTransitTime(Edge edge) {
        return transitTimes.get(edge);
    }

    /**
     * Returns the exact transit time of an edge before rounding. The transit time is the necessary time required to
     * travel along an edge from beginning to end. This time is the defined time within the evacuation model. For usages
     * in network flow algorithms, the rounded transit time is used.
     *
     * @see NetworkFlowModel#getTransitTime(org.zetool.graph.Edge)
     * @param edge the edge
     * @return
     */
    public double getExactTransitTime(Edge edge) {
        return exactTransitTimes.get(edge);
    }

    /**
     * Returns the assignment.
     *
     * @return
     */
    public IdentifiableIntegerMapping<Node> currentAssignment() {
        // TODO return unmodifiable assignmetn
        return currentAssignment;
    }

    public ZToGraphMapping getZToGraphMapping() {
        return mapping;
    }

    // TO be moved into the builder as static
    public EarliestArrivalFlowProblem getEAFP() {
        return getEAFP(0);
    }

    public EarliestArrivalFlowProblem getEAFP(int upperBound) {
        return new EarliestArrivalFlowProblem(edgeCapacities, network.getAsStaticNetwork(), nodeCapacities, supersink, virtualSources, upperBound, transitTimes, currentAssignment);
    }

    /**
     * Internal abstract base class for {@link NetworkFlowModel} builder.
     *
     * @author Jan-Philipp Kappmeier
     */
    private static abstract class AbstractNetworkFlowModelBuilder {

        protected final DynamicNetwork network;
        protected final List<Node> sources;
        protected final List<Node> sinks;
        protected final Node supersink;
        protected final IdentifiableIntegerMapping<Node> nodeCapacities;
        protected final IdentifiableIntegerMapping<Edge> edgeCapacities;
        protected final IdentifiableDoubleMapping<Edge> exactTransitTimes;
        private boolean finalized = false;

        /**
         * The rounded transit times, only available when build is complete.
         */
        protected IdentifiableIntegerMapping<Edge> transitTimes = null;

        /**
         * Initializes all data structures empty, except for the super sink which is added to the network.
         */
        protected AbstractNetworkFlowModelBuilder() {
            // TODO: remove raster and mapping from builder class
            sources = new LinkedList<>();
            sinks = new LinkedList<>();

            nodeCapacities = new IdentifiableIntegerMapping<>(0);
            edgeCapacities = new IdentifiableIntegerMapping<>(0);
            exactTransitTimes = new IdentifiableDoubleMapping<>(0);

            network = new DynamicNetwork();

            supersink = new Node(0);
            network.addNode(supersink);
            nodeCapacities.set(supersink, Integer.MAX_VALUE);
        }

        protected AbstractNetworkFlowModelBuilder(NetworkFlowModel model, boolean keepEdges) {
            int additionalNodes = model.getVirtualSources().size();
            int newNodeCount = model.graph().nodeCount() - additionalNodes;
            int newEdgeCount;
            if (keepEdges) {
                newEdgeCount = model.graph().edgeCount() - additionalNodes;
            } else {
                newEdgeCount = 0;
            }
            network = getBaseModel(model, newNodeCount, newEdgeCount);
            nodeCapacities = new IdentifiableIntegerMapping<>(model.nodeCapacities(), newNodeCount);
            edgeCapacities = new IdentifiableIntegerMapping<>(model.edgeCapacities(), newEdgeCount);
            sources = model.getSources();
            sinks = model.getModelSinks();
            supersink = model.getSupersink();
            exactTransitTimes = new IdentifiableDoubleMapping<>(network.edgeCount());
            for (Edge e : network.edges()) {
                exactTransitTimes.set(e, model.getTransitTime(e));
            }
        }

        private static DynamicNetwork getBaseModel(NetworkFlowModel model, int newNodeCount, int newEdgeCount) {
            // Return copy if no assignment is present
            DynamicNetwork newNetwork = new DynamicNetwork();
            for (int i = 0; i < newNodeCount; ++i) {
                newNetwork.addNode(model.graph().getNode(i));
            }
            for (int i = 0; i < newEdgeCount; ++i) {
                newNetwork.addEdge(model.graph().getEdge(i));
            }
            return newNetwork;
        }

        public Node getSupersink() {
            return supersink;
        }

        protected void checkNotFinalized() {
            if (finalized) {
                throw new IllegalStateException("Building complete");
            }
        }

        protected void checkFinalized() {
            if (!finalized) {
                throw new IllegalStateException("Building complete");
            }
        }

        public NetworkFlowModel build() {
            prepareBuild();
            finalizeBuild();
            postBuild();
            return new NetworkFlowModel(this);
        }

        protected abstract void prepareBuild();

        private void finalizeBuild() {
            finalized = true;
        }

        protected abstract void postBuild();

        /**
         * Returns the nodes that are actual sources due to an assignment, in contrast to
         * {@link #getSources()  model sources}. Source nodes are the nodes with the highest ids.
         *
         * @return a list of nodes that are sources
         */
        protected abstract List<Node> getVirtualSources();

        abstract IdentifiableIntegerMapping<Node> getAssignment();

        abstract ZToGraphMapping getZToGraphMapping();
    }

    /**
     * Builder for new instance of {@link NetworkFlowModel}.
     */
    public static class BasicBuilder extends AbstractNetworkFlowModelBuilder {

        /**
         * The logger object of this algorithm.
         */
        private static final Logger LOG = Debug.globalLogger;

        /**
         * Index of the next node to be added.
         */
        int nodeCount = 1;
        /**
         * Index of the next edge to be added.
         */
        int edgeIndex = 0;

        private final ZToGraphMapping mapping;

        /**
         * Initializes the builder. Contains a supersink by default, its capacity is {@link Integer#MAX_VALUE infinite}.
         *
         * @param raster
         */
        public BasicBuilder(ZToGraphRasterContainer raster) {
            super();
            // TODO: remove raster and mapping from builder class

            mapping = new ZToGraphMapping(raster); // start with an empty mapping in case we have an empty model
            mapping.setNodeRectangle(supersink, new NodeRectangle(0, 0, 0, 0));
            mapping.setFloorForNode(supersink, 0);
        }

        /**
         * Creates an instance of the network flow model builder based on an existing instance. Nodes and edges related
         * to an assignment are deleted. All supplies are resetted and all
         * {@link NetworkFlowModel#getVirtualSources() virtual sources} are removed. A parameter decides whether
         * <i>all</i> edges should be removed.
         *
         * @param model existing model instance
         * @param keepEdges determines whether edges should be kept
         */
        private BasicBuilder(NetworkFlowModel model, boolean keepEdges) {
            super(model, keepEdges);
            mapping = model.getZToGraphMapping();
        }

        /**
         * Creates an instance of the network flow model builder based on an existing instance. Any nodes and edges
         * related to an assignment are removed. All {@link NetworkFlowModel#getVirtualSources() virtual sources} are
         * removed.
         *
         * @param model existing model instance
         * @return the builder instance to build an extended model
         */
        public static BasicBuilder forExtension(NetworkFlowModel model) {
            return new BasicBuilder(model, true);
        }

        /**
         * Creates an instance of the network flow model builder based on an existing instance. Only nodes not related
         * to an assignment are kept. All edges are removed. All
         * {@link NetworkFlowModel#getVirtualSources() virtual sources} are removed.
         *
         * @param model existing model instance
         * @return the builder instance to build a model from the same nodes
         */
        public static BasicBuilder fromNodes(NetworkFlowModel model) {
            return new BasicBuilder(model, false);
        }

        /**
         * Adds a new intermediate node to the model. Other types of nodes to be added are
         * {@link #addSource(org.zetool.graph.Node)  sources} and sinks. New nodes have a capacity of 0 by default.
         *
         * @return the new node
         */
        public Node newNode() {
            checkNotFinalized();
            Node node = new Node(nodeCount);
            nodeCount++;
            network.setNode(node);
            nodeCapacities.set(node, 0);
            return node;
        }

        public void addSource(Node node) {
            checkNotFinalized();
            // TODO: refactor such that nodes are defined already with source/sink/intermediate state
            sources.add(node);
        }

        public void addSink(Node node) {
            checkNotFinalized();
            // TODO: refactor such that nodes are defined already with source/sink/intermediate state
            sinks.add(node);
            Edge e = newEdge(node, supersink);
            setEdgeCapacity(e, Integer.MAX_VALUE);
            setExactTransitTime(e, 0);
            mapping.setEdgeLevel(e, org.zetool.common.util.Level.Equal);
        }

        public void setNodeCapacity(Node node, int value) {
            checkNotFinalized();
            nodeCapacities.set(node, value);
        }

        /**
         * Increases the capacity for a node by a given value.
         *
         * @see #setNodeCapacity(org.zetool.graph.Node, int)
         * @param node the node whose capacity is increased
         * @param i the non negative value by which it is increased
         * @throws java.lang.ArithmeticException if the capacity overflows
         */
        public void increaseNodeCapacity(Node node, int i) {
            checkNotFinalized();
            nodeCapacities.increase(node, i);
        }

        /**
         * Reserves capacity for a given number of edges.
         *
         * @param numberOfEdges the reserved edge capacity
         */
        public void setNumberOfEdges(int numberOfEdges) {
            edgeCapacities.setDomainSize(numberOfEdges);
            exactTransitTimes.setDomainSize(numberOfEdges);
        }

        /**
         * Creates reverse edges. If a reverse edge already exists, it is not created.
         */
        void createReverseEdges() {
            //int edgeIndex = numberOfEdges();
            final int oldEdgeIndex = numberOfEdges();

            setNumberOfEdges(Math.max(0, numberOfEdges() * 2 - numberOfSinks()));

            // don't use an iterator here, as it will result in concurrent modification
            for (int i = 0; i < oldEdgeIndex; ++i) {
                Edge edge = network.getEdge(i);
                if (!edge.isIncidentTo(supersink)) {
                    createReverseEdge(edge);
                }
            }
        }

        /**
         * This should be called before rounding as it uses the exact transit times. Also, multiplication with up/down
         * speed factors may create rounding errors and thus, rounding should be the last operation.
         *
         * @param edge the edge
         */
        public void createReverseEdge(Edge edge) {
            Edge opposite = network.getEdge(edge.end(), edge.start());
            if (opposite == null) {
                Edge newEdge = new Edge(edgeIndex++, edge.end(), edge.start());

                mapping.setEdgeLevel(newEdge, mapping.getEdgeLevel(edge).getInverse());
                setEdgeCapacity(newEdge, edgeCapacities.get(edge));
                setExactTransitTime(newEdge, exactTransitTimes.get(edge));
                network.setEdge(newEdge);
            }
        }

        public void setEdgeCapacity(Edge edge, int value) {
            checkNotFinalized();
            edgeCapacities.set(edge, value);
        }

        /**
         * Adds a new edge. The edge has default capacity of 1 and transit time of 0.
         *
         * @param lastNode
         * @param node
         * @return
         */
        public Edge newEdge(Node lastNode, Node node) {
            checkNotFinalized();
            Edge edge = new Edge(edgeIndex++, lastNode, node);
            System.out.println("Edge (" + edge.start() + "," + edge.end() + ") created.");
            network.setEdge(edge);
            edgeCapacities.set(edge, 1);
            exactTransitTimes.set(edge, 0.0);
            return edge;
        }

        /**
         *
         * @param newEdge the edge to be added
         * @param edgeCapacity the capacity of the edge
         * @param exactTransitTime the transit time of the edge
         * @deprecated adding existing edges is not supported in future versions of the builder
         */
        @Deprecated
        public void addEdge(Edge newEdge, int edgeCapacity, double exactTransitTime) {
            checkNotFinalized();
            System.out.println("Edge (" + newEdge.start() + "," + newEdge.end() + ") created.");
            network.addEdge(newEdge);
            edgeCapacities.set(newEdge, edgeCapacity);
            exactTransitTimes.set(newEdge, exactTransitTime);
        }

        public void increaseEdgeCapacity(Edge edge, int i) {
            edgeCapacities.increase(edge, i);
        }

        public void setExactTransitTime(Edge edge, double value) {
            exactTransitTimes.set(edge, value);
        }

        public void divide(Edge edge, double factor) {
            exactTransitTimes.divide(edge, factor);
        }

        @Override
        protected void prepareBuild() {
            createReverseEdges();

            transitTimes = exactTransitTimes.round();
        }

        @Override
        protected void postBuild() {
            LOG.log(Level.INFO, "Number of nodes: {0}", network.nodeCount());
            LOG.log(Level.INFO, "Number of edges: {0}", network.edgeCount());
        }

        @Override
        IdentifiableIntegerMapping<Node> getAssignment() {
            return new IdentifiableConstantMapping<>(0);
        }

        @Override
        protected List<Node> getVirtualSources() {
            return Collections.emptyList();
        }

        /**
         * Returns the number of nodes of the graph model. Includes the super sink.
         *
         * @return the current number of nodes in the build process
         */
        public int numberOfNodes() {
            return network.nodeCount();
        }

        /**
         * Returns the current number of edges in the graph model builder. Includes the edges connecting the super sink.
         *
         * @return the current number of edges in the build process
         */
        public int numberOfEdges() {
            return network.edgeCount();
        }

        /**
         * Checks wether a given edge is already contained in the builder.
         *
         * @param edge the edge
         * @return {@code true} if the edge is contained
         */
        public boolean contains(Edge edge) {
            return network.contains(edge);
        }

        /**
         * Returns one of the edges identified by it's index. Edges are indexed by their adding order.
         *
         * @param i the edge index
         * @return the edge added at the given position
         */
        public Edge getEdge(int i) {
            return network.getEdge(i);
        }

        /**
         * Returns an edge between two nodes in the builder.
         *
         * @param a the start node
         * @param b the end node
         * @return the edge between the two nodes
         */
        public Edge getEdge(Node a, Node b) {
            return network.getEdge(a, b);
        }

        /**
         * Iterates the current edge set.
         *
         * @return iterable over the current edges
         */
        public Iterable<Edge> edges() {
            return network.edges();
        }

        /**
         * Returns the current number of sinks.
         *
         * @return the current number of sinks in the build process
         */
        public int numberOfSinks() {
            return sinks.size();
        }

        /**
         * Returns the current capacity of an edge. The capacity of an edge represents the number of units that can
         * enter the unit at every time.
         *
         * @param edge the edge
         * @return the current capacity for the edge
         */
        public int getEdgeCapacity(Edge edge) {
            return edgeCapacities.get(edge);
        }

        /**
         * Returns the current transit time of an edge. The transit time is the necessary time required to travel along
         * an edge from beginning to end.
         *
         * @param edge the edge
         * @return the current value of the transit time
         */
        public double getExactTransitTime(Edge edge) {
            return exactTransitTimes.get(edge);
        }

        @Override
        public ZToGraphMapping getZToGraphMapping() {
            return mapping;
        }

    }

    /**
     * Abstract base class for assignment builders. Implementing classes must add logic to actually increase the
     * assignment.
     */
    private static abstract class AssignmentBuilder extends AbstractNetworkFlowModelBuilder {

        /**
         * The mapping stored in the input model.
         */
        private final ZToGraphMapping mapping;

        /**
         * Assignments are offset based, and all new assignments are added to additional nodes.
         */
        protected IdentifiableIntegerMapping<Node> currentAssignment;

        /**
         * Creates an instance of the network flow model builder based on an existing instance to update the node
         * assignment. All supplies are resetted and all {@link NetworkFlowModel#getVirtualSources() virtual sources}
         * are removed
         *
         * @param model existing model instance
         */
        protected AssignmentBuilder(NetworkFlowModel model) {
            super(model, true);
            currentAssignment = new IdentifiableIntegerMapping<>(model.numberOfNodes());
            this.mapping = model.getZToGraphMapping();
        }

        /**
         * Throws an exception if the node is not a source.
         *
         * @param node the node
         */
        protected void checkValidForAssignment(Node node) {
            if (!sources.contains(node)) {
                throw new IllegalArgumentException("Assignment can only be defined for sources.");
            }
        }

        @Override
        protected void prepareBuild() {
            transitTimes = exactTransitTimes.round();
        }

        @Override
        protected void postBuild() {
            checkSupplies();
        }

        /**
         * Checks the feasibility of supplies.
         */
        private void checkSupplies() {
            DirectedGraph newNetwork = this.network.getAsStaticNetwork();

            GraphInstanceChecker checker = new GraphInstanceChecker(newNetwork, currentAssignment);
            checker.supplyChecker();

            if (checker.hasRun()) {
                List<Node> newSources = checker.getNewSources();
                for (Node oldSource : checker.getDeletedSources()) {
                    // TODO pass mapping
//                mapping.setDeletedSourceNode(oldSource, true);
                }
            } else {
                throw new AssertionError(ZETLocalization2.loc.getString("converter.NoCheckException"));
            }

        }

        @Override
        IdentifiableIntegerMapping<Node> getAssignment() {
            checkFinalized();
            return currentAssignment;
        }

        @Override
        ZToGraphMapping getZToGraphMapping() {
            return mapping;
        }
    }

    /**
     * Assignment builder that can place an assignment directly in {@link #getSources() source nodes}. It is not ossible
     * to assign start times to individual flow units.
     */
    public static class ImmediateAssignmentBuilder extends AssignmentBuilder {

        /**
         * Initializes the assignment builder with an existing instance to create an assignment. All supplies are
         * resetted and all {@link NetworkFlowModel#getVirtualSources() virtual sources} are removed.
         *
         * @param model existing model instance
         */
        public ImmediateAssignmentBuilder(NetworkFlowModel model) {
            super(model);
        }

        /**
         * Sets the assignment for a {@link #getSources() source node}. It is possible to set the value to higher and
         * lower amounts, i.e. to decrease the assignment at the source.
         *
         * @param modelSource the model source node for which the assignment is set
         * @param amount the value of the assignment
         */
        public void setNodeAssignment(Node modelSource, int amount) {
            int diff = amount - (currentAssignment.isDefinedFor(modelSource)
                    ? currentAssignment.get(modelSource)
                    : 0);
            increaseNodeAssignment(modelSource, diff);
        }

        /**
         * Increases the assignment for a given node. The assignment is increased by a single flow unit.
         *
         * @param source the source node
         */
        public void increaseNodeAssignment(Node source) {
            increaseNodeAssignment(source, 1);
        }

        /**
         * Increases the assignment for a given node. The increased amount can be negative, in this case it must be
         * smaller than the current capacity.
         *
         * @param source the source node
         * @param amount the number of flow units by which the assignment is increased, can be negative
         */
        public void increaseNodeAssignment(Node source, int amount) {
            Objects.requireNonNull(source, "Node must not be null!");
            checkValidForAssignment(source);
            checkValidAssignment(source, amount);

            currentAssignment.decrease(supersink, amount);
            currentAssignment.increase(source, amount);
            nodeCapacities.increase(source, amount);
        }

        private void checkValidAssignment(Node source, int amount) {
            if (amount < 0 && currentAssignment.get(source) < -amount) {
                throw new IllegalArgumentException("Node assignment must not be negative");
            }
        }

        @Override
        protected List<Node> getVirtualSources() {
            return Collections.emptyList();
        }
    }

    /**
     * Assignment builder supporting individual start times for flow units. The flow units are placed in virtual
     * sources.
     */
    public static class AccurateAssignmentBuilder extends AssignmentBuilder {

        /**
         * Nodes that are created to connect assignment nodes to model source.
         */
        protected final IdentifiableObjectMapping<Node, List<Node>> virtualNodes = new IdentifiableObjectMapping<>(sources);

        /**
         * Initializes the assignment builder with an existing instance to create an assignment. All supplies are
         * resetted and all {@link NetworkFlowModel#getVirtualSources() virtual sources} are removed.
         *
         * @param model existing model instance
         */
        public AccurateAssignmentBuilder(NetworkFlowModel model) {
            super(model);
        }

        /**
         * Increases the assignment for a given node with units without delay.
         *
         * @param source the source node
         */
        void increaseNodeAssignment(Node source) {
            increaseNodeAssignment(source, 1);
        }

        /**
         * Increases the assignment for a given node by a single unit with a delay. A virtual source node is connected
         * to the given source via a single edge.
         * <p>
         * A new virtual node {@code v} is created and connected with a single edge {@code e} to the given source. The
         * following capacities are assigned:
         * <ul>
         * <li>capacity(v): 1</li>
         * <li>assignment(v): 1</li>
         * <li>capacity(e): 1</li>
         * <li>transitTime(e): {@code delay}</li>
         * </ul></p>
         *
         * @param source the source
         * @param delay the time the flow unit requires until it starts at the {@code node}
         * @return the virtual node holding the flow unit
         */
        public Node increaseNodeAssignment(Node source, double delay) {
            return increaseNodeAssignment(source, 1, delay);
        }

        /**
         * Increases the assignment for a given node by multiple units without delay. A virtual source node is connected
         * to the given source via a single edge.
         * <p>
         * A new virtual node {@code v} is created and connected with a single edge {@code e} to the given source. The
         * following capacities are assigned:
         * <ul>
         * <li>capacity(v): {@code amount}</li>
         * <li>assignment(v): {@code amount}</li>
         * <li>capacity(e): {@code amount}</li>
         * <li>transitTime(e): 0</li>
         * </ul></p>
         *
         * @param source the source
         * @param amount the number of units assigned to the virtual node
         * @return the virtual node holding the flow unit
         */
        public Node increaseNodeAssignment(Node source, int amount) {
            return increaseNodeAssignment(source, amount, 0.0);
        }

        private Node increaseNodeAssignment(Node node, int amount, double delay) {
            Objects.requireNonNull(node, "Node must not be null!");
            checkValidForAssignment(node);
            checkValidAssignment(amount);
            currentAssignment.decrease(supersink, amount);
            Node virtualSource = getVirtualSource(node);
            Edge virtualEdge = network.outgoingEdges(virtualSource).get(0);
            updateNodeAssignment(virtualSource, virtualEdge, amount, delay);
            return virtualSource;
        }

        private void checkValidAssignment(int amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Node assignment must not be negative");
            }
        }

        protected void updateNodeAssignment(Node virtualSource, Edge virtualEdge, int amount, double delay) {
            currentAssignment.increase(virtualSource, amount);
            nodeCapacities.increase(virtualSource, amount);

            edgeCapacities.increase(virtualEdge, amount);
            exactTransitTimes.set(virtualEdge, delay);
        }

        /**
         * Retrieves a virtual source node for the given {@code source}. Can be a new instance, or an existing one.
         * <p>
         * The new node {@code v} and the connecting edge {@code e} are initialized empty with
         * <ul>
         * <li>capacity(v): 0</li>
         * <li>assignment(v): 0</li>
         * <li>capacity(e): 0</li>
         * <li>transitTime(e): 0</li>
         * </ul></p>
         *
         * @param source a node in {@link #getSources()}
         * @return the source instance
         */
        protected Node getVirtualSource(Node source) {
            Node node = new Node(network.nodeCount());
            network.setNode(node);
            addVirtualNode(source, node);
            nodeCapacities.set(node, 0);

            Edge edge = new Edge(network.edgeCount(), node, source);
            network.setEdge(edge);
            edgeCapacities.set(edge, 0);
            exactTransitTimes.set(edge, 0);
            return node;
        }

        private void addVirtualNode(Node source, Node node) {
            if (!virtualNodes.isDefinedFor(source)) {
                virtualNodes.set(source, new LinkedList<>());
            }
            virtualNodes.get(source).add(node);
        }

        @Override
        protected List<Node> getVirtualSources() {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(virtualNodes.iterator(), Spliterator.ORDERED), false)
                    .flatMap(List::stream).collect(toList());
        }
    }

    /**
     * Assignment builder supporting individual start times for sources. The start times for flow units starting at a
     * node are averaged.
     */
    public static class AverageAssignmentBuilder extends AccurateAssignmentBuilder {

        /**
         * Nodes that are created to connect assignment nodes to model source.
         *
         * @param model existing model instance
         */
        public AverageAssignmentBuilder(NetworkFlowModel model) {
            super(model);
        }

        @Override
        protected void updateNodeAssignment(Node virtualSource, Edge virtualEdge, int amount, double delay) {
            currentAssignment.increase(virtualSource, amount);
            nodeCapacities.increase(virtualSource, amount);

            edgeCapacities.increase(virtualEdge, amount);
            double averageDelay = computeAverageDelay(virtualSource, virtualEdge, amount, delay);
            exactTransitTimes.set(virtualEdge, averageDelay);
        }

        private double computeAverageDelay(Node virtualSource, Edge virtualEdge, int amount, double delay) {
            int oldCapacity = currentAssignment.get(virtualSource);
            return ((exactTransitTimes.get(virtualEdge) * oldCapacity) + delay) / (oldCapacity + amount);
        }

        /**
         * Returns the virtual node for a source. If the virtual node does not exist, it is created.
         *
         * {@inheritDoc}
         *
         * @param source a node in {@link #getSources()}
         * @return the unique virtual source for the given source
         */
        @Override
        protected Node getVirtualSource(Node source) {
            if (virtualNodes.isDefinedFor(source)) {
                return virtualNodes.get(source).get(0);
            } else {
                return super.getVirtualSource(source);
            }
        }

        @Override
        protected List<Node> getVirtualSources() {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(virtualNodes.iterator(), Spliterator.ORDERED), false)
                    .flatMap(List::stream).collect(toList());
        }
    }
}
