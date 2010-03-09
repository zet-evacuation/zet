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
import gui.ZETProperties;
import gui.visualization.control.GLControl;
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
				minimalFrameCountGraph = 2;
				if( showTimestepGraph ) {
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.graphStep" ) + " " + loc.getFloatConverter().format( control.getGraphStep() ) );
					font.print( 0, this.getHeight() - (row++) * fontSize, loc.getString( "gui.visualization.fps.graphTime" ) + " " + Formatter.secToMin( control.getGraphStep() * control.getGraphSecondsPerStep() ) );
				}
			}
			row++;
		}

		//font.print( 0, this.getHeight() - (row++)*fontSize, "Zeit: " + secToMin( getTimeSinceStart()/Conversion.secToNanoSeconds ) );
		gl.glDisable( GL.GL_TEXTURE_2D );
		gl.glDisable( GL.GL_BLEND );
		ProjectionHelper.resetProjection( gl );
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
