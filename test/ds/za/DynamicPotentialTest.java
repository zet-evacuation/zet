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
 * DynamicPotentialTest.java
 * JUnit based test
 *
 * Created on 3. Dezember 2007, 21:48
 */


package ds.ca;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import algo.ca.SPPotentialController;

import ds.ca.*;

/**
 *
 * @author Sophia
 */
public class DynamicPotentialTest extends TestCase {
    
        RoomCell rc1;
        RoomCell rc2;
        RoomCell rc3;
        RoomCell rc4;
        RoomCell rc5;
    
    public DynamicPotentialTest(String testName) {
        super(testName);
    }
    
    @Before
    protected void setUp() throws Exception {        
        RoomCell rc1 = new RoomCell(1,1);
        RoomCell rc2 = new RoomCell(1,2);
        RoomCell rc3 = new RoomCell(1,3);
        RoomCell rc4 = new RoomCell(1,4);
        RoomCell rc5 = new RoomCell(1,5);
    }


    
    /**
     * Test der Methode deleteCell, in Klasse ds.ca.DynamicPotential.
     */
    
    @Test
    public void testdeleteCell () {
        System.out.println("deleteCell");   
        
        DynamicPotential dp4 = new DynamicPotential();
        
        RoomCell rc1 = new RoomCell(1,1);
        
        dp4.setPotential(rc1,14);
        
        dp4.deleteCell(rc1);
        //dp4.getPotential(rc1);   //assert hier nicht! moeglich, stattdessen 
        //betrachte ob getPotential(rc1) Exception wirft, wenn ja funktioniert delete!
    }
    
    /**
     * Test der Methode setPotential, in Klasse ds.ca.DynamicPotential.
     */
    
    @Test
    public void testsetPotential () {
        System.out.println("setPotential");   
        
        DynamicPotential dp1 = new DynamicPotential();
        
        RoomCell rc1 = new RoomCell(1,1);
        
        dp1.setPotential(rc1,14);
        assertEquals(14,dp1.getPotential(rc1));        
    }
    
    
    /**
     * Test der Methode getPotential, in Klasse ds.ca.DynamicPotential.
     */
    
    @Test
    public void testgetPotential () {
        System.out.println("getPotential");   
        
        RoomCell rc1 = new RoomCell(1,1);
        
        DynamicPotential dp2 = new DynamicPotential();
        
        dp2.setPotential(rc1,10);
        int x = dp2.getPotential(rc1);
        assert(10==x);        
    }
    
    /**
     * Test der Methode increase, in Klasse ds.ca.DynamicPotential.
     */
    @Test
    public void testIncrease() {
        System.out.println("increase");
        
        DynamicPotential dp3 = new DynamicPotential();  
        CellularAutomaton ca = new CellularAutomaton();
        PotentialManager pm = ca.getPotentialManager();
        pm.setDynamicPotential(dp3);
        SPPotentialController pc = new SPPotentialController(ca);
        
        RoomCell rc2 = new RoomCell(1,2);
        RoomCell rc3 = new RoomCell(1,3);
        RoomCell rc4 = new RoomCell(1,4);
        RoomCell rc5 = new RoomCell(1,5);
        
        dp3.setPotential(rc2, 10);
        dp3.setPotential(rc3, -2);
        dp3.setPotential(rc4, -1);
        dp3.setPotential(rc5, 89);       
        
        pc.increaseDynamicPotential(rc2);
        pc.increaseDynamicPotential(rc3);
        pc.increaseDynamicPotential(rc4);
        pc.increaseDynamicPotential(rc5); 
        
        assertEquals(dp3.getPotential(rc2),11);
        assertEquals(dp3.getPotential(rc3),-1);
        assertEquals(dp3.getPotential(rc4),0);
        assertEquals(dp3.getPotential(rc5),90);
    }

    /**
     * Test der Methode decrease, in Klasse ds.ca.DynamicPotential.
     */
    
    @Test
    public void testDecrease() {
        System.out.println("decrease");
        
        DynamicPotential dp4 = new DynamicPotential();  
        CellularAutomaton ca = new CellularAutomaton();
        PotentialManager pm = ca.getPotentialManager();
        pm.setDynamicPotential(dp4);
        SPPotentialController pc = new SPPotentialController(ca);
        
        RoomCell rc2 = new RoomCell(1,2);
        RoomCell rc3 = new RoomCell(1,3);
        RoomCell rc4 = new RoomCell(1,4);
        RoomCell rc5 = new RoomCell(1,5);
        
        dp4.setPotential(rc2, 10);
        dp4.setPotential(rc3, -2);
        dp4.setPotential(rc4, 1);
        dp4.setPotential(rc5, 89);       
        
        pc.decreaseDynamicPotential(rc2);
        pc.decreaseDynamicPotential(rc3);
        pc.decreaseDynamicPotential(rc4);
        pc.decreaseDynamicPotential(rc5); 
        
        assertEquals(dp4.getPotential(rc2),9);
        assertEquals(dp4.getPotential(rc3),-3);
        assertEquals(dp4.getPotential(rc5),88);
        //assertEquals(dp4.getPotential(rc4),0);
        assert(!dp4.contains(rc4));
    }    
}
