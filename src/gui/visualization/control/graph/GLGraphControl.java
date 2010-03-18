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
import ds.graph.Node;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.draw.graph.GLGraph;
import gui.visualization.draw.graph.GLGraphFloor;
import java.util.HashMap;
import java.util.Iterator;
import javax.media.opengl.GL;
import opengl.framework.abs.DrawableControlable;
import zet.xml.FlowVisualization;

/**
 *  @author Jan-Philipp Kappmeier
 */
public class GLGraphControl extends AbstractZETVisualizationControl<GLGraphFloorControl, GLGraph, GLGraphControl> implements DrawableControlable {
	//public static double sizeMultiplicator = 0.1; // for ZET
	public static double sizeMultiplicator = 1;

	private int nodeCount;
	private int nodesDone;
	private HashMap<Integer, GLGraphFloorControl> allFloorsByID;
	// Timing variables
	private long time;
	private double realStep;
//	private long timeSinceLastStep = 0;
	private long nanoSecondsPerStep = Conversion.secToNanoSeconds;
	private double secondsPerStep = 1;
	private long step;
	/** The maximal time step used for the graph */
	private int stepCount = 0;
	/** The status of the simulation, true if all is finished */
	private boolean finished = false;
	double speedFactor = 1;
	private int superSinkID = 0;
	private boolean supportsFloors = false;

	public GLGraphControl( GraphVisualizationResult graphVisResult ) {
		super();
		mainControl = this;

		AlgorithmTask.getInstance().setProgress( 0, Localization.getInstance().getStringWithoutPrefix( "batch.tasks.progress.createGraphVisualizationDataStructure" ), "" );
		nodeCount = graphVisResult.getNetwork().nodes().size();
		nodesDone = 0;
		superSinkID = graphVisResult.getSupersink().id();
		allFloorsByID = new HashMap<Integer, GLGraphFloorControl>();
		supportsFloors = true;
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
	}

	public GLGraphControl( FlowVisualization fv ) {
		mainControl = this;

		AlgorithmTask.getInstance().setProgress( 0, Localization.getInstance().getStringWithoutPrefix( "batch.tasks.progress.createGraphVisualizationDataStructure" ), "" );
		nodeCount = fv.getGv().getNetwork().nodes().size();
		nodesDone = 0;
		//superSinkID = graphVisResult.getSupersink().id();
		superSinkID = fv.getGv().getSinks().get( 0 ).id(); // graphVisResult.getSupersink().id();

		Iterator<Node> it = fv.getGv().getNetwork().nodes().iterator();
		Node supersink = fv.getGv().getSinks().get( 0 );  // graphVisResult.getSupersink();

		GLGraphFloorControl floorControl = new GLGraphFloorControl( fv, fv.getGv().getNetwork().nodes(), mainControl );
		add( floorControl );

		allFloorsByID = new HashMap<Integer, GLGraphFloorControl>();
		allFloorsByID.put( 0, floorControl );

		//allFloorsByID = new HashMap<Integer, GLGraphFloorControl>();
		//int floorCount = graphVisResult.getFloorToNodeMapping().size();
		//for( int i = 0; i < floorCount; i++ )
			//if( graphVisResult.getFloorToNodeMapping().get( i ).size() > 0 ) {
			//	GLGraphFloorControl floorControl = new GLGraphFloorControl( graphVisResult, graphVisResult.getFloorToNodeMapping().get( i ), i, mainControl );
			//	add( floorControl );
			//	allFloorsByID.put( i, floorControl );
			//}
		this.setView( new GLGraph( this ) );
		for( GLGraphFloorControl floor : this )
			view.addChild( floor.getView() );
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
	public double getStep() {
		return realStep;
	}

	@Override
	public void addTime( long timeNanoSeconds ) {
		time += timeNanoSeconds * speedFactor;
		realStep = ((double) time / (double) nanoSecondsPerStep);
		step = (long) realStep;

		for( GLGraphFloorControl g : this )
			for( GLNodeControl node : g ) {
				for( GLEdgeControl edge : node )
					edge.stepUpdate();
				node.stepUpdate( (int) step );
			}
		if( step > stepCount )
			finished = true;
	}

	public void setTime( long time ) {
		this.time = time;
		realStep = time == 0 ? 0 : ((double) time / (double) nanoSecondsPerStep);
		step = (long)realStep;

		for( GLGraphFloorControl g : this )
			for( GLNodeControl node : g ) {
				for( GLEdgeControl edge : node )
					edge.stepUpdate();
				node.stepUpdate( (int) step );
			}
		finished = step > stepCount;
	}

	public void resetTime() {
		setTime( 0 );
	}

	/**
	 * Sets the maximal time used by the graph
	 * @param maxT
	 */
	public void setMaxTime( int maxT ) {
		this.stepCount = Math.max( stepCount, maxT );
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
	 * Sets the time needed for one step of the graph in nano seconds. A graph
	 * step equals a time unit of the network flow.
	 * @param nanoSecondsPerStep the nano seconds needed for one graph step
	 * @see #setSecondsPerStep(double) 
	 */
	public void setNanoSecondsPerStep( long nanoSecondsPerStep ) {
		this.nanoSecondsPerStep = nanoSecondsPerStep;
		secondsPerStep = nanoSecondsPerStep * Conversion.nanoSecondsToSec;

		System.out.println( "Berechnete Graph-Geschwindigkeit: " + nanoSecondsPerStep );
	}

	/**
	 * Sets the time needed for one step of the graph in seconds. A graph step
	 * equals a time unit of the network flow.
	 * @param secondsPerStepGraph the seconds needed for one graph step
	 * @see #setNanoSecondsPerStep(long)
	 */
	public void setSecondsPerStep( double secondsPerStepGraph ) {
		this.secondsPerStep = secondsPerStepGraph;
		nanoSecondsPerStep = Math.round( secondsPerStepGraph * Conversion.secToNanoSeconds );

		System.out.println( "Berechnete Graph-Geschwindigkeit: " + nanoSecondsPerStep );
	}

	public void setSpeedFactor( double speedFactor ) {
		this.speedFactor = speedFactor;
	}

	public double getSecondsPerStep() {
		return secondsPerStep;
	}

	public long getNanoSecondsPerStep() {
		return this.nanoSecondsPerStep;
	}

	public int getStepCount() {
		return stepCount;
	}

	public void draw( GL gl ) {
		getView().draw( gl );
	}

	public void update() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	int superSinkID() {
		return superSinkID;
	}
}
