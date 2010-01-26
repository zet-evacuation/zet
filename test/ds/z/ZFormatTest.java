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
 * ZFormatTest.java
 *
 * Created on 5. Dezember 2007, 14:16
 */

package ds.z;

import converter.RoomRaster;
import converter.RoomRasterSquare;
import ds.Project;
import ds.ca.CellularAutomaton;
import ds.ca.RoomCell;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import util.random.distributions.NormalDistribution;

/**
 * Tests the distribution classes.
 * @author Jan-Philipp Kappmeier
 */
public class ZFormatTest extends TestCase {
	
	Project p;
  
  /** Creates a new instance of Test1 */
  public ZFormatTest() {
    super();
  }
  
	@Override
  protected void setUp() throws Exception {
	  p = new Project();
  }
    
	@Override
  protected void tearDown() throws Exception {
  }
  
  public void testRoomCreate() throws Exception {
    Floor fl = new Floor( "Floor 1" );
    Room room1 = new Room( fl, "Room 1" );
    Room room2 = new Room( fl, "Room 2" );
    try {
      Room room3 = new Room( fl, "Room 1" );
      fail( "Rooms should be equal" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testRoomConnect() throws Exception {
    Floor fl = new Floor( "Floor 1" );
    Room room1 = new Room( fl, "Room 1" );
    Room room2 = new Room( fl, "Room 2" );
    Room door = new Room( fl, "Door" );
    
    // Erzeuge Room1
    ArrayList<PlanPoint> pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( 10, 10 ) );
    pointList.add( new PlanPoint( 10, 100 ) );
    pointList.add( new PlanPoint( 100, 100 ) );
    pointList.add( new PlanPoint( 100, 70 ) );
    pointList.add( new PlanPoint( 100, 40 ) );
    pointList.add( new PlanPoint( 100, 10 ) );
    room1.defineByPoints( pointList );
    
    // Erzeuge Room2
    pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( 110, 10 ) );
    pointList.add( new PlanPoint( 110, 40 ) );
    pointList.add( new PlanPoint( 110, 70 ) );
    pointList.add( new PlanPoint( 110, 100 ) );
    pointList.add( new PlanPoint( 200, 100) );
    pointList.add( new PlanPoint( 200, 10 ) );
    try {
      // Check wheter room1 sends exception if we try adding points a second time
      room1.defineByPoints( pointList );
      fail( "Adding points to not empty room should be forbidden!" );
    } catch( IllegalStateException expected ) {
      
    }
    room2.defineByPoints( pointList );
    
    // Erzeuge Door
    pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( 100, 40 ) );
    pointList.add( new PlanPoint( 100, 70 ) );
    pointList.add( new PlanPoint( 110, 70 ) );
    pointList.add( new PlanPoint( 110, 40 ) );
    door.defineByPoints( pointList );
    
    // Verbinden der R�ume
    RoomEdge r1Edge = room1.getEdge( new PlanPoint( 100, 40 ), new PlanPoint( 100, 70 ) );
    assertFalse( r1Edge.isPassable() );
    assertNull( r1Edge.getLinkTarget () );
    assertTrue( r1Edge.getRoom ().equals( room1 ) );
    room1.connectTo( door, new PlanPoint( 100, 40 ), new PlanPoint( 100, 70 ) );
    RoomEdge r2Edge = door.getEdge( new PlanPoint( 100,40 ), new PlanPoint( 100, 70 ) );
    assertTrue( r1Edge.equals( r2Edge ) );
    assertTrue( r1Edge.isPassable() );
    assertTrue( r2Edge.isPassable() );
    assertTrue( r1Edge.getLinkTarget ().getRoom ().equals( door ) );

    room2.connectTo( door, new PlanPoint( 110, 40), new PlanPoint( 110, 70 ) );
  }
  
