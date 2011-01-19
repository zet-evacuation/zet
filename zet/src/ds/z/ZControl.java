/*
 * ZControl.java
 * Created 16.12.2009, 13:11:44
 */
package ds.z;

import ds.Project;
import ds.z.exception.AssignmentException;
import java.util.List;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.util.Helper;
import de.tu_berlin.math.coga.common.util.IOTools;
import de.tu_berlin.math.coga.rndutils.distribution.Distribution;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.UniformDistribution;
import ds.z.exception.UnknownZModelError;
import event.EventServer;
import event.ZModelChangedEvent;
import gui.ZETMain;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * The class {@code ZControl} represents a front end class to the Z-model.
 * It is called for whatever action should be performed on the model. It will
 * send appropriate actions to the model and will take care for a consistent
 * model description.
 *
 * Thus, no action-changed methods inside the model should be needed.
 * @author Jan-Philipp Kappmeier
 */
public class ZControl {
	/** The localization class. */
	static final Localization loc = ZLocalization.getSingleton();
	/** The project that is root of the controlled model. */
	private Project p;

	public ZControl() {
		newProject();
	}

	/**
	 * Creates a new instance of {@code ProjectControl} which controls a
	 * given project.
	 * @param filename the path to a file that should be loaded as project.
	 */
	public ZControl( String filename ) {
		this( new File( filename ) );
	}

	/**
	 * Creates a new instance of {@code ProjectControl} which controls a
	 * given project.
	 * @param file the file that should be loaded as project.
	 */
	public ZControl( File file ) {
		if( file == null )
			newProject();
		else if( !loadProject( file ) )
			p = newProject();
	}

	/**
	 * Creates a new instance of {@code ZControl}.
	 * @param p 
	 */
	ZControl( Project p ) {
		this.p = p;
	}

	public Project getProject() {
		return p;
	}

	public void loadProject( String projectFile ) {
		loadProject( new File( projectFile ) );
	}

	/**
	 * Loads the specified {@link File}.
	 * @param projectFile the project file
	 * @return returns true, if the project loaded correctly
	 */
	final public boolean loadProject( File projectFile ) {
		try {
			p = Project.load( projectFile );
			p.setProjectFile( projectFile );
			// Update the graphical user interface
			ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.loaded" ) );	// TODO output changed, use listener
		} catch( Exception ex ) {
			System.err.println( loc.getString( "gui.editor.JEditor.error.loadErrorTitle" ) + ":" );
			System.err.println( " - " + loc.getString( "gui.editor.JEditor.error.loadError" ) );
			ex.printStackTrace( System.err );
			ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.loadError" ) );
			return false;
		}
		return true;
	}

	/**
	 * <p>Creates a new project with default settings and returns it. The old
	 * model controlled by this class is replaced by the new empty model for the
	 * new project.</p>
	 * <p>The model parameters follow the guides from RiMEA (http://www.rimea.de) if they
	 * are specified.</p>
	 * @return the newly created project
	 */
	final public Project newProject() {
		p = new Project();
		Floor fl = new Floor( loc.getString( "ds.z.DefaultName.Floor" ) + " 1" );
		p.getBuildingPlan().addFloor( fl );
		Assignment assignment = new Assignment( loc.getString( "ds.z.DefaultName.DefaultAssignment" ) );
		p.addAssignment( assignment );
		Distribution diameter = getDefaultAssignmentTypeDistribution( "diameter" );
		Distribution age = getDefaultAssignmentTypeDistribution( "age" );
		Distribution familiarity = getDefaultAssignmentTypeDistribution( "familiarity" );
		Distribution panic = getDefaultAssignmentTypeDistribution( "panic" );
		Distribution decisiveness = getDefaultAssignmentTypeDistribution( "decisiveness" );
		Distribution reaction = getDefaultAssignmentTypeDistribution( "reaction" );
		AssignmentType assignmentType = new AssignmentType( loc.getString( "ds.z.DefaultName.DefaultAssignmentType" ), diameter, age, familiarity, panic, decisiveness, reaction, 10 );
		assignment.addAssignmentType( assignmentType );
		return p;
	}

