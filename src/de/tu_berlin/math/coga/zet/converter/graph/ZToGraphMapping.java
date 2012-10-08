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
/*
 * ZToGraphMapping.java
 *
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import de.tu_berlin.math.coga.common.util.Level;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.mapping.IdentifiableIntegerMapping;
import ds.mapping.IdentifiableObjectMapping;
import ds.z.Room;
import java.awt.Shape;
import java.util.HashMap;

/**
 * The {@code ZToGraphMapping} class stores a mapping between an evacuation
 * problem based on the Z format and an evacuation problem based on graphs. This
 * mapping is necessary to transfer results from graph algorithms back to the Z
 * format. It also contains information about the relation of the graph and the
 * real world, e.g. where the area covered by a node lies in the real world.
 */
public class ZToGraphMapping {
	protected ZToGraphRasterContainer raster;
	protected IdentifiableObjectMapping<Node, Shape> nodeShapes;
	protected IdentifiableObjectMapping<Node, Double> nodeSpeedFactors;
	protected IdentifiableObjectMapping<Node, Double> nodeDownSpeedFactors;
	protected IdentifiableObjectMapping<Node, Double> nodeUpSpeedFactors;
	protected IdentifiableObjectMapping<Node, NodeRectangle> nodeRectangles;
	protected IdentifiableIntegerMapping<Node> nodeFloorMapping;
	protected HashMap<Node, Room> nodeRoomMapping;
	protected IdentifiableObjectMapping<Edge, Level> edgeLevels;
	//protected IdentifiableObjectMapping<Node, Boolean> isEvacuationNode;
	//protected IdentifiableObjectMapping<Node, Boolean> isSourceNode;
	protected IdentifiableObjectMapping<Node, Boolean> isDeletedSourceNode;
	protected IdentifiableObjectMapping<Node, String> exitName;

	public ZToGraphMapping() {
		this( 0, 0 );
	}

	public ZToGraphMapping( int numberOfNodes, int numberOfEdges ) {
		nodeShapes = new IdentifiableObjectMapping<Node, Shape>( numberOfNodes, Shape.class );
		nodeSpeedFactors = new IdentifiableObjectMapping<Node, Double>( numberOfNodes, Double.class );
		nodeDownSpeedFactors = new IdentifiableObjectMapping<Node, Double>( numberOfNodes, Double.class );
		nodeUpSpeedFactors = new IdentifiableObjectMapping<Node, Double>( numberOfNodes, Double.class );
		edgeLevels = new IdentifiableObjectMapping<Edge, Level>( numberOfEdges, Level.class );
		nodeRectangles = new IdentifiableObjectMapping<Node, NodeRectangle>( numberOfNodes, NodeRectangle.class );
		nodeFloorMapping = new IdentifiableIntegerMapping<Node>( numberOfNodes );
		nodeRoomMapping = new HashMap<>( numberOfNodes );
		//isEvacuationNode = new IdentifiableObjectMapping<Node, Boolean>( numberOfNodes, Boolean.class );
		//isSourceNode = new IdentifiableObjectMapping<Node, Boolean>( numberOfNodes, Boolean.class );
		isDeletedSourceNode = new IdentifiableObjectMapping<Node, Boolean>( numberOfNodes, Boolean.class );
		exitName = new IdentifiableObjectMapping<Node, String>( numberOfNodes, String.class );
		raster = null;
	}

	public IdentifiableObjectMapping<Node, NodeRectangle> getNodeRectangles() {
		return nodeRectangles;
	}

//	public IdentifiableObjectMapping<Node, Boolean> getIsEvacuationNode() {
//		return isEvacuationNode;
//	}

//	public IdentifiableObjectMapping<Node, Boolean> getIsSourceNode() {
//		return isSourceNode;
//	}

	public IdentifiableObjectMapping<Node, Boolean> getIsDeletedSourceNode() {
		return isDeletedSourceNode;
	}

	public IdentifiableIntegerMapping<Node> getNodeFloorMapping() {
		return nodeFloorMapping;
	}

	public HashMap<Node, Room> getNodeRoomMapping() {
		return nodeRoomMapping;
	}

	/**
	 * Set in which direction the edge goes, i.e. whether the end node is up, down
	 * or equal.
	 * @param edge the edge to be consideres
	 * @param level the level of the end node compared to the level of the start
	 * node
	 */
	/* TODO package */ public void setEdgeLevel( Edge edge, Level level ) {
		edgeLevels.set( edge, level );
	}

