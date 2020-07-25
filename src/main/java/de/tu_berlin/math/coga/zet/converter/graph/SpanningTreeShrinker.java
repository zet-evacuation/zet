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

import algo.graph.spanningtree.Prim;
import org.zetool.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;

import java.util.logging.Level;

import org.zetool.algorithm.spanningtree.UndirectedForest;

/**
 *
 * @author Marlen Schwengfelder
 */
public class SpanningTreeShrinker extends GraphShrinker {

    /**
     * Initializes the instance for beeing solved.
     */
    public SpanningTreeShrinker() {
        super(true);
    }

    /**
     * Shrinks the graph in the {@link NetworkFlowModel} by creating a spanning tree. The returned edges are exactly the
     * edges in the minimum spanning tree on the nodes of the graph. The transit times on the arcs are used as spanning
     * tree weights.
     *
     * @see GraphShrinker.runEdge()
     * @return edges in a minimum spanning tree on the problem instance graph
     */
    @Override
    IdentifiableCollection<Edge> runEdge() {

        //using Prims algorithm
        Prim primalgo = new Prim();
        primalgo.setProblem(getProblem());
        log.info("Compute minimum spanning tree using Prim... ");
        primalgo.run();
        log.log(Level.INFO, "done in {0}", primalgo.getRuntimeAsString());
        UndirectedForest minspantree = primalgo.getSolution();
        return minspantree.getEdges();
    }
}
