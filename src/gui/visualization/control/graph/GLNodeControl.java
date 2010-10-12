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

import ds.GraphVisualizationResults;
import ds.graph.Node;
import ds.graph.Edge;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.draw.graph.GLNode;
import gui.visualization.control.FlowHistroryTriple;
import gui.visualization.util.FlowCalculator;
import java.util.ArrayList;
import de.tu_berlin.math.coga.graph.io.xml.FlowVisualization;

public class GLNodeControl extends AbstractZETVisualizationControl<GLFlowEdgeControl, GLNode, GLFlowGraphControl> {
	private double xPosition;
	private double yPosition;
	// TODO read data from file in ZET
	private double zPosition = 70;
	private int capacity;
	private FlowCalculator flowCalculator;
	// TODO transfer the scaling parameter somewhere else (probably into the graphVisResults)
	private static final double Z_TO_OPENGL_SCALING = 0.01d;
	double nwX;
	double nwY;
	double seX;
	double seY;
	private double time;
	private int index;
	private ArrayList<FlowHistroryTriple> graphHistory;
	private boolean isEvacuationNode,  isSourceNode,  isDeletedSourceNode = false;
	private int duration;
	private int startTime;
	private int floor;
	private boolean gridVisible = true;
	private double scaling = 1;

	public GLNodeControl( GraphVisualizationResults graphVisResult, Node node, GLFlowGraphControl glControl ) {
		super( glControl );

		scaling = Z_TO_OPENGL_SCALING;

		nwX = graphVisResult.getNodeRectangles().get( node ).get_nw_point().getX() * scaling;
		nwY = graphVisResult.getNodeRectangles().get( node ).get_nw_point().getY() * scaling;
		seX = graphVisResult.getNodeRectangles().get( node ).get_se_point().getX() * scaling;
		seY = graphVisResult.getNodeRectangles().get( node ).get_se_point().getY() * scaling;

		xPosition = graphVisResult.getNodePositionMapping().get( node ).x * scaling;
		yPosition = graphVisResult.getNodePositionMapping().get( node ).y * scaling;
		capacity = graphVisResult.getNodeCapacities().get( node );
		System.out.println( "Node coordinates: (" + xPosition + "," + yPosition + ")" );

		final boolean showEdgesBetweenFloors = false;

		for( Edge edge : graphVisResult.getNetwork().outgoingEdges( node ) )
			if( edge.start().id() != glControl.superSinkID() && edge.end().id() != glControl.superSinkID() ) {
				int nodeFloor1 = graphVisResult.getNodeToFloorMapping().get( edge.start() );
				int nodeFloor2 = graphVisResult.getNodeToFloorMapping().get( edge.end() );
				if( nodeFloor1 != nodeFloor2 && !showEdgesBetweenFloors )
					System.out.println( "Knoten auf verschiedenen Etagen." );
				else
					add( new GLFlowEdgeControl( graphVisResult, edge, glControl ) );
			}
		isEvacuationNode = graphVisResult.isEvacuationNode( node );
		isSourceNode = graphVisResult.isSourceNode( node );
		isDeletedSourceNode = graphVisResult.isDeletedSourceNode( node );

		floor = graphVisResult.getNodeToFloorMapping().get( node );
		// TODO get the information in ZET from Visualization option manager
		//zPosition += (floor - 1) * VisualizationOptionManager.getFloorDistance();
		zPosition += (floor - 1) * 70;

		setView( new GLNode( this ) );
		for( GLFlowEdgeControl edge : this ) {
			view.addChild( edge.getView() );
			edge.getView().update();
		}

		flowCalculator = new FlowCalculator();
		glControl.nodeProgress();
	}

