/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
package ds;

import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.flow.EdgeBasedFlowOverTime;
import algo.graph.Flags;
import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.util.PathComposition;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphMapping;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import opengl.framework.abs.VisualizationResult;
import java.util.ArrayList;

/**
 * The class <code>GraphVisualizationResults</code> contains all information necessary to visualize the result of a dynamic flow algorithm.
 * Therefore the network itself is included, as well as the result flow and a mapping giving the rectangle in the real world that each node
 * is covering. Also the floor that each node belongs to is saved. The floors have indices according to their position in the list of
 * floors in the z-format.  
 */
public class GraphVisualizationResults implements VisualizationResult {

	/** The structure of the network the algorithm was applied to. */
	private Network network;
	/** The capacities of all edges in the network. */
	private IdentifiableIntegerMapping<Edge> edgeCapacities;
	/** The capacities of all nodes in the network. */
	private IdentifiableIntegerMapping<Node> nodeCapacities;
	/** The transit times of all edges in the network. */
	private IdentifiableIntegerMapping<Edge> transitTimes;
	/** The supplies of all nodes in the network. */
	private IdentifiableIntegerMapping<Node> supplies;
	/** The resulting flow. */
	private PathBasedFlowOverTime dynamicFlow = null;
	/** A mapping saving a rectangle in the real world for each node. */
	private IdentifiableObjectMapping<Node, NodeRectangle> nodeRectangles;
	/** A mapping giving the number of the floor (this node lies in) in the list in the z-format. */
	private IdentifiableIntegerMapping<Node> nodeToFloorMapping;
	/** A mapping telling whether each node is an evacuation node. */
	private IdentifiableObjectMapping<Node, Boolean> isEvacuationNode;
	/** A mapping telling whether each node is a source node. */
	private IdentifiableObjectMapping<Node, Boolean> isSourceNode;
	/** A mapping telling whether each node has been a source node that has been deleted. */
	private IdentifiableObjectMapping<Node, Boolean> isDeletedSourceNode;
	/** */
	private ArrayList<ArrayList<Node>> floorToNodeMapping;
	/** The composed flow over time */
	private EdgeBasedFlowOverTime flowOverTime;
	/** The maximal flow rate in the calculated flow. */
	private int maxFlowRate;
	/** The super sink. */
	private Node supersink;
	/** An lower bound to the time horizon. If negative, no value is known yet. */
	private int neededTimeHorizon = -1;
	/** The node position mapping. */
	private NodePositionMapping nodePositionMapping = null;

	/**
	 * Create a new <code>GraphVisualization</code> object from a network flow model and a dynamic flow.
	 * @param networkFlowModel A network flow model containing the graph and the <code>ZToGraphMapping</code>.
	 * @param dynamicFlow The result flow that shall be visualized.
	 */
	public GraphVisualizationResults( NetworkFlowModel networkFlowModel, PathBasedFlowOverTime dynamicFlow ) {
		this( networkFlowModel );
		this.dynamicFlow = dynamicFlow;
		PathComposition pathComposition = new PathComposition( network, transitTimes, dynamicFlow );
		pathComposition.run();
		this.flowOverTime = pathComposition.getEdgeFlows();
		maxFlowRate = pathComposition.getMaxFlowRate();
		if( Flags.FLOWWRONG ) {
			System.out.println( "Eingabe in die PathComposition:" );
			System.out.println( "Netzwerk:\n" + network + "\n" + "Fahrzeiten:\n" + transitTimes + "\n" + "Fluss:\n" + dynamicFlow );
			System.out.println( "Ausgabe der PathComposition:" );
			System.out.println( flowOverTime );
		}
		this.dynamicFlow = null;
	}

