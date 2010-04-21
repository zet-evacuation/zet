/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * Visualization.java
 * Created 20.05.2008, 23:50:54
 */
package gui.visualization;

import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.math.Conversion;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import event.EventServer;
import event.MessageEvent;
import event.MessageEvent.MessageType;
import io.movie.MovieManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.glu.GLUquadric;
import opengl.drawingutils.GLColor;
import opengl.framework.abs.DrawableControlable;
import opengl.helper.Frustum;
import opengl.helper.ProjectionHelper;
import opengl.helper.Texture;
import opengl.helper.TextureFont;
import opengl.helper.TextureFontStrings;
import opengl.helper.TextureManager;

/**
 * Implements the {@code OpenGL} visualization on a JOGL canvas. The class initializes
 * the canvas, sets up light, textures and other stuff and draws the scene.
 * @author Jan-Philipp Kappmeier
 */
public class Visualization<U extends DrawableControlable> extends AbstractVisualization {
	//public final static double SIZE_MULTIPLICATOR = 0.1;
	public enum RecordingMode {
		Recording,
		NotRecording,
		SkipFrame,
	}
	
	protected double sizeMultiplikator = 0.1;
	/** The localization class. */
	protected Localization loc = Localization.getInstance();
	protected static final int fontSize = 16;
	private TextureManager texMan;
	/** The {@link TextureFont} used to display informations in the screen. */
	protected TextureFont font;
	/** The {@link TextureFont} used to display bold text in the intro. */
	private TextureFont fontBold;
	/** The texture containing the font. */
	protected Texture fontTex;
	/** The texture containing the logo. */
	private Texture logoTex;
	/** The texture for the logo mask (used for blending). */
	private Texture maskTex;
	/** The control object of the graphics data structure (in MVC pattern). */
	protected U control = null;
	/** The {@code OpenGL} context. */
	private GLAutoDrawable drawable;
	/** Indicates if mouse movement in 2d-view rotates or moves the building. */
	protected boolean noRotate = false;
	/** Decides wheather a movie is captured or not */
	private RecordingMode recording = RecordingMode.NotRecording;
	/** If a movie is captured describes the framerate of the movie */
	private int movieFrameRate = 24;
	/** Set to true during drawing if the frame contains some valid information. False at the end of playback. Only valid if in {@code movieRecording} mode.*/
	private boolean frameUsed;
	/** The MovieManager object*/
	private MovieManager movieManager;
	/** The old resolution width used for movie recording */
	int oldX;
	/** The old resolution height used for movie recording */
	int oldY;
	/** The width used for movie recording. */
	int movieWidth;
	/** The height used for movie recording. */
	int movieHeight;
	/** The intro page that is currently shown. Starts from 0 up to the maximal number. */
	int showIntro = 0;
	/** The number of seconds that each intro page is visible. */
	double introSec = 8.3;
	/** The intro pages that are shown. */
	ArrayList<TextureFontStrings> texts = new ArrayList<TextureFontStrings>();
	// Status-Variablen die angezeigte Elemente steuern
	protected boolean showEye = true;
	protected boolean showFPS = true;

	protected Frustum frustum;

	public Frustum getFrustum() {
		return frustum;
	}

	/**
	 * Creates a new instance of the {@code Visualization} panel with given 
	 * properties in an {@code GLCapabilities}.
	 * @param capabilities the open gl properties of the panel
	 */
	public Visualization( GLCapabilities capabilities ) {
		super( capabilities );
		movieManager = new MovieManager();
		set2DView();
		frustum = new Frustum();
		this.setParallelViewMode( ParallelViewMode.Isometric );
	}

	/**
	 * Returns the set of texts that is shown as the intro.
	 * @return the set of texts that is shown as the intro
	 */
	public ArrayList<TextureFontStrings> getTexts() {
		return texts;
	}

	/**
	 * Sets a new set of texts that are shown as the intro.
	 * @param texts the <code>ArrayList</code> containing the texts.
	 */
	public void setTexts( ArrayList<TextureFontStrings> texts ) {
		this.texts = texts;
	}
	int introCount = 0;
	int screenshotCounter = 0;

