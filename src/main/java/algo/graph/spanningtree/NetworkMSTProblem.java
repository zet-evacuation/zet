/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
package algo.graph.spanningtree;

import org.zetool.graph.Edge;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DynamicNetwork;

/**
 *
 * @author Marlen Schwengfelder
 */
public class NetworkMSTProblem {
    
    private DynamicNetwork graph;
    private IdentifiableIntegerMapping<Edge> distances;
    
    public NetworkMSTProblem(DynamicNetwork graph,IdentifiableIntegerMapping<Edge> distances)
    {
        this.graph = graph;
        this.distances = distances;
    }
    
    public DynamicNetwork getGraph()
    {
        return graph;
    }
    public IdentifiableIntegerMapping<Edge> getDistances()
    {
        return distances;
    }
    
}
