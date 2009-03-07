package ds.z.exception;

import ds.z.PlanPolygon;

/**
 * The exception <code>PolygonNotRasterizedException</code> indicates that a
 * polygon is not rasterized. You have to submit the polygon in which the error
 * occurs. That gives a possibility to tell the user where the error occured.
 * This exception is especially used by {@link ds.z.PlanPolygon}.
 * @author Sylvie Temme
 */
public class PolygonNotRasterizedException extends ValidationException {
	
	/**
	 * Creates a new instance of <code>PolygonNotRasterizedException</code>. A {@link PlanPolygon} needs
	 * to be passed.
	 * @param polygon the polygon that caused this exception
	 */
	public PolygonNotRasterizedException ( PlanPolygon polygon ) {
		super (polygon);
	}
	
	/**
	 * Creates a new instance of <code>PolygonNotRasterizedException</code> that contains the errorous {@link PlanPolygon}.
	 * @param polygon the polygon that caused this exception
	 * @param s an additional information string
	 */
	public PolygonNotRasterizedException ( PlanPolygon polygon, String s ) {
		super (polygon,  s );
	}
	
	/**
	 * Returns the not rasterized polygon.
	 * @return the instance of {@link PlanPolygon} that was the cause for this exception.
	 */
	public PlanPolygon getPolygon () {
		return (PlanPolygon)getSource ();
	}
}