	/**
	 * Returns default values for a {@link de.tu_berlin.math.coga.rndutils.distribution.Distribution} distribution
	 * for a specified parameter. Reaction time and age follow the guidelines of
	 * RiMEA (http://www.rimea.de).
	 * @param type the parameter
	 * @return the distribution for the parameter
	 * @throws IllegalArgumentException if type is an unknown string
	 */
	public static Distribution getDefaultAssignmentTypeDistribution( String type ) throws IllegalArgumentException {
		if( type.equals( "diameter" ) ) {
			return new NormalDistribution( 0.5, 1.0, 0.4, 0.7 );
		} else if( type.equals( "age" ) ) {
			return new NormalDistribution( 50, 20, 10, 85 );
		} else if( type.equals( "familiarity" ) ) {
			return new NormalDistribution( 0.8, 1.0, 0.7, 1.0 );
		} else if( type.equals( "panic" ) ) {
			return new NormalDistribution( 0.5, 1.0, 0.0, 1.0 );
		} else if( type.equals( "decisiveness" ) ) {
			return new NormalDistribution( 0.3, 1.0, 0.0, 1.0 );
		} else if( type.equals( "reaction" ) ) {
			return new UniformDistribution( 0, 60 );
		}
		throw new IllegalArgumentException( "Unknown parameter type." );
	}

	public void deletePolygon( PlanPolygon p ) {
		if( p instanceof Area )
			delete( (Area)p );
		else if( p instanceof Room )
			deletePolygon( (Room)p );
		else
			throw new IllegalArgumentException( "Polygon not of type Area or Room" );
	}

	public void deletePolygon( Room r ) {
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

	PlanPolygon newPolygon = null;

	PlanPolygon latestPolygon = null;

	
	public PlanPolygon latestPolygon() {
		return latestPolygon;
	}

	/**
	 * Creates a new polygonal object in the hierarchy. These objects are rooms
	 * and the different types of areas (also barriers). A new object is created,
	 * but it will contain no points. Methods such as {@link #addPoint} have to
	 * be called afterwards. The creation process is completed with a call to
	 * {@link closePolygon()}.
	 * @param polygonClass the Class type of the object to be created
	 * @param parent the parent object. See details in the polygonal object, which is the correct parent type
	 * @throws AssignmentException if an assignment area is to be created without any valid assignment
	 * @throws IllegalArgumentException if object creation is already started or an invalid class was submitted
	 */
	public void createNewPolygon( Class polygonClass, Object parent ) throws AssignmentException, IllegalArgumentException {
		if( newPolygon != null )
			throw new IllegalArgumentException( "Creation already started." );

		if( polygonClass == Room.class )
			newPolygon = new Room( (Floor)parent );
		else if( polygonClass == AssignmentArea.class ) {
			Assignment cur2 = getProject().getCurrentAssignment();
			if( cur2 != null )
				if( cur2.getAssignmentTypes().size() > 0 )
					newPolygon = new AssignmentArea( (Room)parent, cur2.getAssignmentTypes().get( 0 ) );
				else
					throw new AssignmentException( AssignmentException.State.NoAssignmentCreated );
			else
				throw new AssignmentException( AssignmentException.State.NoAssignmentSelected );
		} else if( polygonClass == Barrier.class )
			newPolygon = new Barrier( (Room)parent );
		else if( polygonClass == DelayArea.class )
			newPolygon = new DelayArea( (Room)parent, DelayArea.DelayType.OBSTACLE, 0.7d );
		else if( polygonClass == StairArea.class )
			newPolygon = new StairArea( (Room)parent );
		else if( polygonClass == EvacuationArea.class ) {
			newPolygon = new EvacuationArea( (Room)parent );
			int count = getProject().getBuildingPlan().getEvacuationAreasCount();
			String name = ZLocalization.getSingleton().getString( "ds.z.DefaultName.EvacuationArea" ) + " " + count;
			((EvacuationArea)newPolygon).setName( name );
		} else if( polygonClass == InaccessibleArea.class )
			newPolygon = new InaccessibleArea( (Room)parent );
		else if( polygonClass == SaveArea.class )
			newPolygon = new SaveArea( (Room)parent );
		else if( polygonClass == TeleportArea.class )
			newPolygon = new TeleportArea( (Room)parent );
		else
			throw new IllegalArgumentException( "No valid plygon class given" );

		latestPolygon = newPolygon;
	}

	public boolean addPoints( List<PlanPoint> points ) {
		if( newPolygon == null )
			throw new IllegalStateException( "No polygon creation started." );

		if( points.isEmpty() )
			throw new IllegalArgumentException( "No Points." );
		if( points.size() == 1 )
			return addPoint( points.get(0) );

		for( int i = 0; i < points.size()-1; ++i )
			addPoint( points.get( i ), false );

		return addPoint( points.get( points.size()-1 ), true );
	}

	private static PlanPoint temp = null;

	public boolean addPoint( PlanPoint point ) {
		return addPoint( point, true );
	}

	private boolean addPoint( PlanPoint point, boolean sendEvent ) {
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
			if( sendEvent )
				EventServer.getInstance().dispatchEvent( new ZModelChangedEvent() {} );
			return true;
		}
		if( sendEvent )
			EventServer.getInstance().dispatchEvent( new ZModelChangedEvent() {} );
		return false;
	}

