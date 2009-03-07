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
        BuildingPlan plan = Project.load(new File("testraum2.xml")).getPlan();
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
//            project.getPlan().addFloor(floor);
//        }
//        project.save(new File(".\\examples\\presentation.xml"));
    }

}
