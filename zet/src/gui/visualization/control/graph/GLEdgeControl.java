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
package gui.visualization.control.graph;

import de.tu_berlin.math.coga.math.Conversion;
import ds.graph.Edge;
import ds.graph.flow.EdgeBasedFlowOverTime;
import ds.GraphVisualizationResult;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.draw.graph.GLEdge;
import java.util.ArrayList;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import gui.visualization.control.AbstractZETVisualizationControl;

/**
 * The control class for edges in an MVC-design. This class controls the visualization of such an edge represented
 * by {@code GLEdge}. That means it sets the correct positions depending on the time of the visualization.
 * @author Jan-Philipp Kappmeier
 */
//public class GLEdgeControl extends AbstractControl<GLEdge, Edge, GraphVisualizationResult, GLEdge, GLEdgeControl, GLControl> {
public class GLEdgeControl extends AbstractZETVisualizationControl<GLEdgeControl, GLEdge, GLGraphControl> {
	private static final double Z_TO_OPENGL_SCALING = 0.1d;
	private double time;
	/** Decides wheather this edge is the one from the node with lower id to the one wiith higher id of the two edges between two nodes. */
	private boolean isFirst = false;
	private ArrayList<Integer> flowOnEdge;
	private int maxFlowRate;
	private double deltaStep;
	private Edge controlled;
	private GraphVisualizationResult graphVisResult;

	/**
	 * Creates a new <code>GLEdgeControl</code> object for the edge <code>edge</code> using data from 
	 * the <code>graphVisResult</code> object. 
	 * @param graphVisResult contains all information necessary to visualize a result of a flow algorithm.
	 * @param edge the edge for this control object.
	 * @param glControl the main control class.
	 */
	public GLEdgeControl( GraphVisualizationResult graphVisResult, Edge edge, GLGraphControl glControl ) {
		super( glControl );
		maxFlowRate = graphVisResult.getMaxFlowRate();
		this.graphVisResult = graphVisResult;
		controlled = edge;
		setView( new GLEdge( this ) );

		if( edge.start().id() < edge.end().id() )	// checks weather this edge is the first one of the two representing one undirected edge
			isFirst = true;

		// calculate flow on the edge
		IdentifiableIntegerMapping<Edge> transitTimes = graphVisResult.getTransitTimes();
		EdgeBasedFlowOverTime flowOverTime = graphVisResult.getFlowOverTime();
		int maxT = flowOverTime.get( edge ).getLastTimeWithNonZeroValue(); // maximaler Zeithorizont
		int transit = transitTimes.get( edge );
		if( maxT > 0 )
			glControl.setMaxTime( maxT + transit );
		if( maxT > 0 ) {
			flowOnEdge = new ArrayList<Integer>( maxT + transit + transit );
			for( int i = 0; i < transit; i++ )
				flowOnEdge.add( new Integer( 0 ) );
			for( int i = 0; i <= maxT; i++ )
				flowOnEdge.add( new Integer( flowOverTime.get( edge ).get( i ) ) );
			for( int i = 0; i < transit; i++ )
				flowOnEdge.add( new Integer( 0 ) );
		} else
			flowOnEdge = new ArrayList<Integer>();
	}

	/**
	 * Returns whether this edge is the one going from lower ID to higher ID
	 * (of the two edges between two nodes).
	 * @return whether this edge is the one going from lower ID to higher ID.
	 */
	public boolean isFirstEdge() {
		return isFirst;
	}

	/**
	 * Returns the transit time of this edge.
	 * @return the transit time of this edge.
	 */
	public int getTransitTime() {
		return graphVisResult.getTransitTimes().get( controlled );
	}

	/**
	 * Returns an array list containing the amount of flow going into 
	 * the edge for each time step within 0 and time horizon + transit time of the edge.
	 * @return an array list giving the amount of flow going into the edge for each point in time.
	 */
	public ArrayList<Integer> getFlowOnEdge() {
		return flowOnEdge;
	}

	/**
	 * Returns the model-length of the edge, NOT taking the z-coordinate into account.
	 * @return the model-length of the edge, NOT taking the z-coordinate into account.
	 */
	public double getLength() {
		Node start = controlled.start();
		Node end = controlled.end();
		Vector3 startPos = getMiddlePoint( start );
		Vector3 endPos = getMiddlePoint( end );
		double dx = Math.abs( startPos.x - endPos.x ) * Z_TO_OPENGL_SCALING;
		double dy = Math.abs( startPos.y - endPos.y ) * Z_TO_OPENGL_SCALING;
		double length = Math.sqrt( dx * dx + dy * dy );
		return length;
	}

