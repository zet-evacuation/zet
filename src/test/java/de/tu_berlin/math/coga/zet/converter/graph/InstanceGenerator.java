/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.zet_evakuierung.model.FloorInterface;
import de.zet_evakuierung.model.AssignmentArea;
import de.zet_evakuierung.model.EvacuationArea;
import de.zet_evakuierung.model.PlanPoint;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.ZControl;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class InstanceGenerator {
		ZControl zControl;
		private Room r;
		private FloorInterface fl;
		private AssignmentArea a;
		private EvacuationArea e;

	@Test
	public void testCreation() {
		setUpInstance();
		assertEquals( "Area of room", 12960000, r.getPolygon().area() );
		assertEquals( "Area of assignment area", 1440000, a.area() );
		assertEquals( "Area of evacuation area", 1440000, e.area() );	}

	public void setUpInstance() {
		// create a simple BuildingPlan
		zControl = new ZControl();

		fl = zControl.getProject().getBuildingPlan().getFloors().get( 1 );

		zControl.createNewPolygon( Room.class, fl );

		zControl.addPoint( new PlanPoint( 0, 0 ) );
		zControl.addPoint( new PlanPoint( 3600, 0 ) );
		zControl.addPoint( new PlanPoint( 3600, 3600 ) );
		zControl.addPoint( new PlanPoint( 0, 3600 ) );
		zControl.addPoint( new PlanPoint( 0, 0 ) );

		r = (Room)zControl.latestPolygon();

		zControl.createNewPolygon( AssignmentArea.class, r );
		zControl.addPoint( new PlanPoint( 0, 1200 ) );
		zControl.addPoint( new PlanPoint( 1200, 1200 ) );
		zControl.addPoint( new PlanPoint( 1200, 2400 ) );
		zControl.addPoint( new PlanPoint( 0, 2400 ) );
		zControl.addPoint( new PlanPoint( 0, 1200 ) );

		a = (AssignmentArea)zControl.latestPolygon();

		zControl.createNewPolygon( EvacuationArea.class, r );
		zControl.addPoint( new PlanPoint( 2400, 2400) );
		zControl.addPoint( new PlanPoint( 3600, 2400 ) );
		zControl.addPoint( new PlanPoint( 3600, 3600 ) );
		zControl.addPoint( new PlanPoint( 2400, 3600 ) );
		zControl.addPoint( new PlanPoint( 2400, 2400 ) );

		e = (EvacuationArea)zControl.latestPolygon();
	}

}
