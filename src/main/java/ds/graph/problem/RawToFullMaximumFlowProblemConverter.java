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
package ds.graph.problem;

import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.graph.Edge;
import org.zetool.graph.DefaultDirectedGraph;
import org.zetool.netflow.classic.problems.MaximumFlowProblem;
import ds.graph.problem.RawMaximumFlowProblem;
import org.zetool.container.mapping.IdentifiableIntegerMapping;

/**
 * Converter that transforms memory-efficient raw maximum flow problems into more verbose maximum flow problems that are
 * based on full graphs.
 *
 * @author Martin Groß
 */
public class RawToFullMaximumFlowProblemConverter extends AbstractAlgorithm<RawMaximumFlowProblem, MaximumFlowProblem> {

  /**
   * {@InheritDoc}
   * @return the converted maximum flow problem
   */
  @Override
  protected MaximumFlowProblem runAlgorithm( RawMaximumFlowProblem problem ) {
    DefaultDirectedGraph network = new DefaultDirectedGraph( problem.getNumberOfNodes(), problem.getNumberOfEdges() );
    IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<>( problem.getNumberOfEdges() );
    for( int nodeIndex = 0; nodeIndex < problem.getNumberOfNodes(); nodeIndex++ ) {
      int nextStartIndex = ((nodeIndex + 1 < problem.getNumberOfNodes()) ? problem.getNodeStartIndices()[nodeIndex + 1] : problem.getNumberOfNodes());
      for( int edgeOfNodeIndex = problem.getNodeStartIndices()[nodeIndex]; edgeOfNodeIndex < nextStartIndex; edgeOfNodeIndex++ ) {
        Edge edge = network.createAndSetEdge( network.getNode( nodeIndex ), network.getNode( problem.getEdgeEndIDs()[edgeOfNodeIndex] ) );
        capacities.set( edge, problem.getEdgeCapacities()[edge.id()] );
      }
    }
    MaximumFlowProblem solution = new MaximumFlowProblem( network, capacities, network.getNode( problem.getSourceID() ), network.getNode( problem.getSinkID() ) );
    return solution;
  }
}
