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
package converter;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import converter.ZToCAConverter.ConversionNotSupportedException;

import ds.Project;
import ds.z.BuildingPlan;
import ds.z.Floor;
import ds.ca.CellularAutomaton;
import ds.ca.Room;

/**
 * @author Daniel Pluempe
 *
 */
public class RasterContainerCreatorTest {

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
    public void testZToCARasterContainer() throws Exception{
        BuildingPlan plan = Project.load(new File("testraum2.xml")).getBuildingPlan();
        //BuildingPlan plan = RasterContainerCreator.makeBuildingPlanExampleWithDifferentAreas();
        //ZToCARasterContainer rc = RasterContainerCreator.getInstance().ZToCARasterContainer(plan);
        CellularAutomaton ca = null;
        try{
            ca = ZToCAConverter.getInstance().convert(plan);
        } catch(ConversionNotSupportedException e){
            throw new Exception("The building plan contains features that cannot be converted to a CA. " + e.getMessage());
        }
        
        for(Room room : ca.getRooms()){
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
