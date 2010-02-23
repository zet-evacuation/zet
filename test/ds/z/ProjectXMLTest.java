/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ds.z;

/** This class serves as a simple testing class which instantiates "Project" and some other
 * objects from ds.z, puts them all together and writes them to XML.
 *
 * @author Timon Kelter
 */
import ds.Project;
import java.io.File;
import java.util.ArrayList;
import junit.framework.TestCase;
import util.random.distributions.NormalDistribution;

public class ProjectXMLTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testCreation() throws Exception {
		Project p = new Project();
		Floor fl = new Floor( "Floor 1" );
		p.getBuildingPlan().addFloor( fl );
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
		pointList.add( new PlanPoint( 200, 100 ) );
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

		// Verbinden der Räume
		RoomEdge r1Edge = room1.getEdge( new PlanPoint( 100, 40 ), new PlanPoint( 100, 70 ) );
		assertFalse( r1Edge.isPassable() );
		assertNull( r1Edge.getLinkTarget () );
		assertTrue( r1Edge.getRoom ().equals( room1 ) );
		room1.connectTo( door, new PlanPoint( 100, 40 ), new PlanPoint( 100, 70 ) );
		RoomEdge r2Edge = door.getEdge( new PlanPoint( 100, 40 ), new PlanPoint( 100, 70 ) );
		assertTrue( r1Edge.equals( r2Edge ) );
		assertTrue( r1Edge.isPassable() );
		assertTrue( r2Edge.isPassable() );
		assertTrue( r1Edge.getLinkTarget ().getRoom ().equals( door ) );

		room2.connectTo( door, new PlanPoint( 110, 40 ), new PlanPoint( 110, 70 ) );

		SaveArea a = new SaveArea( room1 );
		pointList = new ArrayList<PlanPoint>();
		pointList.add( new PlanPoint( 10, 10 ) );
		pointList.add( new PlanPoint( 10, 100 ) );
		pointList.add( new PlanPoint( 50, 100 ) );
		pointList.add( new PlanPoint( 50, 10 ) );
		a.defineByPoints( pointList );

		p.save( new File( ".\\examples\\test_simple.xml" ) );

		assertNotNull( p );
	}

	public void testEditorExample() throws Exception {
		// Create a sample building
		Project p = new Project();
		Floor f1 = new Floor( "Erdgeschoss" );
		p.getBuildingPlan().addFloor( f1 );
		Room f1_r1 = new Room( f1, "Diele" );
		RoomEdge f1_e1 = new RoomEdge( new PlanPoint( 400, 400 ), new PlanPoint( 400, 2400 ), f1_r1 );
		RoomEdge f1_e2 = new RoomEdge( new PlanPoint( 400, 2400 ), new PlanPoint( 2400, 2400 ), f1_r1 );
		RoomEdge f1_e10 = new RoomEdge( new PlanPoint( 2400, 2400 ), new PlanPoint( 2400, 1800 ), f1_r1 );
		RoomEdge f1_e11 = new RoomEdge( new PlanPoint( 2400, 1800 ), new PlanPoint( 2400, 1000 ), f1_r1 );
		RoomEdge f1_e3 = new RoomEdge( new PlanPoint( 2400, 1000 ), new PlanPoint( 2400, 400 ), f1_r1 );
		RoomEdge f1_e4 = new RoomEdge( new PlanPoint( 2400, 400 ), new PlanPoint( 400, 400 ), f1_r1 );
		Room f1_r2 = new Room( f1, "Tür ins Nichts" );
		RoomEdge f1_e5 = new RoomEdge( new PlanPoint( 2400, 1000 ), new PlanPoint( 2800, 1000 ), f1_r2 );
		RoomEdge f1_e6 = new RoomEdge( new PlanPoint( 2800, 1000 ), new PlanPoint( 2800, 1800 ), f1_r2 );
		RoomEdge f1_e7 = new RoomEdge( new PlanPoint( 2800, 1800 ), new PlanPoint( 2400, 1800 ), f1_r2 );
		RoomEdge f1_e8 = new RoomEdge( new PlanPoint( 2400, 1800 ), new PlanPoint( 2400, 1000 ), f1_r2 );
		f1_r2.connectTo( f1_r1, f1_e8 );

		Floor f2 = new Floor( "1. Stock" );
		p.getBuildingPlan().addFloor( f2 );
		Room f2_r1 = new Room( f2, "Wohnraum" );
		RoomEdge f2_e1 = new RoomEdge( new PlanPoint( 800, 800 ), new PlanPoint( 800, 2000 ), f2_r1 );
		RoomEdge f2_e2 = new RoomEdge( new PlanPoint( 800, 2000 ), new PlanPoint( 2000, 2000 ), f2_r1 );
		RoomEdge f2_e3 = new RoomEdge( new PlanPoint( 2000, 2000 ), new PlanPoint( 2000, 800 ), f2_r1 );
		RoomEdge f2_e4 = new RoomEdge( new PlanPoint( 2000, 800 ), new PlanPoint( 800, 800 ), f2_r1 );

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

		AssignmentArea dieleAssignment = new AssignmentArea( f1_r1, children );
		ArrayList<PlanPoint> pointList = new ArrayList<PlanPoint>();
		pointList.add( new PlanPoint( 500, 1000 ) );
		pointList.add( new PlanPoint( 1000, 1000 ) );
		pointList.add( new PlanPoint( 1000, 500 ) );
		pointList.add( new PlanPoint( 500, 500 ) );
		dieleAssignment.defineByPoints( pointList );

		//p.save (new File ("C:\\default_editor_demo.xml"));	
		p.save( new File( ".\\examples\\default_editor_demo.xml" ) );
	}
}
