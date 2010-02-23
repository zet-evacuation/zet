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
/*
 * Created on 13.12.2007
 *
 */
package converter;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import util.random.distributions.NormalDistribution;

import converter.ZToCAConverter.ConversionNotSupportedException;

import ds.Project;
import ds.z.Assignment;
import ds.z.AssignmentArea;
import ds.z.AssignmentType;
import ds.z.BuildingPlan;
import ds.z.ConcreteAssignment;
import ds.z.Floor;
import ds.z.PlanPoint;
import ds.ca.CellularAutomaton;
import ds.z.Room;

/**
 * @author Daniel Pluempe
 *
 */
public class applyConcreteAssignmentTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link converter.RasterContainerCreator#ZToCARasterContainer(ds.z.BuildingPlan)}.
     */
    @Test
    public void testApplyConcreteAssignment() throws Exception{
    	Project p = new Project();
		Floor floor = new Floor();
		p.getBuildingPlan().addFloor(floor);

		ArrayList<PlanPoint> points = new ArrayList<PlanPoint>();

		Room pg_room = new Room( floor, "PG-Raum");
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(3.0,3.0) );
		points.add( new PlanPoint(8.6,3.0) );
		points.add( new PlanPoint(8.6,8.2) );
		points.add( new PlanPoint(3.0,8.2) );
		pg_room.defineByPoints( points );

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
		pointList.add( new PlanPoint( 8.0, 4.0 ) );
		pointList.add( new PlanPoint( 8.0, 8.0 ) );
		pointList.add( new PlanPoint( 4.0, 8.0 ) );
		tischAssignment.defineByPoints( pointList );

//		points = new ArrayList<PlanPoint>();
//		points.defineByPoints( new PlanPoint(6.6,7.4));
//		points.defineByPoints( new PlanPoint(7.0,7.4));
//		points.defineByPoints( new PlanPoint(7.0,7.8));
//		points.defineByPoints( new PlanPoint(6.6,7.8));
//		iA = new InaccessibleArea( pg_room );
//		iA.defineByPoints(points);
		
		// den raum vorher zu rastern....
		pg_room.rasterize();

		ConcreteAssignment newCA = uni.createConcreteAssignment( 400 );
        //BuildingPlan plan = RasterContainerCreator.makeBuildingPlanExampleWithDifferentAreas();
        //ZToCARasterContainer rc = RasterContainerCreator.getInstance().ZToCARasterContainer(plan);
        CellularAutomaton ca = null;
        try{
            ca = ZToCAConverter.getInstance().convert(p.getBuildingPlan());
        } catch(ConversionNotSupportedException e){
            throw new Exception("The building plan contains features that cannot be converted to a CA. " + e.getMessage());
        }
        
        ZToCAConverter.getInstance().applyConcreteAssignment(newCA);
        
        for(ds.ca.Room room : ca.getRooms()){
            System.out.println(room.graphicalToString());
            System.out.println();
        }
        
//        Project project = new Project();
//        for(Floor floor : plan.getFloors()){
//            project.getBuildingPlan().addFloor(floor);
//        }
//        project.save(new File(".\\examples\\presentation.xml"));
    }

}
