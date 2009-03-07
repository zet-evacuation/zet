/*
 * PolygonNotClosedException.java
 * Created on 30. November 2007, 15:14
 */

package ds.z.exception;

import ds.z.Edge;
import ds.z.PlanPolygon;

/**
 * The exception <code>PolygonNotClosedException</code> indicates that a
 * polygon is not closed. You have to submit the polygon in which the error
 * occurs. That gives a possibility to tell the user where the error occured.
 * This exception is especially used by {@link ds.z.Room} and {@link ds.z.Area}.
 * @author Jan-Philipp Kappmeier
 */
public class PolygonNotClosedException extends ValidationException {
	
	/**
	 * Creates a new instance of <code>PolygonNotClosedException</code>. A {@link PlanPolygon} needs
	 * to be passed.
	 * @param polygon the polygon that caused this exception
	 */
	public PolygonNotClosedException ( PlanPolygon polygon ) {
		super (polygon);
	}
	
	/**
	 * Creates a new instance of <code>PolygonNotClosedException</code> that contains the errorous {@link PlanPolygon}.
	 * @param polygon the polygon that caused this exception
	 * @param s an additional information string
	 */
	public PolygonNotClosedException ( PlanPolygon polygon, String s ) {
		super (polygon,  s );
	}
	
	/**
	 * Returns the not closed polygon.
	 * @return the instance of {@link PlanPolygon} that was the cause for this exception.
	 */
	public PlanPolygon<Edge> getPolygon () {
		return (PlanPolygon)getSource ();
	}
}