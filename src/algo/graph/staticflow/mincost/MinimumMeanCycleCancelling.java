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
package algo.graph.staticflow.mincost;

import algo.graph.Algorithm;
import algo.graph.DebugFlags;
import algo.graph.staticflow.StaticTransshipment;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Localization;
import ds.graph.Mappings;
import ds.graph.Network;
import ds.graph.ResidualNetwork;
import ds.graph.StaticPath;
import ds.graph.problem.MinimumCostFlowProblem;
import tasks.AlgorithmTask;

/**
 * The class <code>MinimumMeanCycleCancelling</code> 
 * implments the Algorithms with the same name from
 * "Combinatorial Optimization" by "Bernhard Korte, Jens Vygen"
 * The Algorithms can be called either synchronously using 
 * the static compute method, or asynchronously using the 
 * runnable interface
 */
public class MinimumMeanCycleCancelling extends Algorithm<MinimumCostFlowProblem, IdentifiableIntegerMapping<Edge>> {

    /**
     * Creates a new instance of the minimum mean cycle cancelling algorithm.
     */
    public MinimumMeanCycleCancelling() {
    }

   @Override
    protected IdentifiableIntegerMapping<Edge> runAlgorithm(MinimumCostFlowProblem problem) {
        //Do we have everything we need?
        if (problem.getNetwork() == null) {
            throw new NullPointerException(Localization.getInstance().getString("algo.graph.staticflow.mincost.NetworkNullException"));
        }
        if (problem.getCapacities() == null) {
            throw new NullPointerException(Localization.getInstance().getString("algo.graph.staticflow.mincost.CapacitiesNullException"));
        }
        if (problem.getCosts() == null) {
            throw new NullPointerException(Localization.getInstance().getString("algo.graph.staticflow.mincost.CostsNullException"));
        }
        if (problem.getBalances() == null) {
            throw new NullPointerException(Localization.getInstance().getString("algo.graph.staticflow.mincost.SuppliesNullException"));
        }

        // 1. Find a b-flow.               
        StaticTransshipment trans = new StaticTransshipment(problem.getNetwork(), problem.getCapacities(), problem.getBalances());
        trans.run();

        //get the residual network
        ResidualNetwork residualNetwork = trans.getResidualNetwork();
        if (residualNetwork == null) {
            //Invalid instance. -> no feasible solution
            //Die gracefully.
            AlgorithmTask.getInstance().publish("Minimum mean cycle cancelling algorithm", "WARNING: no transshipments were found!");
            return null;
        }

        IdentifiableIntegerMapping<Edge> residualCosts = m_expandCostFunction(problem.getCosts(), problem.getNetwork(), residualNetwork);

        //Repeate this loop as long as we still have MMC to augment
        while (true) {
            //Debugging Code
            if (DebugFlags.MEL_LONG) {
                System.out.print("Residual network:" + residualNetwork);
                System.out.println("Costs: " + Mappings.toString(residualNetwork.edges(), residualCosts));
            }
            if (DebugFlags.MEL && !DebugFlags.MEL_LONG) {
                System.out.println("The residual network has " +
                        residualNetwork.nodes().size() + " nodes and " +
                        residualNetwork.edges().size() + " edges.");
            }
            if (DebugFlags.MEL) {
                System.out.println("Calling Minimum Mean Cycle Detector.");
            }
            ////////////////////////////////////////////////////////////////////

            // 2. Find a minimal circuit
            StaticPath cycle = MinimumMeanCycleDetector.detect(residualNetwork, residualCosts);
            if (cycle == null) {
                break;
            }            
            int cycleCost = residualCosts.sum(cycle);
            if (cycleCost >= 0) {
                if (DebugFlags.MEL_LONG) {
                    System.out.println("Impossible to augment furter - Optimal Flow!\n");
                }
                break;
            }
            int minimumCycleCapacity = residualNetwork.residualCapacities().minimum(cycle);
            if (DebugFlags.MEL_LONG) {
                System.out.println("Augmenting along " + cycle + " by " + minimumCycleCapacity + ".\n");
            }
            for (Edge edge : cycle) {
                residualNetwork.augmentFlow(edge, minimumCycleCapacity);
            }
        }
        return (IdentifiableIntegerMapping<Edge>) residualNetwork.flow().clone();
    }    
    
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
