/**
 * EdgeTest.java
 * Created: 22.03.2012, 17:05:15
 */
package ds.z;

import de.tu_berlin.coga.zet.model.PlanPoint;
import de.tu_berlin.coga.zet.model.PlanEdge;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EdgeTest extends TestCase {

	@Test
	public void testIntersection () {
		PlanEdge e1 = new PlanEdge( new PlanPoint( 6400, 4800), new PlanPoint(4000,4800) );
		PlanEdge e2 = new PlanEdge( new PlanPoint( 6400, 5600), new PlanPoint(8000,4800) );

		PlanEdge.LineIntersectionType a = PlanEdge.intersects( e1, e2 ); // should be NotIntersects
		PlanEdge.LineIntersectionType a2 = PlanEdge.intersects( e2, e1 ); // should be NotIntersects
		System.out.println( a + " " + a2 ); // should be the same!

		PlanEdge e3 = new PlanEdge( new PlanPoint( 7200, 5200 ), new PlanPoint(9000,5200) );

		PlanEdge.LineIntersectionType b = PlanEdge.intersects( e2, e3 ); // should be IntersectsBorder
		PlanEdge.LineIntersectionType b2 = PlanEdge.intersects( e3, e2 ); // should be IntersectsBorder
		System.out.println( b + " " + b2 );// Should be the same!
	}
}
