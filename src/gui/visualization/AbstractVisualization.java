/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui.visualization;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.Screenshot;
import org.zetool.common.debug.Debug;
import org.zetool.math.Conversion;
import event.EventServer;
import event.MessageEvent;
import gui.MessageType;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import org.zetool.opengl.helper.ProjectionHelper;
import org.zetool.math.vectormath.Vector3;
import org.zetool.opengl.framework.Camera;
import org.zetool.opengl.framework.abs.AbstractOpenGLCanvas;

public abstract class AbstractVisualization extends AbstractOpenGLCanvas {

    // TODO: OpenGL evtl. Parallel-view-mode verschieben?
    public enum ParallelViewMode {
        /** Orthognal view from direcly above the scene. */
        Orthogonal,
        /** Isometric view, the scene is rotated 30 degree. */
        Isometric,
        /** Isometric view, the scene is rotated xx degree. */
        Isometric2;
    }

//	// Projection stuff
    /** Stores the information, which of the parallel view modes is active. */
    protected ParallelViewMode pvm = ParallelViewMode.Isometric;
    /** Keeps the information, if 3-dimensional view is active, or not. */
    protected boolean is3D = false;

//	// 2D-Projection vars
    /** The camera. */
    Camera camera = new Camera();
    /** The height of the viewport. */
    int viewportWidth = 0;
    /** The width of the viewport. */
    int viewportHeight = 0;
    /** Keeps the current projection state. If it is changed, the projection matrices has to be recomputed. */
    protected boolean updateProjection = false;
//	private double canvasWidth = 100;
//	private double canvasHeight = 100;

    private double depth = 2000;
    private double currentWidth = 1000;
    private double currentHeight = 1000;

    //	private double zoomFactor = 0.1;
    private double initZ;
    // 3D-Projection vars
    double aspect = 1;
    private double fov = 45;
    private double zNear = 1;
    private double zFar = 2000;

    // Mouse interaction vars
    protected int mouseInvert = 1;
    protected int scrollInvert = 1;
    private double initWidth;
    private double initHeight;
    private int initMouseX;
    private int initMouseY;
    private int mouseMove;
    private Vector3 initView;
    private Vector3 initUp;
    /**
     * The difference angle between the view angle and the angle of the scene rotation.
     */
    protected Vector3 rotation2D = new Vector3(1, 0, 0);
    private Vector3 initRotation2D;

    private static final Vector3 absoluteUp = new Vector3(0, 0, 1);

    //	/** Indicates wheather a screenshot should be taken after a redrow of the scene */
    protected boolean takeScreenshot = false;
//	/** The filename of the screenshot. */
    protected String screenshotFilename = "./screenshots/screenshot.png";

    /**
     * Initializes the visualization class. Sets up background, event-listener and animation.
     *
     * @param caps the capabilities for {@code OpenGL}-rendering
     */
    public AbstractVisualization(GLCapabilities caps) {
        super(caps);
        glu = new GLU();
        setBackground(VisualizationOptionManager.getBackground());
        // TODO: eventl-listener for OpenGL.
        addGLEventListener(this);
        animator = new Animator(this);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        // Initialization of the variables
        camera.setSpeed(camera.getSpeed() * 0.1);
    }

    /*****************************************************************************
     *                                                                           *
     * Projection-Stuff                                                          *
     *                                                                           *
     *****************************************************************************/
    /**
     *
     * @param drawable
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    final public void updateViewport(GLAutoDrawable drawable, int x, int y, int width, int height) {
        //gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);

        if (height <= 0) // avoid a divide by zero error!
        {
            height = 1;
        }
        aspect = (double) width / (double) height;
        this.viewportWidth = width;
        this.viewportHeight = height;
        if (is3D) {
            set3DProjection();
        } else {
            set2DProjection();
        }

        // TODO: event-server usage in Open-GL-Kram
        EventServer.getInstance().dispatchEvent(new MessageEvent<>(this, MessageType.MousePosition, "View: " + width + " x " + height));
    }

    /**
     * Sets up the projection, depending from the current mode: 2-dimensional or 3-dimensional.
     */
    final protected void updateProjection() {
        if (is3D) {
            set3DProjection();
        } else {
            set2DProjection();
        }
    }

