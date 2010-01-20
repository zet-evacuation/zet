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

/**
 * The class <code>EATransshipment</code> solves two variants
 * of the earliest arrival transshipment problem: with or
 * without a given time horizon.
 * The implementation is done with time-expanded networks.
 * For the variant without a time horizon, binary search
 * is used. 
 */
public class EATransshipmentMinCost extends Transshipment<EATransshipmentWithTHMinCost> {

    public EATransshipmentMinCost(Network network, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Node> supplies) {
        super(network, transitTimes, capacities, null, supplies, DynamicTransshipment.class, EATransshipmentWithTHMinCost.class);
    }
}
