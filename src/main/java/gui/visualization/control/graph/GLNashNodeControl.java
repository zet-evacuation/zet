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
package gui.visualization.control.graph;

import de.tu_berlin.math.coga.zet.viewer.NashFlowEdgeData;
import de.tu_berlin.math.coga.zet.viewer.NashFlowVisualization;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import org.zetool.graph.Edge;
import org.zetool.graph.Graph;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Node;
import gui.visualization.draw.graph.GLNashNode;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLNashNodeControl extends GLSimpleNodeControl {
	private final IdentifiableObjectMapping<Edge, NashFlowEdgeData> nashFlowMapping;
	private final NashFlowVisualization nfv;

	public GLNashNodeControl( DirectedGraph graph, Node node, NodePositionMapping nodePositionMapping, IdentifiableObjectMapping<Edge, NashFlowEdgeData> nashFlowMapping, NashFlowVisualization nfv ) {
		super( graph, node, nodePositionMapping, false );
		this.nashFlowMapping = nashFlowMapping;
		this.nfv = nfv;
		setUpEdges();
	}

	@Override
	protected void setUpEdges() {
		// add outgoing edges as children
		for( Edge edge : graph.outgoingEdges( node ) ) {
			GLEdgeControl edgeControl = new GLNashFlowEdgeControl( nodePositionMapping, edge, nashFlowMapping.get( edge ), nfv );
			add( edgeControl );
		}

		setView( new GLNashNode( this ) );
		for( GLEdgeControl edgeControl : this ) {
			view.addChild( edgeControl.getView() );
		}
	}

}
