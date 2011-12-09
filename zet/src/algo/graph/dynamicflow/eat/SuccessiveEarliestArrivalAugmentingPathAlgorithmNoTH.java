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
package algo.graph.dynamicflow.eat;

import algo.graph.dynamicflow.*;
import ds.graph.Edge;
import ds.mapping.IdentifiableIntegerMapping;
import ds.graph.network.AbstractNetwork;
import ds.graph.Node;


/**
 * This class calculates an earliest arrival transshipment
 * by using a successive earliest arrival augmenting path algorithm.
 * The optimal time horizon is found as specified in {@code Transshipment}. 
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH extends
		Transshipment<SuccessiveEarliestArrivalAugmentingPathAlgorithmTH> {

	public SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH(AbstractNetwork network,
			IdentifiableIntegerMapping<Edge> transitTimes,
			IdentifiableIntegerMapping<Edge> edgeCapacities,
			IdentifiableIntegerMapping<Node> nodeCapacities,
			IdentifiableIntegerMapping<Node> supplies) {
		super(network, transitTimes, edgeCapacities, nodeCapacities, supplies, SuccessiveEarliestArrivalAugmentingPathAlgorithmTH.class, null);
	}
	
}