  // Create full example
  public void testCreateExample() throws Exception {
    Floor ground = new Floor( "Erdgeschoss" );
    
    // Eingang, beginnt bei koordinaten (0,0)
    ArrayList<PlanPoint> pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( 0.00, +0.00, true ) );
    pointList.add( new PlanPoint( 0.00, -0.52, true ) );
    pointList.add( new PlanPoint( 0.00, -2.90, true ) );
    pointList.add( new PlanPoint( 0.00, -3.42, true ) );
    pointList.add( new PlanPoint( 0.00, -5.80, true ) );
    pointList.add( new PlanPoint( 0.00, -6.32, true ) );
    pointList.add( new PlanPoint( 2.38, -6.32, true ) );
    pointList.add( new PlanPoint( 2.90, -6.32, true ) );
    // Treppe
    pointList.add( new PlanPoint( 2.90, -11.48, true ) );
    pointList.add( new PlanPoint( 5.28, -11.48, true ) );
    // Raum weiter
    pointList.add( new PlanPoint( 5.28, -6.32, true ) );
    pointList.add( new PlanPoint( 6.28, -6.32, true ) );
    // Treppe und Plattform unten
    pointList.add( new PlanPoint( 6.28, -11.48, true ) );
    pointList.add( new PlanPoint( 2.90, -11.48, true ) );
    pointList.add( new PlanPoint( 2.90, -13.86, true ) );
    pointList.add( new PlanPoint( 8.66, -13.86, true ) );
    // Raum weiter
    pointList.add( new PlanPoint( 8.66, -6.32, true ) );
    pointList.add( new PlanPoint( 8.66, -2.38, true ) );
    pointList.add( new PlanPoint( 8.66, -0.00, true ) );
    
    Room entry = new Room( ground, "Eingang" );
    entry.defineByPoints( pointList );
    
    pointList = new ArrayList<PlanPoint>();
    double roomXStart = 9.18;
    double roomYStart = -2.9;
    pointList.add( new PlanPoint( roomXStart + 00.00, roomYStart + 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 00.00, roomYStart - 8.58, true ) );
    pointList.add( new PlanPoint( roomXStart + 11.15, roomYStart - 8.58, true ) );
    pointList.add( new PlanPoint( roomXStart + 11.15, roomYStart + 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 03.57, roomYStart + 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 02.38, roomYStart + 0.00, true ) );
    Room class1 = new Room( ground, "Klasse 1");
    class1.defineByPoints( pointList );

    pointList = new ArrayList<PlanPoint>();
    roomXStart += 11.15 + 0.52;
    pointList.add( new PlanPoint( roomXStart + 00.00, roomYStart + 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 00.00, roomYStart - 8.58, true ) );
    pointList.add( new PlanPoint( roomXStart + 11.15, roomYStart - 8.58, true ) );
    pointList.add( new PlanPoint( roomXStart + 11.15, roomYStart + 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 03.57, roomYStart + 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 02.38, roomYStart + 0.00, true ) );
    Room class2 = new Room( ground, "Klasse 2");
    class2.defineByPoints( pointList );

    pointList = new ArrayList<PlanPoint>();
    roomXStart += 11.15 + 0.52;
    pointList.add( new PlanPoint( roomXStart + 00.00, roomYStart + 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 00.00, roomYStart - 8.58, true ) );
    pointList.add( new PlanPoint( roomXStart + 11.15, roomYStart - 8.58, true ) );
    pointList.add( new PlanPoint( roomXStart + 11.15, roomYStart + 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 03.57, roomYStart + 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 02.38, roomYStart + 0.00, true ) );
    Room class3 = new Room( ground, "Klasse 3");
    class3.defineByPoints( pointList );
    
    // Flur
    pointList = new ArrayList<PlanPoint>();
    roomXStart = 8.66;
    roomYStart = 0.00;
    pointList.add( new PlanPoint( roomXStart + 00.00, roomYStart - 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 00.00, roomYStart - 2.38, true ) );
    pointList.add( new PlanPoint( roomXStart + 02.90, roomYStart - 2.38, true ) );
    pointList.add( new PlanPoint( roomXStart + 04.09, roomYStart - 2.38, true ) );
    pointList.add( new PlanPoint( roomXStart + 14.57, roomYStart - 2.38, true ) );
    pointList.add( new PlanPoint( roomXStart + 15.76, roomYStart - 2.38, true ) );
    pointList.add( new PlanPoint( roomXStart + 26.24, roomYStart - 2.38, true ) );
    pointList.add( new PlanPoint( roomXStart + 27.43, roomYStart - 2.38, true ) );
    pointList.add( new PlanPoint( roomXStart + 35.53, roomYStart - 2.38, true ) );
    pointList.add( new PlanPoint( roomXStart + 35.53, roomYStart - 0.00, true ) );
    Room corridor = new Room( ground, "Flur" );
    corridor.defineByPoints( pointList );
    