	/**
	 * Draws the scene, including text and takes screenshots, if necessary.
	 * @param drawable the {@code OpenGL} context
	 */
	@Override
	public void display( GLAutoDrawable drawable ) {
		// TODO: richtig machen mit dem update :D
		// Status-Variablen die angezeigte Elemente steuern

		this.drawable = drawable;
		if( isAnimating() == true && recording == RecordingMode.NotRecording )
			animate();

		if( recording == RecordingMode.Recording && (drawable.getWidth() != movieWidth || drawable.getHeight() != movieHeight) ) {
			setSize( movieWidth, movieHeight );
			return;
		}

		if( updateProjection ) {
			frustum.setAll( getFov(), aspect, getzNear(), getzFar() );
			updateProjection();
		}

		boolean introRunning = false;
		if( recording != RecordingMode.Recording  )
			drawScene();
		else if( showIntro < texts.size() && texts.get( showIntro ).size() > 0 ) {
			drawIntroText( showIntro );
			introRunning = true;
			if( (++introCount) >= (introSec * movieFrameRate) ) {
				showIntro++;
				introCount = 0;
			}
		} else
			drawScene();

		// Show logo
		drawLogo();

		printErrors();
		gl.glFlush();

		if( takeScreenshot )
			takeScreenshot( drawable );
		if( recording == RecordingMode.Recording && frameUsed ) {
			String newFilename = movieManager.nextFilename();
			takeScreenshot( drawable, newFilename );
			EventServer.getInstance().dispatchEvent( new MessageEvent<Visualization>( this, MessageType.VideoFrame, "Video frame " + (++screenshotCounter) + " - " + (screenshotCounter * (1.0 / movieFrameRate)) + " sec" ) );
			movieManager.addImage( newFilename );
			if( !introRunning )
				movieStep();
		}
		EventServer.getInstance().dispatchEvent( new MessageEvent<Visualization>( this, MessageType.Status, "Frame rendered" ) );
	}

	/**
	 * Sets the correct animation time. Calculated by the difference
	 * from the current time and last time.
	 * @see #getDeltaTime()
	 */
	@Override
	final public void animate() {
		computeFPS();
		control.addTime( getDeltaTime() );
	}

	/**
	 * Sets the new animation time a specified time to the future
	 * @param timestep the specified time
	 */
	@Override
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
		// Compute the vector of the eye rotation with respect to to the current
		// view vector.
		Vector3 cameraView = new Vector3( camera.getView() );
		cameraView.z = 0;
		cameraView.normalize();
		double eyeRotation = Math.acos( rotation2D.dotProduct( cameraView ) );
		int orientation = Vector3.orientation( rotation2D, camera.getView() );
		if( orientation == -1 )
			eyeRotation = -eyeRotation;
		eyeRotation = eyeRotation * Conversion.DEG2ANGLE;

