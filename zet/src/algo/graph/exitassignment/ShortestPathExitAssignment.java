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

package algo.graph.exitassignment;

import de.tu_berlin.math.coga.algorithm.shortestpath.Dijkstra;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.container.priority.MinHeap;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.container.mapping.IdentifiableObjectMapping;
import de.tu_berlin.coga.graph.DirectedGraph;

/**
 *
 * @author Martin Groß
 */
public class ShortestPathExitAssignment extends Algorithm<NetworkFlowModel, ExitAssignment> implements Assignable {

    @Override
    protected ExitAssignment runAlgorithm(NetworkFlowModel model) {
        ExitAssignment solution = new ExitAssignment(model.graph().nodes());
        DirectedGraph network = model.graph();
        IdentifiableCollection<Node> sinks = network.predecessorNodes(model.getSupersink());
        Dijkstra dijkstra = new Dijkstra(network, model.transitTimes(), null, true);
        IdentifiableObjectMapping<Node, MinHeap> exitDistances = new IdentifiableObjectMapping<>(network.nodes() );
        for (Node sink : sinks) {
            dijkstra.setSource(sink);
            dijkstra.run();
            for (Node source : model.getSources()) {
                if (!exitDistances.isDefinedFor(source)) {
                    exitDistances.set(source, new MinHeap());
                }
                exitDistances.get(source).insert(sink, dijkstra.getDistance(source));
            }
        }
        for (Node start : model.getSources()) {
            Node exit = (Node) exitDistances.get(start).extractMin().getObject();
            for (int i = 0; i < model.currentAssignment().get(start); i++) {
                solution.assignIndividualToExit(start, exit);
            }
        }
        return solution;
    }
		
	/**
	 * Returns the calculated exit assignment.
	 * @return the calculated exit assignment.
	 */
	public ExitAssignment getExitAssignment() {
		return getSolution();
	}
}
