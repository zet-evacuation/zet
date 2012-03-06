/**
 * ZETVisualization.java
 * Created: 09.03.2010, 17:28:30
 */
package zet.gui.main.tabs.visualization;

import de.tu_berlin.math.coga.common.util.Formatter;
import de.tu_berlin.math.coga.common.util.Formatter.TimeUnits;
import ds.PropertyContainer;
import ds.z.ZControl;
import gui.GUIControl;
import gui.ZETProperties;
import gui.visualization.Visualization;
import gui.visualization.control.GLControl;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import opengl.drawingutils.GLColor;
import opengl.helper.ProjectionHelper;
import zet.gui.GUILocalization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class ZETVisualization extends Visualization<GLControl> {
	/** The GUI localization class. */
	GUILocalization loc = GUILocalization.getSingleton();
	/** The minimal number of frames that needs to be captured in movie rendering mode for the cellular automaton */
	private int minimalFrameCountCellularAutomaton;
	/** The minimal number of frames that needs to be captured in movie rendering mode for the graph */
	private int minimalFrameCountGraph;

	private boolean showTimestepGraph = ZETProperties.isShowTimestepGraph();
	private boolean showTimestepCellularAutomaton = ZETProperties.isShowTimestepCellularAutomaton();
	private final GUIControl guiControl;
	private ZControl zcontrol;


	public ZETVisualization( GLCapabilities capabilities, GUIControl guiControl ) {
		super( capabilities );
		
		setBackground( Color.WHITE );
		
		this.guiControl = guiControl;
		showEye = ZETProperties.isShowEye();
		showFPS = ZETProperties.isShowFPS();

		noRotate = !PropertyContainer.getInstance().getAsBoolean( "editor.options.visualization.allowRotateIn2D" );
		mouseInvert = PropertyContainer.getInstance().getAsBoolean( "editor.options.visualization.invertMouse" ) ? -1 : 1;
		scrollInvert = PropertyContainer.getInstance().getAsBoolean( "editor.options.visualization.invertScroll" ) ? 1 : -1;

		// this will create errors!
//		if( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.2d" ) )
//			set2DView();
//		else
//			set3DView();
//		if( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.isometric" ) )
//			this.setParallelViewMode( ParallelViewMode.Isometric );
//		else
//			this.setParallelViewMode( ParallelViewMode.Orthogonal );
		setControl( new GLControl() );
	}
	
	public void setZcontrol( ZControl zcontrol ) {
		this.zcontrol = zcontrol;
	}

	@Override
	public void display( GLAutoDrawable drawable ) {
		// TODO improve option handling
		//showEye = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.eye" );
		//showFPS = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.fps" );
		//showTimestepGraph = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepGraph" );
		//showTimestepCellularAutomaton = PropertyContainer.getInstance().getAsBoolean( "options.visualization.elements.timestepCA" );
		//System.out.println( "display" );
		super.display( drawable );
	}

	/**
	 * Draws the current frame rate on the lower left edge of the screen and
	 * the current time of the cellular automaton and graph, if used.
	 */
	@Override
	final protected void drawFPS() {
		super.drawFPS();
		ProjectionHelper.setPrintScreenProjection( gl, getViewportWidth(), getViewportHeight() );
		GLColor.white.draw( gl );
		gl.glEnable( GL.GL_BLEND );
		gl.glBlendFunc( GL.GL_ONE, GL.GL_ONE );// Copy Image 2 Color To The Screen
		gl.glEnable( GL.GL_TEXTURE_2D );
		fontTex.bind();

		boolean finished = true;

		// TODO gehört hier nicht rein!
		int row = 1;
		if( control.hasCellularAutomaton() ) {
			if( control.isCaFinshed() ) {
				minimalFrameCountCellularAutomaton--;
				if( showTimestepCellularAutomaton ) {
					// TODO updateinformationen anzeigen
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.VisualizationPanel.FPS.Simulation.Finished" ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.VisualizationPanel.FPS.Simulation.Needed" ) + " " + Formatter.formatTimeUnit( control.getCaStep() * control.getCaSecondsPerStep(), TimeUnits.Seconds ) );
				}
			} else {
				finished = false;
				minimalFrameCountCellularAutomaton = 2;
				if( showTimestepCellularAutomaton ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.VisualizationPanel.FPS.Simulation.Step" ) + " " + loc.getFloatConverter().format( control.getCaStep() ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.VisualizationPanel.FPS.Simulation.Time" ) + " " + Formatter.formatTimeUnit( control.getCaStep() * control.getCaSecondsPerStep(), TimeUnits.Seconds ) );
				}
			}
			row++;
		}
		if( control.hasGraph() ) {
			if( control.isGraphFinished() ) {
				minimalFrameCountGraph--;
				if( showTimestepGraph ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.VisualizationPanel.FPS.Graph.Finished" ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.VisualizationPanel.FPS.Graph.Needed" ) + " " + Formatter.formatTimeUnit( control.getGraphStep() * control.getGraphSecondsPerStep(), TimeUnits.Seconds ) );
				}
			} else {
				finished = false;
				minimalFrameCountGraph = 2;
				if( showTimestepGraph ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.VisualizationPanel.FPS.Graph.Step" ) + " " + loc.getFloatConverter().format( control.getGraphStep() ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.VisualizationPanel.FPS.Graph.Time" ) + " " + Formatter.formatTimeUnit( control.getGraphStep() * control.getGraphSecondsPerStep(), TimeUnits.Seconds ) );
				}
			}
			row++;
		}

		if( finished && isAnimating() )
			guiControl.animationFinished();
		//font.print( 0, this.getHeight() - (row++)*fontSize, "Zeit: " + secToMin( getTimeSinceStart()/Conversion.secToNanoSeconds ) );
		gl.glDisable( GL.GL_TEXTURE_2D );
		gl.glDisable( GL.GL_BLEND );
		ProjectionHelper.resetProjection( gl );
	}

	// TODO: wenn eine Belegung keine Typen enthält, gibts eine Exception anstelle einer Warnung

	@Override
	@SuppressWarnings("fallthrough")
	public void keyPressed( KeyEvent e ) {
		switch( e.getKeyCode() ) {
			case KeyEvent.VK_C:
				System.out.println( loc.getStringWithoutPrefix( "gui.VisualizationPanel.Camera.Information" ) );
				System.out.println( getCamera() );
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				guiControl.updateCameraInformation();
				zcontrol.getProject().getVisualProperties().getCameraPosition().pos = getCamera().getPos();
				zcontrol.getProject().getVisualProperties().getCameraPosition().view = getCamera().getView();
				zcontrol.getProject().getVisualProperties().getCameraPosition().up = getCamera().getUp();
				zcontrol.getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
				zcontrol.getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
			default:
				super.keyPressed( e );
		}
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		super.mousePressed( e );
		guiControl.updateCameraInformation();
		zcontrol.getProject().getVisualProperties().getCameraPosition().pos = getCamera().getPos();
		zcontrol.getProject().getVisualProperties().getCameraPosition().view = getCamera().getView();
		zcontrol.getProject().getVisualProperties().getCameraPosition().up = getCamera().getUp();
		zcontrol.getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
		zcontrol.getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		super.mouseDragged( e );
		guiControl.updateCameraInformation();
		zcontrol.getProject().getVisualProperties().getCameraPosition().pos = getCamera().getPos();
		zcontrol.getProject().getVisualProperties().getCameraPosition().view = getCamera().getView();
		zcontrol.getProject().getVisualProperties().getCameraPosition().up = getCamera().getUp();
		zcontrol.getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
		zcontrol.getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		super.mouseWheelMoved( e );
		guiControl.updateCameraInformation();
		zcontrol.getProject().getVisualProperties().getCameraPosition().pos = getCamera().getPos();
		zcontrol.getProject().getVisualProperties().getCameraPosition().view = getCamera().getView();
		zcontrol.getProject().getVisualProperties().getCameraPosition().up = getCamera().getUp();
		zcontrol.getProject().getVisualProperties().setCurrentWidth( getViewWidth() );
		zcontrol.getProject().getVisualProperties().setCurrentHeight( getViewHeight() );
	}
}
