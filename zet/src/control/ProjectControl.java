/*
 * ProjectControl.java
 * Created 09.09.2009, 12:08:24
 */

package control;

import converter.ZToCAConverter;
import ds.Project;
import ds.z.Assignment;
import ds.z.AssignmentType;
import ds.z.Floor;
import gui.ZETMain;
import java.io.File;
import localization.Localization;
import util.Helper;
import util.IOTools;
import util.random.distributions.NormalDistribution;

/**
 * The class <code>ProjectControl</code> represents a control class to implement
 * the MVC design pattern. It has access to a ZET project, can load and store it
 * and change it.
 * The methods should be called from the graphical user interface or some
 * command line.
 * @author Jan-Philipp Kappmeier
 */
public class ProjectControl {
	/** The localization class. */
	static final Localization loc = Localization.getInstance();
	/** The project that is controlled by this class. */
	private Project project;

	/**
	 * Creates a new instance of <code>ProjectControl</code>. The {@link Project}
	 * is set to a new project.
	 */
	public ProjectControl() {
		project = newProject();
	}

	/**
	 * Creates a new instance of <code>ProjectControl</code> which controls a
	 * given project.
	 * @param p the project that should be controlled
	 */
	public ProjectControl( Project p ) {

	}

	/**
	 * Creates a new instance of <code>ProjectControl</code> which controls a
	 * given project.
	 * @param file the path to a file that should be loaded as project.
	 */
	public ProjectControl( String file ) {

	}
	/**
	 * Creates a new instance of <code>ProjectControl</code> which controls a
	 * given project.
	 * @param file the file that should be loaded as project.
	 */
	public ProjectControl( File file ) {
		if( !loadProject( file ) ) {
			project = newProject();
		}
	}

	public void loadProject( String projectFile ) {

	}

	/**
	 * Loads the specified {@link File}.
	 * @param projectFile the project file
	 * @return returns true, if the project loaded correctly
	 */
	public boolean loadProject( File projectFile ) {
		try {
			project = Project.load( projectFile );
//			distribution = null; // Throw away the old assignment window
			project.setProjectFile( projectFile );
//			editView.displayProject( loaded );
			// Löschen eingestellter parameter
			ZToCAConverter.getInstance().clear();
			//firstSwitch = true;
			//if( !PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" ) )
			//	editView.setFloor( 1 );
			// Updaten der gui
			//this.getEditView().update();
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
			//editView.displayProject( projectControl.getProject() );
			ZETMain.sendMessage( loc.getString( "gui.editor.JEditor.message.loadError" ) );
			return false;
		}
		return true;
	}

	/**
	 * Creates a new project file with default settings
	 * @return the newly created project
	 */
	public static Project newProject() {
		Project p = new Project();
		Floor fl = new Floor( loc.getString( "ds.z.DefaultName.Floor" ) + " 1" );
		p.getPlan().addFloor( fl );
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
		} while( !getProject().getPlan().addFloor( fc ) && number <= 99 );
	}

	public void moveFloorUp( int id ) {
		project.getPlan().moveFloorUp( id );
	}

	public void moveFloorDown( int id ) {
		project.getPlan().moveFloorDown( id );
	}

	/**
	 * Returns the currently controlled project
	 * @return the currently controlled project.
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Sets a new project to be controlled.
	 * @param project the new project
	 * @throws NullPointerException if null is submitted as project
	 */
	public void setProject( Project project ) throws NullPointerException {
		if( project == null )
			throw new NullPointerException( "Project is null.");
		this.project = project;
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "ProjectControl";
	}
}