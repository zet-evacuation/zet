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
package algo.graph.dynamicflow.eat;

import algo.graph.dynamicflow.*;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.TimeExpandedNetwork;
import java.util.LinkedList;
import java.util.List;

/**
 * This class calculates an earliest arrival transshipment for a given time horizon
 * by using a successive earliest arrival augmenting path algorithm. 
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithmTH extends
		TransshipmentWithTimeHorizon {
	
	/**
	 * Creates a new <code>SuccessiveEarliestArrivalAugmentingPathAlgorithmTH</code> object
	 * with the given parameters. The method <code>runAlgorithm()</code> tests a time horizon and 
	 * finds an earliest arrival transshipment for the time horizon, if possible.
	 * @param network The original network.
	 * @param transitTimes The transit times of all edges in the network.
	 * @param edgeCapacities The capacities of all edges in the network.
	 * @param supplies The supplies of all nodes in the network.
	 * @param timeHorizon The time horizon to test.
	 */
	public SuccessiveEarliestArrivalAugmentingPathAlgorithmTH(Network network, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Node> supplies, Integer timeHorizon){
		super(network, transitTimes, edgeCapacities, nodeCapacities, supplies, timeHorizon,"Successive Earliest Arrival Augmenting Path Algorithm TH");
	}
	
	/**
	 * 
	 */
	@Override
	public void runAlgorithm(){            
            List<Node> sources = new LinkedList<Node>();
            Node supersink = null;
            int total = 0;
            for (Node node : network.nodes()) {
                if (supplies.get(node) > 0) {
                    sources.add(node);
                    total += supplies.get(node);
                }
                if (supplies.get(node) < 0) supersink = node;
            }
            EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(edgeCapacities, network, nodeCapacities, supersink, sources, timeHorizon.intValue(), transitTimes, supplies);
            SuccessiveEarliestArrivalAugmentingPathAlgorithm algo = new SuccessiveEarliestArrivalAugmentingPathAlgorithm();
            algo.setProblem(problem);
            algo.run();
            if (algo.getSolution().getFlowAmount() == total) {
                resultFlowPathBased = algo.getSolution().getPathBased();
            } else {
                System.out.println(algo.getSolution().getFlowAmount() + " vs. " + total);
            }     
	}

	/**
	 * As we do not use the original <code>runAlgorithm()</code> method, <code>transshipmentWithTimeHorizon</code>
	 * is never called. Thus it's only a stub.
	 */
	@Override
	protected IdentifiableIntegerMapping<Edge> transshipmentWithTimeHorizon(TimeExpandedNetwork network) {
		return null;
	}
	
}