	/**
	 * Returns the model length of the edge, taking the z-coordinate into account.
	 * @return the model length of the edge, taking the z-coordinate into account.
	 */
	public double get3DLength() {
		Node start = controlled.start();
		Node end = controlled.end();
		Vector3 startPos = getMiddlePoint( start );
		Vector3 endPos = getMiddlePoint( end );
		double dz = Math.abs( startPos.z - endPos.z ) * Z_TO_OPENGL_SCALING;
		double xyLength = getLength(); // Length of projection in xy-plane
		double length = Math.sqrt( dz * dz + xyLength * xyLength );
		return length;
	}

	/**
	 * Returns the difference vector between the start node
	 * and the end node of the controlled edge.
	 * @return the difference vector between the start node
	 * and the end node of the controlled edge, i.e. start-end
	 * in each component.
	 */
	public Vector3 getDifferenceVectorInOpenGlScaling() {
		Node start = controlled.start();
		Node end = controlled.end();
		Vector3 startPos = getMiddlePoint( start );
		Vector3 endPos = getMiddlePoint( end );
		double dx = (startPos.x - endPos.x) * Z_TO_OPENGL_SCALING;
		double dy = (startPos.y - endPos.y) * Z_TO_OPENGL_SCALING;
		double dz = (startPos.z - endPos.z) * Z_TO_OPENGL_SCALING;
		return new Vector3( dx, dy, dz );
	}

	/**
	 * Calculates the angle between to vectors a and b, going from a to b.
	 * Lies always between 0 and 180 !
	 * Uses the dot product to calculate the cosine, 
	 * the angle is then calculated with the arcus cosine. 
	 * @param a a vector.
	 * @param b another vector.
	 * @return the angle between the two vectors.
	 */
	public double getAngleBetween( Vector3 a, Vector3 b ) {
		double cosine = (a.dotProduct( b ) / a.length()) / b.length();
		double angle = Math.acos( cosine ) / Conversion.ANGLE2DEG;
		return 180 - angle;
	}

	/**
	 * Returns the cross product of a and b (a being the first vector).
	 * @param a a vector.
	 * @param b another vector.
	 * @return the cross product of a and b (a being the first vector).
	 */
	public Vector3 getRotationAxis( Vector3 a, Vector3 b ) {
		return a.crossProduct( b );
	}

	/**
	 * Returns the capacity of this edge.
	 * @return the capacity of this edge.
	 */
	public int getCapacity() {
		return graphVisResult.getEdgeCapacities().get( controlled );
	}

	/**
	 * Returns the maximal flow rate (taking the flow on all edges into account).
	 * @return the maximal flow rate.
	 */
	public int getMaxFlowRate() {
		return maxFlowRate;
	}

	public int getMaxCapacity() {
		return 0;
	}

	public Vector3 getStartPosition() {
		Node start = controlled.start();
		Node end = controlled.end();
		Vector3 startPos = getMiddlePoint( start );
		Vector3 endPos = getMiddlePoint( end );
		//compare the y-coordinates
		Vector3 startPoint;
		if( startPos.y <= endPos.y )
			startPoint = startPos;
		else
			startPoint = endPos;
		return startPoint;
	}

	public Vector3 getEndPosition() {
		Node start = controlled.start();
		Node end = controlled.end();
		Vector3 startPos = getMiddlePoint( start );
		Vector3 endPos = getMiddlePoint( end );

		//compare the y-coordinates
		Vector3 endPoint;
		if( startPos.y <= endPos.y )
			endPoint = endPos;
		else
			endPoint = endPos;
		return endPoint;
	}

	/**
	 * Private method returning the <code>x</code> and <code>y</code> coordinate of the node by
	 * calculating the middle point of the node rectangle.
	 * @param node a node
	 * @return the middle point of the corresponding rectangle
	 */
	private Vector3 getMiddlePoint( Node node ) {
		NodeRectangle rect = graphVisResult.getNodeRectangles().get( node );
		double dx = rect.get_nw_point().getX() +
						(rect.get_ne_point().getX() -
						rect.get_nw_point().getX()) / 2;

		double dy = rect.get_nw_point().getY() +
						(rect.get_sw_point().getY() -
						rect.get_nw_point().getY()) / 2;
		int floor = graphVisResult.getNodeToFloorMapping().get( node );
		// TODO read the value from VisualizationOptionManager
		//double z = VisualizationOptionManager.getGraphHeight();
		double z = 70;
		z += (floor - 1) * 70;
		z *= 10;	// whereever this 10 comes from...
		return new Vector3( dx, -dy, z );
	}

	/**
	 * The real visualization time used for fluid visualization.
	 * @return the real visualization time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * Returns the current delta step. That means the real value for the progress of the current step
	 * for a fluid visualization.
	 * @return the current real step.
	 */
	public double getDeltaStep() {
		return deltaStep;
	}

	/**
	 * Calculates the current time and delta information used for fluid visualization depending 
	 * of the graph step in the main control class.
	 */
	public void stepUpdate() {
		time = mainControl.getStep();
		deltaStep = time - Math.floor( time );
	}
}