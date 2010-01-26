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
 * RasterizationTest.java
 *
 * Created on 3. Dezember 2007, 21:50
 */

package converter;

import ds.Project;
import ds.z.BuildingPlan;
import ds.z.DelayArea;
import ds.z.Edge;
import ds.z.Floor;
import ds.z.InaccessibleArea;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import ds.z.Room;
import java.io.File;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RasterizationTest extends TestCase {

  /** Creates a new instance of RasterizationTest */
  public RasterizationTest() {
    super();
  }

  @Override
  protected void setUp() throws Exception {
  }

  @Override
  protected void tearDown() throws Exception {
  }

  public void testSimple() throws Exception {
    // erstelle dreiecks-polygon
    PlanPolygon<Edge> dreieck = new PlanPolygon<Edge>( Edge.class );
    ArrayList<PlanPoint> points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint( 0, 40 ) );
    points.add( new PlanPoint( 20, 0 ) );
    points.add( new PlanPoint( 40, 40 ) );
    
    dreieck.defineByPoints( points );
    
    Raster<RasterSquare, PlanPolygon> rasterer = new Raster<RasterSquare, PlanPolygon>(RasterSquare.class, PlanPolygon.class, dreieck, 1000 );
    rasterer.rasterize( );
  }

  public void testMelisExample() throws Exception {
    // 5x5-Raum mit rasterisierten Koordinaten erzeugen
  	BuildingPlan bp = new BuildingPlan();
    Floor floor = new Floor();
    bp.addFloor(floor);
    
    Room room = new Room( floor, "Testraum" );
    
    ArrayList<PlanPoint> points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint( 0.4, 0.4 ) );
    points.add( new PlanPoint( 2.4, 0.4 ) );
    points.add( new PlanPoint( 2.4, 2.4 ) );
    points.add( new PlanPoint( 0.4, 2.4 ) );
    room.defineByPoints( points );
    
    // To exception is allowed here!
    room.checkRasterized();
    
    // Inaccessible Areas
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint( 1.2, 0.4 ) );
    points.add( new PlanPoint( 1.6, 0.4 ) );
    points.add( new PlanPoint( 1.6, 0.8 ) );
    points.add( new PlanPoint( 1.2, 0.8 ) );
    InaccessibleArea iA = new InaccessibleArea( room );
    iA.defineByPoints( points );
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint( 1.2, 1.2 ) );
    points.add( new PlanPoint( 1.6, 1.2 ) );
    points.add( new PlanPoint( 1.6, 1.6 ) );
    points.add( new PlanPoint( 2.0, 1.6 ) );
    points.add( new PlanPoint( 2.0, 2.0 ) );
    points.add( new PlanPoint( 1.2, 2.0 ) );
    iA = new InaccessibleArea( room );
    iA.defineByPoints( points );
    
    // DelayArea
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint( 0.8, 0.8 ) );
    points.add( new PlanPoint( 2.0, 0.8 ) );
    points.add( new PlanPoint( 2.0, 1.2 ) );
    points.add( new PlanPoint( 0.8, 1.2 ) );
    DelayArea dA = new DelayArea( room, DelayArea.DelayType.OTHER, 1.0/3.0 );
    dA.defineByPoints( points );

    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint( 1.6, 0.8 ) );
    points.add( new PlanPoint( 2.4, 0.8 ) );
    points.add( new PlanPoint( 2.4, 2.0 ) );
    points.add( new PlanPoint( 1.6, 2.0 ) );
    dA = new DelayArea( room, DelayArea.DelayType.OTHER, 0.5 );
    dA.defineByPoints( points );

    Room pg_room = new Room( floor, "PG-Raum");
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(3.0,3.0) );
    points.add( new PlanPoint(8.6,3.0) );
    points.add( new PlanPoint(8.6,8.2) );
    points.add( new PlanPoint(3.0,8.2) );
    pg_room.defineByPoints( points );
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(3.4,3.4) );
    points.add( new PlanPoint(3.8,3.4) );
    points.add( new PlanPoint(3.8,5.4) );
    points.add( new PlanPoint(3.4,5.4) );
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(3.8,3.4));
    points.add( new PlanPoint(4.6,3.4));
    points.add( new PlanPoint(4.6,4.6));
    points.add( new PlanPoint(3.8,4.6));
    points.add( new PlanPoint(3.8,4.2));
    points.add( new PlanPoint(4.2,4.2));
    points.add( new PlanPoint(4.2,3.8));
    points.add( new PlanPoint(3.8,3.8));
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(5.0,3.4));
    points.add( new PlanPoint(6.6,3.4));
    points.add( new PlanPoint(6.6,3.8));
    points.add( new PlanPoint(5.4,3.8));
    points.add( new PlanPoint(5.4,5.0));
    points.add( new PlanPoint(6.2,5.0));
    points.add( new PlanPoint(6.2,4.6));
    points.add( new PlanPoint(5.8,4.6));
    points.add( new PlanPoint(5.8,4.2));
    points.add( new PlanPoint(6.6,4.2));
    points.add( new PlanPoint(6.6,5.4));
    points.add( new PlanPoint(5.0,5.4));
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(3.4,5.8) );
    points.add( new PlanPoint(4.6,5.8) );
    points.add( new PlanPoint(4.6,6.2) );
    points.add( new PlanPoint(3.8,6.2) );
    points.add( new PlanPoint(3.8,6.6) );
    points.add( new PlanPoint(4.6,6.6) );
    points.add( new PlanPoint(4.6,7.8) );
    points.add( new PlanPoint(3.4,7.8) );
    points.add( new PlanPoint(3.4,7.4) );
    points.add( new PlanPoint(4.2,7.4) );
    points.add( new PlanPoint(4.2,7.0) );
    points.add( new PlanPoint(3.4,7.0) );
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(5.0,6.6) );
    points.add( new PlanPoint(5.4,6.6) );        
    points.add( new PlanPoint(5.4,7.0) );
    points.add( new PlanPoint(5.0,7.0) );
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(5.4,6.2) );
    points.add( new PlanPoint(5.8,6.2) );        
    points.add( new PlanPoint(5.8,6.6) );
    points.add( new PlanPoint(5.4,6.6) );
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(5.8,5.8) );
    points.add( new PlanPoint(6.2,5.8) );        
    points.add( new PlanPoint(6.2,7.8) );
    points.add( new PlanPoint(5.8,7.8) );
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(6.6,5.8));
    points.add( new PlanPoint(8.2,5.8));
    points.add( new PlanPoint(8.2,6.2));
    points.add( new PlanPoint(7.8,6.2));
    points.add( new PlanPoint(7.8,6.6));
    points.add( new PlanPoint(7.4,6.6));
    points.add( new PlanPoint(7.4,6.2));
    points.add( new PlanPoint(6.6,6.2));
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(6.6,6.6) );
    points.add( new PlanPoint(8.2,6.6) );
    points.add( new PlanPoint(8.2,7.0) );
    points.add( new PlanPoint(6.6,7.0) );
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(7.0,7.0) );
    points.add( new PlanPoint(7.4,7.0) );
    points.add( new PlanPoint(7.4,7.4) );
    points.add( new PlanPoint(7.0,7.4) );
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);
    
    points = new ArrayList<PlanPoint>();
    points.add( new PlanPoint(6.6,7.4));
    points.add( new PlanPoint(7.0,7.4));
    points.add( new PlanPoint(7.0,7.8));
    points.add( new PlanPoint(6.6,7.8));
    iA = new InaccessibleArea( pg_room );
    iA.defineByPoints(points);

    // Rasterrung
    RoomRaster<RoomRasterSquare> rasterer = new RoomRaster<RoomRasterSquare>(RoomRasterSquare.class, room, 400 );
    rasterer.rasterize();
    System.out.println(rasterer);
    
    // Tests
    assertEquals( 25, rasterer.insideSquares().size() );
    assertFalse( rasterer.getSquare( 2, 0 ).accessible());
    assertTrue( rasterer.getSquare( 2, 1 ).accessible());
    assertEquals( 0.5, rasterer.getSquare( 4, 1 ).getSpeedFactor(), 0.1 );
    assertEquals( 1.0/6.0, rasterer.getSquare( 3, 1 ).getSpeedFactor(), 0.1 );
    assertEquals( 1.0/3.0, rasterer.getSquare( 2, 1 ).getSpeedFactor(), 0.1 );
    
    // Store to harddisk    
    Project p = new Project ();
    
    p.getBuildingPlan ().addFloor( floor );
    
    //p.save (new File ("D:\\Semester9\\PG\\pg517.xml"));
    //p.save( new File( "C:\\pg517.xml"));
  }
  
}
