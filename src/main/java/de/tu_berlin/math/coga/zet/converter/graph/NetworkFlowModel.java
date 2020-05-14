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

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.coga.netflow.util.GraphInstanceChecker;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import de.tu_berlin.math.coga.zet.ZETLocalization2;
import org.zetool.math.vectormath.Vector3;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import ds.graph.NodeRectangle;
import org.zetool.graph.DefaultDirectedGraph;
import org.zetool.container.mapping.IdentifiableDoubleMapping;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.DynamicNetwork;
import gui.visualization.VisualizationOptionManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 */
@XStreamAlias("networkFlowModel")
public class NetworkFlowModel implements Iterable<Node> {

    protected DefaultDirectedGraph netw;
    protected DynamicNetwork network;
    protected IdentifiableIntegerMapping<Edge> edgeCapacities;
    protected IdentifiableIntegerMapping<Node> nodeCapacities;
    protected IdentifiableIntegerMapping<Edge> transitTimes;
    protected IdentifiableDoubleMapping<Edge> exactTransitTimes;
    protected IdentifiableIntegerMapping<Node> currentAssignment;
    protected List<Node> sources;
    protected ZToGraphMapping mapping;
    protected Node supersink;
    int edgeIndex = 0;
    int nodeCount;
    private int individualSourceIndex = -1;
    private int individualEdgeIndex = -1;
    boolean finalized = false;
    boolean accurateStartTimes = true;

    public NetworkFlowModel(ZToGraphRasterContainer raster) {
        this.network = new DynamicNetwork();
        this.edgeCapacities = new IdentifiableIntegerMapping<>(0);
        this.nodeCapacities = new IdentifiableIntegerMapping<>(0);
        this.transitTimes = new IdentifiableIntegerMapping<>(0);
        this.exactTransitTimes = new IdentifiableDoubleMapping<>(0);
        this.currentAssignment = new IdentifiableIntegerMapping<>(0);
        this.sources = new LinkedList<>();
        supersink = new Node(0);
        nodeCount = 1;
        network.setNode(supersink);
        mapping = new ZToGraphMapping(raster); // start with an empty mapping in case we have an empty model
        mapping.setNodeRectangle(supersink, new NodeRectangle(0, 0, 0, 0));
        mapping.setFloorForNode(supersink, 0);

    }

    public NetworkFlowModel(NetworkFlowModel model) {
        // a constructor that copies the nodes. TODO: as static method
        this(model.mapping.getRaster());
        network.setNodes(model.network.nodes());
        nodeCount = model.nodeCount;
        supersink = model.supersink;
        sources = model.sources;
        nodeCapacities = model.nodeCapacities;
        currentAssignment = model.currentAssignment;
    }

    public int getEdgeCapacity(Edge edge) {
        if (edgeCapacities.isDefinedFor(edge)) {
            return edgeCapacities.get(edge);
        } else {
            throw new IllegalArgumentException(ZETLocalization2.loc.getString("ds.Graph.NoEdgeCapacityException" + edge + "."));
        }
    }

