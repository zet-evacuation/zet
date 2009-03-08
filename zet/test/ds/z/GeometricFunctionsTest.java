/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * GeometricFunctionsTest.java
 *
 * Created on 7. Dezember 2007, 19:17
 */

package ds.z;

import junit.framework.*;
import ds.z.Edge;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;

/**
 * Tests the geometric functions of {@link PlanPoint}, {@link Edge} and {@link PlanPolygon}
 * @author Jan-Philipp Kappmeier
 */
public class GeometricFunctionsTest extends TestCase {
  
  /** Creates a new instance of GeometricFunctionsTest */
  public GeometricFunctionsTest() {
    super();
  }

  protected void setUp() throws Exception { }
    
  protected void tearDown() throws Exception { }
  
  public void testNormalDistribution() throws Exception {  
    // Erzeuge Punkte und teste ob sich die dadurch erzeugten Edges schneiden
    //PlanPolygon test = new PlanPolygon<Edge>( Edge.class );
    Edge e1 = new Edge( new PlanPoint( 2, 0 ), new PlanPoint( 2, 4 ), new PlanPolygon<Edge>( Edge.class ) );
    Edge e2 = new Edge( new PlanPoint( 2, 4 ), new PlanPoint( 3, 6 ), new PlanPolygon<Edge>( Edge.class ) );
    Edge e3 = new Edge( new PlanPoint( 0, 0 ), new PlanPoint( 4, 0 ), new PlanPolygon<Edge>( Edge.class ) );
    Edge e4 = new Edge( new PlanPoint( 0, 2 ), new PlanPoint( 4, 2 ), new PlanPolygon<Edge>( Edge.class ) );
    Edge e5 = new Edge( new PlanPoint( 2, 2 ), new PlanPoint( 2,-2 ), new PlanPolygon<Edge>( Edge.class ) );
    Edge e6 = new Edge( new PlanPoint( 0, 3 ), new PlanPoint( 0, 4 ), new PlanPolygon<Edge>( Edge.class ) );
    Edge e7 = new Edge( new PlanPoint( 27, 15 ), new PlanPoint( 8, 9 ), new PlanPolygon<Edge>( Edge.class ) );

    assertEquals( Edge.LineIntersectionType.Connected, Edge.intersects( e1, e2 ) ); // ERROR sollte Connected sein
    assertEquals( Edge.LineIntersectionType.IntersectsBorder, Edge.intersects( e1, e3 ) ); // War not Intersects, sollte Intersects sein
    assertEquals( Edge.LineIntersectionType.Intersects, Edge.intersects( e1, e4 ) ); // wie oben
    assertEquals( Edge.LineIntersectionType.Colinear, Edge.intersects( e1, e5 ) );
    assertEquals( Edge.LineIntersectionType.NotIntersects, Edge.intersects( e1, e6 ) );
    assertEquals( Edge.LineIntersectionType.NotIntersects, Edge.intersects( e1, e7 ) );

    Edge t1 = new Edge( new PlanPoint( 10, 10 ), new PlanPoint( 10, 20 ), new PlanPolygon<Edge>( Edge.class ) );
    Edge t2 = new Edge( new PlanPoint( 15, 15 ), new PlanPoint( 35, 15 ), new PlanPolygon<Edge>( Edge.class ) );
    
    assertEquals( Edge.LineIntersectionType.NotIntersects, Edge.intersects( t1, t2 ) );
  }
}