	public GraphVisualizationResults( NetworkFlowModel networkFlowModel ) {
		this.network = networkFlowModel.getNetwork();
		ZToGraphMapping mapping = networkFlowModel.getZToGraphMapping();
		this.nodeRectangles = mapping.getNodeRectangles();
		this.nodeToFloorMapping = mapping.getNodeFloorMapping();
		this.isEvacuationNode = mapping.getIsEvacuationNode();
		this.isSourceNode = mapping.getIsSourceNode();
		this.isDeletedSourceNode = mapping.getIsDeletedSourceNode();
		this.nodeCapacities = networkFlowModel.getNodeCapacities();
		this.edgeCapacities = networkFlowModel.getEdgeCapacities();
		this.transitTimes = networkFlowModel.getTransitTimes();
		this.supplies = networkFlowModel.getCurrentAssignment();
		this.supersink = networkFlowModel.getSupersink();

		this.floorToNodeMapping = new ArrayList<ArrayList<Node>>();
		for( Node node : network.nodes() ) {
			int floor = this.nodeToFloorMapping.get( node );

			if( floor != -1 ) {
				while(this.floorToNodeMapping.size() < floor)
					this.floorToNodeMapping.add( new ArrayList<Node>() );
				if( this.floorToNodeMapping.size() <= floor )
					this.floorToNodeMapping.add( floor, new ArrayList<Node>() );
				this.floorToNodeMapping.get( floor ).add( node );
			}
		}
		maxFlowRate = 0;
		//dynamicFlow = new PathBasedFlowOverTime();
		flowOverTime = new EdgeBasedFlowOverTime( network );
		setUpNodeCoordinates();
	}

	public GraphVisualizationResults( NetworkFlowModel networkFlowModel, EdgeBasedFlowOverTime flowOverTime ) {
		this( networkFlowModel );
		this.maxFlowRate = 0;
		for( Edge edge : networkFlowModel.getNetwork().edges() )
			if( flowOverTime.get( edge ).getMaximumValue() > maxFlowRate )
				maxFlowRate = flowOverTime.get( edge ).getMaximumValue();
		this.flowOverTime = flowOverTime;
		this.dynamicFlow = null;
	}

	public GraphVisualizationResults( EarliestArrivalFlowProblem eatf, IdentifiableIntegerMapping<Node> xPos, IdentifiableIntegerMapping<Node> yPos ) {
		// change this to something with NodePositionMapping!
		this.network = eatf.getNetwork();

		nodePositionMapping = new NodePositionMapping( network.numberOfNodes() );

		int nodeCount = eatf.getNetwork().numberOfNodes();
		this.nodeRectangles = new IdentifiableObjectMapping<Node, NodeRectangle>( nodeCount, NodeRectangle.class );
		for( Node node : eatf.getNetwork().nodes() ) {
			int x = xPos.get( node );
			int y = yPos.get( node );
			nodePositionMapping.set( node, new Vector3( x, y, 0 ) );
			NodeRectangle nodeRectangle = new NodeRectangle( x, y, x, y );
			nodeRectangles.set( node, nodeRectangle );
		}

		supersink = eatf.getSink();

		nodeToFloorMapping = new IdentifiableIntegerMapping<Node>( nodeCount );
		isSourceNode = new IdentifiableObjectMapping<Node, Boolean>( nodeCount, Boolean.class );
		isEvacuationNode = new IdentifiableObjectMapping<Node, Boolean>( nodeCount, Boolean.class );
		isDeletedSourceNode = new IdentifiableObjectMapping<Node, Boolean>( nodeCount, Boolean.class );
		for( Node node : eatf.getNetwork().nodes() ) {
			nodeToFloorMapping.set( node, 1 );
			isDeletedSourceNode.set( node, false );
			if( eatf.getSources().contains( node ) )
				isSourceNode.set( node, true );
			else
				isSourceNode.set( node, false );
			isEvacuationNode.set( node, false );
		}

		for( Edge edge : eatf.getNetwork().edges() )
			if( edge.end().equals( supersink ) )
				isEvacuationNode.set( edge.start(), true );

		this.nodeCapacities = eatf.getNodeCapacities();
		this.edgeCapacities = eatf.getEdgeCapacities();
		this.transitTimes = eatf.getTransitTimes();
		this.supplies = eatf.getSupplies();

		this.floorToNodeMapping = new ArrayList<ArrayList<Node>>();
		for( Node node : network.nodes() ) {
			int floor = this.nodeToFloorMapping.get( node );

			if( floor != -1 ) {
				while(this.floorToNodeMapping.size() < floor)
					this.floorToNodeMapping.add( new ArrayList<Node>() );
				if( this.floorToNodeMapping.size() <= floor )
					this.floorToNodeMapping.add( floor, new ArrayList<Node>() );
				this.floorToNodeMapping.get( floor ).add( node );
			}
		}

		maxFlowRate = 0;
		//dynamicFlow = new PathBasedFlowOverTime();
		flowOverTime = new EdgeBasedFlowOverTime( network );
	}