		gl.glTranslated( camera.getPos().x, camera.getPos().y, camera.getPos().z );
		GLColor red = new GLColor( Color.red );
		red.draw( gl );
		GLUquadric quadObj = glu.gluNewQuadric();
		glu.gluSphere( quadObj, 10 * sizeMultiplikator, 10, 3 );
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
		gl.glTranslated( -camera.getPos().x, -camera.getPos().y, -camera.getPos().z );
	}

	/**
	 * Prints some getText on the screen. The getText is shown before the visualization
	 * starts if a movie is recorded.
	 */
	final private void drawIntroText( int index ) {
		gl.glClear( clearBits );
		ProjectionHelper.setPrintScreenProjection( gl, viewportWidth, viewportHeight );
		GLColor.white.draw( gl );
		gl.glEnable( gl.GL_TEXTURE_2D );
		fontTex.bind();
		TextureFontStrings tfs = texts.get( index );
		for( int i = 0; i < tfs.size(); ++i )
//			font.print( 100, this.getHeight() - (7+i) * fontSize, tfs.getText( index ) );
			if( tfs.getBold( i ) )
				fontBold.print( 100, this.getHeight() - (int)tfs.getY( i ), tfs.getText( i ) );
			else
				font.print( 100, this.getHeight() - (int)tfs.getY( i ), tfs.getText( i ) );
		gl.glDisable( gl.GL_TEXTURE_2D );
		ProjectionHelper.resetProjection( gl );
	}

	/**
	 * Draws the ZET logo on the lower right edge.
	 */
	final private void drawLogo() {
		int logoHeight = 128;
		ProjectionHelper.setPrintScreenProjection( gl, viewportWidth, viewportHeight );
		gl.glEnable( GL.GL_BLEND );
		gl.glBlendFunc( GL.GL_DST_COLOR, GL.GL_ZERO );
		gl.glEnable( GL.GL_TEXTURE_2D );
		// Draw the mask
		maskTex.bind();
		gl.glBegin( GL.GL_QUADS );
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3d( drawable.getWidth() - 2 * logoHeight, 0, -1 );
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3d( drawable.getWidth() - 2 * logoHeight, logoHeight, -1 );
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3d( drawable.getWidth(), logoHeight, -1 );
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3d( drawable.getWidth(), 0, -1 );
		gl.glEnd();

		// Draw the logo
		gl.glBlendFunc( GL.GL_SRC_COLOR, GL.GL_ONE );// Copy Image 2 Color To The Screen
		logoTex.bind();
		gl.glBegin( GL.GL_QUADS );
		gl.glTexCoord2f( 0.0f, 1.0f );
		gl.glVertex3d( drawable.getWidth() - 2 * logoHeight, 0, -1 );
		gl.glTexCoord2f( 0.0f, 0.0f );
		gl.glVertex3d( drawable.getWidth() - 2 * logoHeight, logoHeight, -1 );
		gl.glTexCoord2f( 1.0f, 0.0f );
		gl.glVertex3d( drawable.getWidth(), logoHeight, -1 );
		gl.glTexCoord2f( 1.0f, 1.0f );
		gl.glVertex3d( drawable.getWidth(), 0, -1 );
		gl.glEnd();
		gl.glDisable( GL.GL_TEXTURE_2D );
		gl.glDisable( GL.GL_BLEND );
		ProjectionHelper.resetProjection( gl );
	}

	/**
	 * Prints some getText on the screen. The getText is shown after the visualization
	 * has finished if a movie is recorded.
	 */
	final private void drawOutroText() {
		ProjectionHelper.setPrintScreenProjection( gl, viewportWidth, viewportHeight );
		GLColor.white.draw( gl );
		gl.glEnable( gl.GL_TEXTURE_2D );
		int row = 1;
		font.print( 100, this.getHeight() - (7) * fontSize, loc.getString( "testtext" ) );
		gl.glDisable( gl.GL_TEXTURE_2D );
		ProjectionHelper.resetProjection( gl );
	}

	/**
	 * Draws the main scene, that is the walls, the cellular automaton, the individuals and the graph.
	 * Includes getText visible in the scene but not the copyright notice, etc.
	 */
	final private void drawScene() {
		gl.glClear( clearBits );

		gl.glMatrixMode( GL.GL_MODELVIEW );
		gl.glLoadIdentity();
		float[] light_position = new float[4];
		light_position[0] = (float)camera.getView().x;
		light_position[1] = (float)camera.getView().y;
		light_position[2] = (float)camera.getView().z;
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

		if( control != null )

		control.draw( gl );
		gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
		drawFPS();
	}

	private int minimalFrameCount = 0;

	/**
	 * Draws the current framerate on the lower left edge of the screen and
	 * the current time of the cellular automaton and graph, if used.
	 */
	protected void drawFPS() {
		ProjectionHelper.setPrintScreenProjection( gl, viewportWidth, viewportHeight );
		GLColor.white.draw( gl );
		gl.glEnable( GL.GL_BLEND );
		gl.glBlendFunc( GL.GL_ONE, GL.GL_ONE );// Copy Image 2 Color To The Screen
		gl.glEnable( gl.GL_TEXTURE_2D );
		fontTex.bind();
		if( showFPS )
			font.print( 0, 0, Integer.toString( getFPS() ) + " FPS" );

		if( control.isFinished() )
			minimalFrameCount--;
		else
			minimalFrameCount = 2;

		if( recording == RecordingMode.Recording ) {
			final boolean frameUsedOld = frameUsed;
			//frameUsed = Math.max( minimalFrameCountCellularAutomaton, minimalFrameCountGraph ) >= 1;
			frameUsed = minimalFrameCount >= 1;
			// somehow stop recording if paused
			if( frameUsedOld != frameUsed ) {
				createMovie();
			}
		}
		//font.print( 0, this.getHeight() - (row++)*fontSize, "Zeit: " + secToMin( getTimeSinceStart()/Conversion.secToNanoSeconds ) );
		gl.glDisable( GL.GL_TEXTURE_2D );
		gl.glDisable( GL.GL_BLEND );
		ProjectionHelper.resetProjection( gl );
	}


	/**
	 * Converts rendered frames to a movie or stops creating a movie. It is called
	 * if the visualization is finished (CA and graph finished). Call it, if you
	 * stop movie recording in between.
	 */
	public void createMovie() {
		if( recording == RecordingMode.NotRecording )
			throw new IllegalStateException( "Not in recording mode." );
		// The movie is finished completely
		this.repaint();
		movieManager.performFinishingActions();
		setRecording( RecordingMode.NotRecording, getSize() );
		if( isAnimating() )
			stopAnimation();
		createInformationFile();
	}

	/**
	 * Sets the current control object.
	 * @param control the control object
	 */
	public final void setControl( U control ) {
		this.control = control;
		control.setFrustum( frustum );
	}

	/**
	 * Returns the current control object.
	 * @return the control object
	 */
	public final U getControl() {
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
	public RecordingMode getRecording() {
		return recording;
	}

	/**
	 * <p>Enables or disables the movie capture mode and sets the movie resolution
	 * if recording is enabled and resets back to normal resolution if recording
	 * is disabled.</p>
	 * <p>If called with <code>recording</code> set to true the current resolution
	 * is stored and rewritten if called with false. Thus automatic resetting of
	 * the resolution only works if the method is called with true and false
	 * in this order.</p>
	 * @param recording the status of the movie capture mode
	 * @param resolution the resolution used for video recording
	 */
	public void setRecording( RecordingMode recording, Dimension resolution ) {
		
		this.recording = recording;
		if( recording == RecordingMode.Recording ) {
			showIntro = 0;
			oldX = this.getSize().width;
			oldY = this.getSize().height;
			movieWidth = resolution.width;
			movieHeight = resolution.height;
			introCount = 0;
			screenshotCounter = 0;
		} else if( recording == RecordingMode.NotRecording )
			setSize( oldX, oldY );
	}

	/**
	 * Only use this method to pause recording (set recording to SkipFrame), no
	 * intro times are reset and the movie width is not changed.
	 * @param recording
	 */
	public void setRecording( RecordingMode recording ) {
		this.recording = recording;
	}

	/**
	 * Starts animation and stores animation state.
	 */
	@Override
	public void startAnimation() {
		super.startAnimation();
		this.frameUsed = true;
	}

	@Override
	public void initGFX( GLAutoDrawable drawable ) {
		gl = drawable.getGL();

		gl.glClearDepth( 1.0f );																						// Initialize depth-buffer precision
		gl.glDepthFunc( GL.GL_LEQUAL );																		// Quality of depht-testing
		gl.glEnable( GL.GL_DEPTH_TEST );																	// Enable depth-buffer. (z-buffer)
		gl.glShadeModel( GL.GL_SMOOTH );																 // Activate smooth-shading (Gauraud)
		gl.glHint( GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST );	 // Perspective calculations with high precision
		gl.glHint( GL.GL_GENERATE_MIPMAP_HINT, GL.GL_NICEST );				 //
//    gl.glHint( GL.GL_FOG_HINT, GL.GL_NICEST );											 //
		gl.glHint( GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST );              //
		gl.glHint( GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST );            //
		gl.glHint( GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST );         //

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
		texMan.setGL( gl );
		texMan.setGLU( glu );
		maskTex = texMan.newTexture( "logo", "./textures/logomask.png" );
		logoTex = texMan.newTexture( "logo", "./textures/logo2.png" );
		fontTex = texMan.newTexture( "font2", "./textures/fontl.png" );

		// load texture font
		fontBold = new TextureFont( gl, fontTex );
		fontBold.buildFont( 8, 32, 32, 32, 19 );

		font = new TextureFont( gl, fontTex );
		font.buildFont( 8, 32, 32, 16, 9.5 );		// fontl.bmp
		fontTex.bind();

		gl.glEnable( GL.GL_NORMALIZE );

		// activate anisotropic filtering. had no effect on my computer
//		if ( glewIsSupported( "GL_EXT_texture_filter_anisotropic" ) ) {
//	    float maxAni;
//			glGetFloatv( GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, &maxAni );
//			glBindTexture( GL_TEXTURE_2D, theTex );
//			glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAni );
//		}

//		if( control == null )
//			control = new GLControl();
	}

	public void update() {
		// TODO update weiterleiten an den MovieManager
		//moviePath = PropertyContainer.getInstance().getAsString( "options.filehandling.moviePath" );
		//movieFrameName = PropertyContainer.getInstance().getAsString( "options.filehandling.movieFrameName" );
		repaint();
	}

	/**
	 * Writes some information to a file. Camera position, commands used to execute
	 */
	private void createInformationFile() {
		FileWriter writer = null;
		String fileName = movieManager.getFullFilePath();
		fileName.substring( 0, fileName.length() - 5 );
		fileName += ".txt";
		try {
			System.out.println( "Schreibe Video-Informationen in die Datei '" + fileName + "'" );
			writer = new FileWriter( new File( fileName ) );
			writer.write( "[Commands]\n" );
			writer.write( movieManager.getCommands() + "\n" );
			writer.write( "[Camera]\n" );
			writer.write( camera.getPos().toString() + "\n" );
			writer.write( camera.getView().toString() + "\n" );
			writer.write( camera.getUp().toString() + "\n" );
			writer.close();
		} catch( IOException ex ) {
			System.err.println( "Fehler beim Erstellen der Datei '" + fileName + "'" );
		} finally {
			try {
				writer.close();
			} catch( IOException ex ) {
				System.err.println( "Fehler beim Schließen der Datei '" + fileName + "'" );
			}
		}
	}

	@Override
	public void keyPressed( KeyEvent e ) {
		super.keyPressed( e );
		frustum.update( camera.getPos(), camera.getView(), camera.getUp() );
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		super.mouseDragged( e );
		frustum.update( camera.getPos(), camera.getView(), camera.getUp() );
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		super.mouseWheelMoved( e );
		frustum.update( camera.getPos(), camera.getView(), camera.getUp() );
	}


}
