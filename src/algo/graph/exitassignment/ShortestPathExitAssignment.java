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
import ds.graph.NetworkFlowModel;
import ds.graph.Node;
import sandbox.Algorithm;

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