	GLNodeControl( FlowVisualization fv, Node node, GLFlowGraphControl mainControl ) {
		super( mainControl );

		nwX = fv.getGv().getNodePositionMapping().get( node ).x * fv.getGv().getScale() * scaling;
		nwY = fv.getGv().getNodePositionMapping().get( node ).y * fv.getGv().getScale() * scaling;
		seX = fv.getGv().getNodePositionMapping().get( node ).x * fv.getGv().getScale() * scaling;
		seY = fv.getGv().getNodePositionMapping().get( node ).y * fv.getGv().getScale() * scaling;

		xPosition = (nwX + 0.5 * (seX - nwX)) * scaling;
		yPosition = (nwY + 0.5 * (seY - nwY)) * scaling;

		xPosition = (fv.getGv().getNodePositionMapping().get( node ).x + fv.getGv().getEffectiveOffset().x) * fv.getGv().getScale();
		yPosition = (fv.getGv().getNodePositionMapping().get( node ).y + fv.getGv().getEffectiveOffset().y) * fv.getGv().getScale();
		capacity = fv.getGv().getNodeCapacities().get( node );

		//final boolean showEdgesBetweenFloors = true;

		for( Edge edge : fv.getGv().getNetwork().outgoingEdges( node ) )
			if( !fv.getGv().isContainsSuperSink() )
				add( new GLFlowEdgeControl( fv, edge, mainControl ) );
			else if( edge.start().id() != mainControl.superSinkID() && edge.end().id() != mainControl.superSinkID() ) {
				// edit ignore floors
//				int nodeFloor1 = graphVisResult.getNodeToFloorMapping().get( edge.start() );
//				int nodeFloor2 = graphVisResult.getNodeToFloorMapping().get( edge.end() );
//				if( nodeFloor1 != nodeFloor2 && !showEdgesBetweenFloors )
//					System.out.println( "Knoten auf verschiedenen Etagen." );
//				else
					add( new GLFlowEdgeControl( fv, edge, mainControl ) );
			}
		isEvacuationNode = fv.getGv().isEvacuationNode( node );
		isSourceNode = fv.getGv().isSourceNode( node );
		//isDeletedSourceNode = graphVisResult.isDeletedSourceNode( node );

		floor = 0; // ignore floors at the moment

		//zPosition += (floor - 1) * VisualizationOptionManager.getFloorDistance();
		zPosition = fv.getGv().getNodePositionMapping().get( node ).z * fv.getGv().getScale();

		setView( new GLNode( this ) );
		for( GLFlowEdgeControl edge : this ) {
			view.addChild( edge.getView() );
			edge.getView().update();
		}

		flowCalculator = new FlowCalculator();
		mainControl.nodeProgress();
	}

	public boolean isEvacuationNode() {
		return isEvacuationNode;
	}

	public boolean isSourceNode() {
		return isSourceNode;
	}

	public boolean isDeletedSourceNode() {
		return isDeletedSourceNode;
	}

	public double getNwX() {
		return nwX ;
	}

	public double getNwY() {
		return nwY ;
	}

	public double getSeX() {
		return seX ;
	}

	public double getSeY() {
		return seY ;
	}

	public GLNode getGLNode() {
		return this.getView();
	}

	public int getCapacity() {
		return capacity;
	}

	public double getXPosition() {
		return xPosition;
	}

	public double getYPosition() {
		return yPosition;
	}

	public double getZPosition() {
		return zPosition;
	}

	public FlowCalculator getFlowCalculator() {
		return flowCalculator;
	}

	public void stepUpdate( int step ) {
		time = mainControl.getStep();
		graphHistory = flowCalculator.getCalculatedFlow();

		if( graphHistory.size() <= 0 )
			return;
		if( index < graphHistory.size() && this.graphHistory.get( index ).getFirstValue() <= step ) {
			this.startTime = this.graphHistory.get( index ).getTime();
			this.duration = this.graphHistory.get( index ).getDuration();
			//this.flow = this.graphHistory.get( index ).getFlow();
			index++;
		}
	}

	public final boolean isCurrentlyOccupied() {
		if( startTime < time && startTime + duration > time )
			return true;
		else
			return false;
	}

	public void setRectangleVisible( boolean val ) {
		this.gridVisible = val;
	}

	/**
	 * Returns <code>true</code> if the rectangled area belonging to the node is visible.
	 * @return <code>true</code> if the rectangled node area is visible, <code>false</code> otherwise
	 */
	public boolean isRectangleVisible() {
		return gridVisible;
	}
}