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
 * Created on 01.12.2007
 *
 */
package ds.za;

import ds.ca.DynamicPotential;
import ds.ca.StaticPotential;
import ds.ca.Cell;
import ds.ca.PotentialManager;
import ds.ca.RoomCell;
import ds.ca.DoorCell;
import ds.ca.Room;
import ds.ca.InitialConfiguration;
import java.util.Collection;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import ds.ca.results.MoveAction;
import ds.ca.results.VisualResultsRecorder;
import ds.ca.results.VisualResultsRecording;

public class VisualResultsRecorderTest extends TestCase {

    private InitialConfiguration anInitialConfig;
        
    @Before
    public void setUp() throws Exception {
        Room room1 = new Room(10, 10,"Etage0", 0);
        Room room2 = new Room(10, 10,"Etage0", 0);
        
        PotentialManager potentials = new PotentialManager();
                
        StaticPotential lp1 = new StaticPotential();
        StaticPotential lp2 = new StaticPotential();
        
        DynamicPotential dp = new DynamicPotential();
        
        for(int x=0; x < 10; x++){
            for(int y=0; y < 10; y++){
                RoomCell cell = new RoomCell(x,y);
                lp1.setPotential(cell, 5);
                lp2.setPotential(cell, 15);
                dp.setPotential(cell, 0);
                room1.setCell(cell);
            }
        }
        
        for(int x=0; x < 10; x++){
            for(int y=0; y < 10; y++){
                RoomCell cell = new RoomCell(x,y);
                lp1.setPotential(cell, 5);
                lp2.setPotential(cell, 15);
                dp.setPotential(cell, 0);
                room2.setCell(cell);
            }
        }    
        
        DoorCell door1 = new DoorCell(0,5);
        DoorCell door2 = new DoorCell(0,5);
        DoorCell door3 = new DoorCell(9,5);
        DoorCell door4 = new DoorCell(9,5);
        
        lp1.setPotential(door1, 0);
        lp2.setPotential(door1, 0);
        dp.setPotential(door1, 0);
        
        lp1.setPotential(door2, 0);
        lp2.setPotential(door2, 0);
        dp.setPotential(door2, 0);
        
        lp1.setPotential(door3, 0);
        lp2.setPotential(door3, 0);
        dp.setPotential(door3, 0);
        
        lp1.setPotential(door4, 0);
        lp2.setPotential(door4, 0);
        dp.setPotential(door4, 0);
       
        potentials.addStaticPotential(lp1);
        potentials.addStaticPotential(lp2);
        potentials.setDynamicPotential(dp);        
        
        door1.addNextDoor(door2);
        //door2.addNextDoor(door1);
        door3.addNextDoor(door4);
        //door4.addNextDoor(door3);      
        
        room1.setCell(door1);
        room1.setCell(door3);
        room2.setCell(door2);
        room2.setCell(door4);   
        
        Vector<Room> rooms = new Vector<Room>();
        rooms.add(room1);
        rooms.add(room2);
                      
        anInitialConfig = new InitialConfiguration(rooms, potentials, 1.0d);
        System.out.println(anInitialConfig);
    }

    @Test
    public void testCloneAConfiguration() {
       VisualResultsRecorder recorder = new VisualResultsRecorder(anInitialConfig); 
       VisualResultsRecording recording = recorder.getRecording();

       //Originals
       PotentialManager potManager = anInitialConfig.getPotentialManager();
       Collection<StaticPotential> staticPots = potManager.getStaticPotentials();
       DynamicPotential dynamicPot= potManager.getDynamicPotential();
       Vector<Room> rooms = new Vector<Room>(anInitialConfig.getRooms());
       
       //Clones
       InitialConfiguration clone = recording.getInitialConfig();
       PotentialManager potManagerClone = clone.getPotentialManager();
       Collection<StaticPotential> staticPotsClone = potManagerClone.getStaticPotentials();
       DynamicPotential dynamicPotClone = potManagerClone.getDynamicPotential();
       Vector<Room> roomsClone = new Vector<Room>(clone.getRooms()); 

       for(int r=0; r < rooms.size(); r++){
           Room room = rooms.get(r);
           Room roomClone = roomsClone.get(r);
           for(int x=0; x < room.getWidth(); x++){
               for(int y=0; y < room.getHeight(); y++){
                   Cell cell = room.getCell(x,y);
                   Cell cellClone = roomClone.getCell(x,y);
                   if(cell != null){
                       if(cellClone == null){
                           fail("Your clone misses a cell!");
                       } 
                       if((cellClone.getX() != cell.getX()) || (cellClone.getY() != cell.getY())){
                           fail("There is a cloned cell that does not match its original!");
                       }
                       
                       if(cellClone.getIndividual() != null && cell.getIndividual() == null){
                           fail("You won an individual!");
                       }
                       
                       if(cellClone.getIndividual() == null && cell.getIndividual() != null){
                           fail("You lost an individual!");
                       }
                   } else {
                       if(cellClone != null){
                           fail("Your clone has a cell that the original has not!");
                       }
                   }
                   
                   if(cell == cellClone){
                       fail("You forgot to copy a cell!");
                   }
                   
                   for(StaticPotential pot : staticPots){
                      if(pot != null && pot.contains(cell)){
                          boolean containsCell = false;
                          for(StaticPotential potClone : staticPotsClone){
                               if(potClone.contains(cell)){
                                   containsCell = true;
                                   int pot1 = pot.getPotential(cell);
                                   int pot2 = potClone.getPotential(cell);
                                   if(pot1 != pot2){
                                       fail("A potential was changed by the cloning!");
                                   }
                               }                               
                           }
                          if(!containsCell){
                              fail("Your cloned potential lost a cell!");
                          }
                       }
                   }
               }
           }
       }
    }

    @Test
    public void testRecordAction() throws Exception {
        VisualResultsRecorder recorder = new VisualResultsRecorder(anInitialConfig);

        Room room = anInitialConfig.getRooms().iterator().next();
        DoorCell cell1 = null;
        DoorCell cell2 = null;
        if(room != null && !room.getDoors().isEmpty()){
            cell1 = room.getDoors().get(0);
            cell2 = cell1.getNextDoor(0);
        } else {
            if(room == null){
                fail("The list of doors in the first room of initialConfig has" +
                		"not been initialized. Please do so!");
            } else {
                fail("Don't use a room without any doors for this test!");
            }
        }
        
        if(cell1 == null || cell2 == null){
            fail("Your room lost a door!");
        }
        
        MoveAction move1 = new MoveAction(cell1, cell2, cell1.getIndividual());
        MoveAction move2 = new MoveAction(cell2, cell1, cell1.getIndividual());
        
        recorder.startRecording();
        recorder.recordAction(move1);
        recorder.nextTimestep();
        recorder.recordAction(move2);
    }
}
