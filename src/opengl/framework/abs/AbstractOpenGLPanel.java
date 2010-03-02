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
 * OpenGLComponent.java
 * Created on 28.01.2008, 23:19:42
 */

package opengl.framework.abs;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;

/**
 * By default depth buffer is used.
 * @author Jan-Philipp Kappmeier
 */
abstract public class AbstractOpenGLPanel extends GLJPanel implements GLEventListener, OpenGLRenderer, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	private int clear = GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT;
	private Animator animator;
	private int maxFPS = 200;
	private long lastFrameTime = 0;
	private long animationStartTime = 0;
	private int fps=0;
	private int frameCount=0;
	private long deltaTime;
	private long lastTime=0;
	protected GL gl;
	
	/**
	 * 
	 * @param caps 
	 */
	public AbstractOpenGLPanel( GLCapabilities caps ) {
		super( caps );
		setBackground( Color.black );
		addGLEventListener( this );
		animator = new FPSAnimator( this, maxFPS );
	}
	
	/**
	 * Starts animation and resets counter.
	 */
	public void startAnimation() {
		animationStartTime = System.currentTimeMillis();
		animator.start();
		animator.setRunAsFastAsPossible( true );
	}
	
	/**
	 * Stops animation.
	 */
	public void stopAnimation() {
		animator.stop();
	}
	
	/**
	 * Decides, wheather animation is turned on or of.
	 * @return {@code true} if animation is on, {@code false otherwise}
	 */
	public boolean isAnimating() {
		return animator.isAnimating();
	}

	/**
	 * 
	 * @param maxFPS
	 */
	public void setMaxFPS( int maxFPS ) {
		this.maxFPS = maxFPS;
		if( isAnimating() ) {
			animator.stop();
			animator = new FPSAnimator( this, maxFPS );
			startAnimation();
			lastTime = System.currentTimeMillis();
		} else
			animator = new FPSAnimator( this, maxFPS );
	}
	
	/**
	 * Returns the maximal allowed framerate per second.
	 * @return the maximal allowed framerate per second
	 */
	public int getMaxFPS() {
		return maxFPS;
	}

	/**
	 * Returns the time passed since the last frame was drawn.
	 * @return the time passed since the last frame was drawn
	 */
	public long getDeltaTime() {
		return deltaTime;
	}
	
	/**
	 * Returns the elapsed time since the animation was started.
	 * @return the elapsed time since the animation was started
	 */
	public long getTimeSinceStart() {
		return lastTime - animationStartTime;
	}

	/**
	 * Returns the current framerate per second.
	 * @return the current framerate per second
	 */
	public int getFps() {
		return fps;
	}

	
	/**
	 * 
	 */
	public AbstractOpenGLPanel() {
		super( new GLCapabilities() );
		setBackground( Color.black );
		addGLEventListener( this );
		animator = new FPSAnimator( this, 60 );
	}
	
	/**
	 * This methods is used to draw our stuff to the GL context. It is called
	 * every frame.
	 * @param drawable the GL context that we can use
	 */
	public void display( GLAutoDrawable drawable ) {
		// calculate real FPS and delay time for animation
		this.gl = drawable.getGL();
		long currentTime = System.currentTimeMillis();
		deltaTime = currentTime - lastTime;
		lastTime = currentTime;
		if( currentTime-lastFrameTime >= 1000 ) {
				lastFrameTime = currentTime;
				fps = frameCount;
				frameCount = 0;
		} else {
				frameCount++;
		}
		if( animator.isAnimating() == true )
			animate();
		gl.glClear( clear );
		//this.renderScene( drawable );
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
	final public void displayChanged( GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged ) { }
		
	public int getClearBits() {
		return clear;
	}
	
	public void setClearBits( int clear ) {
		this.clear = clear;
	}

	public void useListener() {
		useKeyListener();
		useMouseListener();
		useMouseMotionListener();
		useMouseWheelListener();
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
	
	public void keyTyped( KeyEvent e ) { }

	public void keyPressed( KeyEvent e ) { }

	public void keyReleased( KeyEvent e ) { }

	public void mouseClicked( MouseEvent e ) {
		requestFocusInWindow();
	}

	public void mousePressed( MouseEvent e ) { }

	public void mouseReleased( MouseEvent e ) { }

	public void mouseEntered( MouseEvent e ) { }

	public void mouseExited( MouseEvent e ) { }

	public void mouseDragged( MouseEvent e ) { }

	public void mouseMoved( MouseEvent e ) { }

	public void mouseWheelMoved( MouseWheelEvent e ) { }
}
