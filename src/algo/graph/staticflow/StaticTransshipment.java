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
 * StaticTransshipment.java
 *
 */
package algo.graph.staticflow;

import algo.graph.staticflow.maxflow.DischargingGlobalGapHighestLabelPreflowPushAlgorithm;
import ds.graph.problem.MaximumFlowProblem;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.ResidualNetwork;
import java.util.LinkedList;
import java.util.List;

import algo.graph.staticflow.maxflow.PreflowPushAlgorithm;

/**
 *
 * @author Martin Groß
 */
public class StaticTransshipment implements Runnable {

    private IdentifiableIntegerMapping<Node> balances;
    private IdentifiableIntegerMapping<Edge> capacities;
    private Network network;
    private IdentifiableIntegerMapping<Edge> flow;
    private ResidualNetwork residualNetwork;
    private boolean feasible;
    private int valueOfFlow;
    private PreflowPushAlgorithm algorithm;

    public PreflowPushAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(PreflowPushAlgorithm algorithm) {
        this.algorithm = algorithm;
    }    

    public StaticTransshipment(Network network, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Node> balances) {
        this.balances = balances;
        this.capacities = capacities;
        this.network = network;
        algorithm = new DischargingGlobalGapHighestLabelPreflowPushAlgorithm();
    }

    protected int getBalanceOfNode(Node node) {
        int result = 0;
        for (Edge edge : network.outgoingEdges(node)) {
            result += residualNetwork.flow().get(edge);
        }
        for (Edge edge : network.incomingEdges(node)) {
            result -= residualNetwork.flow().get(edge);
        }
        return result;
    }

    public void run() {
        feasible = false;
        List<Node> sources = new LinkedList<Node>();
        List<Node> sinks = new LinkedList<Node>();
        for (Node node : network.nodes()) {
            if (balances.get(node) < 0) {
                sinks.add(node);
            }
            if (balances.get(node) > 0) {
                sources.add(node);
            }
        }
        network.setNodeCapacity(network.getNodeCapacity() + 2);
        Node supersource = network.getNode(network.getNodeCapacity() - 2);
        Node supersink = network.getNode(network.getNodeCapacity() - 1);
        network.setEdgeCapacity(network.getEdgeCapacity() + sources.size() + sinks.size());
        capacities.setDomainSize(network.getEdgeCapacity());
        for (Node source : sources) {
            Edge edge = network.createAndSetEdge(supersource, source);
            capacities.set(edge, balances.get(source));
        }
        for (Node sink : sinks) {
            Edge edge = network.createAndSetEdge(sink, supersink);
            capacities.set(edge, -balances.get(sink));
        }
        MaximumFlowProblem problem = new MaximumFlowProblem(network, capacities, supersource, supersink);
        algorithm.setProblem(problem);
        algorithm.run();
        flow = algorithm.getSolution();
        valueOfFlow = algorithm.getSolution().getFlowValue();
        residualNetwork = algorithm.getResidualNetwork();
        network.setNodeCapacity(network.getNodeCapacity() - 2);
        network.setEdgeCapacity(network.getEdgeCapacity() - sources.size() - sinks.size());
        capacities.setDomainSize(network.getEdgeCapacity());
        flow.setDomainSize(network.getEdgeCapacity());
        residualNetwork = new ResidualNetwork(network, capacities);
        for (Edge edge : network.edges()) {
            residualNetwork.augmentFlow(edge, flow.get(edge));
        }
        feasible = true;
        for (Node node : network.nodes()) {
            if (balances.get(node) != getBalanceOfNode(node)) {
                feasible = false;
            }
        }
    }

    public IdentifiableIntegerMapping<Edge> getFlow() {
        if (feasible) {
            return flow;
        } else {
            return null;
        }
    }

    public IdentifiableIntegerMapping<Edge> getFlowEvenIfNotFeasible() {
        return flow;
    }

    public int getFlowValueEvenIfInfeasibleFlow() {
        return valueOfFlow;
    }

    public ResidualNetwork getResidualNetwork() {
        if (feasible) {
            return residualNetwork;
        } else {
            return null;
        }
    }
}
