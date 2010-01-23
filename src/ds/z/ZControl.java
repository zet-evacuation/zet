/*
 * ZControl.java
 * Created 16.12.2009, 13:11:44
 */
package ds.z;

import ds.Project;
import ds.z.exception.AssignmentException;
import java.util.List;
import localization.Localization;

/**
 * The class <code>ZControl</code> represents a front end class to the Z-model.
 * It is called for whatever action should be performed on the model. It will
 * send appropriate actions to the model and will take care for a consistent
 * model description.
 *
 * Thus, no action-changed methods inside the model should be needed.
 * @author Jan-Philipp Kappmeier
 */
public class ZControl {
	private Project p;

	/**
	 * Creates a new instance of <code>ZControl</code>.
	 *
	 * @param p 
	 */
	public ZControl( Project p ) {
		this.p = p;
	}

	public void delete( PlanPolygon p ) {
		if( p instanceof Area )
			delete( (Area)p );
		else if( p instanceof Room )
			delete( (Room)p );
		else
			throw new IllegalArgumentException( "Polygon not of type Area or Room" );
	}

	public void delete( Room r ) {
		r.delete();
	}

	// Delete Stuff
	public void delete( Area area ) {
		if( area instanceof EvacuationArea ) {
			for( Assignment a : p.getAssignments() )
				for( AssignmentType t : a.getAssignmentTypes() )
					for( AssignmentArea aa : t.getAssignmentAreas() )
						if( aa.getExitArea() != null && aa.getExitArea().equals( (EvacuationArea)area ) )
							aa.setExitArea( null );
			area.delete();
		} else
			area.delete();
	}

	public Project getProject() {
		return p;
	}

	PlanPolygon newPolygon = null;

	PlanPolygon latestPolygon = null;

	
	public PlanPolygon latestPolygon() {
		return latestPolygon;
	}

	// Methoden, um neue Objekte zu erzeugen:
	public void createNew( Class a, Object parent ) throws AssignmentException {
		if( newPolygon != null ) {
			throw new IllegalArgumentException( "Creation already started." );
		}
		if( a == Room.class )
			newPolygon = new Room( (Floor)parent );
		else if( a == AssignmentArea.class ) {
			Assignment cur2 = getProject().getCurrentAssignment();
			if( cur2 != null )
				if( cur2.getAssignmentTypes().size() > 0 )
					newPolygon = new AssignmentArea( (Room)parent, cur2.getAssignmentTypes().get( 0 ) );
				else
					throw new AssignmentException( AssignmentException.State.NoAssignmentCreated );
			else
				throw new AssignmentException( AssignmentException.State.NoAssignmentSelected );
		} else if( a == Barrier.class )
			newPolygon = new Barrier( (Room)parent );
		else if( a == DelayArea.class )
			newPolygon = new DelayArea( (Room)parent, DelayArea.DelayType.OBSTACLE, 0.7d );
		else if( a == StairArea.class )
			newPolygon = new StairArea( (Room)parent );
		else if( a == EvacuationArea.class ) {
			newPolygon = new EvacuationArea( (Room)parent );
			int count = getProject().getBuildingPlan().getEvacuationAreasCount();
			String name = Localization.getInstance().getString( "ds.z.DefaultName.EvacuationArea" ) + " " + count;
			((EvacuationArea)newPolygon).setName( name );
		} else if( a == InaccessibleArea.class )
			newPolygon = new InaccessibleArea( (Room)parent );
		else if( a == SaveArea.class )
			newPolygon = new SaveArea( (Room)parent );

		latestPolygon = newPolygon;
	}

	public boolean addPoints( List<PlanPoint> points ) {
		if( newPolygon == null )
			throw new IllegalStateException( "No polygon creation started." );

		if( points.size() == 0 )
			throw new IllegalArgumentException( "No Points." );
		if( points.size() == 1 )
			return addPoint( points.get(0) );

		for( int i = 0; i < points.size()-1; ++i ) {
			addPoint( points.get( i ) );
		}
		return addPoint( points.get( points.size()-1 ) );

//		newPolygon.add( points, true );
//		if( newPolygon.isClosed() ) {
//			if( newPolygon instanceof AssignmentArea )
//				((AssignmentArea)newPolygon).setEvacuees( Math.min( newPolygon.getMaxEvacuees(), ((AssignmentArea)newPolygon).getAssignmentType().getDefaultEvacuees() ) );
//		}
//		return newPolygon.isClosed();
	}

	private static PlanPoint temp = null;

	public boolean addPoint( PlanPoint point ) {
		if( newPolygon.isClosed() )
			throw new IllegalStateException( "Polygon is closed." );

		if( newPolygon.getEnd() == null ) {
			if( temp == null )
				temp = point;
			else
				newPolygon.newEdge( temp, point );
		} else
			newPolygon.addPointLast( point );
		if( newPolygon.isClosed() ) {
			if( newPolygon instanceof AssignmentArea )
				((AssignmentArea)newPolygon).setEvacuees( Math.min( newPolygon.getMaxEvacuees(), ((AssignmentArea)newPolygon).getAssignmentType().getDefaultEvacuees() ) );
			newPolygon = null;
			temp = null;
			return true;
		}
		return false;
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "ZControl";
	}
}