    // T�ren
    roomXStart += 2.90;
    roomYStart = -2.38;
    pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.52, true ) );
    pointList.add( new PlanPoint( roomXStart + 1.19, roomYStart - 0.52, true ) );
    pointList.add( new PlanPoint( roomXStart + 1.19, roomYStart - 0.00, true ) );
    Room door1 = new Room( ground, "Tuer 1" );
    door1.defineByPoints( pointList );

    roomXStart += 10.48 + 1.19;
    pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.52, true ) );
    pointList.add( new PlanPoint( roomXStart + 1.19, roomYStart - 0.52, true ) );
    pointList.add( new PlanPoint( roomXStart + 1.19, roomYStart - 0.00, true ) );
    Room door2 = new Room( ground, "Tuer 2" );
    door2.defineByPoints( pointList );

    roomXStart += 10.48 + 1.19;
    pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.52, true ) );
    pointList.add( new PlanPoint( roomXStart + 1.19, roomYStart - 0.52, true ) );
    pointList.add( new PlanPoint( roomXStart + 1.19, roomYStart - 0.00, true ) );
    Room door3 = new Room( ground, "Tuer 3" );
    door3.defineByPoints( pointList );

    // Verbinde rooms und doors im erdgeschoss
    class1.connectTo( door1, new PlanPoint( 11.56, -2.90, true ), new PlanPoint( 12.75, -2.90, true ) );
    class2.connectTo( door2, new PlanPoint( 24.42, -2.90, true ), new PlanPoint( 23.23, -2.90, true ) );
    class3.connectTo( door3, new PlanPoint( 36.09, -2.90, true ), new PlanPoint( 34.90, -2.90, true ) );

    corridor.connectTo( door1, new PlanPoint( 11.56, -2.38, true ), new PlanPoint( 12.75, -2.38, true ) );
    corridor.connectTo( door2, new PlanPoint( 24.42, -2.38, true ), new PlanPoint( 23.23, -2.38, true ) );
    corridor.connectTo( door3, new PlanPoint( 36.09, -2.38, true ), new PlanPoint( 34.90, -2.38, true ) );

    // Erzeuge assignments
    Assignment schoolDemo = new Assignment( "Unterricht" );
    p.addAssignment( schoolDemo );

    // Sch�ler
    NormalDistribution diameter = new NormalDistribution( 0.5, 1.0, 0.4, 0.7 );
    NormalDistribution age = new NormalDistribution( 16, 1, 14, 18 );
    NormalDistribution familiarity = new NormalDistribution( 0.8, 1.0, 0.7, 1.0 );
    NormalDistribution panic = new NormalDistribution( 0.5, 1.0, 0.0, 1.0 );
    NormalDistribution decisiveness = new NormalDistribution( 0.3, 1.0, 0.0, 1.0 );
    AssignmentType children = new AssignmentType( "Children", diameter, age, familiarity, panic, decisiveness, 26 );
    schoolDemo.addAssignmentType( children );
    
    // Sch�ler
    diameter = new NormalDistribution( 0.6, 1.0, 0.5, 0.75 );
    age = new NormalDistribution( 40, 1, 29, 65 );
    familiarity = new NormalDistribution( 0.9, 1.0, 0.7, 1.0 );
    panic = new NormalDistribution( 0.25, 1.0, 0.0, 0.6 );
    decisiveness = new NormalDistribution( 0.7, 1.0, 0.5, 1.0 );
    AssignmentType teacher = new AssignmentType( "Teachers", diameter, age, familiarity, panic, decisiveness, 1 );
    schoolDemo.addAssignmentType( teacher );
    
    AssignmentArea class1Assignment = new AssignmentArea( class1, children );
    AssignmentArea class2Assignment = new AssignmentArea( class2, children );
    AssignmentArea class3Assignment = new AssignmentArea( class3, children );
    AssignmentArea class1Teacher = new AssignmentArea( class1, teacher );
    AssignmentArea class2Teacher = new AssignmentArea( class2, teacher );
    AssignmentArea class3Teacher = new AssignmentArea( class3, teacher );
    
    pointList = new ArrayList<PlanPoint>();
    roomXStart = 9.7;
    roomYStart = -3.42;
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 7.54, true ) );
    pointList.add( new PlanPoint( roomXStart + 10.11, roomYStart - 7.54, true ) );
    pointList.add( new PlanPoint( roomXStart + 10.11, roomYStart - 0.00, true ) );
    class1Assignment.defineByPoints( pointList );

    pointList = new ArrayList<PlanPoint>();
    roomXStart = 21.37;
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 7.54, true ) );
    pointList.add( new PlanPoint( roomXStart + 10.11, roomYStart - 7.54, true ) );
    pointList.add( new PlanPoint( roomXStart + 10.11, roomYStart - 0.00, true ) );
    class2Assignment.defineByPoints( pointList );

    pointList = new ArrayList<PlanPoint>();
    roomXStart = 33.04;
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 7.54, true ) );
    pointList.add( new PlanPoint( roomXStart + 10.11, roomYStart - 7.54, true ) );
    pointList.add( new PlanPoint( roomXStart + 10.11, roomYStart - 0.00, true ) );
    class3Assignment.defineByPoints( pointList );

    pointList = new ArrayList<PlanPoint>();
    roomXStart = 19.56;
    roomYStart = -7.44;
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.25, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.25, roomYStart - 0.25, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.25, roomYStart - 0.00, true ) );
    class1Teacher.defineByPoints( pointList );

    pointList = new ArrayList<PlanPoint>();
    roomXStart = 31.23;
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.25, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.25, roomYStart - 0.25, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.25, roomYStart - 0.00, true ) );
    class2Teacher.defineByPoints( pointList );

    pointList = new ArrayList<PlanPoint>();
    roomXStart = 42.9;
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.00, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.00, roomYStart - 0.25, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.25, roomYStart - 0.25, true ) );
    pointList.add( new PlanPoint( roomXStart + 0.25, roomYStart - 0.00, true ) );
    class3Teacher.defineByPoints( pointList );
    
    assertTrue( class1.contains( class1Assignment ) );
    assertTrue( class2.contains( class2Assignment ) );
    assertTrue( class3.contains( class3Assignment ) );
    
    assertTrue( class1.contains( class1Teacher ) );
    assertTrue( class2.contains( class2Teacher ) );
    assertTrue( class3.contains( class3Teacher ) );
    
    // verbinde Flur und Eingangsbereich
    entry.connectTo( corridor, new PlanPoint( 8.66, 0.00, true ), new PlanPoint( 8.66, -2.38, true ));
    
    // Raum oben
    Floor first = new Floor( "1. Stock" );
    pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( 0.00, +0.00, true ) );
    pointList.add( new PlanPoint( 0.00, -6.32, true ) );
    pointList.add( new PlanPoint( 2.38, -6.32, true ) );
    pointList.add( new PlanPoint( 2.90, -6.32, true ) );
    // Treppe zum 2. Stock