	public void closePolygon() {
		if( newPolygon.isClosed() )
			throw new IllegalStateException( "Polygon closed." );

		if( newPolygon.getNumberOfEdges() == 0 )
			throw new IllegalStateException( "No edges" );
		else {
			if( newPolygon.area() == 0 && !(newPolygon instanceof Barrier) )
				throw new IllegalStateException( "Area zero" );
			else if( newPolygon.getNumberOfEdges() >= ((newPolygon instanceof Barrier) ? 1 : 2) ) { // The new edge would be the third
				newPolygon.close();
				newPolygon = null;
				temp = null;
			} else
				throw new IllegalStateException( "Three edges" );
		}
		// todo remove event server
		EventServer.getInstance().dispatchEvent( new ZModelChangedEvent() {} );
	}

	/**
	 * <p>Creates a new floor in the hierarchy. A floor does not have a parent and
	 * is immediately created. It has no explicit bounds that have to be specified
	 * (in contrast to polygonal objects).</p>
	 * <p>The floor will have the default name followed by a number (which is
	 * the current number of floors).</p>
	 * @return the newly created floor
	 */
	public Floor createNewFloor() {
		return createFloor( ZLocalization.getSingleton().getString( "ds.z.DefaultName.Floor" ) + " " + p.getBuildingPlan().floorCount() );
	}
	
	/**
	 * <p>Creates a new floor in the hierarchy. A floor does not have a parent and
	 * is immediately created. It has no explicit bounds that have to be specified
	 * (in contrast to polygonal objects).</p>
	 * @param name the name of the floor
	 * @return the newly created floor
	 */
	public Floor createFloor( String name ) {
		final Floor f = new Floor( name );
		p.getBuildingPlan().addFloor( f );
		return f;
	}

	public void movePolygon( PlanPolygon polygon, int x, int y ) {
		
		//return true;
	}

