/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
package de.zet_evakuierung.visualization.network.control;

import java.util.ArrayList;

import de.tu_berlin.math.coga.graph.io.xml.visualization.FlowVisualization;
import de.zet_evakuierung.visualization.network.FlowHistroryTriple;
import de.zet_evakuierung.visualization.network.draw.GLNode;
import de.zet_evakuierung.visualization.network.util.FlowCalculator;
import ds.GraphVisualizationResults;
import gui.visualization.control.AbstractZETVisualizationControl;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;

public class GLNodeControl extends AbstractZETVisualizationControl<GLFlowEdgeControl, GLNode, NetworkVisualizationModel> {
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

	public GLNodeControl( GraphVisualizationResults graphVisResult, Node node, NetworkVisualizationModel visualizationModel ) {
		super( visualizationModel );

		nwX = graphVisResult.getNodeRectangles().get( node ).get_nw_point().getX() * visualizationModel.scaling;
		nwY = graphVisResult.getNodeRectangles().get( node ).get_nw_point().getY() * visualizationModel.scaling;
		seX = graphVisResult.getNodeRectangles().get( node ).get_se_point().getX() * visualizationModel.scaling;
		seY = graphVisResult.getNodeRectangles().get( node ).get_se_point().getY() * visualizationModel.scaling;

		xPosition = graphVisResult.getNodePositionMapping().get( node ).x * visualizationModel.scaling;
		yPosition = graphVisResult.getNodePositionMapping().get( node ).y * visualizationModel.scaling;
		capacity = graphVisResult.getNodeCapacities().get( node );

		final boolean showEdgesBetweenFloors = false;

		for( Edge edge : graphVisResult.getNetwork().outgoingEdges( node ) )
			if( edge.start().id() != visualizationModel.superSinkID() && edge.end().id() != visualizationModel.superSinkID() ) {
				int nodeFloor1 = graphVisResult.getNodeToFloorMapping().get( edge.start() );
				int nodeFloor2 = graphVisResult.getNodeToFloorMapping().get( edge.end() );
				if( nodeFloor1 != nodeFloor2 && !showEdgesBetweenFloors && !drawInterFloorEdges)
					System.out.println( "Knoten auf verschiedenen Etagen." );
				else
					add( new GLFlowEdgeControl( graphVisResult, edge, visualizationModel ) );
			}
		id = node.id();
		isEvacuationNode = graphVisResult.isEvacuationNode( node );
		isSourceNode = graphVisResult.isSourceNode( node );
		isDeletedSourceNode = graphVisResult.isDeletedSourceNode( node );

		floor = graphVisResult.getNodeToFloorMapping().get( node );

		zPosition = visualizationModel.defaultFloorHeight * 0.1 * visualizationModel.scaling; // set bottom graph 10% above the ground
		zPosition += floor * visualizationModel.defaultFloorHeight * visualizationModel.scaling;

		setView( new GLNode( this ) );
		for( GLFlowEdgeControl edge : this ) {
			view.addChild( edge.getView() );
			edge.getView().update();
		}

		flowCalculator = new FlowCalculator();
		visualizationModel.nodeProgress();
	}

	GLNodeControl( FlowVisualization fv, Node node, NetworkVisualizationModel visualizationModel ) {
		super( visualizationModel );

		nwX = fv.getNodePositionMapping().get( node ).x * fv.getScale() * visualizationModel.scaling;
		nwY = fv.getNodePositionMapping().get( node ).y * fv.getScale() * visualizationModel.scaling;
		seX = fv.getNodePositionMapping().get( node ).x * fv.getScale() * visualizationModel.scaling;
		seY = fv.getNodePositionMapping().get( node ).y * fv.getScale() * visualizationModel.scaling;

		xPosition = (nwX + 0.5 * (seX - nwX)) * visualizationModel.scaling;
		yPosition = (nwY + 0.5 * (seY - nwY)) * visualizationModel.scaling;

		xPosition = (fv.getNodePositionMapping().get( node ).x + fv.getEffectiveOffset().x) * fv.getScale() * visualizationModel.scaling;
		yPosition = (fv.getNodePositionMapping().get( node ).y + fv.getEffectiveOffset().y) * fv.getScale() * visualizationModel.scaling;
		capacity = fv.getNodeCapacities().get( node );

		//final boolean showEdgesBetweenFloors = true;

		for( Edge edge : fv.getNetwork().outgoingEdges( node ) )
			if( !fv.isContainsSuperSink() )
				add( new GLFlowEdgeControl( fv, edge, visualizationModel ) );
			else if( edge.start().id() != visualizationModel.superSinkID() && edge.end().id() != visualizationModel.superSinkID() ) {
				// edit ignore floors
				add( new GLFlowEdgeControl( fv, edge, visualizationModel ) );
			}
		isEvacuationNode = fv.isEvacuationNode( node );
		isSourceNode = fv.isSourceNode( node );
		//isDeletedSourceNode = graphVisResult.isDeletedSourceNode( node );

		floor = 0; // ignore floors at the moment

		zPosition = fv.getNodePositionMapping().get( node ).z * fv.getScale() * visualizationModel.scaling;

		setView( new GLNode( this ) );
		for( GLFlowEdgeControl edge : this ) {
			view.addChild( edge.getView() );
			edge.getView().update();
		}

		flowCalculator = new FlowCalculator();
		visualizationModel.nodeProgress();
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
		time = visualizationModel.getStep();
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
		return visualizationModel.defaultFloorHeight*visualizationModel.scaling*0.1;
	}
	
	public int getNumber() {
		return id;
	}
}
