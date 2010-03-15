/**
 * ZETVisualization.java
 * input:
 * output:
 *
 * method:
 *
 * Created: Mar 9, 2010,5:28:30 PM
 */
package gui.visualization;

import de.tu_berlin.math.coga.common.util.Formatter;
import ds.PropertyContainer;
import event.EventListener;
import event.EventServer;
import event.OptionsChangedEvent;
import event.VisualizationEvent;
import gui.JEditor;
import gui.ZETProperties;
import gui.visualization.control.GLControl;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import opengl.drawingutils.GLColor;
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

	@Override
	public void display( GLAutoDrawable drawable ) {
		showEye = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.eye" );
		showFPS = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.fps" );
		showTimestepGraph = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepGraph" );
		showTimestepCellularAutomaton = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepCA" );
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
				JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().pos = camera.getPos();
				JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().view = camera.getView();
				JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().up = camera.getUp();
				JEditor.getInstance().getZControl().getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
				JEditor.getInstance().getZControl().getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
			default:
				super.keyPressed( e );
		}
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		super.mousePressed( e );
		JEditor.getInstance().getVisualizationView().updateCameraInformation();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().pos = camera.getPos();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().view = camera.getView();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().up = camera.getUp();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
		JEditor.getInstance().getZControl().getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		super.mouseDragged( e );
		JEditor.getInstance().getVisualizationView().updateCameraInformation();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().pos = camera.getPos();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().view = camera.getView();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().up = camera.getUp();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
		JEditor.getInstance().getZControl().getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		super.mouseWheelMoved( e );
		JEditor.getInstance().getVisualizationView().updateCameraInformation();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().pos = camera.getPos();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().view = camera.getView();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().getCameraPosition().up = camera.getUp();
		JEditor.getInstance().getZControl().getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
		JEditor.getInstance().getZControl().getProject().getVisualProperties().setCurrentHeight( getViewHeight() );

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
