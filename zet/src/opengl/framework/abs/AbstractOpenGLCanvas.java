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

/*
 * AbstractOpenGLCanvas.java
 * Created on 28.01.2008, 23:19:42
 */

package opengl.framework.abs;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import de.tu_berlin.math.coga.math.Conversion;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.PrintStream;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import opengl.helper.Util;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
abstract public class AbstractOpenGLCanvas extends GLCanvas implements GLEventListener, OpenGLRenderer, Animatable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	protected int clearBits = GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT;
	protected Animator animator;
	private int maxFPS = 200;
	private long lastFrameTime = 0;
	private long animationStartTime = 0;
	private int fps = 0;
	private int frameCount = 0;
	private long deltaTime;
	protected long lastTime = 0;
	protected GL gl;
	protected GLU glu;

	/**
	 * 
	 * @param caps 
	 */
	public AbstractOpenGLCanvas( GLCapabilities caps ) {
		super( caps );
		setBackground( Color.black );
		addGLEventListener( this );
		//animator = new FPSAnimator( this, maxFPS );
		animator = new Animator( this );
	}

	/**
	 * Starts animation and resets the counter.
	 */
	public void startAnimation() {
		//System.out.print( "Try to start animation. Previous state was" );
		//System.out.println( animator.isAnimating() ? " animating." : " not animating." );
		animationStartTime = System.nanoTime();
		animator.start();
		//animator.setRunAsFastAsPossible( true );
		lastTime = System.nanoTime();
	}

	/**
	 * Stops the animation.
	 */
	public void stopAnimation() {
		//System.out.print( "Try to stop animation. Previous state was" );
		//System.out.println( animator.isAnimating() ? " animating." : " not animating." );
		animator.stop();
		fps = 0;
	}

	/**
	 * Decides, wheather animation is turned on or of.
	 * @return {@code true} if animation is on, {@code false} otherwise
	 */
	public final boolean isAnimating() {
		return animator.isAnimating();
	}

	/**
	 * Sets the maximal framerate per second.
	 * @param maxFPS the framerate
	 */
	public final void setMaxFPS( int maxFPS ) {
		this.maxFPS = maxFPS;
		if( isAnimating() ) {
			animator.stop();
			animator = new FPSAnimator( this, maxFPS );
			startAnimation();
		} else
			animator = new FPSAnimator( this, maxFPS );
	}

	/**
	 * Returns the maximal allowed framerate per second.
	 * @return the maximal allowed framerate per second
	 */
	public final int getMaxFPS() {
		return maxFPS;
	}

	/**
	 * Returns the time passed in nano seconds since the last frame was drawn.
	 * @return the time passed in nano seconds since the last frame was drawn
	 */
	public long getDeltaTime() {
		return deltaTime;
	}

	/**
	 * Returns the elapsed time in nano seconds since the animation was started.
	 * @return the elapsed time in nano seconds since the animation was started
	 */
	public long getTimeSinceStart() {
		return lastTime - animationStartTime;
	}

	/**
	 * Returns the current framerate per second.
	 * @return the current framerate per second
	 */
	public final int getFPS() {
		return fps;
	}

	/**
	 * Computes the current framerate and updates the elapsed time from the last
	 * rendered frame. This method should be called in the display-method if
	 * animation is on.
	 */
	final public void computeFPS() {
		// calculate real FPS and delay time for animation
		final long currentTime = System.nanoTime();
		deltaTime = currentTime - lastTime;
		lastTime = currentTime;
		if( currentTime - lastFrameTime >= Conversion.secToNanoSeconds ) {
			lastFrameTime = currentTime;
			fps = frameCount;
			frameCount = 0;
		} else
			frameCount++;
	}

	/**
	 * 
	 */
	public AbstractOpenGLCanvas() {
		super( new GLCapabilities() );
		setBackground( Color.black );
		addGLEventListener( this );
		animator = new FPSAnimator( this, maxFPS );
	}

	/**
	 * This methods is used to draw our stuff to the GL context. It is called
	 * every frame.
	 * @param drawable the GL context that we can use
	 */
	public void display( GLAutoDrawable drawable ) {
		// compute real FPS and delay time for animation
		this.gl = drawable.getGL();
		if( animator.isAnimating() == true )
			animate();
		gl.glClear( clearBits );
	}

	/**
	 * Initializes this {@code OpenGL} component. This method is called directly after
	 * the component is created. Do all you init-gfx stuff here.
	 * @param drawable the GL context that we can use
	 */
	final public void init( GLAutoDrawable drawable ) {
		this.initGFX( drawable );
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

	/**
	 * Called everytime when the mode or the device of the GL context are changed.
	 * <p>This method must not to be implemented as it is not yet supported by
	 * JOGL!</p>
	 * @param drawable the GL context that we can use
	 * @param modeChanged true if mode has changed
	 * @param deviceChanged true if display device has changed
	 */
	final public void displayChanged( GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged ) {
	}

	public int getClearBits() {
		return clearBits;
	}

	public void setClearBits( int clear ) {
		this.clearBits = clear;
	}

	public void useListener() {
		useKeyListener();
		useMouseListener();
		useMouseMotionListener();
		useMouseWheelListener();
	}

	public void removeListener() {
		removeKeyListener( this );
		removeMouseListener( this );
		removeMouseMotionListener( this );
		removeMouseWheelListener( this );
	}

	public void useKeyListener() {
		addKeyListener( this );
	}

	public void useMouseListener() {
		addMouseListener( this );
	}

	public void useMouseMotionListener() {
		this.addMouseMotionListener( this );
	}

	public void useMouseWheelListener() {
		addMouseWheelListener( this );
	}

	public void keyTyped( KeyEvent e ) {
	}

	public void keyPressed( KeyEvent e ) {
	}

	public void keyReleased( KeyEvent e ) {
	}

	public void mouseClicked( MouseEvent e ) {
		requestFocusInWindow();
	}

	public void mousePressed( MouseEvent e ) {
	}

	public void mouseReleased( MouseEvent e ) {
	}

	public void mouseEntered( MouseEvent e ) {
	}

	public void mouseExited( MouseEvent e ) {
	}

	public void mouseDragged( MouseEvent e ) {
	}

	public void mouseMoved( MouseEvent e ) {
	}

	public void mouseWheelMoved( MouseWheelEvent e ) {
	}
	/**
	 * Prints out all error messages that are in the error queue to
	 * {@code System.err}.
	 */
	protected void printErrors() {
		printErrors( System.err );
	}

	/**
	 * Gives out all error messages to a submitted {@link PrintStream}.
	 * @param stream
	 */
	protected void printErrors( PrintStream stream ) {
		Util.printErrors( gl, stream );
	}
}
