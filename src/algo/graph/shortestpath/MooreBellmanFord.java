/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package algo.graph.shortestpath;

import ds.graph.GraphLocalization;
import ds.graph.Edge;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.container.mapping.IdentifiableObjectMapping;
import ds.graph.network.AbstractNetwork;
import ds.graph.Node;
import ds.graph.Path;
import ds.graph.StaticPath;

/**
 *
 * @author Martin Groß
 */
public class MooreBellmanFord {

    private IdentifiableIntegerMapping<Edge> costs;
    private AbstractNetwork graph;
    private Node source;
    private IdentifiableIntegerMapping<Node> distances;
    private IdentifiableObjectMapping<Node, Edge> edges;
    private IdentifiableObjectMapping<Node, Node> nodes;

    public MooreBellmanFord() {
    }

    public MooreBellmanFord(AbstractNetwork graph, IdentifiableIntegerMapping<Edge> costs, Node source) {
        this.costs = costs;
        this.graph = graph;
        this.source = source;
    }

    public IdentifiableIntegerMapping<Node> getDistances() {
        if (distances == null) {
            throw new IllegalStateException(GraphLocalization.loc.getString ("algo.graph.shortestpath.NotCalledYetException"));
        }
        return distances;
    }

    public double getDistance(Node node) {
        if (distances == null) {
            throw new IllegalStateException(GraphLocalization.loc.getString ("algo.graph.shortestpath.NotCalledYetException"));
        }
        return distances.get(node);
    }

    public IdentifiableObjectMapping<Node, Edge> getLastEdges() {
        if (edges == null) {
            throw new IllegalStateException(GraphLocalization.loc.getString ("algo.graph.shortestpath.NotCalledYetException"));
        }
        return edges;
    }

    public Edge getLastEdge(Node node) {
        if (edges == null) {
            throw new IllegalStateException(GraphLocalization.loc.getString ("algo.graph.shortestpath.NotCalledYetException"));
        }
        return edges.get(node);
    }

    public IdentifiableObjectMapping<Node, Node> getPredecessors() {
        if (nodes == null) {
            throw new IllegalStateException(GraphLocalization.loc.getString ("algo.graph.shortestpath.NotCalledYetException"));
        }
        return nodes;
    }

    public Node getPredecessor(Node node) {
        if (nodes == null) {
            throw new IllegalStateException(GraphLocalization.loc.getString ("algo.graph.shortestpath.NotCalledYetException"));
        }
        return nodes.get(node);
    }

    public Path getShortestPath(Node target) {
        Path path = new StaticPath();
        Node node = target;
        while (node != source) {
            Edge edge = getLastEdge(node);
            path.addFirstEdge(edge);
            node = edge.opposite(node);
        }
        return path;
    }

    public boolean isInitialized() {
        return graph != null && source != null;
    }

    public void run() {
        if (graph == null) {
            throw new IllegalStateException(GraphLocalization.loc.getString ("algo.graph.shortestpath.GraphIsNullException"));
        }
        if (source == null) {
            throw new IllegalStateException(GraphLocalization.loc.getString ("algo.graph.shortestpath.SourceIsNullException"));
        }
        if (distances != null) {
            return;
        }
        distances = new IdentifiableIntegerMapping<>(graph.numberOfNodes());
        edges = new IdentifiableObjectMapping<>(graph.numberOfNodes(), Edge.class);
        nodes = new IdentifiableObjectMapping<>(graph.numberOfNodes(), Node.class);
        for (Node node : graph.nodes()) {
            distances.set(node, Integer.MAX_VALUE);
        }
        distances.set(source, 0);
        for (int i = 0; i < graph.numberOfNodes(); i++) {
            for (Edge e : graph.edges()) {
                Node v = e.start();
                Node w = e.end();
                long dw = distances.get(w);
                long dv = distances.get(v);
                if (dw > dv + costs.get(e)) {
                    distances.set(w, distances.get(v) + costs.get(e));
                    edges.set(w, e);
                    nodes.set(w, v);
                }
            }
        }
    }

    public AbstractNetwork getNetwork() {
        return graph;
    }

    public void setNetwork(AbstractNetwork graph) {
        if (graph != this.graph) {
            this.graph = graph;
            distances = null;
            edges = null;
            nodes = null;
        }
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        if (source != this.source) {
            this.source = source;
            distances = null;
            edges = null;
            nodes = null;
        }
    }
}
