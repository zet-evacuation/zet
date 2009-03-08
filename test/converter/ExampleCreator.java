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
package converter;

import ds.Project;
import ds.z.Assignment;
import ds.z.AssignmentArea;
import ds.z.AssignmentType;
import ds.z.Barrier;
import ds.z.BuildingPlan;
import ds.z.DelayArea;
import ds.z.DelayArea.DelayType;
import ds.z.EvacuationArea;
import ds.z.Floor;
import ds.z.InaccessibleArea;
import ds.z.PlanPoint;
import ds.z.Room;
import ds.z.RoomEdge;
import java.io.File;
import java.util.ArrayList;
import junit.framework.TestCase;
import util.random.distributions.NormalDistribution;


public class ExampleCreator extends TestCase {
	/** Creates a new instance of RasterizationTest */
	public ExampleCreator() {
		super();
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testPgRaum() throws Exception {
		// 5x5-Raum mit rasterisierten Koordinaten erzeugen
		BuildingPlan bp = new BuildingPlan();
		Floor floor = new Floor();
		bp.addFloor( floor );

		ArrayList<PlanPoint> points = new ArrayList<PlanPoint>();
		InaccessibleArea iA;

		Room pg_room = new Room( floor, "PG-Raum" );
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 3.0, 3.0 ) );
		points.add( new PlanPoint( 8.6, 3.0 ) );
		points.add( new PlanPoint( 8.6, 8.2 ) );
		points.add( new PlanPoint( 3.0, 8.2 ) );
		pg_room.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 3.4, 3.4 ) );
		points.add( new PlanPoint( 3.8, 3.4 ) );
		points.add( new PlanPoint( 3.8, 5.4 ) );
		points.add( new PlanPoint( 3.4, 5.4 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 3.8, 3.4 ) );
		points.add( new PlanPoint( 4.6, 3.4 ) );
		points.add( new PlanPoint( 4.6, 4.6 ) );
		points.add( new PlanPoint( 3.8, 4.6 ) );
		points.add( new PlanPoint( 3.8, 4.2 ) );
		points.add( new PlanPoint( 4.2, 4.2 ) );
		points.add( new PlanPoint( 4.2, 3.8 ) );
		points.add( new PlanPoint( 3.8, 3.8 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 5.0, 3.4 ) );
		points.add( new PlanPoint( 6.6, 3.4 ) );
		points.add( new PlanPoint( 6.6, 3.8 ) );
		points.add( new PlanPoint( 5.4, 3.8 ) );
		points.add( new PlanPoint( 5.4, 5.0 ) );
		points.add( new PlanPoint( 6.2, 5.0 ) );
		points.add( new PlanPoint( 6.2, 4.6 ) );
		points.add( new PlanPoint( 5.8, 4.6 ) );
		points.add( new PlanPoint( 5.8, 4.2 ) );
		points.add( new PlanPoint( 6.6, 4.2 ) );
		points.add( new PlanPoint( 6.6, 5.4 ) );
		points.add( new PlanPoint( 5.0, 5.4 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 3.4, 5.8 ) );
		points.add( new PlanPoint( 4.6, 5.8 ) );
		points.add( new PlanPoint( 4.6, 6.2 ) );
		points.add( new PlanPoint( 3.8, 6.2 ) );
		points.add( new PlanPoint( 3.8, 6.6 ) );
		points.add( new PlanPoint( 4.6, 6.6 ) );
		points.add( new PlanPoint( 4.6, 7.8 ) );
		points.add( new PlanPoint( 3.4, 7.8 ) );
		points.add( new PlanPoint( 3.4, 7.4 ) );
		points.add( new PlanPoint( 4.2, 7.4 ) );
		points.add( new PlanPoint( 4.2, 7.0 ) );
		points.add( new PlanPoint( 3.4, 7.0 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 5.0, 6.6 ) );
		points.add( new PlanPoint( 5.4, 6.6 ) );
		points.add( new PlanPoint( 5.4, 7.0 ) );
		points.add( new PlanPoint( 5.0, 7.0 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 5.4, 6.2 ) );
		points.add( new PlanPoint( 5.8, 6.2 ) );
		points.add( new PlanPoint( 5.8, 6.6 ) );
		points.add( new PlanPoint( 5.4, 6.6 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 5.8, 5.8 ) );
		points.add( new PlanPoint( 6.2, 5.8 ) );
		points.add( new PlanPoint( 6.2, 7.8 ) );
		points.add( new PlanPoint( 5.8, 7.8 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 6.6, 5.8 ) );
		points.add( new PlanPoint( 8.2, 5.8 ) );
		points.add( new PlanPoint( 8.2, 6.2 ) );
		points.add( new PlanPoint( 7.8, 6.2 ) );
		points.add( new PlanPoint( 7.8, 6.6 ) );
		points.add( new PlanPoint( 7.4, 6.6 ) );
		points.add( new PlanPoint( 7.4, 6.2 ) );
		points.add( new PlanPoint( 6.6, 6.2 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 6.6, 6.6 ) );
		points.add( new PlanPoint( 8.2, 6.6 ) );
		points.add( new PlanPoint( 8.2, 7.0 ) );
		points.add( new PlanPoint( 6.6, 7.0 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 7.0, 7.0 ) );
		points.add( new PlanPoint( 7.4, 7.0 ) );
		points.add( new PlanPoint( 7.4, 7.4 ) );
		points.add( new PlanPoint( 7.0, 7.4 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 6.6, 7.4 ) );
		points.add( new PlanPoint( 7.0, 7.4 ) );
		points.add( new PlanPoint( 7.0, 7.8 ) );
		points.add( new PlanPoint( 6.6, 7.8 ) );
		iA = new InaccessibleArea( pg_room );
		iA.add( points );

		// Store to harddisk    
		Project p = new Project();

		p.getPlan().addFloor( floor );

		//p.save (new File ("D:\\Semester9\\PG\\pg517.xml"));
		p.save( new File( ".\\examples\\pg517.xml" ) );

	}

	public void testraum() throws Exception {
		// 5x5-Raum mit rasterisierten Koordinaten erzeugen
		BuildingPlan bp = new BuildingPlan();
		Floor floor = new Floor();
		bp.addFloor( floor );

		Room room = new Room( floor, "Testraum" );

		ArrayList<PlanPoint> points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 0.4, 0.4 ) );
		points.add( new PlanPoint( 2.4, 0.4 ) );
		points.add( new PlanPoint( 2.4, 1.2 ) );
		points.add( new PlanPoint( 2.4, 2.4 ) );
		points.add( new PlanPoint( 2.0, 2.4 ) );
		points.add( new PlanPoint( 1.2, 2.4 ) );
		points.add( new PlanPoint( 0.4, 2.4 ) );
		room.add( points );

		Room room2 = new Room( floor, "Anderer Raum" );
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 2.4, 2.4 ) );
		points.add( new PlanPoint( 2.0, 2.4 ) );
		points.add( new PlanPoint( 1.2, 2.4 ) );
		points.add( new PlanPoint( 1.2, 3.2 ) );
		points.add( new PlanPoint( 2.0, 3.2 ) );
		points.add( new PlanPoint( 2.4, 3.2 ) );
		room2.add( points );
		room2.connectTo( room, new PlanPoint( 2.0, 2.4, true ), new PlanPoint( 1.2, 2.4, true ) );

		Room room3 = new Room( floor, "Nochn Raum" );
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 2.4, 0.4 ) );
		points.add( new PlanPoint( 4.0, 0.4 ) );
		points.add( new PlanPoint( 4.0, 1.2 ) );
		points.add( new PlanPoint( 2.4, 1.2 ) );
		room3.add( points );
		room3.connectTo( room, new PlanPoint( 2.4, 0.4, true ), new PlanPoint( 2.4, 1.2, true ) );

		// To exception is allowed here!
		room.checkRasterized();

		// Inaccessible Areas
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 1.2, 0.4 ) );
		points.add( new PlanPoint( 1.6, 0.4 ) );
		points.add( new PlanPoint( 1.6, 0.8 ) );
		points.add( new PlanPoint( 1.2, 0.8 ) );
		InaccessibleArea iA = new InaccessibleArea( room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 1.2, 1.2 ) );
		points.add( new PlanPoint( 1.6, 1.2 ) );
		points.add( new PlanPoint( 1.6, 1.6 ) );
		points.add( new PlanPoint( 2.0, 1.6 ) );
		points.add( new PlanPoint( 2.0, 2.0 ) );
		points.add( new PlanPoint( 1.2, 2.0 ) );
		iA = new InaccessibleArea( room );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 0.4, 1.6 ) );
		points.add( new PlanPoint( 0.8, 1.6 ) );
		points.add( new PlanPoint( 0.8, 2.0 ) );
		points.add( new PlanPoint( 0.8, 2.4 ) );
		Barrier ba = new Barrier( room );
		ba.add( points );

		// DelayArea
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 0.8, 0.8 ) );
		points.add( new PlanPoint( 2.0, 0.8 ) );
		points.add( new PlanPoint( 2.0, 1.2 ) );
		points.add( new PlanPoint( 0.8, 1.2 ) );
		DelayArea dA = new DelayArea( room, DelayArea.DelayType.OTHER, 1.0 / 3.0 );
		dA.add( points );

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 1.6, 0.8 ) );
		points.add( new PlanPoint( 2.4, 0.8 ) );
		points.add( new PlanPoint( 2.4, 2.0 ) );
		points.add( new PlanPoint( 1.6, 2.0 ) );
		dA = new DelayArea( room, DelayArea.DelayType.OTHER, 0.5 );
		dA.add( points );

		// Store to harddisk    
		Project p = new Project();

		p.getPlan().addFloor( floor );

		//p.save (new File ("D:\\Semester9\\PG\\pg517.xml"));
		p.save( new File( ".\\examples\\testraum.xml" ) );
	}

	public static void testAnotherExample() throws Exception {
		BuildingPlan bp = new BuildingPlan();
		Floor floor = new Floor( "Erdgeschoss" );
		bp.addFloor( floor );

		Room room = new Room( floor, "Groesserer Raum" );

		ArrayList<PlanPoint> points;
		InaccessibleArea iA;
		DelayArea dA;
		EvacuationArea eA;

		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint( 0.4, 0.4 ) );
		points.add( new PlanPoint( 4.0, 0.4 ) );
		points.add( new PlanPoint( 4.0, 4.0 ) );
		points.add( new PlanPoint( 0.4, 4.0 ) );
		room.add( points );

		points = new ArrayList<PlanPoint>();
		iA = new InaccessibleArea( room );
		points.add( new PlanPoint( 0.8, 3.6 ) );
		points.add( new PlanPoint( 3.6, 3.6 ) );
		points.add( new PlanPoint( 3.6, 4.0 ) );
		points.add( new PlanPoint( 0.8, 4.0 ) );
		iA.add( points );

		points = new ArrayList<PlanPoint>();
		dA = new DelayArea( room, DelayType.OBSTACLE );
		points.add( new PlanPoint( 0.8, 2.4 ) );
		points.add( new PlanPoint( 1.6, 2.4 ) );
		points.add( new PlanPoint( 1.6, 3.2 ) );
		points.add( new PlanPoint( 0.8, 3.2 ) );
		dA.add( points );

		points = new ArrayList<PlanPoint>();
		eA = new EvacuationArea( room );
		points.add( new PlanPoint( 3.4, 2.4 ) );
		points.add( new PlanPoint( 4.0, 2.4 ) );
		points.add( new PlanPoint( 4.0, 3.2 ) );
		points.add( new PlanPoint( 3.4, 3.2 ) );
		eA.add( points );

		Project p = new Project();

		p.getPlan().addFloor( floor );

		//p.save (new File ("D:\\Semester9\\PG\\pg517.xml"));
		p.save( new File( ".\\examples\\delayExitRoom.xml" ) );

	}

	public void testDefaultRoom() throws Exception {
		Project p = new Project();
		Floor f1 = new Floor( "Erdgeschoss" );
		p.getPlan().addFloor( f1 );
		Room f1_r1 = new Room( f1, "Diele" );
		RoomEdge f1_e1 = new RoomEdge( new PlanPoint( 400, 400 ), new PlanPoint( 400, 2400 ), f1_r1 );
		RoomEdge f1_e2 = new RoomEdge( new PlanPoint( 400, 2400 ), new PlanPoint( 2400, 2400 ), f1_r1 );
		RoomEdge f1_e10 = new RoomEdge( new PlanPoint( 2400, 2400 ), new PlanPoint( 2400, 1800 ), f1_r1 );
		RoomEdge f1_e11 = new RoomEdge( new PlanPoint( 2400, 1800 ), new PlanPoint( 2400, 1000 ), f1_r1 );
		RoomEdge f1_e3 = new RoomEdge( new PlanPoint( 2400, 1000 ), new PlanPoint( 2400, 400 ), f1_r1 );
		RoomEdge f1_e4 = new RoomEdge( new PlanPoint( 2400, 400 ), new PlanPoint( 400, 400 ), f1_r1 );
		Room f1_r2 = new Room( f1, "T�r ins Nichts" );
		RoomEdge f1_e5 = new RoomEdge( new PlanPoint( 2400, 1000 ), new PlanPoint( 2800, 1000 ), f1_r2 );
		RoomEdge f1_e6 = new RoomEdge( new PlanPoint( 2800, 1000 ), new PlanPoint( 2800, 1800 ), f1_r2 );
		RoomEdge f1_e7 = new RoomEdge( new PlanPoint( 2800, 1800 ), new PlanPoint( 2400, 1800 ), f1_r2 );
		RoomEdge f1_e8 = new RoomEdge( new PlanPoint( 2400, 1800 ), new PlanPoint( 2400, 1000 ), f1_r2 );
		f1_r2.connectTo( f1_r1, f1_e8 );

		Floor f2 = new Floor( "1. Stock" );
		p.getPlan().addFloor( f2 );
		Room f2_r1 = new Room( f2, "Wohnraum" );
		RoomEdge f2_e1 = new RoomEdge( new PlanPoint( 800, 800 ), new PlanPoint( 800, 2000 ), f2_r1 );
		RoomEdge f2_e2 = new RoomEdge( new PlanPoint( 800, 2000 ), new PlanPoint( 2000, 2000 ), f2_r1 );
		RoomEdge f2_e3 = new RoomEdge( new PlanPoint( 2000, 2000 ), new PlanPoint( 2000, 800 ), f2_r1 );
		RoomEdge f2_e4 = new RoomEdge( new PlanPoint( 2000, 800 ), new PlanPoint( 800, 800 ), f2_r1 );

		// Erzeuge assignments

		Assignment schoolDemo = new Assignment( "Unterricht" );
		p.addAssignment( schoolDemo );


		Assignment schoolDemo2 = new Assignment( "Pause" );
		p.addAssignment( schoolDemo2 );


		// Sch�ler
		NormalDistribution diameter = new NormalDistribution( 0.5, 1.0, 0.4, 0.7 );
		NormalDistribution age = new NormalDistribution( 16, 1, 14, 18 );
		NormalDistribution familiarity = new NormalDistribution( 0.8, 1.0, 0.7, 1.0 );
		NormalDistribution panic = new NormalDistribution( 0.5, 1.0, 0.0, 1.0 );
		NormalDistribution decisiveness = new NormalDistribution( 0.3, 1.0, 0.0, 1.0 );
		AssignmentType children = new AssignmentType( "Children", diameter, age, familiarity, panic, decisiveness, 1 );
		schoolDemo.addAssignmentType( children );

		AssignmentType parents = new AssignmentType( "Parents", diameter, age, familiarity, panic, decisiveness, 1 );
		schoolDemo.addAssignmentType( parents );

		AssignmentType rest = new AssignmentType( "Rest", diameter, age, familiarity, panic, decisiveness, 2 );
		schoolDemo.addAssignmentType( rest );

		AssignmentType rest2 = new AssignmentType( "Rest2", diameter, age, familiarity, panic, decisiveness, 3 );
		schoolDemo2.addAssignmentType( rest2 );

		AssignmentArea dieleAssignment = new AssignmentArea( f1_r1, children );
		ArrayList<PlanPoint> pointList = new ArrayList<PlanPoint>();
		pointList.add( new PlanPoint( 500, 1000 ) );
		pointList.add( new PlanPoint( 1000, 1000 ) );
		pointList.add( new PlanPoint( 1000, 500 ) );
		pointList.add( new PlanPoint( 500, 500 ) );
		dieleAssignment.add( pointList );
		p.save( new File( ".\\examples\\default_editor_demo.xml" ) );

	}
}
