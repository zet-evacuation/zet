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


package ds.za;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import ds.ca.*;

/**
 *
 * @author Sophia
 */
public class StaticPotentialTest extends TestCase {
 
    StaticPotential sp1;        
    StaticPotential sp2;        
    StaticPotential sp3;
    
 public StaticPotentialTest(String testName) {
        super(testName);
    }

    
    @Before
    protected void setUp() throws Exception {    
        sp1 = new StaticPotential();        
        sp2 = new StaticPotential();        
        sp3 = new StaticPotential();
    }


    
    /**
     * Test der Methode deleteCell, in Klasse ds.za.DynamicPotential.
     */
    
    @Test
    public void testgetID () {
        System.out.println("getID");   
        
        
        assertEquals(sp1.getID(),0);
        assertEquals(sp2.getID(),1);
        assertEquals(sp3.getID(),2);
        
        System.out.println(sp1.getID());
        
        StaticPotential sp4 = new StaticPotential();
        assertEquals(sp4.getID(),3); 
        assertEquals(sp1.getID(),0);
        
    }  
}
