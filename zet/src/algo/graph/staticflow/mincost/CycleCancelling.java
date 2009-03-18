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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

/*
 * CycleCancelling.java
 *
 */
package algo.graph.staticflow.mincost;

import algo.graph.Algorithm;
import algo.graph.staticflow.StaticTransshipment;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.ResidualNetwork;
import ds.graph.StaticPath;
import ds.graph.problem.MinimumCostFlowProblem;

/**
 * A framework for cycle cancelling algorithms to solve minimum cost flow 
 * problems.
 * @author Martin Groß
 */
public abstract class CycleCancelling extends Algorithm<MinimumCostFlowProblem, IdentifiableIntegerMapping<Edge>> {

    /**
     * Solves the given minimum cost flow problem by cycle cancelling. A 
     * feasible solution is computed using a static transshipment and optimized
     * afterwards by augmenting cycles of negative cost.
     * @param problem the minium cost flow problem.
     * @return a minimum cost flow for the specified problem or 
     * <code>null</code> if there is no feasible flow.
     */
    @Override
    protected IdentifiableIntegerMapping<Edge> runAlgorithm(MinimumCostFlowProblem problem) {
        StaticTransshipment algorithm = new StaticTransshipment(problem.getNetwork(), problem.getCapacities(), problem.getBalances());
        algorithm.run();
        ResidualNetwork residualNetwork = algorithm.getResidualNetwork();
        if (residualNetwork == null) {
            fireEvent("The instance has no feasible solution.");
            return null;
        } else {
            IdentifiableIntegerMapping<Edge> residualCosts = m_expandCostFunction(problem.getCosts(), problem.getNetwork(), residualNetwork);
            StaticPath cycle = findCycle(residualNetwork, residualCosts);
            while (cycle != null) {
                int minimumCycleCapacity = residualNetwork.residualCapacities().minimum(cycle);
                for (Edge edge : cycle) {
                    residualNetwork.augmentFlow(edge, minimumCycleCapacity);
                }
                cycle = findCycle(residualNetwork, residualCosts);
            }
            return residualNetwork.flow();
        }
    }

    /**
     * Obtain a cycle with negative total cost if such a cycle exists.
     * @return a cycle of negative total cost or <code>null</code> if no such 
     * cycle exists.
     */
    protected abstract StaticPath findCycle(Network network, IdentifiableIntegerMapping<Edge> costs);

    /**
     * This method expand the given cost function over some network to cover 
     * also the residual network
     * @param oldCosts The old cost function to be expanded
     * @param sNetwork The network.
     * @param resNetwork The residual network
     * @return an new costs function that is identical with the old function
     * on the old domain. On all other edges in the residual network it returns
     * either the ngated cost of the oposite edge if it exists or 0.
     */
    public IdentifiableIntegerMapping<Edge> m_expandCostFunction(IdentifiableIntegerMapping<Edge> oldCosts, Network sNetwork, ResidualNetwork resNetwork) {
        resNetwork = resNetwork.clone();
        resNetwork.showAllEdges();
        IdentifiableIntegerMapping<Edge> newCosts = oldCosts.clone();

        if (newCosts.getDomainSize() < resNetwork.getEdgeCapacity()) {
            newCosts.setDomainSize(resNetwork.getEdgeCapacity());
        }

        for (Edge edge : resNetwork.edges()) {
            if (edge != null) {
                if (sNetwork.contains(edge)) {
                    newCosts.set(edge, oldCosts.get(edge));
                } else {
                    Edge opEdge = sNetwork.getEdge(edge.end(), edge.start());
                    if (opEdge != null) {
                        newCosts.set(edge, -oldCosts.get(opEdge));
                    } else {
                        newCosts.set(edge, 0);
                    }
                }
            }
        }
        return newCosts;
    }
}
