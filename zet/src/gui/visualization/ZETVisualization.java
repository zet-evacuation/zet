/**
 * ZETVisualization.java
 * Created: 09.03.2010, 17:28:30
 */
package gui.visualization;

import de.tu_berlin.math.coga.common.util.Formatter;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.PropertyContainer;
import ds.z.ZControl;
import event.EventListener;
import event.EventServer;
import event.OptionsChangedEvent;
import event.VisualizationEvent;
import zet.gui.JEditor;
import gui.ZETProperties;
import gui.visualization.control.GLControl;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import opengl.drawingutils.GLColor;
import opengl.helper.Frustum.CullingLocation;
import opengl.helper.ProjectionHelper;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ZETVisualization extends Visualization<GLControl> implements EventListener<OptionsChangedEvent>  {
	
	/** The minimal number of frames that needs to be captured in movie rendering mode for the cellular automaton */
	private int minimalFrameCountCellularAutomaton;
	/** The minimal number of frames that needs to be captured in movie rendering mode for the graph */
	private int minimalFrameCountGraph;

	private boolean showTimestepGraph = ZETProperties.isShowTimestepGraph();
	private boolean showTimestepCellularAutomaton = ZETProperties.isShowTimestepCellularAutomaton();

	public ZETVisualization( GLCapabilities capabilities ) {
		super( capabilities );
		showEye = ZETProperties.isShowEye();
		showFPS = ZETProperties.isShowFPS();
		EventServer.getInstance().registerListener( this, OptionsChangedEvent.class );

		noRotate = !PropertyContainer.getInstance().getAsBoolean( "editor.options.visualization.allowRotateIn2D" );
		mouseInvert = PropertyContainer.getInstance().getAsBoolean( "editor.options.visualization.invertMouse" ) ? -1 : 1;
		scrollInvert = PropertyContainer.getInstance().getAsBoolean( "editor.options.visualization.invertScroll" ) ? 1 : -1;

		// this will create errors!
		if( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.2d" ) )
			set2DView();
		else
			set3DView();
		if( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.isometric" ) )
			this.setParallelViewMode( ParallelViewMode.Isometric );
		else
			this.setParallelViewMode( ParallelViewMode.Orthogonal );
	}
	
	private ZControl zcontrol;

	public void setZcontrol( ZControl zcontrol ) {
		this.zcontrol = zcontrol;
	}
	
	
		static Vector3 camPos = new Vector3( 0, 0, 10 );
	static Vector3 camView = new Vector3( 0, 0, -1 );
	static Vector3 camUp = new Vector3( 0, 1, 0 );
	static boolean done = false;
	static Vector3 pointPos = new Vector3( 0, 0, 0 );
	static GLUquadric quadObj = null;

		@Override
	public void display( GLAutoDrawable drawable ) {
		showEye = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.eye" );
		showFPS = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.fps" );
		showTimestepGraph = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepGraph" );
		showTimestepCellularAutomaton = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepCA" );


		// begin test
//		if( !done ) {
//			camera.setPos( camPos );
//			camera.setView( camView );
//			camera.setUp( camUp );
//		}
//		frustum.setAll( getFov(), aspect, getzNear(), getzFar() );
//		frustum.update( camera.getPos(), camera.getView(), camera.getUp() );
//
//		if( quadObj == null )
//			quadObj = glu.gluNewQuadric();
//		glu.gluQuadricDrawStyle( quadObj, GLU.GLU_FILL );
//		glu.gluQuadricOrientation( quadObj, GLU.GLU_OUTSIDE );
//		gl.glClear( clearBits );
//
//		gl.glMatrixMode( GL.GL_MODELVIEW );
//		gl.glLoadIdentity();
//		float[] light_position = new float[4];
//		light_position[0] = (float)camera.getView().x;
//		light_position[1] = (float)camera.getView().y;
//		light_position[2] = (float)camera.getView().z;
//		//light_position[0] = 0;
//		//light_position[1] = 1;
//		//light_position[2] = 0;
//		light_position[3] = 1.0f;
//
//		if( frustum.isPointInFrustum( pointPos ) == CullingLocation.inside ) {
//			System.out.println( "Point is in frustum" );
//		} else
//			System.out.println( "Point is not in frustum" );
//
//		look();
//		glu.gluSphere( quadObj, 5, 32, 16);
////		gl.glClear( GL.GL_DEPTH_BUFFER_BIT );
////		drawFPS();


		super.display( drawable );
	}

	/**
	 * Draws the current framerate on the lower left edge of the screen and
	 * the current time of the cellular automaton and graph, if used.
	 */
	@Override
	final protected void drawFPS() {
		super.drawFPS();
		ProjectionHelper.setPrintScreenProjection( gl, viewportWidth, viewportHeight );
		GLColor.white.draw( gl );
		gl.glEnable( GL.GL_BLEND );
		gl.glBlendFunc( GL.GL_ONE, GL.GL_ONE );// Copy Image 2 Color To The Screen
		gl.glEnable( gl.GL_TEXTURE_2D );
		fontTex.bind();

		boolean finished = true;

		// TODO gehört hier nicht rein!
		int row = 1;
		if( control.hasCellularAutomaton() ) {
			if( control.isCaFinshed() ) {
				minimalFrameCountCellularAutomaton--;
				if( showTimestepCellularAutomaton ) {
					// TODO updateinformationen anzeigen
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.simulationFinished" ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.simulationNeeded" ) + " " + Formatter.secToMin( control.getCaStep() * control.getCaSecondsPerStep() ) );
				}
			} else {
				finished = false;
				minimalFrameCountCellularAutomaton = 2;
				if( showTimestepCellularAutomaton ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.simulationStep" ) + " " + loc.getFloatConverter().format( control.getCaStep() ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.simulationTime" ) + " " + Formatter.secToMin( control.getCaStep() * control.getCaSecondsPerStep() ) );
				}
			}
			row++;
		}
		if( control.hasGraph() ) {
			if( control.isGraphFinished() ) {
				minimalFrameCountGraph--;
				if( showTimestepGraph ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.graphFinished" ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.graphNeeded" ) + " " + Formatter.secToMin( control.getGraphStep() * control.getGraphSecondsPerStep() ) );
				}
			} else {
				finished = false;
				minimalFrameCountGraph = 2;
				if( showTimestepGraph ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.graphStep" ) + " " + loc.getFloatConverter().format( control.getGraphStep() ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.graphTime" ) + " " + Formatter.secToMin( control.getGraphStep() * control.getGraphSecondsPerStep() ) );
				}
			}
			row++;
		}

		if( finished && isAnimating() )
			EventServer.getInstance().dispatchEvent( new VisualizationEvent( this ) );

		//font.print( 0, this.getHeight() - (row++)*fontSize, "Zeit: " + secToMin( getTimeSinceStart()/Conversion.secToNanoSeconds ) );
		gl.glDisable( GL.GL_TEXTURE_2D );
		gl.glDisable( GL.GL_BLEND );
		ProjectionHelper.resetProjection( gl );
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
				zcontrol.getProject().getVisualProperties().getCameraPosition().pos = camera.getPos();
				zcontrol.getProject().getVisualProperties().getCameraPosition().view = camera.getView();
				zcontrol.getProject().getVisualProperties().getCameraPosition().up = camera.getUp();
				zcontrol.getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
				zcontrol.getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
			default:
				super.keyPressed( e );
		}
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		super.mousePressed( e );
		JEditor.getInstance().getVisualizationView().updateCameraInformation();
		zcontrol.getProject().getVisualProperties().getCameraPosition().pos = camera.getPos();
		zcontrol.getProject().getVisualProperties().getCameraPosition().view = camera.getView();
		zcontrol.getProject().getVisualProperties().getCameraPosition().up = camera.getUp();
		zcontrol.getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
		zcontrol.getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		super.mouseDragged( e );
		JEditor.getInstance().getVisualizationView().updateCameraInformation();
		zcontrol.getProject().getVisualProperties().getCameraPosition().pos = camera.getPos();
		zcontrol.getProject().getVisualProperties().getCameraPosition().view = camera.getView();
		zcontrol.getProject().getVisualProperties().getCameraPosition().up = camera.getUp();
		zcontrol.getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
		zcontrol.getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		super.mouseWheelMoved( e );
		JEditor.getInstance().getVisualizationView().updateCameraInformation();
		zcontrol.getProject().getVisualProperties().getCameraPosition().pos = camera.getPos();
		zcontrol.getProject().getVisualProperties().getCameraPosition().view = camera.getView();
		zcontrol.getProject().getVisualProperties().getCameraPosition().up = camera.getUp();
		zcontrol.getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
		zcontrol.getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
	}

	/**
	 * Called if an {@link OptionsChangedEvent} is send to the visualization class.
	 * Updates the variables indicating the visible elements in the visualization.
	 * @param event the event
	 */
	@Override
	public void handleEvent( OptionsChangedEvent event ) {
		showEye = ZETProperties.isShowEye();
		showFPS = ZETProperties.isShowFPS();
		showTimestepGraph = ZETProperties.isShowTimestepGraph();
		showTimestepCellularAutomaton = ZETProperties.isShowTimestepCellularAutomaton();
		repaint();
		//update();
	}

}
