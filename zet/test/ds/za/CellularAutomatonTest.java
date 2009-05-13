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
package ds.ca;

import ds.ca.CellularAutomaton;
import ds.ca.StaticPotential;
import ds.ca.ExitCell;
import ds.ca.Individual;
import ds.ca.PotentialManager;
import ds.ca.Room;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import algo.ca.SPPotentialController;

public class CellularAutomatonTest extends TestCase {

	CellularAutomaton ca;
	Room r1,r2,r3;
	Individual i1,i2,i3;
	ExitCell e1,e2,e3;
	StaticPotential sP1,sP2;
	ArrayList<StaticPotential> pTM;
	
	
	@Before
	public void setUp() throws Exception {
		ca = new CellularAutomaton();
		sP1 = new StaticPotential();
		sP2 = new StaticPotential();
		//System.out.println("sP1-ID:"+sP1.getID());
		r1 = new Room(1,3,"Etage0", 0);
		r2 = new Room(20,20,"Etage0", 0);
		r3 = new Room(30,30,"Etage0", 0);
		i1 = new Individual();
		i2 = new Individual();
		i3 = new Individual();
		e1 = new ExitCell(0,0);
		e2 = new ExitCell(0,1);
		e3 = new ExitCell(0,2);
		pTM = new ArrayList<StaticPotential>();
		pTM.add(sP1);
		pTM.add(sP2);
		r1.setCell(e1);
		r1.setCell(e2);
		r1.setCell(e3);
		sP1.setPotential(e1, 4);
		sP1.setPotential(e2, 8);
		sP1.setPotential(e3,12);
		sP2.setPotential(e1, 2);
		sP2.setPotential(e2, 6);
		sP2.setPotential(e3,17);
		
		ca.addRoom(r1);
		
		
		ca.getPotentialManager().addStaticPotential(sP1);
		ca.getPotentialManager().addStaticPotential(sP2);
		//ca.addIndividual(i1);
		//ca.addExit(e1);
		
		
		
	}
	
	@Test
	public void testAddRoom(){
		try{
			ca.addRoom(r3);
		} catch(IllegalArgumentException e){
			fail(e.toString());
		}
		
		try{
			ca.addRoom(r3);
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	public void testRemoveRoom(){
		try{
			ca.removeRoom(r1);
		} catch(IllegalArgumentException e){
			fail(e.toString());
		}
		
		try{
			ca.removeRoom(r2);
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	public void testAddExit(){
		try{
			//ca.addExit(e3);
		} catch(IllegalArgumentException e){
			fail(e.toString());
		}
		
		try{
			//ca.addExit(e3);
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	public void testRemoveExit(){
		try{
			//ca.removeExit(e1);
		} catch(IllegalArgumentException e){
			fail(e.toString());
		}
		
		try{
			//ca.removeExit(e2);
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	public void testAddIndividual(){
		try{
			//ca.addIndividual(i3);
		} catch(IllegalArgumentException e){
			fail(e.toString());
		}
		
		try{
			//ca.addIndividual(i3);
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	public void testRemoveIndividual(){
		try{
			//ca.removeIndividual(i1);
		} catch(IllegalArgumentException e){
			fail(e.toString());
		}
		
		try{
			//ca.removeIndividual(i2);
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	public void testMergePotentials(){
		SPPotentialController pc = new SPPotentialController(ca);
		StaticPotential sP = pc.mergePotentials(pTM);
		//System.out.println("sP1-ID:"+sP1.getID());
		//System.out.println("sP2-ID:"+sP2.getID());
		PotentialManager pM = ca.getPotentialManager();
		if(pM==null)fail("");
		pM.addStaticPotential(sP);
		//System.out.println("sP-ID:" + sP.getID());
		if(sP==null)fail("");
		assertEquals(sP.getPotential(e1),2);
		assertEquals(sP.getPotential(e2),6);
		assertEquals(sP.getPotential(e3),12);
	}

}
