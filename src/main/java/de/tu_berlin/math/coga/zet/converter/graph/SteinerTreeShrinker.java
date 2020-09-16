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

import de.zet_evakuierung.network.model.NetworkFlowModel;
import ds.graph.NodeRectangle;
import org.zetool.algorithm.steinertree.MinSteinerTreeProblem;
import org.zetool.algorithm.steinertree.SteinerTree;
import org.zetool.algorithm.steinertree.SteinerTreeSpanningTreeApproximationAlgorithm;
import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.collection.ListSequence;
import org.zetool.graph.DynamicNetwork;
import org.zetool.graph.Edge;
import org.zetool.graph.Graph;
import org.zetool.graph.Node;

/**
 * Reduces a {@link NetworkFlowModel network flow model} to a Steiner tree like graph connecting the sources and sinks.
 * The inner network of the results forms a Steiner tree. Additional edges connect the sink nodes with the super sink.
 *
 * @author Marlen Schwengfelder
 */
public class SteinerTreeShrinker extends AbstractAlgorithm<NetworkFlowModel, NetworkFlowModel> {

    @Override
    protected NetworkFlowModel runAlgorithm(NetworkFlowModel problem) {
        ZToGraphMapping originalMapping = problem.getZToGraphMapping();
        System.out.println("number of edges of original graph:" + problem.numberOfEdges());

        NetworkFlowModel.BasicBuilder steinerModel = NetworkFlowModel.BasicBuilder.fromNodes(problem);

        Node Super = steinerModel.getSupersink();
        ZToGraphMapping newMapping = steinerModel.getZToGraphMapping();

        newMapping.setNodeSpeedFactor(Super, 1);
        newMapping.setNodeRectangle(Super, new NodeRectangle(0, 0, 0, 0));
        newMapping.setFloorForNode(Super, -1);

        //using
        System.out.print("Compute Steiner... ");
        SteinerTree steinertree = computeSteiner(problem);
        IdentifiableCollection<Edge> MinEdges = steinertree.getEdges();
        // add edges to super sink
        for (Edge e : problem.graph().incidentEdges(problem.getSupersink())) {
            System.out.println("Adding supersink edge: " + e);
            MinEdges.add(e);
        }
        System.out.println("done");

        for (Node node : problem) {
            if (!node.equals(problem.getSupersink())) {
                steinerModel.setNodeCapacity(node, problem.getNodeCapacity(node));
                newMapping.setNodeSpeedFactor(node, originalMapping.getNodeSpeedFactor(node));
                newMapping.setNodeUpSpeedFactor(node, originalMapping.getUpNodeSpeedFactor(node));
                newMapping.setNodeDownSpeedFactor(node, originalMapping.getDownNodeSpeedFactor(node));
            }
        }

        for (Edge neu : MinEdges) {
            Edge newEdge = steinerModel.newEdge(neu.start(), neu.end());
            steinerModel.setEdgeCapacity(newEdge, problem.getEdgeCapacity(neu));
            steinerModel.setExactTransitTime(newEdge, problem.getTransitTime(neu));
            newMapping.setEdgeLevel(newEdge, originalMapping.getEdgeLevel(neu));
        }

        //values from mapping of original network
        newMapping.raster = originalMapping.getRaster();
        newMapping.nodeRectangles = originalMapping.getNodeRectangles();
        newMapping.nodeFloorMapping = originalMapping.getNodeFloorMapping();
        newMapping.isDeletedSourceNode = originalMapping.isDeletedSourceNode;
        newMapping.exitName = originalMapping.exitName;

        return steinerModel.build();
    }

    private SteinerTree computeSteiner(NetworkFlowModel networkFlowModel) {
        Graph modelGraph = networkFlowModel.graph();

        DynamicNetwork firstnet = new DynamicNetwork();

        IdentifiableCollection<Node> terminalNodes = new ListSequence();
        for (Node node : modelGraph.nodes()) {
            if (!node.equals(networkFlowModel.getSupersink())) {
                boolean isSource = networkFlowModel.getSources().contains(node);
                boolean isSink = networkFlowModel.getModelSinks().contains(node);
                firstnet.addNode(node);
                if (isSource || isSink) {
                    terminalNodes.add(node);
                }
            }
        }
        System.out.println("Number of terminal nodes: " + terminalNodes.size());
        for (Node node : modelGraph.nodes()) {
            for (Edge edge : modelGraph.incidentEdges(node)) {
                if (edge.start().id() < edge.end().id() && !edge.start().equals(networkFlowModel.getSupersink())) {
                    firstnet.addEdge(edge);
                }
            }
        }

        MinSteinerTreeProblem steinerProblem = new MinSteinerTreeProblem(modelGraph, networkFlowModel.transitTimes(), terminalNodes);
        SteinerTreeSpanningTreeApproximationAlgorithm steinerApproximation = new SteinerTreeSpanningTreeApproximationAlgorithm();
        steinerApproximation.setProblem(steinerProblem);
        return steinerApproximation.call();
    }

}
