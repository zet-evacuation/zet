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
package ds.graph;

import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;

/**
 *
 * @author Marlen Schwengfelder
 */
public class MinSteinerTree {

    private IdentifiableIntegerMapping<Edge> shortestdist;
    private IdentifiableCollection<Edge> Edges;
    private IdentifiableCollection<Node> nodes;
    private int overalldist;

    public MinSteinerTree(IdentifiableIntegerMapping<Edge> shortestdist, IdentifiableCollection<Edge> Edges, IdentifiableCollection<Node> nodes, int overalldist) {
        this.shortestdist = shortestdist;
        this.Edges = Edges;
        this.nodes = nodes;
        this.overalldist = overalldist;
    }

    public MinSteinerTree() {

    }

    public IdentifiableCollection<Edge> getEdges() {
        return Edges;
    }

    public IdentifiableCollection<Node> getNodes() {
        return nodes;
    }

    public IdentifiableIntegerMapping<Edge> getdist() {
        return shortestdist;
    }

}
