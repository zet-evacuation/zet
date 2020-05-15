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
package algo.graph.spanningtree;

import java.util.Random;

import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.collection.ListSequence;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.container.priority.MinHeap;
import org.zetool.graph.Edge;
import org.zetool.graph.Graph;
import org.zetool.graph.Node;

/**
 *
 * @author Marlen Schwengfelder
 */
public class Prim extends AbstractAlgorithm<MinSpanningTreeProblem, UndirectedTree> {
    // TODO: make prim independent from network flow model
    // requires MinSpanningTreeProblem only uses graphs, but no network flow model
    // Super-sink specific handling must be done by caller then.

    @Override
    public UndirectedTree runAlgorithm(MinSpanningTreeProblem minspan) {
        NetworkFlowModel originNetwork = minspan.getNetworkFlowModel();
        Node supersink = minspan.getNetworkFlowModel().getSupersink();

        IdentifiableIntegerMapping<Edge> transitTimes = originNetwork.transitTimes();

        Node startNode = getStartNode(originNetwork.graph());

        IdentifiableIntegerMapping<Node> distances = new IdentifiableIntegerMapping<>(originNetwork.numberOfNodes());
        IdentifiableObjectMapping<Node, Edge> heapedges = new IdentifiableObjectMapping<>(originNetwork.numberOfEdges());
        MinHeap<Node, Integer> queue = new MinHeap<>(originNetwork.numberOfNodes());

        for (Node node : originNetwork) {
            if (node != supersink) {
                distances.add(node, Integer.MAX_VALUE);
                heapedges.set(node, null);
            }
        }

        distances.set(startNode, 0);
        queue.insert(startNode, 0);

        int edgeCount = 0;
        IdentifiableCollection<Edge> solEdges = new ListSequence<>();
        IdentifiableCollection<Node> solNodes = new ListSequence<>();
        while (!queue.isEmpty()) {
            MinHeap<Node, Integer>.Element min = queue.extractMin();
            Node v = min.getObject();
            solNodes.add(v);
            distances.set(v, Integer.MIN_VALUE);

            if (v != startNode) {
                Edge edge = new Edge(edgeCount++, heapedges.get(v).start(), heapedges.get(v).end());
                //only consider edges that are not incident to supersink
                if (heapedges.get(v).start() != supersink && heapedges.get(v).end() != supersink) {
                    solEdges.add(edge);
                }
            }
            IdentifiableCollection<Edge> incidentEdges = originNetwork.graph().incidentEdges(v);
            for (Edge edge : incidentEdges) {
                Node w = edge.opposite(v);
                if (distances.get(w) == Integer.MAX_VALUE) {
                    distances.set(w, transitTimes.get(edge));
                    heapedges.set(w, edge);
                    queue.insert(w, distances.get(w));
                } else {
                    if (transitTimes.get(edge) < distances.get(w)) {
                        distances.set(w, transitTimes.get(edge));
                        queue.decreasePriority(w, transitTimes.get(edge));
                        heapedges.set(w, edge);
                    }
                }

            }
        }

        IdentifiableCollection<Edge> addEdges = originNetwork.graph().incidentEdges(supersink);
        for (Edge edge : addEdges) {
            Edge supersinkedge = new Edge(edgeCount++, edge.start(), edge.end());
            solEdges.add(supersinkedge);
        }

        return new UndirectedTree(solEdges);

    }

    /**
     * Retruns a random node in the graph. Supersink is excluded.
     *
     * @param originGraph the input graph
     * @return a random node except the super sink
     */
    private Node getStartNode(Graph originGraph) {
        Random r = new Random();
        long seed = r.nextLong();
        r.setSeed(seed);
        int num = 0 + Math.abs(r.nextInt()) % originGraph.nodeCount();
        if (num != 0) {
            return originGraph.getNode(num);
        } else {
            return originGraph.getNode(num + 1);
        }
    }
}
