/**
 * GraphAlgorithmTask.java
 * Created: Jul 29, 2010,5:41:36 PM
 */
package tasks;

import batch.GraphAlgorithm;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToNonGridGraphConverter;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.GraphVisualizationResults;
import ds.Project;
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

	public GraphAlgorithmTask( GraphAlgorithm graphAlgorithm ) {
		this.graphAlgorithm = graphAlgorithm;
	}

	@Override
	protected GraphVisualizationResults runAlgorithm( Project project ) {
		// convert the graph
		final ZToNonGridGraphConverter conv = new ZToNonGridGraphConverter();
		conv.setProblem( project.getBuildingPlan() );
		conv.run();

		// convert and create the concrete assignment
		ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
		GraphAssignmentConverter cav = new GraphAssignmentConverter( conv.getSolution() );
		cav.setProblem( concreteAssignment );
		cav.run();
		networkFlowModel = cav.getSolution();


		// call the graph algorithm
		int maxTime = (int) PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		Algorithm<NetworkFlowModel, PathBasedFlowOverTime> gt = null;
		gt = graphAlgorithm.createTask( cav.getSolution(), maxTime );
		gt.setProblem( cav.getSolution() );
		//if (gt instanceof SuccessiveEarliestArrivalAugmentingPathAlgorithmTask3 && listener != null) {
		//    ((SuccessiveEarliestArrivalAugmentingPathAlgorithmTask3) gt).addAlgorithmListener(listener);
		//}
		gt.run();

		// create graph vis result
		GraphVisualizationResults gvr = new GraphVisualizationResults( cav.getSolution(), gt.getSolution() );
		return gvr;
	}

	public NetworkFlowModel getNetworkFlowModel() {
		return networkFlowModel;
	}
}
