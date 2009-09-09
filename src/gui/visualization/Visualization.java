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
/**
 * Class Visualization
 * Erstellt 20.05.2008, 23:50:54
 */
package gui.visualization;

import com.sun.opengl.util.texture.Texture;
import ds.PropertyContainer;
import event.EventListener;
import event.EventServer;
import event.OptionsChangedEvent;
import gui.JEditor;
import gui.visualization.control.GLControl;
import gui.visualization.util.VisualizationConstants;
import io.movie.MovieManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.glu.GLUquadric;
import localization.Localization;
import opengl.drawingutils.GLColor;
import opengl.helper.Frustum;
import opengl.helper.TextureFont;
import opengl.helper.TextureManager;
import util.vectormath.Vector3;

/**
 * Implements the {@code OpenGL} visualization on a JOGL canvas. The class initializes
 * the canvas, sets up light, textures and other stuff and draws the scene.
 * @author Jan-Philipp Kappmeier
 */
public class Visualization extends AbstractVisualization implements EventListener<OptionsChangedEvent> {
	/** The localization class. */
	private Localization loc = Localization.getInstance();
	private static final int fontSize = 16;
	private TextureManager texMan;
	private TextureFont font;
	private Texture fontTex;
	/** The control object of the graphics data structure (in MVC pattern). */
	private GLControl control = null;
	private GLAutoDrawable drawable;
	private boolean noRotate = false;
	private static GLColor white = new GLColor( Color.white );
	/** Decides wheather a movie is captured or not */
	private boolean recording = false;
	/** If a movie is captured describes the framerate of the movie */
	private int movieFrameRate = 24;
	/** The minimal number of frames that needs to be captured in movie rendering mode for the cellular automaton */
	private int minimalFrameCountCellularAutomaton;
	/** The minimal number of frames that needs to be captured in movie rendering mode for the graph */
	private int minimalFrameCountGraph;
	/** Set to true during drawing if the frame contains some valid information. False at the end of playback. Only valid if in {@code movieRecording} mode.*/
	private boolean frameUsed;
	/** The MovieManager object*/
	private MovieManager movieManager;
	/** The old resolution width used vor movie recording */
	int oldX;
	/** The old resolution height used vor movie recording */
	int oldY;
	/** The width used for movie recording. */
	int movieWidth;
	/** The height used for movie recording. */
	int movieHeight;

	// Status-Variablen die angezeigte Elemente steuern
	private boolean showEye = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.eye" );
	private boolean showFPS = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.fps" );
	private boolean showTimestepGraph = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepGraph" );
	private boolean showTimestepCellularAutomaton = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepCA" );

