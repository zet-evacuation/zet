/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
package gui.visualization.control.graph;

import de.tu_berlin.math.coga.graph.io.xml.visualization.FlowVisualization;
import ds.GraphVisualizationResults;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.control.FlowHistroryTriple;
import gui.visualization.draw.graph.GLNode;
import gui.visualization.util.FlowCalculator;
import java.util.ArrayList;

public class GLNodeControl extends AbstractZETVisualizationControl<GLFlowEdgeControl, GLNode, GLFlowGraphControl> {
	private double xPosition;
	private double yPosition;
	// TODO read data from file in ZET
	private double zPosition = 0;
	private int capacity;
	private FlowCalculator flowCalculator;
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
	private boolean drawInterFloorEdges = true;
	private int id = 0;

	public GLNodeControl( GraphVisualizationResults graphVisResult, Node node, GLFlowGraphControl glControl ) {
		super( glControl );

		nwX = graphVisResult.getNodeRectangles().get( node ).get_nw_point().getX() * glControl.scaling;
		nwY = graphVisResult.getNodeRectangles().get( node ).get_nw_point().getY() * glControl.scaling;
		seX = graphVisResult.getNodeRectangles().get( node ).get_se_point().getX() * glControl.scaling;
		seY = graphVisResult.getNodeRectangles().get( node ).get_se_point().getY() * glControl.scaling;

		xPosition = graphVisResult.getNodePositionMapping().get( node ).x * glControl.scaling;
		yPosition = graphVisResult.getNodePositionMapping().get( node ).y * glControl.scaling;
		capacity = graphVisResult.getNodeCapacities().get( node );

		final boolean showEdgesBetweenFloors = false;

		for( Edge edge : graphVisResult.getNetwork().outgoingEdges( node ) )
			if( edge.start().id() != glControl.superSinkID() && edge.end().id() != glControl.superSinkID() ) {
				int nodeFloor1 = graphVisResult.getNodeToFloorMapping().get( edge.start() );
				int nodeFloor2 = graphVisResult.getNodeToFloorMapping().get( edge.end() );
				if( nodeFloor1 != nodeFloor2 && !showEdgesBetweenFloors && !drawInterFloorEdges)
					System.out.println( "Knoten auf verschiedenen Etagen." );
				else
					add( new GLFlowEdgeControl( graphVisResult, edge, glControl ) );
			}
		id = node.id();
		isEvacuationNode = graphVisResult.isEvacuationNode( node );
		isSourceNode = graphVisResult.isSourceNode( node );
		isDeletedSourceNode = graphVisResult.isDeletedSourceNode( node );

		floor = graphVisResult.getNodeToFloorMapping().get( node );

		zPosition = glControl.defaultFloorHeight * 0.1 * glControl.scaling; // set bottom graph 10% above the ground
		zPosition += floor * glControl.defaultFloorHeight * glControl.scaling;

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

		nwX = fv.getNodePositionMapping().get( node ).x * fv.getScale() * mainControl.scaling;
		nwY = fv.getNodePositionMapping().get( node ).y * fv.getScale() * mainControl.scaling;
		seX = fv.getNodePositionMapping().get( node ).x * fv.getScale() * mainControl.scaling;
		seY = fv.getNodePositionMapping().get( node ).y * fv.getScale() * mainControl.scaling;

		xPosition = (nwX + 0.5 * (seX - nwX)) * mainControl.scaling;
		yPosition = (nwY + 0.5 * (seY - nwY)) * mainControl.scaling;

		xPosition = (fv.getNodePositionMapping().get( node ).x + fv.getEffectiveOffset().x) * fv.getScale() * mainControl.scaling;
		yPosition = (fv.getNodePositionMapping().get( node ).y + fv.getEffectiveOffset().y) * fv.getScale() * mainControl.scaling;
		capacity = fv.getNodeCapacities().get( node );

		//final boolean showEdgesBetweenFloors = true;

		for( Edge edge : fv.getNetwork().outgoingEdges( node ) )
			if( !fv.isContainsSuperSink() )
				add( new GLFlowEdgeControl( fv, edge, mainControl ) );
			else if( edge.start().id() != mainControl.superSinkID() && edge.end().id() != mainControl.superSinkID() ) {
				// edit ignore floors
				add( new GLFlowEdgeControl( fv, edge, mainControl ) );
			}
		isEvacuationNode = fv.isEvacuationNode( node );
		isSourceNode = fv.isSourceNode( node );
		//isDeletedSourceNode = graphVisResult.isDeletedSourceNode( node );

		floor = 0; // ignore floors at the moment

		zPosition = fv.getNodePositionMapping().get( node ).z * fv.getScale() * mainControl.scaling;

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
		return startTime < time && startTime + duration > time;
	}

	public void setRectangleVisible( boolean val ) {
		this.gridVisible = val;
	}

	/**
	 * Returns {@code true} if the rectangular area belonging to the node is visible.
	 * @return {@code true} if the rectangular node area is visible, {@code false} otherwise
	 */
	public boolean isRectangleVisible() {
		return gridVisible;
	}

	/**
	 * Returns the default floor height. Thus, the rectangles can be drawn under
	 * the nodes by exactly this amount
	 * @return 
	 */
	public double getFloorHeight() {
		return mainControl.defaultFloorHeight*mainControl.scaling*0.1;
	}
	
	public int getNumber() {
		return id;
	}
}
