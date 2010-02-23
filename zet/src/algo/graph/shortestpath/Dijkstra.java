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
/*
 * Dijkstra.java
 *
 */
package algo.graph.shortestpath;

import ds.graph.Localization;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.MinHeap;
import ds.graph.Network;
import ds.graph.Node;

/**
 *
 * @author Martin Groß
 */
public class Dijkstra {

    private IdentifiableIntegerMapping<Edge> costs;
    private Network graph;
    private Node source;
    private boolean reverse;
    private IdentifiableIntegerMapping<Node> distances;
    private IdentifiableObjectMapping<Node, Edge> edges;
    private IdentifiableObjectMapping<Node, Node> nodes;

    public Dijkstra(Network graph, IdentifiableIntegerMapping<Edge> costs, Node source) {
        this.costs = costs;
        this.graph = graph;
        this.source = source;
        this.reverse = false;
    }

    public Dijkstra(Network graph, IdentifiableIntegerMapping<Edge> costs, Node source, boolean reverse) {
        this.costs = costs;
        this.graph = graph;
        this.source = source;
        this.reverse = reverse;
    }

    public IdentifiableIntegerMapping<Node> getDistances() {
        if (distances == null) {
            throw new IllegalStateException(Localization.getInstance().getString("algo.graph.shortestpath.NotCalledYetException"));
        }
        return distances;
    }

    public int getDistance(Node node) {
        if (distances == null) {
            throw new IllegalStateException(Localization.getInstance().getString("algo.graph.shortestpath.NotCalledYetException"));
        }
        return distances.get(node);
    }

    public IdentifiableObjectMapping<Node, Edge> getLastEdges() {
        if (edges == null) {
            throw new IllegalStateException(Localization.getInstance().getString("algo.graph.shortestpath.NotCalledYetException"));
        }
        return edges;
    }

    public Edge getLastEdge(Node node) {
        if (edges == null) {
            throw new IllegalStateException(Localization.getInstance().getString("algo.graph.shortestpath.NotCalledYetException"));
        }
        return edges.get(node);
    }

    public IdentifiableObjectMapping<Node, Node> getPredecessors() {
        if (nodes == null) {
            throw new IllegalStateException(Localization.getInstance().getString("algo.graph.shortestpath.NotCalledYetException"));
        }
        return nodes;
    }

    public Node getPredecessor(Node node) {
        if (nodes == null) {
            throw new IllegalStateException(Localization.getInstance().getString("algo.graph.shortestpath.NotCalledYetException"));
        }
        return nodes.get(node);
    }

    public Forest getShortestPathTree() {
        return new Forest(graph.nodes(), getLastEdges());
    }

    public boolean isInitialized() {
        return graph != null && source != null;
    }

    public void run() {
        if (graph == null) {
            throw new IllegalStateException(Localization.getInstance().getString("algo.graph.shortestpath.GraphIsNullException"));
        }
        if (source == null) {
            throw new IllegalStateException(Localization.getInstance().getString("algo.graph.shortestpath.SourceIsNullException"));
        }
        if (distances != null) {
            return;
        }
        distances = new IdentifiableIntegerMapping<Node>(graph.numberOfNodes());
        edges = new IdentifiableObjectMapping<Node, Edge>(graph.numberOfNodes(), Edge.class);
        nodes = new IdentifiableObjectMapping<Node, Node>(graph.numberOfNodes(), Node.class);
        MinHeap<Node, Integer> queue = new MinHeap<Node, Integer>(graph.numberOfNodes());
        for (int v = 0; v < graph.numberOfNodes(); v++) {
            distances.set(graph.getNode(v), Integer.MAX_VALUE);
            queue.insert(graph.getNode(v), Integer.MAX_VALUE);
        }
        distances.set(source, 0);
        queue.decreasePriority(source, 0);
        while (!queue.isEmpty()) {
            MinHeap<Node, Integer>.Element min = queue.extractMin();
            Node v = min.getObject();
            Integer pv = min.getPriority();
            distances.set(v, pv);
            IdentifiableCollection<Edge> incidentEdges;
            if (!reverse) {
                incidentEdges = graph.outgoingEdges(v);
            } else {
                incidentEdges = graph.incomingEdges(v);
            }
            for (Edge edge : incidentEdges) {
                Node w = edge.opposite(v);
                if (queue.contains(w) && (long) queue.priority(w) > (long) pv + (long) costs.get(edge)) {
                    queue.decreasePriority(w, pv + costs.get(edge));
                    edges.set(w, edge);
                    nodes.set(w, v);
                }
            }
        }
    }

    public Network getGraph() {
        return graph;
    }

    public void setGraph(Network graph) {
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
