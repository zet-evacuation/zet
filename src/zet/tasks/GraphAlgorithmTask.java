/**
 * GraphAlgorithmTask.java
 * Created: Jul 29, 2010,5:41:36 PM
 */
package zet.tasks;

import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.algorithm.AlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.graph.RectangleConverter;
import ds.GraphVisualizationResults;
import ds.PropertyContainer;
import org.zetool.netflow.ds.flow.PathBasedFlowOverTime;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;
import de.tu_berlin.coga.zet.model.Project;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphAlgorithmTask extends Algorithm<Project, GraphVisualizationResults> implements AlgorithmListener {

	GraphAlgorithmEnumeration graphAlgorithm;
	NetworkFlowModel networkFlowModel;
	Algorithm<BuildingPlan,NetworkFlowModel> conv = new RectangleConverter();

	public GraphAlgorithmTask( GraphAlgorithmEnumeration graphAlgorithm ) {
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
		ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment( project.getCurrentAssignment(), 400 );
		
		GraphAssignmentConverter cav = new GraphAssignmentConverter( networkFlowModel );
		
		cav.setProblem( concreteAssignment );
		cav.run();
		networkFlowModel = cav.getSolution();


		// call the graph algorithm
		int maxTime = (int) PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		Algorithm<NetworkFlowModel, PathBasedFlowOverTime> gt;
		gt = graphAlgorithm.createTask( cav.getSolution(), maxTime );
		gt.setProblem( cav.getSolution() );
		gt.addAlgorithmListener( this );
		gt.run();

		// create graph vis result
		GraphVisualizationResults gvr = new GraphVisualizationResults( cav.getSolution(), gt.getSolution() );
		return gvr;
	}

	public NetworkFlowModel getNetworkFlowModel() {
		return networkFlowModel;
	}

	public void setConv( Algorithm<BuildingPlan,NetworkFlowModel> conv ) {
		this.conv = conv;
	}
	
	public void setNetworkFlowModel( NetworkFlowModel networkFlowModel) {
		this.networkFlowModel = networkFlowModel;
	}

	@Override
	public void eventOccurred( AlgorithmEvent event ) {

	}
}