	public void movePoints( List<PlanPoint> points, int x, int y ) {
		Iterator<PlanPoint> itPP = points.iterator();

						HashSet<Area> affected_areas = new HashSet<Area>();
						PlanPolygon lastPolygon = null;
		PlanPoint planPoint;
		while( itPP.hasNext() && itPP.hasNext() ) {
			// The drag targets are already rasterized, if neccessary
			planPoint = itPP.next();
			//newLocation = itDT.next();
			//if( !trueDrag && !newLocation.equals( planPoint.getLocation() ) )
				// Check for !trueDrag to update "trueDrag" only once
			//	trueDrag = true;

			// TODO use translate
			planPoint.setLocation( planPoint.x + x, planPoint.y + y );

			// Keep track of the areas that we move
			PlanPolygon currentPolygon =
							planPoint.getNextEdge() != null ? planPoint.getNextEdge().getAssociatedPolygon() : planPoint.getPreviousEdge() != null ? planPoint.getPreviousEdge().getAssociatedPolygon() : null;
			currentPolygon.edgeChangeHandler( planPoint.getNextEdge(), planPoint );

			// At the moment disabled area handling...
			// TODO reenable. not so important right now
			// Add the last polygon (whose modification should be complete
			// by now to avoid duplicate entries in the area hashset)
//			if( currentPolygon != lastPolygon ) {
//				if( lastPolygon != null && lastPolygon instanceof Area )
//					affected_areas.add( (Area)lastPolygon );
//				lastPolygon = currentPolygon;
//			}
						// If the user dragged areas into a different room, then we
						// must assign the affected areas to their new rooms
//						for( Area a : affected_areas )
//							// If the area has left its room:
//							if( !a.getAssociatedRoom().contains( a ) ) {
//								// 1) Search for the new room
//								Room newRoom = null;
//
//								for( Component c : getComponents() )
//									if( ((JPolygon)c).getPlanPolygon().contains( a ) ) {
//										newRoom = (Room)((JPolygon)c).getPlanPolygon();
//										break;
//									}
//
//								if( newRoom != null ) {
//									// Assign new room if we found one
//									a.setAssociatedRoom( newRoom );
//									selectPolygon( a );
//								} else
//									// Delete the area if it was dragged out of
//									// its old room and not into any new one
//									a.deletePolygon();
//							}
			EventServer.getInstance().dispatchEvent( new ZModelChangedEvent() {} );
		}
	}

	/**
	 * Clones the floor and adds it to the project. The name of the floor is
	 * extended by '_##' where ## represents a number. If the name of the floor
	 * was ending with a two-digit number, this number is increased by one. It is
	 * not possible to have more than 100 floors with the same name
	 * (only automatically created).
	 * @param f the floor that is copied
	 */
	public void copyFloor( Floor f ) {
		Floor fc = null;
		try {
			 fc = f.clone();
		} catch( UnknownZModelError ex ) {
			if( ZETMain.isDebug() ) {
				System.err.println( "DEBUG-out:" );
				ex.printOriginalStackTrace();
				System.out.println();
			}
			System.err.println( "Floor copy failed due to unknown reason. Check the model for errors." );
			return;
		}

		int number = 0;
		String newName = f.getName() + "_";

		// Check if floorname ends with '##'
		if( Helper.isBetween( f.getName().charAt( f.getName().length() - 2 ), '0', '9' ) && Helper.isBetween( f.getName().charAt( f.getName().length() - 1 ), '0', '9' )  ) {
			number = Integer.parseInt( f.getName().substring( f.getName().length()-2, f.getName().length()-0 ) ) + 1;
			newName = f.getName().substring( 0, f.getName().length()-2 );
		}
		do {
			fc.setName( newName + IOTools.fillLeadingZeros( number++, 2 ) );
		} while( !p.getBuildingPlan().addFloor( fc ) && number <= 99 );
	}

	public void moveFloorUp( int id ) {
		p.getBuildingPlan().moveFloorUp( id );
	}

	public void moveFloorDown( int id ) {
		p.getBuildingPlan().moveFloorDown( id );
	}

	public void deletePoint( PlanPolygon poly, PlanPoint currentPoint ) {
		Edge currentEdge = currentPoint.getNextEdge();
		poly.combineEdges( currentEdge, currentPoint.getOtherEdge( currentEdge ), true );
		EventServer.getInstance().dispatchEvent( new ZModelChangedEvent() {} );
	}

	public void insertPoint( Edge onEdge, PlanPoint newPoint ) {
		// Replace the old edge
		ArrayList<PlanPoint> pointList = new ArrayList<PlanPoint>( 3 );
		pointList.add( onEdge.getSource() );
		pointList.add( newPoint );
		pointList.add( onEdge.getTarget() );
		onEdge.getAssociatedPolygon().replaceEdge( onEdge, pointList );
		EventServer.getInstance().dispatchEvent( new ZModelChangedEvent() {} );
	}
}