    /**
     * Sets up the projection matrix for 3-dimensional view.
     */
    private void set3DProjection() {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(fov, aspect, zNear, zFar);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        updateProjection = false;
    }

    /**
     * Sets up the projection matrix for 2-dimensional (orthogonal or parallel) view.
     */
    private void set2DProjection() {
        ProjectionHelper.setViewOrthogonal(gl, viewportWidth, viewportHeight, camera.getPos().x, camera.getPos().y, camera.getPos().z, currentWidth, currentHeight, depth);
        // We need to reset the model/view matrix (and swith to the mode!)
        gl.glMatrixMode(GL.GL_MODELVIEW);			// Set model/view matrix-mode
        updateProjection = false;
    }

    /**
     * Sets view to 3-dimensional perspective view.
     */
    final public void set3DView() {
        is3D = true;
        updateProjection = true;
    }

    /**
     * Sets view to one of the 2-dimensional views which are isometric or orthogonal.
     */
    final public void set2DView() {
        is3D = false;
        updateProjection = true;
    }

    /**
     * Sets a 3d-view or a 2-dimensional view.
     *
     * @param view3d set to {@code true} if 3-dimensional view should be enabled, {@code false} for 2-dimensional view
     */
    final public void setView(boolean view3d) {
        is3D = view3d;
        updateProjection = true;
    }

    final public void toggleView() {
        is3D = !is3D;
        updateProjection = true;
    }

    /**
     * Returns {@code true} if the 3-dimensonal perspective view is enabled.
     *
     * @return {@code true} if 3-dimensonal perspective view is enabled, {@code false} otherwise.
     */
    final public boolean is3D() {
        return is3D;
    }

    /**
     * Returns the view mode for parallel/orthogonal projection.
     *
     * @return the view mode for parallel/orthogonal projection
     */
    final public ParallelViewMode getParallelViewMode() {
        return pvm;
    }

    /**
     * Sets the view mode for parallel/orthogonal projection.
     *
     * @param pvm the projection mode
     */
    final public void setParallelViewMode(ParallelViewMode pvm) {
        this.pvm = pvm;
    }

    /**
     * // TODO OpenGL: move to AbstractOpenGLCanvas/Panel Sets the eye position and direction of view.
     */
    final public void look() {
        glu.gluLookAt(camera.getPos().x, camera.getPos().y, camera.getPos().z, camera.getPos().x + camera.getView().x, camera.getPos().y + camera.getView().y, camera.getPos().z + camera.getView().z, camera.getUp().x, camera.getUp().y, camera.getUp().z);
    }

    final public void moveUp(double init, double value) {
        camera.getPos().z = init + 1 * (value);								// addTo( camera.getView().scalarMultiplicate( e.getWheelRotation() * camera.getSpeed() ) );
        if (camera.getPos().z <= 0.001) // let epsilon space to zero
        {
            camera.getPos().z = 0.001;
        }
        currentWidth = initWidth * camera.getPos().z / init;
        currentHeight = initHeight * camera.getPos().z / init;
        updateProjection = true;
        repaint();
    }

    // Getter & Setter
//	// Setter & Getter
//	/**
//	 * Returns the time passed since the last frame was drawn.
//	 * @return the time passed since the last frame was drawn
//	 */
//	final public long getDeltaTime() {
//		return deltaTime;
//	}
    /**
     * Returns the angle between the current rotate vector and the vector in direction of the x-axis.
     *
     * @return the angle of the rotate vector
     */
    final public double getRotateAngle() {
        double rotateAngle = Math.atan(-rotation2D.y / rotation2D.x) * Conversion.DEG2ANGLE;
        if (rotation2D.x < 0) {
            rotateAngle += 180;
        }
        return rotateAngle;
    }

    /**
     * Returns the current height of the visible rendered area. It can be larger than the size of the window.
     *
     * @return the current height of the visible rendered area
     */
    public double getViewHeight() {
        return currentHeight;
    }

