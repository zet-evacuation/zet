/**
 * InstanceGenerator.java
 * Created: Oct 23, 2012, 12:32:56 PM
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.coga.zet.model.AssignmentArea;
import de.tu_berlin.coga.zet.model.EvacuationArea;
import de.tu_berlin.coga.zet.model.Floor;
import de.tu_berlin.coga.zet.model.PlanPoint;
import de.tu_berlin.coga.zet.model.Room;
import de.tu_berlin.coga.zet.model.ZControl;
import static org.junit.Assert.*;
import org.junit.Test;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class InstanceGenerator {
		ZControl zControl;
		private Room r;
		private Floor fl;
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
