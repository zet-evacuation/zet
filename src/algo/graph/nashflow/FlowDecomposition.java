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
package algo.graph.nashflow;

import ds.graph.Edge;
import java.util.HashSet;
import java.util.HashMap;
import ds.graph.Node;
import de.tu_berlin.coga.container.mapping.IdentifiableDoubleMapping;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Sebastian Schenker
 */
public class FlowDecomposition {

	private final static double EPSILON = 0.00001;
	private IdentifiableDoubleMapping<Edge> flowValues;
	private Node source;
	private Node sink;
	private double initRate;
	private HashMap<Node, List<Edge>> nodeToOutGoingEdges;
	private List<List<Edge>> paths;
	private HashMap<List<Edge>, Double> pathFlowSizes;

	public FlowDecomposition( List<Node> nodes, HashSet<Edge> usedEdges, HashMap<Edge, Edge> newEdgeToOrigEdge, IdentifiableDoubleMapping<Edge> flows, Node sourceNode, Node sinkNode, double initRate ) {
		source = sourceNode;
		sink = sinkNode;
		this.initRate = initRate;
		nodeToOutGoingEdges = new HashMap<Node, List<Edge>>( nodes.size() );
		for( Node n : nodes ) {
			nodeToOutGoingEdges.put( n, new ArrayList<Edge>() );
		}
		flowValues = new IdentifiableDoubleMapping<Edge>( usedEdges.size() );
		double flowvalue;
		for( Edge edge : usedEdges ) {
			flowvalue = flows.get( edge );
			if( newEdgeToOrigEdge.containsKey( edge ) ) {
				edge = newEdgeToOrigEdge.get( edge );
			}
			if( flowvalue > EPSILON ) {
				nodeToOutGoingEdges.get( edge.start() ).add( edge );
				flowValues.set( edge, flowvalue );
			}
		}
		paths = new ArrayList<List<Edge>>();
		pathFlowSizes = new HashMap<List<Edge>, Double>();
	}

	private void addUsedPath( List<Edge> usedPath ) {
		paths.add( usedPath );
	}

	private void addPathFlowSize( List<Edge> usedPath, double flowvalue ) {
		pathFlowSizes.put( usedPath, flowvalue );
	}

	public List<List<Edge>> getPaths() {
		return paths;
	}

	public HashMap<List<Edge>, Double> getPathFlowSizes() {
		return pathFlowSizes;
	}

	private void updateFlowValues( List<Edge> usedEdges, double flowsize ) {
		for( Edge e : usedEdges ) {
			flowValues.decrease( e, flowsize );
			if( flowValues.get( e ) < EPSILON ) {
				nodeToOutGoingEdges.get( e.start() ).remove( e );
			}
		}
	}

	public void computePathAndFlowSizes() {
		double minflowvalue;
		Edge minEdge, currentEdge;
		Node currentNode;

		while( !nodeToOutGoingEdges.get( source ).isEmpty() ) {

			currentEdge = nodeToOutGoingEdges.get( source ).get( 0 );
			minEdge = currentEdge;

			minflowvalue = flowValues.get( currentEdge );
			currentNode = currentEdge.end();

			List<Edge> usedEdges = new ArrayList<Edge>();
			usedEdges.add( currentEdge );

			while( currentNode != sink ) {

				currentEdge = nodeToOutGoingEdges.get( currentNode ).get( 0 );
				usedEdges.add( currentEdge );
				if( flowValues.get( currentEdge ) < minflowvalue ) {
					minflowvalue = flowValues.get( currentEdge );
					minEdge = currentEdge;
				}
				currentNode = currentEdge.end();
			}

			addUsedPath( usedEdges );
			addPathFlowSize( usedEdges, minflowvalue / initRate );
			updateFlowValues( usedEdges, minflowvalue );

		}
	}

}
