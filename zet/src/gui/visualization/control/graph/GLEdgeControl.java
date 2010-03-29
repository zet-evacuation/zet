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
import gui.visualization.draw.graph.GLEdge;
import java.util.ArrayList;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import gui.visualization.control.AbstractZETVisualizationControl;
import zet.xml.FlowVisualization;

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
	//private GraphVisualizationResult graphVisResult;
	private double length = 0;
	private double length3d = 0;
	private Vector3 differenceVectorInOpenGLScaling;
	private Vector3 startPoint;
	private Vector3 endPoint;
	private int transitTime;
	private int capacity;

	/**
	 * Creates a new <code>GLEdgeControl</code> object for the edge <code>edge</code> using data from
	 * the <code>graphVisResult</code> object.
	 * @param graphVisResult contains all information necessary to visualize a result of a flow algorithm.
	 * @param edge the edge for this control object.
	 * @param glControl the main control class.
	 */
	public GLEdgeControl( GraphVisualizationResult graphVisResult, Edge edge, GLGraphControl glControl ) {
		super( glControl );
		controlled = edge;
		setView( new GLEdge( this ) );

		if( edge.start().id() < edge.end().id() )	// checks weather this edge is the first one of the two representing one undirected edge
			isFirst = true;

		// general edge attributes
		maxFlowRate = graphVisResult.getMaxFlowRate();
		transitTime = graphVisResult.getTransitTimes().get( edge );
		capacity = graphVisResult.getEdgeCapacities().get( edge );

		scaling = Z_TO_OPENGL_SCALING;
		// calculate position of start node ...
		NodeRectangle rect = graphVisResult.getNodeRectangles().get( edge.start() );
		final double xs = rect.get_nw_point().getX() + (rect.get_ne_point().getX() - rect.get_nw_point().getX()) * 0.5;
		final double ys = rect.get_nw_point().getY() + (rect.get_sw_point().getY() - rect.get_nw_point().getY()) * 0.5;
		int floor = graphVisResult.getNodeToFloorMapping().get( edge.start() );
		// TODO read the value from VisualizationOptionManager (is 70 maybe)
		final double zs = floor * 70 * 10; // whereever this 10 comes from... probably scaling factor?
		// ... and of end node ...
		rect = graphVisResult.getNodeRectangles().get( edge.end() );
		final double xe = rect.get_nw_point().getX() + (rect.get_ne_point().getX() - rect.get_nw_point().getX()) * 0.5;
		final double ye = rect.get_nw_point().getY() + (rect.get_sw_point().getY() - rect.get_nw_point().getY()) * 0.5;
		floor = graphVisResult.getNodeToFloorMapping().get( edge.end() );
		final double ze = floor * 70 * 10; // whereever this 10 comes from...
		// ... and call init method
		init( new Vector3( xs, -ys, zs ), new Vector3( xe, -ye, ze ) );

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

	GLEdgeControl( FlowVisualization fv, Edge edge, GLGraphControl mainControl ) {
		super( mainControl );
		controlled = edge;
		setView( new GLEdge( this ) );

		isFirst = !fv.isEdgesDoubled() || edge.start().id() < edge.end().id() ? true : false;
		//if(  )	// checks weather this edge is the first one of the two representing one undirected edge
		//	isFirst = true;

		// store general edge attributes
		maxFlowRate = fv.getMaxFlowRate();
		mainControl.setMaxTime( fv.getTimeHorizon() );
		transitTime = fv.getGv().getTransitTimes().get( edge );
		capacity = fv.getGv().getEdgeCapacities().get( edge );

		// compute the positions for the start ...
		final double xs = fv.getGv().getNodePositionMapping().get( edge.start() ).x * fv.getGv().getScale();
		final double ys = fv.getGv().getNodePositionMapping().get( edge.start() ).y * fv.getGv().getScale();
		final double zs = fv.getGv().getNodePositionMapping().get( edge.start() ).z * fv.getGv().getScale();
		// ... and for the end node ...
		final double xe = fv.getGv().getNodePositionMapping().get( edge.end() ).x * fv.getGv().getScale();
		final double ye = fv.getGv().getNodePositionMapping().get( edge.end() ).y * fv.getGv().getScale();
		final double ze = fv.getGv().getNodePositionMapping().get( edge.end() ).z * fv.getGv().getScale();
		// ... and call init method
		init( new Vector3( xs, -ys, zs ), new Vector3( xe, -ye, ze) );

		// compute flow visualization data structure
		IdentifiableIntegerMapping<Edge> transitTimes = fv.getGv().getTransitTimes();

		EdgeBasedFlowOverTime flowOverTime = fv.getFlow();

		int maxT = flowOverTime.get( edge ).getLastTimeWithNonZeroValue(); // maximaler Zeithorizont
		int transit = transitTimes.get( edge );
//		if( maxT > 0 )
//			mainControl.setMaxTime( maxT + transit );
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

	double scaling = 1;

	private void init( Vector3 startPos, Vector3 endPos ) {
		// calculate differences between the points
		final double dx = (startPos.x - endPos.x) * scaling;
		final double dy = (startPos.y - endPos.y) * scaling;
		final double dz = (startPos.z - endPos.z) * scaling;
		differenceVectorInOpenGLScaling = new Vector3( dx, dy, dz );

		// calculate length and 3d length
		length = Math.sqrt( dx * dx + dy * dy );
		length3d = Math.sqrt( dz * dz + length * length );

		// compare the y-coordinates and set the point with lower coordinate
		// as start point
		if( startPos.y <= endPos.y ) {
			startPoint = startPos;
			endPoint = endPos;
		} else {
			startPoint = endPos;
			endPoint = startPos;
		}
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
		return transitTime;
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
		return length;
	}

	/**
	 * Returns the model length of the edge, taking the z-coordinate into account.
	 * @return the model length of the edge, taking the z-coordinate into account.
	 */
	public double get3DLength() {
		return length3d;
	}

	/**
	 * Returns the difference vector between the start node
	 * and the end node of the controlled edge.
	 * @return the difference vector between the start node
	 * and the end node of the controlled edge, i.e. start-end
	 * in each component.
	 */
	public Vector3 getDifferenceVectorInOpenGlScaling() {
		return differenceVectorInOpenGLScaling;
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
		return capacity;
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
		return startPoint;
	}

	public Vector3 getEndPosition() {
		return endPoint;
	}

	/**
	 * Private method returning the <code>x</code> and <code>y</code> coordinate of the node by
	 * calculating the middle point of the node rectangle.
	 * @param node a node
	 * @return the middle point of the corresponding rectangle
	 */
	private Vector3 getMiddlePoint( Node node ) {
		return null;
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