    /**
     * Returns the current width of the visible rendered area. It can be larger than the size of the window.
     *
     * @return the current width of the visible rendered area
     */
    public double getViewWidth() {
        return currentWidth;
    }

    /**
     * Returns the height of the viewport.
     *
     * @return the height of the viewport
     */
    protected int getViewportHeight() {
        return viewportHeight;
    }

    /**
     * Returns the width of the viewport.
     *
     * @return the width of the viewport
     */
    protected int getViewportWidth() {
        return viewportWidth;
    }

    /**
     * Directly sets the current size of view (which is the visible area that is rendered). Note that this method does
     * not ensure that the size of the viewport fits to the window or panel.
     *
     * @param width the new width
     * @param height the new height
     */
    public void setView(double width, double height) {
        currentWidth = width;
        currentHeight = height;
    }

    // TODO OpenGL: check wheather rotation2D could be a vector2
    //	/**
//	 * Returns the time since start of animation in nano seconds. In case the
//	 * animation has not been started yet, 0 is returned.
//	 * @return the time since start of animation in nano seconds
//	 */
//	final public long getTimeSinceStart() {
//		return animator.isAnimating() ? lastTime - animationStartTime : 0;
//	}
    /**
     * Returns the camera object of the scene.
     *
     * @return the camera object of the scene
     */
    final public Camera getCamera() {
        return camera;
    }

    // Take a screenshot
    /**
     * Call this method to make a screenshot after the next redraw.
     *
     * @param filename the filename of the screenshot file
     */
    public void takeScreenshot(String filename) {
        takeScreenshot = true;
        screenshotFilename = filename;
        System.out.println("screenshot");
        repaint();
    }

    // TODO: OpenGL JEditor-Exception-Printing entfernen
    /**
     * Takes a screenshot and saves it to the file indicated by the filename submitted by the other screenshot method.
     *
     * @param drawable the {@code OpenGL} context
     */
    protected void takeScreenshot(GLAutoDrawable drawable) {
        System.out.println("Save screenshot to " + screenshotFilename);
        try {
            Screenshot.writeToFile(new File(screenshotFilename), drawable.getWidth(), drawable.getHeight(), false);
        } catch (IOException ex) {
            Debug.printException(ex);
        } catch (GLException ex) {
            Debug.printException(ex);
        } catch (Exception ex) {
            Debug.printException(ex);
        }
        takeScreenshot = false;
    }

    /**
     * Takes a screenshot and saves it to the submitted file
     *
     * @param drawable the {@code OpenGL} context
     * @param filename the filename
     */
    protected void takeScreenshot(GLAutoDrawable drawable, String filename) {
        screenshotFilename = filename;
        takeScreenshot(drawable);
    }

