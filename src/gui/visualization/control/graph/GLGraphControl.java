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
 * Class GLGraphControl
 * Created 02.05.2008, 18:44:28
 */

package gui.visualization.control.graph;

import batch.tasks.AlgorithmTask;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.math.Conversion;
import ds.GraphVisualizationResult;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.draw.graph.GLGraph;
import java.util.HashMap;
import java.util.Iterator;
import opengl.framework.abs.Controlable;

/**
 *  @author Jan-Philipp Kappmeier
 */
//public class GLGraphControl extends AbstractControl<GLGraph, Network, GraphVisualizationResult, GLGraphFloor, GLGraphFloorControl, GLControl> {
public class GLGraphControl extends AbstractZETVisualizationControl<GLGraphFloorControl, GLGraph, GLGraphControl> implements Controlable {

	private int nodeCount;
	private int nodesDone;
	private HashMap<Integer, GLGraphFloorControl> allFloorsByID;

	// Timing variables
	private long time;
	private double realStepGraph;
	private long timeSinceLastStep = 0;
	private long nanoSecondsPerStep;
	private double secondsPerStepGraph;
	private long stepGraph;
	/** The maximal time step used for the graph */
	private int graphStepCount = 0;
	/** The status of the simulation, true if all is finished */
	private boolean finished = false;

	public GLGraphControl( GraphVisualizationResult graphVisResult ) {
		super();
		mainControl = this;

		AlgorithmTask.getInstance().setProgress( 0, Localization.getInstance().getStringWithoutPrefix( "batch.tasks.progress.createGraphVisualizationDataStructure" ), "" );
		nodeCount = graphVisResult.getNetwork().nodes().size();
		nodesDone = 0;

		allFloorsByID = new HashMap<Integer, GLGraphFloorControl>();
		int floorCount = graphVisResult.getFloorToNodeMapping().size();
		for( int i = 0; i < floorCount; i++ )
			if( graphVisResult.getFloorToNodeMapping().get( i ).size() > 0 ) {
				GLGraphFloorControl floorControl = new GLGraphFloorControl( graphVisResult, graphVisResult.getFloorToNodeMapping().get( i ), i, mainControl );
				add( floorControl );
				allFloorsByID.put( i, floorControl );
			}
		this.setView( new GLGraph( this ) );
		for( GLGraphFloorControl floor : this )
			view.addChild( floor.getView() );
		

			// Set speed such that it arrives when the last individual is evacuated.
//			if( hasCA && PropertyContainer.getInstance().getAsBoolean( "options.visualization.flow.equalArrival" ) ) {
//				nanoSecondsPerStepGraph = graphStepCount == 0 ? 0 : (nanoSecondsPerStepCA * caVisResults.getRecording().length()) / graphStepCount;
//				secondsPerStepGraph = nanoSecondsPerStepGraph * Conversion.nanoSecondsToSec;
//				System.err.println( "Für gleichzeitige Ankunft berechnete Geschwindigkeit: " + nanoSecondsPerStepGraph );
//			} else {
				//secondsPerStepGraph = secondsPerStepGraph();
		this.setSecondsPerStepGraph( 0.75 );
				System.err.println( "Berechnete Geschwindigkeit (durchschnitt der ZA-Geschwindigkeiten): " + nanoSecondsPerStep );
//			}

	}

	@Override
	public void clear() {
		allFloorsByID.clear();
		childControls.clear();
	}

	@Override
	public Iterator<GLGraphFloorControl> fullIterator() {
		return allFloorsByID.values().iterator();
	}

	GLGraphFloorControl getFloorControl( Integer floorID ) {
		return this.allFloorsByID.get( floorID );
	}

	public void showOnlyFloor( Integer floorID ) {
		childControls.clear();
		childControls.add( allFloorsByID.get( floorID ) );
	}

	public void showAllFloors() {
		childControls.clear();
		childControls.addAll( allFloorsByID.values() );
	}

	/**
	 * <p>This method increases the number of nodes that are already created and
	 * calculates a new progress. The progress will at most reach 99% so that
	 * after all objects are created a final "Done" message can be submitted.</p>
	 * <p>Note that before this method can be used in the proper way the private
	 * variable <code>nodesDone</code> and <code>nodeCount</code> should be
	 * initialized correct. However, it is guaranteed to calculate a value from
	 * 0 to 99.
	 */
	public void nodeProgress() {
		nodesDone++;
		int progress = Math.max( 0, Math.min( (int) Math.round( ((double) nodesDone / nodeCount) * 100 ), 99 ) );
		AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Graph...", "Knoten " + nodesDone + " von " + nodeCount + " erzeugt." );
	}

	/**
	 * Returns the current step of the graph. The step counter is stopped if the graph is finished.
	 * @return the current step of the graph
	 */
	public double getGraphStep() {
		return realStepGraph;
	}

	public void addTime( long timeNanoSeconds ) {
		time += timeNanoSeconds;
		timeSinceLastStep += timeNanoSeconds;
		long elapsedSteps = (timeSinceLastStep / nanoSecondsPerStep);
		stepGraph += elapsedSteps;
		timeSinceLastStep = timeSinceLastStep % nanoSecondsPerStep;
		realStepGraph = ((double) time / (double) nanoSecondsPerStep);
			for( GLGraphFloorControl g : this ) {
				for( GLNodeControl node : g ) {
					for( GLEdgeControl edge : node ) {
						//edges.add( edge );	// TODO use addAll
						edge.stepUpdate();
					}
					node.stepUpdate( (int) stepGraph );
				}
			}
//		for( GLNodeControl node : nodes )
//			node.stepUpdate( (int) stepGraph );
//		for( GLEdgeControl edge : edges )
//			edge.stepUpdate();
		if( stepGraph > graphStepCount ) {
			finished = true;
		}
	}

	/**
	 * Sets the maximal time used by the graph
	 * @param maxT
	 */
	public void setGraphMaxTime( int maxT ) {
		this.graphStepCount = Math.max( graphStepCount, maxT );
	}

	/**
	 * Checks wheather graph visualization is finished, or not.
	 *  @return true if the simulation is finished, false otherwise
	 */
	@Override
	public final boolean isFinished() {
		return finished;
	}

	/**
	 * ... also sets visualization speed to real-time speed.
	 * @param secondsPerStepGraph
	 */
	public void setSecondsPerStepGraph( double secondsPerStepGraph ) {
		this.secondsPerStepGraph = secondsPerStepGraph;
		nanoSecondsPerStep = Math.round( secondsPerStepGraph * Conversion.secToNanoSeconds );
	}

	public void setSpeedFactor( double speedFactor ) {
		nanoSecondsPerStep = (long)(Math.round( secondsPerStepGraph * Conversion.secToNanoSeconds ) / speedFactor);
	}

	public double getSecondsPerStepGraph() {
		return secondsPerStepGraph;
	}

	public int getGraphStepCount() {
		return graphStepCount;
	}




}
