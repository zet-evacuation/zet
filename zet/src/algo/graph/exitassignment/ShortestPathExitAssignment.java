/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * ShortestPathExitAssignment.java
 *
 */

package algo.graph.exitassignment;

import algo.graph.shortestpath.Dijkstra;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.MinHeap;
import ds.graph.Network;
import ds.NetworkFlowModel;
import ds.graph.Node;
import algo.graph.Algorithm;

/**
 *
 * @author Martin Groß
 */
public class ShortestPathExitAssignment extends Algorithm<NetworkFlowModel, ExitAssignment> implements Assignable {

    @Override
    protected ExitAssignment runAlgorithm(NetworkFlowModel model) {
        ExitAssignment solution = new ExitAssignment(model.getNetwork().nodes());
        Network network = model.getNetwork();
        IdentifiableCollection<Node> sinks = network.predecessorNodes(model.getSupersink());
        Dijkstra dijkstra = new Dijkstra(network, model.getTransitTimes(), null, true);
        IdentifiableObjectMapping<Node, MinHeap> exitDistances = new IdentifiableObjectMapping<Node, MinHeap>(network.nodes(), MinHeap.class);
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
            for (int i = 0; i < model.getCurrentAssignment().get(start); i++) {
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