    /**
     * Returns a linked list containing the sources. May be consistent with the current assignment or not.
     *
     * @return a linked list containing the sources.
     */
    public List<Node> getSources() {
        return Collections.unmodifiableList(sources);
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

    public ZToGraphMapping getZToGraphMapping() {
        return mapping;
    }

    public DirectedGraph graph() {
        return network;
    }

    public int getNodeCapacity(Node node) {
        if (nodeCapacities.isDefinedFor(node)) {
            return nodeCapacities.get(node);
        } else {
            throw new IllegalArgumentException(ZETLocalization2.loc.getString("ds.Graph.NoNodeCapacityException" + node + "."));
        }
    }

    public IdentifiableIntegerMapping<Node> nodeCapacities() {
        return nodeCapacities;
    }

    public int getTransitTime(Edge edge) {
        if (transitTimes.isDefinedFor(edge)) {
            return transitTimes.get(edge);
        } else {
            throw new IllegalArgumentException(ZETLocalization2.loc.getString("ds.Graph.NoTransitTimeException" + edge + "."));
        }
    }

    public double getExactTransitTime(Edge edge) {
        if (exactTransitTimes.isDefinedFor(edge)) {
            return exactTransitTimes.get(edge);
        } else {
            return -1;
        }
    }

    public Node getSupersink() {
        return supersink;
    }

    public int numberOfNodes() {
        return network.nodeCount();
    }

    public int numberOfEdges() {
        return network.edgeCount();
    }

    public Iterable<Edge> edges() {
        return network.edges();
    }

    public int numberOfSinks() {
        return network.degree(getSupersink());
    }

    public Edge getEdge(int i) {
        return network.getEdge(i);
    }

    public Edge getEdge(Node lastNode, Node node) {
        return network.getEdge(lastNode, node);
    }

    public boolean contains(Edge edge) {
        return network.contains(edge);
    }

    public Node getNode(int i) {
        return network.getNode(i);
    }

    @Override
    public Iterator<Node> iterator() {
        return network.nodes().iterator();
    }

    @Override
    public String toString() {
        return "NetworkFlowModel{" + "network=" + network + ", edgeCapacities=" + edgeCapacities + ", nodeCapacities=" + nodeCapacities + ", transitTimes=" + transitTimes + ", sources=" + sources + ", supersink=" + supersink + '}';
    }

    public void checkSupplies() {
        DirectedGraph network = this.network.getAsStaticNetwork();
        IdentifiableIntegerMapping<Node> supplies = currentAssignment;

        GraphInstanceChecker checker = new GraphInstanceChecker(network, supplies);
        checker.supplyChecker();

        if (checker.hasRun()) {
            currentAssignment = checker.getNewSupplies();
            sources = checker.getNewSources();
            for (Node oldSource : checker.getDeletedSources()) {
                //mapping.setIsSourceNode( oldSource, false );
                mapping.setDeletedSourceNode(oldSource, true);
            }
        } else {
            throw new AssertionError(ZETLocalization2.loc.getString("converter.NoCheckException"));
        }
    }

    public Node newNode() {
        if (finalized) {
            throw new IllegalStateException("Cannot create new nodes if finalized!");
        }
        Node node = new Node(nodeCount);
        nodeCount++;
        network.setNode(node);
        return node;
    }

    void setNumberOfEdges(int numberOfEdges) {
        edgeCapacities.setDomainSize(numberOfEdges);
        transitTimes.setDomainSize(numberOfEdges);
        exactTransitTimes.setDomainSize(numberOfEdges);
    }

    void setEdgeCapacity(Edge edge, int value) {
        edgeCapacities.set(edge, value);
    }

    void setNodeCapacity(Node node, int value) {
        nodeCapacities.set(node, value);
    }

    public void setTransitTime(Edge edge, int value) {
        transitTimes.set(edge, value);
    }

    void setExactTransitTime(Edge edge, double value) {
        exactTransitTimes.set(edge, value);
    }

    void roundTransitTimes() {
        transitTimes = exactTransitTimes.round();
    }

    /**
     * This should be called before rounding as it uses the exact transit times. Also, multiplication with up/down speed
     * factors may create rounding errors and thus, rounding should be the last operation.
     *
     * @param edge the edge
     * @return the reverse edge
     */
    Edge createReverseEdge(Edge edge) {
        Edge newEdge = new Edge(edgeIndex++, edge.end(), edge.start());

        while (network.edges().contains(newEdge)) {
            newEdge = new Edge(edgeIndex++, edge.end(), edge.start());
        }

        mapping.setEdgeLevel(newEdge, mapping.getEdgeLevel(edge).getInverse());
        setEdgeCapacity(newEdge, getEdgeCapacity(edge));
        setExactTransitTime(newEdge, getExactTransitTime(edge));
        network.setEdge(newEdge);
        //System.out.println( "Reverse Edge (" + newEdge.start() + "," + newEdge.end() + ") created." );
        //System.out.println( " - capacity " + getEdgeCapacity( edge ) + " transit time + " + getExactTransitTime( edge ) );
        return newEdge;
    }

    void setNodeAssignment(Node node, int i) {
        currentAssignment.set(node, i);
    }

    void increaseNodeAssignment(Node node) {
        increaseNodeAssignment(node, 0);
    }

    void increaseNodeAssignment(Node node, double transitTime) {
        Objects.requireNonNull(node, "Node must not be null!");
        if (accurateStartTimes) {
            Node individualNode = new Node(nodeCount++);
            network.setNode(individualNode);
            currentAssignment.increase(individualNode, 1);
            nodeCapacities.set(individualNode, 1);
            mapping.setDeletedSourceNode(individualNode, false);

            // edge
            Edge edge = new Edge(edgeIndex++, individualNode, node);
            System.out.println("Dummy detailed assignment Edge(" + edge.start() + "," + edge.end() + ") created.");
            network.setEdge(edge);
            edgeCapacities.set(edge, 1);
            transitTimes.set(edge, (int) transitTime);
            exactTransitTimes.set(edge, transitTime);

            mapping.setFloorForNode(individualNode, 0);

            int virtualId = individualNode.id() - individualSourceIndex;
            NodeRectangle rect = new NodeRectangle(400 * virtualId, -800, 400 * (virtualId + 1), -400);

            mapping.setNodeRectangle(individualNode, rect);

        } else {
            currentAssignment.increase(node, 1);
        }
    }

    void addSource(Node node) {
        sources.add(node);
    }

    void ensureCapacities() {
        nodeCapacities.setDomainSize(network.nodeCount());

        edgeCapacities.setDomainSize(network.edgeCount() * network.edgeCount()); // TODO weird???
    }

    private void ensureCapacities(int persons) {
        nodeCapacities.setDomainSize(individualSourceIndex + persons);
        edgeCapacities.setDomainSize(individualEdgeIndex + persons);
    }

    void increaseNodeCapacity(Node node, int i) {
        nodeCapacities.increase(node, i);
    }

    public Edge newEdge(Node lastNode, Node node) {
        if (finalized) {
            throw new IllegalStateException("Cannot create edges when finalized!");
        }
        Edge edge = new Edge(edgeIndex++, lastNode, node);
        System.out.println("Edge (" + edge.start() + "," + edge.end() + ") created.");
        network.setEdge(edge);
        edgeCapacities.set(edge, 0);
        return edge;
    }

    void increaseEdgeCapacity(Edge edge, int i) {
        edgeCapacities.increase(edge, i);
    }

    void addEdge(Edge newEdge, int edgeCapacity, int transitTime, double exactTransitTime) {
        network.addEdge(newEdge);
        edgeCapacities.set(newEdge, edgeCapacity);
        transitTimes.set(newEdge, transitTime);
        exactTransitTimes.set(newEdge, exactTransitTime);
    }

    void resetAssignment() {
        resetAssignment(0);
//        currentAssignment = new IdentifiableIntegerMapping<>( network.nodeCount() );
//        for( int i = 0; i < network.nodes().size(); i++ )
//            currentAssignment.set( network.nodes().get( i ), 0 );
    }

    /**
     * Resets the current assignment (if any) and prepares for addition of {@code person} flow units as capacities.
     *
     * @param persons
     */
    void resetAssignment(int persons) {
        if (!finalized) {
            completeConstruction();
        }

//        // add one for test purposes
//        network.addNode(new Node(nodeCount++));

        // try to remove nodes with higher index
        Node current = network.nodes().last();
        List<Node> toRemove = new LinkedList<>();
        while (current != null) {
            if (current.id() >= individualSourceIndex) {
                toRemove.add(current);
            }
            current = network.nodes().predecessor(current);
        }
        network.removeNodes(toRemove);
        nodeCount = individualSourceIndex;
        edgeIndex = individualEdgeIndex;

        currentAssignment = new IdentifiableIntegerMapping<>(network.nodeCount() + persons);
        for (int i = 0; i < network.nodes().size(); i++) {
            currentAssignment.set(network.nodes().get(i), 0);
        }

        ensureCapacities(persons);

    }

    public IdentifiableIntegerMapping<Edge> transitTimes() {
        return transitTimes;
    }

    public IdentifiableIntegerMapping<Node> currentAssignment() {
        return currentAssignment;
    }

    public IdentifiableIntegerMapping<Edge> edgeCapacities() {
        return edgeCapacities;
    }

    void divide(Edge edge, double factor) {
        exactTransitTimes.divide(edge, factor);
    }

    public NodePositionMapping getNodeCoordinates() {
        NodePositionMapping nodePositionMapping = new NodePositionMapping(network.nodeCount());
        for (Node n : network.nodes()) {
            final Vector3 v;
//      if( n.id() < individualSourceIndex ) {
            NodeRectangle rect = mapping.getNodeRectangles().get(n);
            final double zs = mapping.getNodeFloorMapping().get(n) * VisualizationOptionManager.getFloorDistance();
            v = new Vector3(rect.getCenterX(), rect.getCenterY(), zs);
//      } else {
//        
//        final double zs = 0 * VisualizationOptionManager.getFloorDistance();
//        v = new Vector3( rect.getCenterX(), rect.getCenterY(), zs );
//      }

            nodePositionMapping.set(n, v);
        }
        return nodePositionMapping;
    }

    // TO be moved into the builder as static
    public EarliestArrivalFlowProblem getEAFP() {
        return getEAFP(0);
    }

    public EarliestArrivalFlowProblem getEAFP(int upperBound) {
        return new EarliestArrivalFlowProblem(edgeCapacities, network.getAsStaticNetwork(), nodeCapacities, supersink, sources, upperBound, transitTimes, currentAssignment);
    }

    /**
     * Finalizes the construction process. After the method is called the structure of the base network cannot be
     * changed any more. However, it is still possible to create new source nodes if an assignment is added.
     */
    private void completeConstruction() {
        individualSourceIndex = nodeCount;
        individualEdgeIndex = edgeIndex;
        finalized = true;
    }

}