//    pointList.defineByPoints( new PlanPoint( 2.90, -11.48, true ) );
//    pointList.defineByPoints( new PlanPoint( 5.28, -11.48, true ) );
    // Raum weiter
    pointList.add( new PlanPoint( 5.28, -6.32, true ) );
    pointList.add( new PlanPoint( 6.28, -6.32, true ) );
    // Treppe und Plattform unten
    pointList.add( new PlanPoint( 6.28, -11.48, true ) );  // Dies wird die Teleport-Edge
    pointList.add( new PlanPoint( 5.28, -11.48, true ) );
    pointList.add( new PlanPoint( 2.90, -11.48, true ) );
    pointList.add( new PlanPoint( 2.90, -13.86, true ) );
    pointList.add( new PlanPoint( 8.66, -13.86, true ) );
    // Raum weiter
    pointList.add( new PlanPoint( 8.66, -6.32, true ) );
    pointList.add( new PlanPoint( 8.66, -2.38, true ) );
    pointList.add( new PlanPoint( 8.66, -0.00, true ) );

    Room upperRoom = new Room( first, "Eingangsbereich oben" );
    upperRoom.defineByPoints( pointList );
    
    // Verbinde Oben und Unten
    // Does not work yet
    //entry.connectToWithTeleportEdge( upperRoom, new PlanPoint( 2.90, -11.48 , true), new PlanPoint( 5.28, -11.48 , true) );
    
    // Erzeuge rooms draussen mit save-area
    pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( +0.00, +0.00, true ) );
    pointList.add( new PlanPoint( +0.00, -0.52, true ) );
    pointList.add( new PlanPoint( +0.00, -2.90, true ) );
    pointList.add( new PlanPoint( +0.00, -3.42, true ) );
    pointList.add( new PlanPoint( +0.00, -5.80, true ) );
    pointList.add( new PlanPoint( +0.00, -6.32, true ) );
    pointList.add( new PlanPoint( -9.48, -6.32, true ) );
    pointList.add( new PlanPoint( -9.48, +0.00, true ) );
    pointList.add( new PlanPoint( -6.32, +0.00, true ) );
    pointList.add( new PlanPoint( -6.32, +3.16, true ) );
    pointList.add( new PlanPoint( -2.00, +3.16, true ) );
    pointList.add( new PlanPoint( -2.00, +0.00, true ) );
    Room outside = new Room( ground, "Au�enbereich" );
    outside.defineByPoints( pointList );
    
    // Verbinde doors
    outside.connectTo( entry, new PlanPoint( +0.00, -0.52, true ), new PlanPoint( +0.00, -2.90, true ) );
    outside.connectTo( entry, new PlanPoint( +0.00, -3.42, true ), new PlanPoint( +0.00, -5.80, true ) );
    
    // Save Area
    SaveArea safe = new SaveArea( outside );
    pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( +0.00, +0.00, true ) );
    pointList.add( new PlanPoint( +0.00, -6.32, true ) );
    pointList.add( new PlanPoint( -6.32, -6.32, true ) );
    pointList.add( new PlanPoint( -6.32, +0.00, true ) );
    safe.defineByPoints( pointList );
    
    // Evacuation Areas
    EvacuationArea eva1 = new EvacuationArea( outside );
    pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( -6.32, -6.32, true ) );
    pointList.add( new PlanPoint( -9.48, -6.32, true ) );
    pointList.add( new PlanPoint( -9.48, +0.00, true ) );
    pointList.add( new PlanPoint( -6.32, +0.00, true ) );
    eva1.defineByPoints( pointList );

    EvacuationArea eva2 = new EvacuationArea( outside );
    pointList = new ArrayList<PlanPoint>();
    pointList.add( new PlanPoint( -6.32, +0.00, true ) );
    pointList.add( new PlanPoint( -6.32, +3.16, true ) );
    pointList.add( new PlanPoint( -2.00, +3.16, true ) );
    pointList.add( new PlanPoint( -2.00, +0.00, true ) );
    eva2.defineByPoints( pointList );

    assertTrue( outside.contains( safe ) );
    assertTrue( outside.contains( eva1 ) );
    assertTrue( outside.contains( eva2 ) );

    // Treppe
