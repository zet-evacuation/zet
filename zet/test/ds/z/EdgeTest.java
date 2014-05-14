/**
 * EdgeTest.java
 * Created: 22.03.2012, 17:05:15
 */
package ds.z;

import de.tu_berlin.coga.zet.model.PlanPoint;
import de.tu_berlin.coga.zet.model.PlanEdge;
import de.tu_berlin.coga.zet.model.PlanPolygon;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EdgeTest extends TestCase {

	@Test
	public void testIntersection () {
    PlanPolygon p = new PlanPolygon( PlanEdge.class );
		PlanEdge e1 = new PlanEdge( new PlanPoint( 6400, 4800), new PlanPoint(4000,4800), p );
    p = new PlanPolygon( PlanEdge.class );
		PlanEdge e2 = new PlanEdge( new PlanPoint( 6400, 5600), new PlanPoint(8000,4800), p );

		PlanEdge.LineIntersectionType a = PlanEdge.intersects( e1, e2 ); // should be NotIntersects
		PlanEdge.LineIntersectionType a2 = PlanEdge.intersects( e2, e1 ); // should be NotIntersects
    assertEquals( a, a2 );
		System.out.println( a + " " + a2 ); // should be the same!

    p = new PlanPolygon( PlanEdge.class );
		PlanEdge e3 = new PlanEdge( new PlanPoint( 7200, 5200 ), new PlanPoint(9000,5200), p );

		PlanEdge.LineIntersectionType b = PlanEdge.intersects( e2, e3 ); // should be IntersectsBorder
		PlanEdge.LineIntersectionType b2 = PlanEdge.intersects( e3, e2 ); // should be IntersectsBorder
    assertEquals( b, b2 );
		System.out.println( b + " " + b2 );// Should be the same!
	}
}
