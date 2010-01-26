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

import junit.framework.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import util.random.distributions.NormalDistribution;

import ds.z.*;


public class RoomTest extends TestCase {

	public void estRasterizeEdge() {
		Floor f = new Floor();
		Room r = new Room(f);
		ArrayList<PlanPoint>pointList = new ArrayList<PlanPoint>();
		int erg;
		
		//Testpunkte 1
		PlanPoint p1 = new PlanPoint(3600,3200);
		PlanPoint p2 = new PlanPoint(400,3200);
		PlanPoint p3 = new PlanPoint(400,2000);
		PlanPoint p4 = new PlanPoint(2000,400);
		PlanPoint p5 = new PlanPoint(3600,400);
		
		pointList.add(p1);
		pointList.add(p2);
		pointList.add(p3);
		pointList.add(p4);
		pointList.add(p5);
		
		r.defineByPoints(pointList);
		
		r.rasterizeEdge (r.getEdge (new PlanPoint(400,2000),new PlanPoint(2000,400)));
	}
	
	public void testRasterize(){
		Floor f = new Floor();
		
		//Room1
		Room r1 = new Room(f);
		ArrayList<PlanPoint>pointList1= new ArrayList<PlanPoint>();
		PlanPoint p11 = new PlanPoint(2000,400);
		PlanPoint p12 = new PlanPoint(4000,800);
		PlanPoint p13 = new PlanPoint(4400,2000);
		PlanPoint p14 = new PlanPoint(3200,3200);
		PlanPoint p15 = new PlanPoint(2400,1600);
		PlanPoint p16 = new PlanPoint(400,1600);
		pointList1.add(p11);
		pointList1.add(p12);
		pointList1.add(p13);
		pointList1.add(p14);
		pointList1.add(p15);
		pointList1.add(p16);
		
		r1.defineByPoints(pointList1);
		r1.rasterize();
		
		//Room1
		Room r2 = new Room(f);
		ArrayList<PlanPoint>pointList2= new ArrayList<PlanPoint>();
		PlanPoint p21 = new PlanPoint(3200,3200);
		PlanPoint p22 = new PlanPoint(3200,4800);
		PlanPoint p23 = new PlanPoint(400,4800);
		PlanPoint p24 = new PlanPoint(400,1600);
		PlanPoint p25 = new PlanPoint(2400,1600);
		pointList2.add(p21);
		pointList2.add(p22);
		pointList2.add(p23);
		pointList2.add(p24);
		pointList2.add(p25);
		
		r2.defineByPoints(pointList2);
		r2.rasterize();
		
		//Room1
		Room r3 = new Room(f);
		ArrayList<PlanPoint>pointList3= new ArrayList<PlanPoint>();
		PlanPoint p31 = new PlanPoint(6400,4800);
		PlanPoint p32 = new PlanPoint(3200,4800);
		PlanPoint p33 = new PlanPoint(3200,3200);
		PlanPoint p34 = new PlanPoint(4400,2000);
		PlanPoint p35 = new PlanPoint(6400,2000);
		pointList3.add(p31);
		pointList3.add(p32);
		pointList3.add(p33);
		pointList3.add(p34);
		pointList3.add(p35);
		
		r3.defineByPoints(pointList3);
		r3.rasterize();
		
		//Room4
		Room r4 = new Room(f);
		ArrayList<PlanPoint>pointList4= new ArrayList<PlanPoint>();
		PlanPoint p41 = new PlanPoint(7600,800);
		PlanPoint p42 = new PlanPoint(4000,800);
		PlanPoint p43 = new PlanPoint(4400,2000);
		PlanPoint p44 = new PlanPoint(6400,2000);
		PlanPoint p45 = new PlanPoint(6400,3600);
		pointList4.add(p41);
		pointList4.add(p42);
		pointList4.add(p43);
		pointList4.add(p44);
		pointList4.add(p45);
		
		r4.defineByPoints(pointList4);
		r4.rasterize();
		
		
		
	}
	public void estReplaceEdge(){
		Floor f = new Floor();
		Room r = new Room(f);
		ArrayList<PlanPoint>pointList = new ArrayList<PlanPoint>();
		ArrayList<PlanPoint>insertList = new ArrayList<PlanPoint>();
		PlanPoint p1 = new PlanPoint(3000,1000);
		PlanPoint p2 = new PlanPoint(1000,5000);
		PlanPoint p3 = new PlanPoint(4000,3000);
		PlanPoint p4 = new PlanPoint(2000,2000);
		pointList.add(p1);
		pointList.add(p2);
		pointList.add(p3);
		
		insertList.add(p2);
		insertList.add(p4);
		insertList.add(p3);
		
		r.defineByPoints(pointList);
		
		r.replaceEdge(p2,p3, insertList);		
		for(Edge e : r.getEdges()){
			System.out.println("("+e.getSource()+","+e.getTarget()+")");
		}
	}
}
