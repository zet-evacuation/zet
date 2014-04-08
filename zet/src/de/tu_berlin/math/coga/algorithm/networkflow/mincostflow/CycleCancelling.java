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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

/*
 * CycleCancelling.java
 *
 */
package de.tu_berlin.math.coga.algorithm.networkflow.mincostflow;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import algo.graph.staticflow.StaticTransshipment;
import ds.graph.Edge;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import ds.graph.network.AbstractNetwork;
import ds.graph.network.ResidualNetwork;
import ds.graph.StaticPath;
import ds.graph.flow.Flow;
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
     * {@code null} if there is no feasible flow.
     */
    @Override
    protected Flow runAlgorithm(MinimumCostFlowProblem problem) {
        StaticTransshipment algorithm = new StaticTransshipment(problem.getNetwork(), problem.getCapacities(), problem.getBalances());
        algorithm.run();
        ResidualNetwork residualNetwork = algorithm.getResidualNetwork();
        if (residualNetwork == null) {
            fireEvent("The instance has no feasible solution.");
            return null;
        } else {
            IdentifiableIntegerMapping<Edge> residualCosts = residualNetwork.expandCostFunction(problem.getCosts());
            StaticPath cycle = findCycle(residualNetwork, residualCosts);
            while (cycle != null) {
                int minimumCycleCapacity = residualNetwork.residualCapacities().minimum(cycle);
                for (Edge edge : cycle) {
                    residualNetwork.augmentFlow(edge, minimumCycleCapacity);
                }
                cycle = findCycle(residualNetwork, residualCosts);
            }
            return new Flow(residualNetwork.flow());
        }
    }

    /**
     * Obtain a cycle with negative total cost if such a cycle exists.
     * @return a cycle of negative total cost or {@code null} if no such 
     * cycle exists.
     */
    protected abstract StaticPath findCycle(AbstractNetwork network, IdentifiableIntegerMapping<Edge> costs);
}
