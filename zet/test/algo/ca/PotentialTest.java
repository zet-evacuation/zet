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
 * Created on 13.12.2007
 *
 */
package algo.ca;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import converter.ZToCAConverter;

import ds.Project;
import ds.z.EvacuationArea;
import ds.z.InaccessibleArea;
import ds.z.Floor;
import ds.z.PlanPoint;
import ds.ca.CellularAutomaton;
import ds.z.Room;
import algo.ca.PotentialController;
import ds.ca.StaticPotential;

/**
 * @author Daniel Pluempe
 *
 */
public class PotentialTest {

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
    public void testStaticPotential() throws Exception{
    	Project project = new Project();
		Floor floor = new Floor();
		project.getPlan().addFloor(floor);

		ArrayList<PlanPoint> points = new ArrayList<PlanPoint>();

		Room aussen = new Room( floor, "Aussen");
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(0.4, 4.0) );
		points.add( new PlanPoint(2.0, 4.0) );
		points.add( new PlanPoint(2.0, 3.2) );
		points.add( new PlanPoint(2.0, 2.4) );
		points.add( new PlanPoint(2.0, 1.2) );
		points.add( new PlanPoint(0.4, 1.2) );
		aussen.add( points );
		
		EvacuationArea evacuationArea = new EvacuationArea(aussen);
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(0.4, 4.0) );
		points.add( new PlanPoint(1.2, 4.0) );
		points.add( new PlanPoint(1.2, 1.2) );
		points.add( new PlanPoint(0.4, 1.2) );
		evacuationArea.add( points );
		
		Room aussenTuer = new Room(floor, "Aussentuer");
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(2.0, 2.4) );
		points.add( new PlanPoint(2.4, 2.4) );
		points.add( new PlanPoint(2.4, 3.2) );
		points.add( new PlanPoint(2.0, 3.2) );
		aussenTuer.add( points );
		
		Room flur = new Room(floor, "Flur");
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(2.4, 2.4) );
		points.add( new PlanPoint(3.2, 2.4) );
		points.add( new PlanPoint(4.0, 2.4) );
		points.add( new PlanPoint(8.0, 2.4) );
		points.add( new PlanPoint(8.0, 3.2) );
		points.add( new PlanPoint(6.8, 3.2) );
		points.add( new PlanPoint(6.0, 3.2) );
		points.add( new PlanPoint(4.8, 3.2) );
		points.add( new PlanPoint(4.0, 3.2) );
		points.add( new PlanPoint(2.4, 3.2) );
		flur.add( points );
		
		EvacuationArea evacuationArea2 = new EvacuationArea(flur);
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(7.2, 2.4) );
		points.add( new PlanPoint(7.2, 3.2) );
		points.add( new PlanPoint(8.0, 3.2) );
		points.add( new PlanPoint(8.0, 2.4) );
		evacuationArea2.add( points );
		
		Room pg1 = new Room(floor,"PG1");
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(3.2, 3.2) );
		points.add( new PlanPoint(4.0, 3.2) );
		points.add( new PlanPoint(4.8, 3.2) );
		points.add( new PlanPoint(5.2, 3.2) );
		points.add( new PlanPoint(5.2, 5.6) );
		points.add( new PlanPoint(3.2, 5.6) );
		pg1.add( points );

		Room pg2 = new Room(floor,"PG2");
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(5.2, 3.2) );
		points.add( new PlanPoint(6.0, 3.2) );
		points.add( new PlanPoint(6.8, 3.2) );
		points.add( new PlanPoint(8.0, 3.2) );
		points.add( new PlanPoint(6.8, 5.6) );
		points.add( new PlanPoint(5.2, 5.6) );
		pg2.add( points );

		Room foyer = new Room(floor,"Foyer");
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(2.8, 2.4) );
		points.add( new PlanPoint(3.2, 2.4) );
		points.add( new PlanPoint(4.0, 2.4) );
		points.add( new PlanPoint(5.2, 2.4) );
		points.add( new PlanPoint(5.2, 1.2) );
		points.add( new PlanPoint(6.0, 1.2) );
		points.add( new PlanPoint(6.8, 1.2) );
		points.add( new PlanPoint(7.2, 1.2) );
		points.add( new PlanPoint(8.0, 1.2) );
		points.add( new PlanPoint(8.4, 1.2) );
		points.add( new PlanPoint(8.4, 0.4) );
		points.add( new PlanPoint(2.8, 0.4) );
		foyer.add( points );
		
		InaccessibleArea inacessibleArea = new InaccessibleArea(foyer);
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(7.2, 2.4) );
		points.add( new PlanPoint(7.2, 3.2) );
		points.add( new PlanPoint(8.0, 3.2) );
		points.add( new PlanPoint(8.0, 2.4) );
		inacessibleArea.add( points );

		Room m = new Room(floor,"Toilette M");
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(5.2, 1.2) );
		points.add( new PlanPoint(6.0, 1.2) );
		points.add( new PlanPoint(6.8, 1.2) );
		points.add( new PlanPoint(6.8, 2.4) );
		points.add( new PlanPoint(5.2, 2.4) );
		m.add( points );
		
		Room w = new Room(floor,"Toilette W");
		points = new ArrayList<PlanPoint>();
		points.add( new PlanPoint(6.8, 1.2) );
		points.add( new PlanPoint(7.2, 1.2) );
		points.add( new PlanPoint(8.0, 1.2) );
		points.add( new PlanPoint(8.4, 1.2) );
		points.add( new PlanPoint(8.4, 2.4) );
		points.add( new PlanPoint(6.8, 2.4) );
		w.add( points );

		aussen.connectTo(aussenTuer, new PlanPoint(2.0, 2.4), new PlanPoint(2.0, 3.2));
		aussenTuer.connectTo(flur, new PlanPoint(2.4, 2.4), new PlanPoint(2.4, 3.2));
		flur.connectTo(foyer, new PlanPoint(3.2, 2.4), new PlanPoint(4.0, 2.4));
		flur.connectTo(pg1, new PlanPoint(4.0, 3.2), new PlanPoint(4.8, 3.2));
		flur.connectTo(pg2, new PlanPoint(6.0, 3.2), new PlanPoint(6.8, 3.2));
		foyer.connectTo(m, new PlanPoint(6.0, 1.2), new PlanPoint(6.8, 1.2));
		foyer.connectTo(w, new PlanPoint(7.2, 1.2), new PlanPoint(8.0, 1.2));
		
		CellularAutomaton ca = ZToCAConverter.getInstance().convert(project.getPlan());

		PotentialController pm = new SPPotentialController(ca);
		
		ArrayList<StaticPotential> allSP = new ArrayList<StaticPotential>();
		for(StaticPotential sp : ca.getPotentialManager().getStaticPotentials()){
			allSP.add(sp);
		}
		ca.getPotentialManager().addStaticPotential(pm.mergePotentials(allSP)); 
		
		for(StaticPotential sp : pm.getPm().getStaticPotentials()){
			System.out.println("=============StaticPotential "+ sp.getID() +"===========");
			for(ds.ca.Room room : ca.getRooms()){
				for(int x=0; x < room.getHeight(); x++){
					System.out.print("|");
					for(int y=0; y < room.getWidth(); y++){
						if(room.getCell(y,x) != null){
							System.out.print(sp.getPotential(room.getCell(y,x)) + "|");
						}
					}
					System.out.println("");
				}
				System.out.println("");
			}
		}
		
        /*for(ds.ca.Room room : ca.getRooms()){
            System.out.println(room.graphicalToString());
            System.out.println();
        }*/
        
    }

}