	public GraphVisualizationResults( EarliestArrivalFlowProblem eatf, IdentifiableIntegerMapping<Node> xPos, IdentifiableIntegerMapping<Node> yPos, PathBasedFlowOverTime dynamicFlow ) {
		this( eatf, xPos, yPos );

		System.out.print( "Start converting path based to edge based flow..." );
		PathComposition pathComposition = new PathComposition( network, transitTimes, dynamicFlow );
		pathComposition.run();
		System.out.println( " done." );

		this.flowOverTime = pathComposition.getEdgeFlows();
		maxFlowRate = pathComposition.getMaxFlowRate();
		if( Flags.FLOWWRONG ) {
			System.out.println( "Eingabe in die PathComposition:" );
			System.out.println( "Netzwerk:\n" + network + "\n" + "Fahrzeiten:\n" + transitTimes + "\n" + "Fluss:\n" + dynamicFlow );
			System.out.println( "Ausgabe der PathComposition:" );
			System.out.println( flowOverTime );
		}
		this.dynamicFlow = dynamicFlow;
	}

	/**
	 * Constructor creating an object where no nodes and edges exists.
	 * For testing!
	 */
	public GraphVisualizationResults() {
		this.network = new Network( 0, 0 );
		//this.dynamicFlow = new PathBasedFlowOverTime();
		this.nodeRectangles = new IdentifiableObjectMapping<Node, NodeRectangle>( 0, NodeRectangle.class );
		this.nodeToFloorMapping = new IdentifiableIntegerMapping<Node>( 0 );
		this.nodeCapacities = new IdentifiableIntegerMapping<Node>( 0 );
		this.edgeCapacities = new IdentifiableIntegerMapping<Edge>( 0 );
		this.transitTimes = new IdentifiableIntegerMapping<Edge>( 0 );
		this.supplies = new IdentifiableIntegerMapping<Node>( 0 );
		this.floorToNodeMapping = new ArrayList<ArrayList<Node>>();
		this.supersink = new Node( 0 );
		setUpNodeCoordinates();
	}

	private void setUpNodeCoordinates() {
		nodePositionMapping = new NodePositionMapping( network.numberOfNodes() );
		for( Node n : network ) {
			NodeRectangle rect = getNodeRectangles().get( n );
			final double xs = rect.get_nw_point().getX() + (rect.get_ne_point().getX() - rect.get_nw_point().getX()) * 0.5;
			final double ys = rect.get_nw_point().getY() + (rect.get_sw_point().getY() - rect.get_nw_point().getY()) * 0.5;
			// TODO read the value from VisualizationOptionManager (is 70 maybe)
			final double zs = getNodeToFloorMapping().get( n ) * 70 * 10; // whereever this 10 comes from... probably scaling factor?
			final Vector3 v = new Vector3( xs, ys, zs );
			nodePositionMapping.set( n, v );
		}
	}

	/**
	 * Returns the mapping of nodes to coordinates in the 3 dimensional space.
	 * @return the mapping of nodes to coordinates in the 3 dimensional space
	 */
	public NodePositionMapping getNodePositionMapping() {
		return nodePositionMapping;
	}

	public Node getSupersink() {
		return supersink;
	}