	/**
	 * Get in which direction the edge goes, i.e. whether the end node is up, down
	 * or equal.
	 * @param edge the edge to be consideres
	 * @return the level of the end node compared to the level of the start node
	 */
	public Level getEdgeLevel( Edge edge ) {
		if( edgeLevels.isDefinedFor( edge ) )
			return edgeLevels.get( edge );
		else
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "converter.EdgeLvlNotDefinedException" + " (" + edge + ")" ) );

	}

	/**
	 * Sets the name of the exit the node belongs to.
	 * @param node An exit node.
	 * @param nameOfExit The name of the exit the node belongs to.
	 */
	void setNameOfExit( Node node, String nameOfExit ) {
		exitName.set( node, nameOfExit );
	}

	/**
	 * Returns the name of the exit the node belongs to if it belongs to any exit.
	 * @param node The node to be looked for.
	 * @return the name of the exit the node belongs to if it belongs to any exit.
	 */
	public String getNameOfExit( Node node ) {
		if( exitName.isDefinedFor( node ) )
			return exitName.get( node );
		else
			throw new IllegalArgumentException( "The node " + node + " is not an exit node." );
	}

	void setNodeRectangle( Node node, NodeRectangle nodeRectangle ) {
		nodeRectangles.set( node, nodeRectangle );
	}

	void setFloorForNode( Node node, int floor ) {
		nodeFloorMapping.set( node, floor );
	}

	void setRoomForNode( Node node, Room r ) {
		nodeRoomMapping.put( node, r );
	}

	public ZToGraphRasterContainer getRaster() {
		return raster;
	}

	void setRaster( ZToGraphRasterContainer raster ) {
		this.raster = raster;
	}

	public Shape getNodeShape( Node node ) {
		if( nodeShapes.isDefinedFor( node ) )
			return nodeShapes.get( node );
		else
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "converter.NodeShapeNotDefinedException" + " (" + node + ")" ) );
	}

	void setNodeShape( Node node, Shape value ) {
		nodeShapes.set( node, value );
	}

	public double getNodeSpeedFactor( Node node ) {
		if( nodeSpeedFactors.isDefinedFor( node ) )
			return nodeSpeedFactors.get( node );
		else
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "converter.NodeSpeedNotDefinedException" + " (" + node + ")" ) );
	}

	public double getUpNodeSpeedFactor( Node node ) {
		if( nodeUpSpeedFactors.isDefinedFor( node ) )
			return nodeUpSpeedFactors.get( node );
		else
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "converter.NodeSpeedNotDefinedException" + " (" + node + ")" ) );
	}

	public double getDownNodeSpeedFactor( Node node ) {
		if( nodeDownSpeedFactors.isDefinedFor( node ) )
			return nodeDownSpeedFactors.get( node );
		else
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "converter.NodeSpeedNotDefinedException" + " (" + node + ")" ) );
	}

//	public boolean getIsEvacuationNode( Node node ) {
//		if( isEvacuationNode.isDefinedFor( node ) )
//			return isEvacuationNode.get( node );
//		else
//			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "converter.NodeStatusNotDefinedException" + " (" + node + ")" ) );
//	}

//	public boolean getIsSourceNode( Node node ) {
//		if( isSourceNode.isDefinedFor( node ) )
//			return isSourceNode.get( node );
//		else
//			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "converter.NodeSourceNotDefinedException" + " (" + node + ")" ) );
//	}

	public boolean getIsDeletedSourceNode( Node node ) {
		if( isDeletedSourceNode.isDefinedFor( node ) )
			return isDeletedSourceNode.get( node );
		else
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "converter.NodeSourceNotDefinedException" + " (" + node + ")" ) );
	}

	void setNodeSpeedFactor( Node node, double value ) {
		nodeSpeedFactors.set( node, value );
	}

	void setNodeUpSpeedFactor( Node node, double value ) {
		nodeUpSpeedFactors.set( node, value );
	}

	void setNodeDownSpeedFactor( Node node, double value ) {
		nodeDownSpeedFactors.set( node, value );
	}

//	void setIsEvacuationNode( Node node, Boolean isEvacuationNode ) {
//		this.isEvacuationNode.set( node, isEvacuationNode );
//	}

	/* TODO package */ public void setDeletedSourceNode( Node node, Boolean isSourceNode ) {
		this.isDeletedSourceNode.set( node, isSourceNode );
	}
}
