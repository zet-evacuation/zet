/**
 * Enum.java
 * Created: 04.04.2012, 18:25:34
 */

package ds.z;

/**
 * Enumeration used by the relative position test. Specifies if a
 * polygon is left or right from the border.
 * @see #relativePolygonPosition(ds.z.Edge, ds.z.PlanPolygon.RelativePosition)
 */
/**
 *
 * @author Jan-Philipp Kappmeier
 */
public enum RelativePosition {
	/**Checks wheater the room is on the right side of an edge. */
	Right,
	/** Checks wheather the room is on the left side of an edge. */
	Left;
}
