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
package ds.z;

import java.util.ArrayList;
import java.util.HashSet;

import util.random.distributions.NormalDistribution;
import ds.Project;
import junit.framework.*;
import java.io.File;

public class ConcreteAssignmentTest extends TestCase {


	/** Creates a new instance of RasterizationTest */
	public ConcreteAssignmentTest() {
		super();
	}

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testOneCA() throws Exception{
		Project p = new Project();
		Floor floor = new Floor();
		p.getPlan().addFloor(floor);

		ArrayList<PlanPoint> points = new ArrayList<PlanPoint>();

		Room pg_room = new Room( floor, "PG-Raum");
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(3.0,3.0) );
		points.add( new PlanPoint(8.6,3.0) );
		points.add( new PlanPoint(8.6,8.2) );
		points.add( new PlanPoint(3.0,8.2) );
		pg_room.add( points );

		// Erzeuge assignments
		Assignment uni = new Assignment( "Uni" );
		p.addAssignment( uni );

		// students
		NormalDistribution diameter = new NormalDistribution( 0.5, 1.0, 0.4, 0.7 );
		NormalDistribution age = new NormalDistribution( 16, 1, 14, 18 );
		NormalDistribution familiarity = new NormalDistribution( 0.8, 1.0, 0.7, 1.0 );
		NormalDistribution panic = new NormalDistribution( 0.5, 1.0, 0.0, 1.0 );
		NormalDistribution decisiveness = new NormalDistribution( 0.3, 1.0, 0.0, 1.0 );
		AssignmentType students = new AssignmentType( "Students", diameter, age, familiarity, panic, decisiveness, 15 );
		uni.addAssignmentType( students );

		AssignmentArea tischAssignment = new AssignmentArea( pg_room, students );
		ArrayList<PlanPoint> pointList = new ArrayList<PlanPoint>();
		pointList.add( new PlanPoint( 4.0, 4.0 ) );
		pointList.add( new PlanPoint( 7.0, 4.0 ) );
		pointList.add( new PlanPoint( 7.0, 7.0 ) );
		pointList.add( new PlanPoint( 4.0, 7.0 ) );
		tischAssignment.add( pointList );

//		points = new ArrayList<PlanPoint>();
//		points.add( new PlanPoint(6.6,7.4));
//		points.add( new PlanPoint(7.0,7.4));
//		points.add( new PlanPoint(7.0,7.8));
//		points.add( new PlanPoint(6.6,7.8));
//		iA = new InaccessibleArea( pg_room );
//		iA.add(points);
		
		// den raum vorher zu rastern....
		pg_room.rasterize();

		ConcreteAssignment newCA = uni.createConcreteAssignment( 400 );
		
		//positionen ausgeben
		
//		int i = 1;
//		System.out.println("number of persons: "+ newCA.getPersons().size());
//		for(Person person : newCA.getPersons()){
//			System.out.println("Person"+i+" " +person.getPosition());
//			i++;
//		}
		//prüfung auf doppelte einträge
		HashSet<PlanPoint> positionen = new HashSet<PlanPoint>();
		for(Person person : newCA.getPersons()){
			if(!positionen.add(person.getPosition())){
				fail();
			}
		}
		
		//p.save (new File ("D:\\Semester9\\PG\\pg517.xml"));
		//p.save( new File( "assignment.xml"));

	}
}