    // Listener
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (is3D) {
                    camera.stepRight();
                } else {
                    moveAbsolute(0);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (is3D) {
                    camera.stepLeft();
                } else {
                    moveAbsolute(180);
                }
                break;
            case KeyEvent.VK_UP:
                if (is3D) {
                    camera.stepForward();
                } else {
                    moveAbsolute(270);
                }
                break;
            case KeyEvent.VK_DOWN:
                if (is3D) {
                    camera.stepBackward();
                } else {
                    moveAbsolute(90);
                }
                break;
            case KeyEvent.VK_PLUS:
                initWidth = currentWidth;
                initHeight = currentHeight;
                moveUp(getCamera().getPos().z, -getCamera().getPos().z * 0.1);
                break;
            case KeyEvent.VK_MINUS:
                initWidth = currentWidth;
                initHeight = currentHeight;
                moveUp(getCamera().getPos().z, getCamera().getPos().z * 0.1);
                break;
        }
        if (!is3D) {
            updateProjection = true;
        }
        repaint();
    }

    /**
     * <p>
     * Performs a move in the orthogonal view, that means the building is moved right/left and up/down on the screen
     * independently from the currently set view direction and rotation. The camera position is not changed.</p>
     * The submitted angle describes the direction. 0 means to the right, 180 to the left etc.
     *
     * @param angle the angle in degrees from 0 to 360
     */
    private void moveAbsolute(double angle) {
        Vector3 oldView = new Vector3(camera.getView().x, camera.getView().y, camera.getView().z);
        //Vector3 oldUp = new Vector3(camera.getUp().x, camera.getUp().y, camera.getUp().z);

        // Calculate angle
        double rotateAngle = Math.atan(-rotation2D.y / rotation2D.x) * Conversion.DEG2ANGLE;
        if (rotation2D.x < 0) {
            rotateAngle += 180;
        }
        rotateAngle = -rotateAngle;

        camera.setView(new Vector3(1, 0, 0));
        camera.getView().rotate(rotateAngle + angle, absoluteUp);
        camera.stepForward();
        camera.setView(oldView);
        //camera.setUp( oldUp );
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (mouseMove != MouseEvent.NOBUTTON) {
            return;
        }
        initMouseX = e.getX();
        initMouseY = e.getY();
        initRotation2D = new Vector3(rotation2D);
        initView = camera.getView();
        initUp = camera.getUp();
        mouseMove = e.getButton();
        initZ = camera.getPos().z;
        initWidth = currentWidth;
        initHeight = currentHeight;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseMove = MouseEvent.NOBUTTON;
    }

    @Override
    public Dimension getSize() {
        //return super.getSize(); //To change body of generated methods, choose Tools | Templates.
        return new Dimension(2048, 2048);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int y = e.getY();
        int x = e.getX();
        switch (mouseMove) {
            case MouseEvent.BUTTON1:
                Dimension size = e.getComponent().getSize();
                if (!is3D && pvm == ParallelViewMode.Isometric) {
                } else if (!is3D && pvm == ParallelViewMode.Orthogonal) {
                    // Hiermit wird der sichtbereich der kamera gedreht
                    //camera.rotate( 90.0f * ( (float)(initMouseX-x)/(float)size.width ), new Vector3( 0, 0, 1) );	// left/right
                    // Hiermit wird der gebäudeplan gedreht
                    rotation2D = new Vector3(initRotation2D);
                    rotation2D.rotate(90.0f * ((float) (initMouseX - x) / (float) size.width), new Vector3(0, 0, 1));
                } else {
                    // Look up/down and right/left
                    camera.setUp(initUp);
                    camera.setView(initView);
                    camera.rotate(90.0f * ((float) (initMouseX - x) / (float) size.width), new Vector3(0, 0, 1));	// left/right
                    if (is3D) // do not look up/down if in 2D-Mode
                    {
                        camera.pitch(mouseInvert * 90.0f * ((float) (initMouseY - y) / (float) size.height));		// up/down
                    }
                }
                // TODO: check that not up and down are reversed (the floor is above the camera!)
                repaint();
                break;
            case MouseEvent.BUTTON3:	// Right mouse button (at least on my pc)
                moveUp(initZ, y - initMouseY);
                break;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (is3D) {
            camera.getPos().addTo(camera.getView().scalarMultiplicate(scrollInvert * e.getWheelRotation() * camera.getSpeed()));
        } else {
            initWidth = currentWidth;
        }
        initHeight = currentHeight;
        moveUp(camera.getPos().z, scrollInvert * e.getWheelRotation());
        repaint();
    }

    public double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
        updateProjection = true;
    }

    public double getzFar() {
        return zFar;
    }

    public void setzFar(double zFar) {
        this.zFar = zFar;
        updateProjection = true;
    }

    public double getzNear() {
        return zNear;
    }

    public void setzNear(double zNear) {
        this.zNear = zNear;
        updateProjection = true;
    }

}

//	public void zoomIn() {
//		currentWidth *= 1 - zoomFactor;
//		currentHeight *= 1 - zoomFactor;
//		updateProjection = true;
//		repaint();
//	}
//
//	public void zoomOut() {
//		currentWidth /= 1 - zoomFactor;
//		currentHeight /= 1 - zoomFactor;
//		updateProjection = true;
//		repaint();
//	}
