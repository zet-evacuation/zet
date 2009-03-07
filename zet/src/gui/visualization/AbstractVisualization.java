/**
 * Class AbstractVisualisation
 * Erstellt 20.05.2008, 23:51:14
 */

package gui.visualization;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.Screenshot;
import ds.PropertyContainer;
import gui.JEditor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import opengl.framework.Camera;
import opengl.framework.abs.OpenGLRenderer;
import opengl.helper.Frustum;
import opengl.helper.ProjectionHelper;
import util.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractVisualization extends GLCanvas implements GLEventListener, OpenGLRenderer, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	public enum ParallelViewMode {
		/** Orthognal view from direcly above the scene. */
		Orthogonal,
		/** Isometric view, the scene is rotated 30 degree. */
		Isometric,
		/** Isometric view, the scene is rotated xx degree. */
		Isometric2;
	}
	
	// General vars
	protected GL gl;
	private Animator animator;
	protected int clearBits = GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT;
	protected GLU glu;

	// FPS vars
	private long lastFrameTime=0;
	private long animationStartTime = 0;
	protected int fps=0;
	private int frameCount=0;
	private long deltaTime;
	private long lastTime=0;

	// Projection stuff
	protected ParallelViewMode pvm = ParallelViewMode.Isometric;
	protected boolean is3D = false;

	// 2D-Projection vars
	Camera camera = new Camera();
	int viewportWidth = 0;
	int viewportHeight = 0;
	protected boolean updateProjection = false;
	private double canvasWidth = 100;
	private double canvasHeight = 100;
	private double depth = 2000;
	private double currentWidth = 1000;
	private double currentHeight = 1000;
	private double zoomFactor = 0.1;
	private double initZ;
	
	// 3D-Projection vars
	double aspect = 1;

	// Mouse interaction vars
	private final int mouseInvert = PropertyContainer.getInstance().getAsBoolean( "editor.options.visualization.invertMouse" ) ? -1 : 1;
	private final int scrollInvert = PropertyContainer.getInstance().getAsBoolean( "editor.options.visualization.invertScroll" ) ? 1 : -1;
	private double initWidth;
	private double initHeight;
	private int initMouseX;
	private int initMouseY;
	private int mouseMove;
	private Vector3 initView;
	private Vector3 initUp;
	/** The difference angle between the view angle and the angle of the scene rotation. */
	protected Vector3 rotation2D = new Vector3( 1, 0, 0 );
	private Vector3 initRotation2D;
	private static final Vector3 absoluteUp = new Vector3( 0, 0, 1 );
	
	/** Indicates wheather a screenshot should be taken after a redrow of the scene */
	protected boolean takeScreenshot = false;
	/** The filename of the screenshot. */
	protected String screenshotFilename = "./screenshots/screenshot.png";
	
	/**
	 * 
	 * @param caps 
	 */
	public AbstractVisualization( GLCapabilities caps ) {
		super( caps );
		glu = new GLU();
		setBackground( Color.black );
		addGLEventListener( this );
		//animator = new FPSAnimator( this, maxFPS );
		animator = new Animator( this );
		addKeyListener( this );
		addMouseListener( this );
		addMouseMotionListener( this );
		addMouseWheelListener( this );
		// Initialization of the variables
		camera.setSpeed( camera.getSpeed() * 0.1 );
	}

	/**
	 * This method is called everytime the GL context is resized. Calculates the
	 * current viewport and aspect ratio of the visible area.
	 * @param drawable the GL context that we can use
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the context
	 * @param height the height of the context
	 */
	final public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		updateViewport( drawable, x, y, width, height );
	}

	final public void displayChanged( GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged ) { }

	// Setting up of the projections and other stuff
	@Override
	final public void updateViewport( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		//super.updateViewport( drawable, x, y, width, height );	// calculate viewport
		//gl = drawable.getGL();
		gl.glViewport(0,0, width, height);
		
		if( height <= 0 ) // avoid a divide by zero error!
			height = 1;
		aspect = (double) width / (double) height;
		if( is3D ) {
			set3DProjection();
		} else {
			this.viewportWidth = width;
			this.viewportHeight = height;
			set2DProjection();
		}
	}
	
	final protected void updateProjection() {
		if( is3D )
			set3DProjection();
		else
			set2DProjection();
	}

	final private void set3DProjection() {
		gl.glMatrixMode( GL.GL_PROJECTION );
		gl.glLoadIdentity();
		glu.gluPerspective( 45.0, aspect, 1, 1000 );
		gl.glMatrixMode( GL.GL_MODELVIEW );
		updateProjection = false;
	}
	
	final private void set2DProjection() {
		ProjectionHelper.setViewOrthogonal(gl, viewportWidth, viewportHeight, camera.getPos().x, camera.getPos().y, camera.getPos().z, currentWidth, currentHeight, depth  );
		// We need to reset the model/view matrix (and swith to the mode!)
		gl.glMatrixMode( GL.GL_MODELVIEW );			// Set model/view matrix-mode
		updateProjection = false;
	}

	final public void setProjectionPrint() {
		ProjectionHelper.setPrintScreenProjection( gl, viewportWidth, viewportHeight );
	}
	
	final public void resetProjection() {
		ProjectionHelper.resetProjection( gl );
	}
	
	final public ParallelViewMode getPvm() {
		return pvm;
	}

	final public void setPvm( ParallelViewMode pvm ) {
		this.pvm = pvm;
	}

	final public void set3DView() {
		is3D = true;
		updateProjection = true;
	}
	
	final public void set2DView() {
		is3D = false;
		updateProjection = true;
	}
	
	final public boolean is3D() {
		return is3D;
	}
	
	final public boolean is2D() {
		return !is3D;
	}
	
	final public void setView( boolean view ) {
		is3D = view;
		updateProjection = true;
	}
	
	final public void toggleView() {
		is3D = !is3D;
		updateProjection = true;
	}

	// look-method
	final public void look() {
		// set eye position and direction of view
		glu.gluLookAt( camera.getPos().x, camera.getPos().y, camera.getPos().z, camera.getPos().x + camera.getView().x, camera.getPos().y + camera.getView().y, camera.getPos().z +camera.getView().z, camera.getUp().x, camera.getUp().y, camera.getUp().z );
	}

	public void zoomIn() {
		currentWidth *= 1-zoomFactor;
		currentHeight *= 1-zoomFactor;
		updateProjection = true;
		repaint();
	}

	public void zoomOut() {
		currentWidth /= 1-zoomFactor;
		currentHeight /= 1-zoomFactor;
		updateProjection = true;
		repaint();
	}
	
	final public void moveUp( double init, double value ) {
		camera.getPos().z = init + 1 * (value);								// add( camera.getView().scalaryMultiplication( e.getWheelRotation() * camera.getSpeed() ) );
		if( camera.getPos().z <= 0.001)												// let epsilon space to zero
			camera.getPos().z = 0.001;
		currentWidth = initWidth * camera.getPos().z / init;
		currentHeight = initHeight * camera.getPos().z / init;
		updateProjection = true;
		repaint();
	}
	
	// Helper Methods
	final public void calculateFPS() {
		// calculate real FPS and delay time for animation
		long currentTime = System.nanoTime(); // currentTimeMillis();
		deltaTime = currentTime - lastTime;
		lastTime = currentTime;
		if( currentTime - lastFrameTime >= 1000000000 ) {			
				lastFrameTime = currentTime;
				fps = frameCount;
				frameCount = 0;
		} else {
				frameCount++;
		}
	}

	/**
	 * Starts animation with the maximal speed (frames per second).
	 */
	public void startAnimation() {
		animationStartTime = System.nanoTime();
		animator.start();
		animator.setRunAsFastAsPossible( true );
		lastTime = System.nanoTime();
	}
	
	/**
	 * Stops animation if started.
	 */
	final public void stopAnimation() {
		animator.stop();
	}

	/**
	 * 
	 * @return
	 */
	final public boolean isAnimating() {
		return animator.isAnimating();
	}
	
	// Setter & Getter
	/**
	 * 
	 * @return
	 */
	final public long getDeltaTime() {
		return deltaTime;
	}

	/**
	 * Returns the angle between the current rotate vector and the vector in
	 * direction of the x-axis.
	 * @return the angle of the rotate vector
	 */
	final public double getRotateAngle() {
		double rotateAngle = Math.atan( -rotation2D.y / rotation2D.x ) / Frustum.ANGLE2DEG;
		if( rotation2D.x < 0 )
			rotateAngle+=180;
		return rotateAngle;
	}
	
	/**
	 * Returns the time since start of animation in nano seconds. In case the
	 * animation has not startet yet, 0 is returned.
	 * @return the time since start of animation in nano seconds
	 */
	final public long getTimeSinceStart() {
		return animator.isAnimating() ? lastTime - animationStartTime : 0;
	}

	/**
	 * Returns the camera object of the scene.
	 * @return the camera object of the scene
	 */
	final public Camera getCamera() {
		return camera;
	}
	
	/**
	 * Call this method to make a screenshot after the next redraw.
	 * @param filename the filename of the screenshot file
	 */
	public void takeScreenshot( String filename ) {
		takeScreenshot = true;
		screenshotFilename = filename;
		repaint();
	}
	
	/**
	 * Takes a screenshot and saves it to the file indiciated by the filename
	 * submitted by the other screenshot method.
	 * @param drawable the OpenGL context
	 */
	protected void takeScreenshot( GLAutoDrawable drawable ) {
		File file = new File( screenshotFilename );
		//System.out.println( "Save screenshot to " + file.getAbsolutePath() );
		try {
			Screenshot.writeToFile( file , drawable.getWidth(), drawable.getHeight(), false );
		} catch( IOException ex ) {
			JEditor.printException( ex );
		} catch( GLException ex ) {
			JEditor.printException( ex );
		} catch( Exception ex ) {
			JEditor.printException( ex );
		}
		takeScreenshot = false;
	}

		/**
	 * Takes a screenshot and saves it to the submitted file
	 * @param drawable the OpenGL context
	 * @param filename the filename
	 */
	protected void takeScreenshot( GLAutoDrawable drawable, String filename ) {
		screenshotFilename = filename;
		takeScreenshot( drawable );
	}
	
	// Listener
	public void keyTyped( KeyEvent e ) { }

	@Override
	public void keyPressed( KeyEvent e ) {
		switch( e.getKeyCode() ) {
			case KeyEvent.VK_LEFT:
				if( is3D )
					camera.stepRight();
				else {
					moveAbsolute( 0 );
				}
				break;
			case KeyEvent.VK_RIGHT:
				if( is3D )
					camera.stepLeft();
				else {
					moveAbsolute( 180 );
				}
				break;
			case KeyEvent.VK_UP:
				if( is3D )
					camera.stepForward();
				else {
					moveAbsolute( 270 );
				}
				break;
			case KeyEvent.VK_DOWN:
				if( is3D )
					camera.stepBackward();
				else {
					moveAbsolute( 90 );
				}
				break;
			case KeyEvent.VK_PLUS:
				initWidth = currentWidth;
				initHeight = currentHeight;
				moveUp( getCamera().getPos().z, -getCamera().getPos().z*0.1 );
				break;
			case KeyEvent.VK_MINUS:
				initWidth = currentWidth;
				initHeight = currentHeight;
				moveUp(getCamera().getPos().z, getCamera().getPos().z*0.1 );
				break;
		}
		if( !is3D)
			updateProjection = true;
		repaint();
	}
	
	/**
	 * <p>Performs a move in the orthogonal view, that means the building is moved
	 * right/left and up/down on the screen independently from the currently set
	 * view direction and rotation. The camera position is not changed.</p>
	 * The submitted angle describes the direction. 0 means to the right, 180 to the left etc.
	 * @param angle the angle in degrees from 0 to 360
	 */
	private final void moveAbsolute( double angle ) {
		Vector3 oldView = new Vector3(camera.getView().x, camera.getView().y, camera.getView().z);
		//Vector3 oldUp = new Vector3(camera.getUp().x, camera.getUp().y, camera.getUp().z);

		// Calculate angle
		double rotateAngle = Math.atan( -rotation2D.y / rotation2D.x ) / Frustum.ANGLE2DEG;
		if( rotation2D.x < 0 )
			rotateAngle+=180;
		rotateAngle = -rotateAngle;

		camera.setView( new Vector3( 1, 0, 0) );
		camera.getView().rotate( rotateAngle + angle, absoluteUp );
		camera.stepForward();
		camera.setView( oldView );
		//camera.setUp( oldUp );
	}

	public void keyReleased( KeyEvent e ) { }

	public void mouseClicked( MouseEvent e ) {
		requestFocusInWindow();
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		if( mouseMove != MouseEvent.NOBUTTON )
			return;
		initMouseX = e.getX();
		initMouseY = e.getY();
		initRotation2D = new Vector3( rotation2D );
		initView = camera.getView();
		initUp = camera.getUp();
		mouseMove = e.getButton();
		initZ = camera.getPos().z;
		initWidth = currentWidth;
		initHeight = currentHeight;
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
		mouseMove = MouseEvent.NOBUTTON;
	}

	public void mouseEntered( MouseEvent e ) { }

	public void mouseExited( MouseEvent e ) { }

	@Override
	public void mouseDragged( MouseEvent e ) {
		int y = e.getY();
		int x = e.getX();
		switch( mouseMove ) {
			case MouseEvent.BUTTON1:
				Dimension size = e.getComponent().getSize();
				if( !is3D && pvm == ParallelViewMode.Isometric ) {
					
				} else if( !is3D && pvm == ParallelViewMode.Orthogonal ) {
					// Hiermit wird der sichtbereich der kamera gedreht
					//camera.rotate( 90.0f * ( (float)(initMouseX-x)/(float)size.width ), new Vector3( 0, 0, 1) );	// left/right
					// Hiermit wird der gebäudeplan gedreht
					rotation2D = new Vector3( initRotation2D );
					rotation2D.rotate( 90.0f * ( (float)(initMouseX-x)/(float)size.width ), new Vector3( 0, 0, 1 ) );
				}else {
				// Look up/down and right/left
				camera.setUp( initUp );
				camera.setView( initView );
				camera.rotate( 90.0f * ( (float)(initMouseX-x)/(float)size.width ), new Vector3( 0, 0, 1) );	// left/right
				if( is3D )	// do not look up/down if in 2D-Mode
					camera.pitch( mouseInvert * 90.0f * ( (float)(initMouseY-y)/(float)size.height ) );		// up/down
				}
				// TODO: check that not up and down are reversed (the floor is above the camera!)
				repaint();
				break;
			case MouseEvent.BUTTON3:	// Right mouse button (at least on my pc)
				moveUp( initZ, y - initMouseY );
				break;
		}
	}

	public void mouseMoved( MouseEvent e ) { }

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		if( is3D )
			camera.getPos().add( camera.getView().scalaryMultiplication( scrollInvert * e.getWheelRotation() * camera.getSpeed() ) );
		else
			initWidth = currentWidth;
			initHeight = currentHeight;
			moveUp( camera.getPos().z, scrollInvert * e.getWheelRotation() );
		repaint();
	}
}