	/**
	 * Returns the network the saved visualization results are based on.
	 * @return the network the saved visualization results are based on.
	 */
	public Network getNetwork() {
		return network;
	}

///**
// * Returns the dynamic flow that shall be visualized.
// * @return the dynamic flow that shall be visualized.
// */
//public PathBasedFlowOverTime getDynamicFlow(){
//	return dynamicFlow;
//}
	/**
	 * Returns a mapping that assigns nodes to rectangles in the real world.
	 * @return a mapping that assigns nodes to rectangles in the real world.
	 */
	public IdentifiableObjectMapping<Node, NodeRectangle> getNodeRectangles() {
		return nodeRectangles;
	}

	/**
	 * Returns a mapping that assigns a flor number to each node.
	 * @return a mapping that assigns a flor number to each node.
	 */
	public IdentifiableIntegerMapping<Node> getNodeToFloorMapping() {
		return nodeToFloorMapping;
	}

	/**
	 * Returns a mapping that contains all nodes connected to a floor.
	 * @return a mapping that contains all nodes connected to a floor.
	 */
	public ArrayList<ArrayList<Node>> getFloorToNodeMapping() {
		return floorToNodeMapping;
	}

	/**
	 * Returns whether <code>node</code> is an evacuation node.
	 * @param node a node
	 * @return whether <code>node</code> is an evacuation node.
	 */
	public boolean isEvacuationNode( Node node ) {
		return isEvacuationNode.get( node );
	}

	/**
	 * Returns whether <code>node</code> is a source node.
	 * @param node a node
	 * @return whether <code>node</code> is a source node.
	 */
	public boolean isSourceNode( Node node ) {
		return isSourceNode.get( node );
	}

	/**
	 * Returns whether <code>node</code> has been a source node that was deleted.
	 * @param node a node
	 * @return whether <code>node</code> has been a source node that was deleted.
	 */
	public boolean isDeletedSourceNode( Node node ) {
		return isDeletedSourceNode.get( node );
	}

	/**
	 * Returns a mapping that assigns capacities to all edges of the network.
	 * @return a mapping that assigns capacities to all edges of the network.
	 */
	public IdentifiableIntegerMapping<Edge> getEdgeCapacities() {
		return edgeCapacities;
	}

	/**
	 * Returns a mapping that assigns capacities to all nodes of the network.
	 * @return a mapping that assigns capacities to all nodes of the network.
	 */
	public IdentifiableIntegerMapping<Node> getNodeCapacities() {
		return nodeCapacities;
	}

	/**
	 * Returns a mapping that assigns transit times to all edges of the network.
	 * @return a mapping that assigns transit times to all edges of the network.
	 */
	public IdentifiableIntegerMapping<Edge> getTransitTimes() {
		return transitTimes;
	}

	/**
	 * Returns a mapping that assigns supplies to all nodes of the network.
	 * @return a mapping that assigns supplies to all nodes of the network.
	 */
	public IdentifiableIntegerMapping<Node> getSupplies() {
		return supplies;
	}

	/**
	 * Returns the composed flow over time.
	 */
	public EdgeBasedFlowOverTime getFlowOverTime() {
		return flowOverTime;
	}

	/**
	 * Returns the maximal flow rate in the computed flow.
	 */
	public int getMaxFlowRate() {
		return maxFlowRate;
	}

	/**
	 * Returns a String containing a description of the network, the dynamic flow and the mapping of nodes to the real
	 * world (rectangle and floor).
	 * @return a String containing a description of the network, the dynamic flow and the mapping of nodes to the real
	 * world (rectangle and floor).
	 */
	@Override
	public String toString() {
		String result = "Network: " + network + "\n";
		result += "Node Rectangles: " + nodeRectangles + "\n";
		result += "Node to floor mapping:" + nodeToFloorMapping + "\n";
		result += "DynamicFlow" + dynamicFlow + "\n";
		return result;
	}

	public int getNeededTimeHorizon() {
		return neededTimeHorizon;
	}

	public void setNeededTimeHorizon( int neededTimeHorizon ) {
		this.neededTimeHorizon = neededTimeHorizon;
	}

	
}
