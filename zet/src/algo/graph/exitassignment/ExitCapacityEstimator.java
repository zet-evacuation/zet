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

/*
 * ExitCapacityEstimator.java
 */
package algo.graph.exitassignment;

import de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.PushRelabelHighestLabelGlobalGapRelabelling;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.network.AbstractNetwork;
import ds.graph.problem.MaximumFlowProblem;
import ds.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author Joscha
 */
public class ExitCapacityEstimator {

	public ExitCapacityEstimator() {
	}

	public int estimateCapacityByMaximumFlow( NetworkFlowModel model, Node sink ) {
		IdentifiableCollection<Node> sinks = model.graph().predecessorNodes( model.getSupersink() );
		IdentifiableIntegerMapping<Edge> newCapacities = new IdentifiableIntegerMapping<Edge>( model.edgeCapacities() );
		for( Node s : sinks )
			for( Edge edge : model.graph().outgoingEdges( s ) )
				//if (sinks.contains(edge.start())) {
				newCapacities.set( edge, 0 );
		MaximumFlowProblem problem = new MaximumFlowProblem( (AbstractNetwork)model.graph(), newCapacities, model.getSources(), sink );
		Algorithm<MaximumFlowProblem, MaximumFlow> algorithm = new PushRelabelHighestLabelGlobalGapRelabelling();
		algorithm.setProblem( problem );
		algorithm.run();
		return algorithm.getSolution().getFlowValue();
	}

	public int estimateCapacityByIncomingEdges( NetworkFlowModel model, Node sink ) {
		IdentifiableCollection<Node> sinks = model.graph().predecessorNodes( model.getSupersink() );
		int result = 0;
		for( Edge edge : model.graph().incomingEdges( sink ) ) {
			if( sinks.contains( edge.start() ) )
				continue;
			result += model.getEdgeCapacity( edge );
		}
		return result;
	}
}