//    DelayArea stairsO = new DelayArea( outside, ds.z.DelayArea.DelayType.STAIR );
//    pointList = new ArrayList<PlanPoint>();
//    pointList.defineByPoints( new PlanPoint( +0.00, +0.00, true ) );
//    pointList.defineByPoints( new PlanPoint( -0.00, -6.32, true ) );
//    pointList.defineByPoints( new PlanPoint( -2.00, -6.32, true ) );
//    pointList.defineByPoints( new PlanPoint( -2.00, +0.00, true ) );
//    stairsO.defineByPoints( pointList );
    
    //Project p = new Project ();
    
    p.getBuildingPlan ().addFloor( ground );
    p.getBuildingPlan().addFloor( first );

    //ConcreteAssignment c = schoolDemo.createConcreteAssignment( 100 );
		p.save( new File( ".\\examples\\stgh.xml"));
  }
  
  public void testConvertToCA() throws Exception{
	  testCreateExample();
	  BuildingPlan bp = p.getBuildingPlan();
	  RoomRaster<RoomRasterSquare> rasterer;
	  List<Floor> floors = bp.getFloors();
	  CellularAutomaton ca = new CellularAutomaton();
	  
	  for(Floor f : floors){
		  for(Room r : f.getRooms()){
			  rasterer = new RoomRaster( RoomRasterSquare.class, r, 0.4);
			  rasterer.rasterize();
			  //create new room for the ca
			  ds.ca.Room newRoom = new ds.ca.Room(rasterer.getColumnCount(),rasterer.getRowCount(),f.getName(), 0);
			  //create room cell if the square is inside this room, otherwise null
			  for(RoomRasterSquare s : rasterer.insideSquares()){
				  newRoom.setCell(new RoomCell(s.getX(),newRoom.getHeight()-1 - s.getY()));
			  }
			  ca.addRoom(newRoom);
		  }
	  }
	  
	  //output ('x' is a null cell, '#' a room cell)
	  for(ds.ca.Room r : ca.getRooms()){
		  System.out.println(r.getFloor());
		  for(int i = 0; i < r.getWidth(); i++){
			  for(int j = 0; j < r.getHeight(); j++){
				  System.out.print((r.getCell(i, j)==null ? "x" : "#"));
			  }
			  System.out.println("");
		  }
		  System.out.println("");
	  }
  }
}
