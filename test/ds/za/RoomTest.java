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
package ds.za;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import ds.ca.*;

/**
 *
 * @author Sophia
 */
public class RoomTest extends TestCase {    
   
    public RoomTest(String testName) {
        super(testName);
    }
    
    @Before
    protected void setUp() throws Exception {        
    }
    
    @Test
    public void testhashCode () {
        System.out.println("hashCode");   
        
        Room r1 = new Room(2,2,"Etage0", 0);
        Room r2 = new Room(2,2,"Etage0", 0);
        Room r3 = new Room(2,2,"Etage0", 0);
        Room r4 = new Room(2,2,"Etage0", 0);
        
        assertEquals(r1.hashCode(),0);
        assertEquals(r2.hashCode(),1);
        assertEquals(r3.hashCode(),2);
        assertEquals(r4.hashCode(),3);
    }
    
    @Test
    public void testsetCell () {
        System.out.println("setCell");   
        
        Room r1 = new Room(2,2,"Etage0", 0);        
        
        RoomCell rc1 = new RoomCell(0,0);
        RoomCell rc2 = new RoomCell(0,1);      
        RoomCell rc3 = new RoomCell(1,0);
        DoorCell dc1 = new DoorCell(1,1);
        
        r1.setCell(rc1);
        r1.setCell(rc2);
        r1.setCell(rc3); 
        r1.setCell(dc1);
        
        assertEquals(r1.getCell(0,0),rc1);
        assertEquals(r1.getCell(0,1),rc2);
        assertEquals(r1.getCell(1,0),rc3);
        assertEquals(r1.getCell(1,1),dc1);
        assertEquals(r1.getDoors().get(0),dc1);
        
        
        //dc1(1,1) mit rc4(1,1) ueberschreiben
        RoomCell rc4 = new RoomCell(1,1);
        
        r1.setCell(rc4);
        
        assertEquals(r1.getCell(1,1),rc4);
        //assertEquals(r1.getDoors().contains(dc1),false);
        
        
        // rc1(0,0) mit dc2(0,0) ueberschreiben
        DoorCell dc2 = new DoorCell(0,0);
        
        r1.setCell(dc2);
        assertEquals(r1.getCell(0,0),dc2);
        assertEquals(r1.getDoors().contains(dc2),true);
    }
    
    @Test
    public void testenter () {
        System.out.println("enter");   
        
        Room r1 = new Room(2,2,"Etage0", 0);
        RoomCell rc1 = new RoomCell(0,0);
        RoomCell rc2 = new RoomCell(0,1); 
        DoorCell dc3 = new DoorCell(1,0);              
        DoorCell dc1 = new DoorCell(1,1);
        
        Room r2 = new Room(2,2,"Etage0", 0);
        RoomCell rc5 = new RoomCell(0,0);
        RoomCell rc6 = new RoomCell(0,1); 
        DoorCell dc4 = new DoorCell(1,0);              
        DoorCell dc2 = new DoorCell(1,1); 
        
        dc1.addNextDoor(dc2);
        dc2.addNextDoor(dc1);
        dc3.addNextDoor(dc4);
        dc4.addNextDoor(dc3);
        
        Individual i1 = new Individual();        
        i1.setCell(dc1);
        Individual i2= new Individual();        
        i2.setCell(dc3);
        Individual i3 = new Individual();        
        i3.setCell(rc1);
        
        r1.setCell(rc1);
        r1.setCell(rc2);
        r1.setCell(dc3); 
        r1.setCell(dc1);
        
        r2.setCell(rc5);
        r2.setCell(rc6);
        r2.setCell(dc4); 
        r2.setCell(dc2);    
     
        assertEquals(r2.getIndividuals().contains(i1),false); //individual ist noch nicht im raum
        
        //r2.enter(i1, dc2);
				
        assertEquals(dc1.getIndividual(),null);
        assertEquals(dc2.getIndividual(),i1);
        assertEquals(r2.getIndividuals().contains(i1),true);
        
        //r2.enter(i3); //<- liefert richige exception        
        //r1.enter(i2); //<- liefert richige exception
        
    }
    
    
}