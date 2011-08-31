/**
 * GraphAlgorithmTask.java
 * Created: Jul 29, 2010,5:41:36 PM
 */
package tasks;

import batch.GraphAlgorithm;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.BaseZToGraphConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToSpanTreeConverter;
import ds.GraphVisualizationResults;
import ds.z.Project;
import ds.PropertyContainer;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.z.ConcreteAssignment;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphAlgorithmTask extends Algorithm<Project, GraphVisualizationResults> {

	GraphAlgorithm graphAlgorithm;
	NetworkFlowModel networkFlowModel;
	BaseZToGraphConverter conv = new ZToSpanTreeConverter();

	public GraphAlgorithmTask( GraphAlgorithm graphAlgorithm ) {
		this.graphAlgorithm = graphAlgorithm;
	}

	@Override
	protected GraphVisualizationResults runAlgorithm( Project project ) {
		if( networkFlowModel == null ) {
			conv.setProblem( project.getBuildingPlan() );
			conv.run();
			networkFlowModel = conv.getSolution();
		}

		// convert and create the concrete assignment
		ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
		
		GraphAssignmentConverter cav = new GraphAssignmentConverter( networkFlowModel );
		
		cav.setProblem( concreteAssignment );
		cav.run();
		networkFlowModel = cav.getSolution();


		// call the graph algorithm
		int maxTime = (int) PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		Algorithm<NetworkFlowModel, PathBasedFlowOverTime> gt = null;
		gt = graphAlgorithm.createTask( cav.getSolution(), maxTime );
		gt.setProblem( cav.getSolution() );
		gt.run();

		// create graph vis result
		GraphVisualizationResults gvr = new GraphVisualizationResults( cav.getSolution(), gt.getSolution() );
		return gvr;
	}

	public NetworkFlowModel getNetworkFlowModel() {
		return networkFlowModel;
	}

	public void setConv( BaseZToGraphConverter conv ) {
		this.conv = conv;
	}
	
	public void setNetworkFlowModel( NetworkFlowModel networkFlowModel) {
		this.networkFlowModel = networkFlowModel;
	}
	
	
}
