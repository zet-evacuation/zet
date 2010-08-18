/**
 * GLEdgeControl.java
 * Created: Aug 17, 2010,3:21:15 PM
 */
package gui.visualization.control.graph;

import de.tu_berlin.math.coga.math.Conversion;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import gui.visualization.draw.graph.GLEdge;
import opengl.framework.abs.AbstractControl;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
//public class GLEdgeControl extends AbstractZETVisualizationControl<GLEdgeControl, GLEdge, GLFlowGraphControl> {
public class GLEdgeControl extends AbstractControl<GLEdgeControl, GLEdge> {

	/** Decides whether this edge is the one from the node with lower id to the one with higher id of the two edges between two nodes. */
	protected boolean isFirst = false;
	private double length = 0;
	private double length3d = 0;
	private Vector3 differenceVectorInOpenGLScaling;
	private Vector3 startPoint;
	private Vector3 endPoint;
	double scaling = 1;

	public GLEdgeControl( NodePositionMapping nodePositionMapping, Edge edge ) {
		super();
		//super( glControl );

		// Do not set a view here. maybe changed later on
		setView();

		if( edge.start().id() < edge.end().id() )	// checks weather this edge is the first one of the two representing one undirected edge
			isFirst = true;


		init( nodePositionMapping.get( edge.start() ), nodePositionMapping.get( edge.end() ) );

		view.update();

	}


	protected void setView() {
		System.out.println( "A normal edge was set" );
		setView( new GLEdge( this ) );
	}

	private void init( Vector3 startPos, Vector3 endPos ) {
		// calculate differences between the points
		final double dx = (startPos.x - endPos.x) ;//* scaling;
		final double dy = (-startPos.y + endPos.y);// * scaling;
		final double dz = (startPos.z - endPos.z);// * scaling;
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

	public void setScaling( double scaling ) {
		startPoint.scalarMultiplicateTo( 1/this.scaling );
		endPoint.scalarMultiplicateTo( 1/this.scaling );
		this.scaling = scaling;
		startPoint.scalarMultiplicateTo( scaling );
		endPoint.scalarMultiplicateTo( scaling );
		// recompute everything...
		init( startPoint, endPoint );
	}



	public Vector3 getStartPosition() {
		return startPoint;
	}

	public Vector3 getEndPosition() {
		return endPoint;
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

}
