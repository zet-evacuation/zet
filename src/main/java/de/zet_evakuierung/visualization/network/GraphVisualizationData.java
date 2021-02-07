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
package de.zet_evakuierung.visualization.network;

import java.util.Optional;

import ds.graph.NodeRectangle;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.math.vectormath.Vector3;

/**
 * Data supplier for flow graph visualization. Provides the {@link #getNetwork()  static network structure} as well as
 * {@link #getFlow(org.zetool.graph.Edge, int) dynamic flow information}. Supports additional information about layers
 * in a graph, wich is not supported by the original graph.
 * <p>
 * Several methods provide information on data assigned to {@link Node nodes} and {@link Edge edges}.</p>
 * <p>
 * Other than the default information in a graph, it adds {@link #getLayer(org.zetool.graph.Node) layers} on which nodes
 * can be visualized.</p>
 *
 * @author Jan-Philipp Kappmeier
 */
public interface GraphVisualizationData {

    /**
     * Returns the underlying network.
     *
     * @return the network
     */
    DirectedGraph getNetwork();

    /**
     *
     * @param node the node
     * @return
     */
    boolean isSource(Node node);

    /**
     * Determines whether a node technically is a source, but practially does not because there are no supplies.
     *
     * @param node the node
     * @return wether a node us a non-used source node
     */
    default boolean isDeletedSource(Node node) {
        return false;
    }

    /**
     * Returns whether a node is a sink.
     *
     * @param node the node
     * @return whether a node is a sink node
     */
    boolean isSink(Node node);

    /**
     * Returns the super sink node of the network.
     *
     * @return the super sink node
     */
    Node getSupersink();

    /**
     * Returns the position of a node in 3-dimensional space. The meaning of a node's position is not strictly defined.
     * Implementing classes an use it as absolute position as well as using them as offset, e.g. by the
     * {@link #getLayer(org.zetool.graph.Node) level}.
     *
     * @param node the node
     * @return the position of the node
     */
    Vector3 getPosition(Node node);

    /**
     * Returns an optional rectangular shape around a node. The shape represents the area occupied by the node. Default
     * return value is an {@link Optional#empty() no shape}.
     *
     * @param node the node
     * @return an optional rectangular shape around the node
     */
    default Optional<NodeRectangle> getNodeRectangle(Node node) {
        return Optional.empty();
    }

    /**
     * Returns the layer of the graph in a multi layer graph. The default return value is 0.
     *
     * @param node the node
     * @return the layer of the node
     */
    default int getLayer(Node node) {
        return 0;
    }

    /**
     * Returns the number of layers in the graph. The default return value is 0.
     *
     * @return the number of layers
     */
    default int getLayerCount() {
        return 0;
    }

    /**
     * Iterates over all nodes on a certain layer. By default returns all nodes in the graph, i.e. only the single
     * ({@link #getLayer(org.zetool.graph.Node) layer 0}) is assumed.
     *
     * @param layer the layer
     * @return an iterable over all nodes on the layer
     */
    default Iterable<Node> getNodesOnLayer(int layer) {
        return getNetwork().nodes();
    }

    /**
     * Returns the capacity of a node.
     *
     * @param node the node
     * @return the capacity of the node
     */
    int getCapacity(Node node);

    /**
     * Returns the capacity of an edge.
     *
     * @param edge the edge
     * @return the capacity of an edge
     */
    int getCapacity(Edge edge);

    /**
     * Returns the transit time along an edge
     *
     * @param edge the edge
     * @return the transit time along the edge
     */
    int getTransitTime(Edge edge);

    /**
     *
     * @param edge the edge
     * @param time
     * @return
     */
    int getFlow(Edge edge, int time);

    /**
     * Returns the maximum flow rate on any edge and point in time.
     *
     * @return the maximum flow rate
     */
    int getMaximumFlowValue();

    /**
     * Returns the last point in time there is flow on an edge.
     *
     * @param edge the edge
     * @return
     */
    int getLastFlowTime(Edge edge);

}
