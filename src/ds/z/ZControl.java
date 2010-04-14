/*
 * ZControl.java
 * Created 16.12.2009, 13:11:44
 */
package ds.z;

import converter.ZToCAConverter;
import ds.Project;
import ds.z.exception.AssignmentException;
import java.util.List;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.util.Helper;
import de.tu_berlin.math.coga.common.util.IOTools;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;
import gui.ZETMain;
import java.io.File;

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
	/** The localization class. */
	static final Localization loc = Localization.getInstance();
	/** The project that is root of the controlled model. */
	private Project p;

	public ZControl() {
		newProject();
	}

	/**
	 * Creates a new instance of <code>ProjectControl</code> which controls a
	 * given project.
	 * @param filename the path to a file that should be loaded as project.
	 */
	public ZControl( String filename ) {
		this( new File( filename ) );
	}

	/**
	 * Creates a new instance of <code>ProjectControl</code> which controls a
	 * given project.
	 * @param file the file that should be loaded as project.
	 */
	public ZControl( File file ) {
		if( !loadProject( file ) ) {
			p = newProject();
			//zcontrol = new ZControl( p );
			//zcontrol = newProject();
		}
	}

	/**
	 * Creates a new instance of <code>ZControl</code>.
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
	public boolean loadProject( File projectFile ) {
		try {
			p = Project.load( projectFile );
//			distribution = null; // Throw away the old assignment window
			p.setProjectFile( projectFile );
			//zcontrol = new ZControl( p );
//			editView.displayProject( loaded );
			// delete parameters that are set
			ZToCAConverter.getInstance().clear();
			//firstSwitch = true;
			//if( !PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) )
			//	editView.setFloor( 1 );
			// Update the graphical user interface
			ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.loaded" ) );	// TODO output changed, use listener
		} catch( Exception ex ) {
			//JOptionPane.showMessageDialog( null,
			//				loc.getString( "gui.editor.JEditor.error.loadError" ),
			//				loc.getString( "gui.editor.JEditor.error.loadErrorTitle" ),
			//				JOptionPane.ERROR_MESSAGE );
			System.err.println( loc.getString( "gui.editor.JEditor.error.loadErrorTitle" ) + ":" );
			System.err.println( " - " + loc.getString( "gui.editor.JEditor.error.loadError" ) );
			ex.printStackTrace();
			//editView.displayProject( EditorStart.newProject() );
			//projectControl.newProject();
			//editView.displayProject( projectControl.getZControl() );
			ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.loadError" ) );
			return false;
		}
		return true;
	}

	/**
	 * Creates a new project with default settings and returns it. The old model
	 * controlled by this class is replaced by the new empty model for the new
	 * project.
	 * @return the newly created project
	 */
	public Project newProject() {
		p = new Project();
		Floor fl = new Floor( loc.getString( "ds.z.DefaultName.Floor" ) + " 1" );
		p.getBuildingPlan().addFloor( fl );
		Assignment assignment = new Assignment( loc.getString( "ds.z.DefaultName.DefaultAssignment" ) );
		p.addAssignment( assignment );
		NormalDistribution d = new NormalDistribution( 0.5, 1.0, 0.4, 0.7 );
		NormalDistribution a = new NormalDistribution( 16, 1, 14, 80 );
		NormalDistribution f = new NormalDistribution( 0.8, 1.0, 0.7, 1.0 );
		NormalDistribution pa = new NormalDistribution( 0.5, 1.0, 0.0, 1.0 );
		NormalDistribution de = new NormalDistribution( 0.3, 1.0, 0.0, 1.0 );
		AssignmentType assignmentType = new AssignmentType( loc.getString( "ds.z.DefaultName.DefaultAssignmentType" ), d, a, f, pa, de, 10 );
		assignment.addAssignmentType( assignmentType );
		return p;
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
	 * Clones the floor and adds it to the project. The name of the floor is
	 * extanded by '_##' where ## represents a number. If the name of the floor
	 * was ending with a two-digit number, this number is increased by one. It is
	 * not possible to have more than 100 floors with the same name
	 * (only automatically crated).
	 * @param f the floor that is copied
	 */
	public void copyFloor( Floor f ) {
		final Floor fc = f.clone();
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

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "ZControl";
	}
}