	/**
	 * Creates a new instance of the {@code Visualization} panel with given 
	 * properties in an {@code GLCapabilities}.
	 * @param capabilities the open gl properties of the panel
	 */
	public Visualization( GLCapabilities capabilities ) {
		super( capabilities );
		camera.setPos( new Vector3( 0, 0, 100 ) );
		camera.setUp( new Vector3( 0, 0, 1 ) );
		camera.setView( new Vector3( 1, -1, 0 ) );
		//JEditor.getInstance().getVisualizationView().setCamera( camera );
		noRotate = !PropertyContainer.getInstance().getAsBoolean( "editor.options.visualization.allowRotateIn2D" );
		movieManager = new MovieManager();

		// this will create errors!
		if( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.2d" ) )
			set2DView();
		else
			set3DView();
		if( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.isometric" ) )
			this.setPvm( ParallelViewMode.Isometric );
		else
			this.setPvm( ParallelViewMode.Orthogonal );




		EventServer.getInstance().registerListener( this, OptionsChangedEvent.class );
	}

	public void init( GLAutoDrawable drawable ) {
		gl = drawable.getGL();

		gl.glClearDepth( 1.0f );																				// Initialize depth-buffer precision
		gl.glDepthFunc( GL.GL_LEQUAL );																	// Quality of depht-testing
		gl.glEnable( GL.GL_DEPTH_TEST );																// Enable depth-buffer. (z-buffer)
		gl.glShadeModel( GL.GL_SMOOTH );																// Activate smooth-shading (Gauraud)
		gl.glHint( GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST );		// Perspective calculations with high precision
    gl.glHint( GL.GL_GENERATE_MIPMAP_HINT, GL.GL_NICEST );					//
//    gl.glHint( GL.GL_FOG_HINT, GL.GL_NICEST );										//
    gl.glHint( GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST );							//
    gl.glHint( GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST );							//
    gl.glHint( GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST );						//

		// Enable VSync
		gl.setSwapInterval( 1 );

		gl.glEnable( GL.GL_LIGHTING );
		gl.glEnable( GL.GL_LIGHT0 );
		//float[] a = { 0, 0, -100 };
		//gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, a, 0 );
		float[] mat_specular = {1.0f, 1.0f, 1.0f, 1.0f};
		float[] mat_shininess = {100.0f};
		//float[] mat_ambient = {0.4f, 0.4f, 0.4f, 1.0f};
		//float[] mat_diffuse = {0.4f, 0.8f, 0.4f, 1.0f};
		float[] mat_ambient = {1f, 0f, 0f, 1.0f};
		float[] mat_diffuse = {0f, 0f, 0f, 1.0f};

		//float[] light_position = {10.0f, 10.0f, 0.0f, 1.0f};
		float[] light_position = {10.0f, 10.0f, 0.0f, 0.0f};
		//float[] light_ambient = {0.8f, 0.8f, 0.8f, 1.0f};
		//float[] light_ambient = {1f, 1f, 1f, 1.0f};
		float[] light_ambient = {0.8f, 0.8f, 0.8f, 1.0f};
		float[] light_diffuse = {0.4f, 0.4f, 0.4f, 1.0f};
		//float[] light_diffuse = {0.8f, 0.8f, 0.8f, 1.0f};

		gl.glMaterialfv( GL.GL_FRONT, GL.GL_SPECULAR, mat_specular, 0 );
		gl.glMaterialfv( GL.GL_FRONT, GL.GL_SHININESS, mat_shininess, 0 );
		gl.glMaterialfv( GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient, 0 );
		gl.glMaterialfv( GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0 );

		gl.glLightfv( GL.GL_LIGHT0, GL.GL_AMBIENT, light_ambient, 0 );
		gl.glLightfv( GL.GL_LIGHT0, GL.GL_DIFFUSE, light_diffuse, 0 );
		gl.glLightfv( GL.GL_LIGHT0, GL.GL_POSITION, light_position, 0 );


		// load textures
		texMan = TextureManager.getInstance();
		texMan.load( "font2", "./textures/font2.bmp" );
		// load texture font
		fontTex = texMan.get( "font2" );
		font = new TextureFont( gl, fontTex );
		font.buildFont( 16, 8, 16, 12, 9 );
		//font.buildFont( 16, 8, 16, 24, 19 );
		fontTex.bind();

		gl.glEnable( GL.GL_NORMALIZE );

//		if ( glewIsSupported( "GL_EXT_texture_filter_anisotropic" ) ) {
//	    float maxAni;
//			glGetFloatv( GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, &maxAni );
//			glBindTexture( GL_TEXTURE_2D, theTex );
//			glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAni );
//		}
}


		if( control == null )
			control = new GLControl();
	}

	/**
	 * Draws the scene.
	 * @param drawable the {@code OpenGL} context
	 */
	public void display( GLAutoDrawable drawable ) {
		// TODO: richtig machen mit dem update :D
		// Status-Variablen die angezeigte Elemente steuern
		showEye = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.eye" );
		showFPS = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.fps" );
		showTimestepGraph = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepGraph" );
		showTimestepCellularAutomaton = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepCA" );


		this.drawable = drawable;
		calculateFPS();
		if( isAnimating() == true && recording == false )
			animate();
		if( recording )
			if( drawable.getWidth() != movieWidth || drawable.getHeight() != movieHeight ) {
				setSize( movieWidth, movieHeight );
				return;
			}

		if( updateProjection )
			updateProjection();
		gl.glClear( clearBits );

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();
		float[] light_position = new float[4];
		light_position[0] = (float)getCamera().getView().x;
		light_position[1] = (float)getCamera().getView().y;
		light_position[2] = (float)getCamera().getView().z;
		//light_position[0] = 0;
		//light_position[1] = 1;
		//light_position[2] = 0;
		light_position[3] = 1.0f;

		//gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position, 0);
		if( is3D )
			look();
		else {
			if( pvm != ParallelViewMode.Orthogonal ) {	// Isometric view
				if( pvm == ParallelViewMode.Isometric )
					gl.glRotatef( 35.264f, 1.0f, 0.0f, 0.0f );
				else
					gl.glRotatef( 30f, 1.0f, 0.0f, 0.0f );
				gl.glRotatef( -45.0f, 0.0f, 1.0f, 0.0f );
				gl.glRotated( -90, 1, 0., 0. );
			} else	// Orthogonal view
				gl.glLoadIdentity();
			if( showEye )
				drawEye();
		}

		control.draw( drawable );
		drawFPS();

		switch( gl.glGetError() ) {
			case GL.GL_NO_ERROR:
				break;
			case GL.GL_INVALID_ENUM:
				System.err.println( "INVALID ENUM" );
				break;
			case GL.GL_INVALID_VALUE:
				System.err.println( "INVALID VALUE" );
				break;
			case GL.GL_INVALID_OPERATION:
				System.err.println( "INVALID OPERATION" );
				break;
			case GL.GL_STACK_OVERFLOW:
				System.err.println( "STACK OVERFLOW" );
				break;
			case GL.GL_STACK_UNDERFLOW:
				System.err.println( "STACK UNDERFLOW" );
				break;
			case GL.GL_OUT_OF_MEMORY:
				System.err.println( "OUT OF MEMORY" );
				break;
		}
		gl.glFlush();


		if( takeScreenshot )
			takeScreenshot( drawable );
		if( recording && frameUsed ) {
			String newFilename = movieManager.nextFilename();
			takeScreenshot( drawable, newFilename );
			movieManager.addImage( newFilename );
			movieStep();
		}
	}

	/**
	 * Sets the correct animation time. Calculated by the difference
	 * from the current time and last time.
	 * @see #getDeltaTime()
	 */
	final public void animate() {
		control.addTime( getDeltaTime() );
	}

	/**
	 * Sets the new animation time a specified time to the future
	 * @param timestep the specified time
	 */
	final public void animate( long timestep ) {
		control.addTime( timestep );
	}

	/**
	 * Sets the new time basing on the movie frame time
	 */
	final public void movieStep() {
		animate( Math.round( (1000. / movieFrameRate) * 1000 * 1000 ) );
	}

	/**
	 * Draws the eye and the lines representing the 3d-sight-field in the 2d-view.
	 */
	final private void drawEye() {
		// Calculate the vector of the eye rotation with respect to to the current
		// view vector.
		Vector3 cameraView = new Vector3( getCamera().getView() );
		cameraView.z = 0;
		cameraView.normalize();
		double eyeRotation = Math.acos( rotation2D.dotProduct( cameraView ) );
		int orientation = Vector3.orientation( rotation2D, getCamera().getView() );
		if( orientation == -1 )
			eyeRotation = -eyeRotation;
		eyeRotation = eyeRotation * Frustum.DEG2ANGLE;

		gl.glTranslated( getCamera().getPos().x, getCamera().getPos().y, getCamera().getPos().z );
		GLColor red = new GLColor( Color.red );
		red.performGL( gl );
		GLUquadric quadObj = glu.gluNewQuadric();
		glu.gluSphere( quadObj, 10 * VisualizationConstants.SIZE_MULTIPLICATOR, 10, 3 );
		gl.glPushMatrix();
		gl.glRotated( eyeRotation + 25, 0, 0, 1 );
		gl.glBegin( GL.GL_LINES );
		gl.glVertex3d( 0.0, 0.0, 0.0 );
		gl.glVertex3d( 300, 0.0, 0.0 );
		gl.glEnd();
		gl.glRotated( -45, 0, 0, 1 );
		gl.glBegin( GL.GL_LINES );
		gl.glVertex3d( 0.0, 0.0, 0.0 );
		gl.glVertex3d( 300, 0.0, 0.0 );
		gl.glEnd();
		gl.glPopMatrix();
		gl.glRotated( getRotateAngle(), 0, 0, 1 );
		gl.glTranslated( -getCamera().getPos().x, -getCamera().getPos().y, -getCamera().getPos().z );
	}

	/**
	 * Draws the current framerate on the lower left edge of the screen and
	 * the current time of the cellular automaton and graph, if used.
	 */
	final private void drawFPS() {
			if( gl.glGetError() == gl.GL_INVALID_VALUE )
			System.err.println( "ERROR vor look" );
	this.setProjectionPrint();
		if( gl.glGetError() == gl.GL_INVALID_VALUE )
			System.err.println( "ERROR nach look" );
		white.performGL( gl );
		gl.glEnable( gl.GL_TEXTURE_2D );
		if( showFPS )
			font.print( 0, 0, Integer.toString( this.fps ) + " FPS" );
		int row = 1;
		if( control.hasCellularAutomaton() ) {
			if( control.isCaFinshed() ) {
				minimalFrameCountCellularAutomaton--;
				if( showTimestepCellularAutomaton ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.simulationFinished" ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.simulationNeeded" ) + " " + secToMin( control.getCaStep() * control.getCaSecondsPerStep() ) );
				}
			} else {
				minimalFrameCountCellularAutomaton = 2;
				if( showTimestepCellularAutomaton ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.simulationStep" ) + " " + Double.toString( control.getCaStep() ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.simulationTime" ) + " " + secToMin( control.getCaStep() * control.getCaSecondsPerStep() ) );
				}
			}
			row++;
		}
		if( control.hasGraph() ) {
			if( control.isGraphFinished() ) {
				minimalFrameCountGraph--;
				if( showTimestepGraph ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.graphFinished" ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.graphNeeded" ) + " " + secToMin( control.getGraphStep() * control.getGraphSecondsPerStep() ) );
				}
			} else {
				minimalFrameCountGraph = 2;
				if( showTimestepGraph ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.graphStep" ) + " " + Double.toString( control.getGraphStep() ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.graphTime" ) + " " + secToMin( control.getGraphStep() * control.getGraphSecondsPerStep() ) );
				}
			}
			row++;
		}
		if( recording ) {
			boolean frameUsedOld = frameUsed;
			frameUsed = Math.max( minimalFrameCountCellularAutomaton, minimalFrameCountGraph ) >= 1;
			if( frameUsedOld != frameUsed ) {
				// The movie is finished completely
				this.repaint();
				movieManager.performFinishingActions();
				setRecording( false, getSize() );
				stopAnimation();
			}
		}
		//font.print( 0, this.getHeight() - (row++)*fontSize, "Zeit: " + secToMin( getTimeSinceStart()/1000000000 ) );
		gl.glDisable( gl.GL_TEXTURE_2D );
		this.resetProjection();
	}

	/**
	 * Helper method that converts an double seconds value in an string
	 * representing minutes and seconds.
	 * @param sec
	 * @return a string representing the time in minutes
	 */
	final private String secToMin( double sec ) {
		int min = (int)Math.floor( sec / 60 );
		int secs = (int)Math.floor( sec - (60 * min) );
		String ssecs = secs < 10 ? "0" + Integer.toString( secs ) : Integer.toString( secs );
		return ssecs.length() >= 2 ? Integer.toString( min ) + ":" + ssecs.substring( 0, 2 ) + " Min" : Integer.toString( min ) + ":" + ssecs + " Min";
	}

	/**
	 * Sets the current control object.
	 * @param control the control object
	 */
	public final void setControl( GLControl control ) {
		this.control = control;
	}

	/**
	 * Returns the current control object.
	 * @return the control object
	 */
	public final GLControl getControl() {
		return control;
	}

	/**
	 * Returns the movie creator.
	 * @return the movie creator
	 */
	public final MovieManager getMovieCreator() {
		return movieManager;
	}

	/**
	 * Returns the currently set framerate for movie capture
	 * @return the currently set framerate for movie capture
	 */
	public int getMovieFPS() {
		return movieFrameRate;
	}

	/**
	 * Sets a new framerate for movie capture
	 * @param movieFPS the framerate
	 */
	public void setMovieFramerate( int movieFPS ) {
		this.movieFrameRate = movieFPS;
	}

	/**
	 * Checks if the panel is in movie capture mode
	 * @return true if panel is in movie capture mode, false otherwise
	 */
	public boolean isRecording() {
		return recording;
	}

	/**
	 * <p>Enables or disables the movie capture mode and sets the movie resolution
	 * if recording is enabled and resets back to normal resolution if recording
	 * is disabled.</p>
	 * <p>If called with <code>recording</code> set to true the current resolution
	 * is stored and rewritten if called with false. Thus automatic resetting of
	 * the resolution only works if the method is called with true and false
	 * in this order.
	 * </p>
	 * @param recording the status of the movie capture mode
	 * @param resolution the resolution used for video recording
	 */
	public void setRecording( boolean recording, Dimension resolution ) {
		this.recording = recording;
		if( recording ) {
			oldX = this.getSize().width;
			oldY = this.getSize().height;
			movieWidth = resolution.width;
			movieHeight = resolution.height;
		} else
			setSize( oldX, oldY );
	}

	/**
	 * Starts animation, stores animation state.
	 */
	@Override
	public void startAnimation() {
		super.startAnimation();
		this.frameUsed = true;
	}

	public void initGFX( GLAutoDrawable drawable ) {
		throw new UnsupportedOperationException( "Not supported by JOGL yet." );
	}

	/**
	 * Called if an {@link OptionsChangedEvent} is send to the visualization class.
	 * Updates the variables indicating the visible elements in the visualization.
	 * @param event the event
	 */
	public void handleEvent( OptionsChangedEvent event ) {
		update();
	}

	public void update() {
		// TODO update weiterleiten an den MovieManager
		//moviePath = PropertyContainer.getInstance().getAsString( "options.filehandling.moviePath" );
		//movieFrameName = PropertyContainer.getInstance().getAsString( "options.filehandling.movieFrameName" );
		showEye = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.eye" );
		showFPS = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.fps" );
		repaint();
	}

	@Override
	@SuppressWarnings("fallthrough")
	public void keyPressed( KeyEvent e ) {
		switch( e.getKeyCode() ) {
			case KeyEvent.VK_C:
				System.out.println( loc.getStringWithoutPrefix( "gui.visualizationView.cameraInformation" ) );
				System.out.println( camera );
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				JEditor.getInstance().getVisualizationView().updateCameraInformation();
			default:
				super.keyPressed( e );
		}
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		super.mousePressed( e );
		JEditor.getInstance().getVisualizationView().updateCameraInformation();
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		super.mouseDragged( e );
		JEditor.getInstance().getVisualizationView().updateCameraInformation();
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		super.mouseWheelMoved( e );
		JEditor.getInstance().getVisualizationView().updateCameraInformation();
	}
}
