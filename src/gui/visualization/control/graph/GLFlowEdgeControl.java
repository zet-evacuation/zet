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
package gui.visualization.control.graph;

import ds.graph.Edge;
import ds.graph.flow.EdgeBasedFlowOverTime;
import ds.GraphVisualizationResults;
import ds.mapping.IdentifiableIntegerMapping;
import gui.visualization.draw.graph.GLFlowEdge;
import java.util.ArrayList;

import de.tu_berlin.math.coga.graph.io.xml.FlowVisualization;

/**
 * The control class for edges in an MVC-design. This class controls the visualization of such an edge represented
 * by {@code GLFlowEdge}. That means it sets the correct positions depending on the time of the visualization.
 * @author Jan-Philipp Kappmeier
 */
public class GLFlowEdgeControl extends GLEdgeControl {
	protected GLFlowGraphControl mainControl;
	private double time;
	private ArrayList<Integer> flowOnEdge;
	private int maxFlowRate;
	private double deltaStep;
	private int transitTime;
	private int capacity;
	private static final double Z_TO_OPENGL_SCALING = 0.01d;

	/**
	 * Creates a new {@code GLFlowEdgeControl} object for the edge {@code edge} using data from
	 * the {@code graphVisResult} object.
	 * @param graphVisResult contains all information necessary to visualize a result of a flow algorithm.
	 * @param edge the edge for this control object.
	 * @param glControl the main control class.
	 */
	public GLFlowEdgeControl( GraphVisualizationResults graphVisResult, Edge edge, GLFlowGraphControl glControl ) {
		super( graphVisResult.getNodePositionMapping(), edge );
		setScaling( Z_TO_OPENGL_SCALING );

		this.mainControl = glControl;
		//controlled = edge;
		setView( new GLFlowEdge( this ) );

		// general edge attributes
		maxFlowRate = graphVisResult.getMaxFlowRate();
		transitTime = graphVisResult.getTransitTimes().get( edge );
		capacity = graphVisResult.getEdgeCapacities().get( edge );

		// calculate flow on the edge
		IdentifiableIntegerMapping<Edge> transitTimes = graphVisResult.getTransitTimes();

		EdgeBasedFlowOverTime flowOverTime = graphVisResult.getFlowOverTime();

		int maxT = flowOverTime.get( edge ).getLastTimeWithNonZeroValue(); // maximaler Zeithorizont
		int transit = transitTimes.get( edge );
		if( maxT > 0 )
			glControl.setMaxTime( maxT + transit );
		if( maxT > 0 ) {
			flowOnEdge = new ArrayList<Integer>( maxT + transit + transit );
			for( int i = 0; i < transit; i++ )
				flowOnEdge.add( new Integer( 0 ) );
			for( int i = 0; i <= maxT; i++ )
				flowOnEdge.add( new Integer( flowOverTime.get( edge ).get( i ) ) );
			for( int i = 0; i < transit; i++ )
				flowOnEdge.add( new Integer( 0 ) );
		} else
			flowOnEdge = new ArrayList<Integer>();
	}

	GLFlowEdgeControl( FlowVisualization fv, Edge edge, GLFlowGraphControl mainControl ) {
		super( fv.getGv().getNodePositionMapping(), edge );
		this.mainControl = mainControl;

		// todo: usage of isFirst by flag...
		isFirst = !fv.isEdgesDoubled() || edge.start().id() < edge.end().id() ? true : false;

		// store general edge attributes
		maxFlowRate = fv.getMaxFlowRate();
		mainControl.setMaxTime( fv.getTimeHorizon() + 1 );
		transitTime = fv.getGv().getTransitTimes().get( edge );
		capacity = fv.getGv().getEdgeCapacities().get( edge );


		// compute flow visualization data structure
		IdentifiableIntegerMapping<Edge> transitTimes = fv.getGv().getTransitTimes();

		EdgeBasedFlowOverTime flowOverTime = fv.getFlow();
		if( flowOverTime != null ) {

		}
		int maxT = flowOverTime.get( edge ).getLastTimeWithNonZeroValue(); // maximaler Zeithorizont
		int transit = transitTimes.get( edge );

		if( maxT > 0 ) {
			flowOnEdge = new ArrayList<Integer>( maxT + transit + transit );
			for( int i = 0; i < transit; i++ )
				flowOnEdge.add( new Integer( 0 ) );
			for( int i = 0; i <= maxT; i++ )
				flowOnEdge.add( new Integer( flowOverTime.get( edge ).get( i ) ) );
			for( int i = 0; i < transit; i++ )
				flowOnEdge.add( new Integer( 0 ) );
		} else
			flowOnEdge = new ArrayList<Integer>();
	}

	@Override
	protected void setView() {
		setView( new GLFlowEdge( this ) );
	}

	/**
	 * Returns the transit time of this edge.
	 * @return the transit time of this edge.
	 */
	public int getTransitTime() {
		return transitTime;
	}

	/**
	 * Returns an array list containing the amount of flow going into
	 * the edge for each time step within 0 and time horizon + transit time of the edge.
	 * @return an array list giving the amount of flow going into the edge for each point in time.
	 */
	public ArrayList<Integer> getFlowOnEdge() {
		return flowOnEdge;
	}

	/**
	 * Returns the capacity of this edge.
	 * @return the capacity of this edge.
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Returns the maximal flow rate (taking the flow on all edges into account).
	 * @return the maximal flow rate.
	 */
	public int getMaxFlowRate() {
		return maxFlowRate;
	}

	/**
	 * The real visualization time used for fluid visualization.
	 * @return the real visualization time
	 */
	public double getTime() {
		return time;
	}

	/**
	 * Returns the current delta step. That means the real value for the progress of the current step
	 * for a fluid visualization.
	 * @return the current real step.
	 */
	public double getDeltaStep() {
		return deltaStep;
	}

	/**
	 * Calculates the current time and delta information used for fluid visualization depending
	 * of the graph step in the main control class.
	 */
	public void stepUpdate() {
		time = mainControl.getStep();
		deltaStep = time - Math.floor( time );
	}

	@Override
	public GLFlowEdge getView() {
		return (GLFlowEdge)super.getView();
	}
}
