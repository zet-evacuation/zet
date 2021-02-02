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
package de.zet_evakuierung.visualization.network.model;

import de.tu_berlin.math.coga.zet.viewer.NashFlowEdgeData;
import de.tu_berlin.math.coga.zet.viewer.NashFlowVisualization;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.graph.visualization.NodePositionMapping;
import org.zetool.math.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLNashNodeModel extends GLSimpleNodeModel {
	private final IdentifiableObjectMapping<Edge, NashFlowEdgeData> nashFlowMapping;
	private final NashFlowVisualization nfv;

	public GLNashNodeModel( DirectedGraph graph, Node node, NodePositionMapping<Vector3> nodePositionMapping, IdentifiableObjectMapping<Edge, NashFlowEdgeData> nashFlowMapping, NashFlowVisualization nfv ) {
		super( graph, node, nodePositionMapping );
		this.nashFlowMapping = nashFlowMapping;
		this.nfv = nfv;
	}

}
