/**
 * EdgeTest.java
 * Created: 22.03.2012, 17:05:15
 */
package ds.z;

import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EdgeTest extends TestCase {
	
	@Test
	public void testIntersection () {
		Edge e1 = new Edge( new PlanPoint( 6400, 4800), new PlanPoint(4000,4800) );
		Edge e2 = new Edge( new PlanPoint( 6400, 5600), new PlanPoint(8000,4800) );
		
		Edge.LineIntersectionType a = Edge.intersects( e1, e2 ); // should be NotIntersects
		Edge.LineIntersectionType a2 = Edge.intersects( e2, e1 ); // should be NotIntersects
		System.out.println( a + " " + a2 ); // should be the same!
		
		Edge e3 = new Edge( new PlanPoint( 7200, 5200 ), new PlanPoint(9000,5200) );
		
		Edge.LineIntersectionType b = Edge.intersects( e2, e3 ); // should be IntersectsBorder
		Edge.LineIntersectionType b2 = Edge.intersects( e3, e2 ); // should be IntersectsBorder
		System.out.println( b + " " + b2 );// Should be the same!
	}
}
