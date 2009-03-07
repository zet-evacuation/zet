package gui.visualization.control.graph;

import ds.graph.GraphVisualizationResult;
import ds.graph.Node;
import ds.graph.Edge;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.draw.graph.GLNode;
import gui.visualization.control.AbstractControl;
import gui.visualization.control.GLControl;
import gui.visualization.control.FlowHistroryTriple;
import gui.visualization.draw.graph.GLEdge;
import gui.visualization.util.FlowCalculator;
import java.util.ArrayList;

public class GLNodeControl extends AbstractControl<GLNode, Node, GraphVisualizationResult, GLEdge, GLEdgeControl> {
	private double xPosition;
	private double yPosition;
	private double zPosition = VisualizationOptionManager.getGraphHeight();
	private int capacity;
	private FlowCalculator flowCalculator;
	private static final double Z_TO_OPENGL_SCALING = 0.1d;
	double nwX;
	double nwY;
	double seX;
	double seY;
	private double time;
	private int index;
	private ArrayList<FlowHistroryTriple> graphHistory;
	private boolean isEvacuationNode,  isSourceNode,  isDeletedSourceNode;
	private int duration;
	private double flow;
	private int startTime;
	private int floor;
	private boolean gridVisible = true;

	public GLNodeControl( GraphVisualizationResult graphVisResult, Node node, GLControl glControl ) {
		super( node, graphVisResult, glControl );

		nwX = graphVisResult.getNodeRectangles().get( node ).get_nw_point().getX();
		nwY = graphVisResult.getNodeRectangles().get( node ).get_nw_point().getY();
		seX = graphVisResult.getNodeRectangles().get( node ).get_se_point().getX();
		seY = graphVisResult.getNodeRectangles().get( node ).get_se_point().getY();

		xPosition = (nwX + 0.5 * (seX - nwX)) * Z_TO_OPENGL_SCALING;
		yPosition = (nwY + 0.5 * (seY - nwY)) * Z_TO_OPENGL_SCALING;
		capacity = graphVisResult.getNodeCapacities().get( node );

		for( Edge edge : graphVisResult.getNetwork().outgoingEdges( node ) ) {
			if( edge.start().id() != 0 && edge.end().id() != 0 ) {
				add( new GLEdgeControl( graphVisResult, edge, glControl ) );
			}
		}
		isEvacuationNode = graphVisResult.isEvacuationNode( node );
		isSourceNode = graphVisResult.isSourceNode( node );
		isDeletedSourceNode = graphVisResult.isDeletedSourceNode( node );

		floor = graphVisResult.getNodeToFloorMapping().get( node );
		zPosition += (floor - 1) * (VisualizationOptionManager.getFloorDistance() + VisualizationOptionManager.getFloorHeight());

		setView( new GLNode( this ) );
		flowCalculator = new FlowCalculator();
		glControl.nodeProgress();
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
		return nwX * Z_TO_OPENGL_SCALING;
	}

	public double getNwY() {
		return nwY * Z_TO_OPENGL_SCALING;
	}

	public double getSeX() {
		return seX * Z_TO_OPENGL_SCALING;
	}

	public double getSeY() {
		return seY * Z_TO_OPENGL_SCALING;
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
		return -yPosition;
	}

	public double getZPosition() {
		return zPosition;
	}

	public FlowCalculator getFlowCalculator() {
		return flowCalculator;
	}

	public Node getNode() {
		return getControlled();
	}

	public void stepUpdate( int step ) {
		time = mainControl.getGraphStep();
		graphHistory = flowCalculator.getCalculatedFlow();

		if( graphHistory.size() <= 0 )
			return;
		if( index < graphHistory.size() && this.graphHistory.get( index ).getFirstValue() <= step ) {
			this.startTime = this.graphHistory.get( index ).getTime();
			this.duration = this.graphHistory.get( index ).getDuration();
			this.flow = this.graphHistory.get( index ).getFlow();
			index++;
		}
	}

	public final boolean isCurrentlyOccupied() {
		if( startTime < time && startTime + duration > time ) {
			return true;
		} else {
			return false;
		}
	}

	public void setGridVisible( boolean val ) {
		this.gridVisible = val;
	}
	
	public boolean isGridVisible() {
		return gridVisible;
	}
}
