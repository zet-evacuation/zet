/**
 * BasicOptimization.java
 * Created: 27.03.2014, 16:28:25
 */
package de.tu_berlin.math.coga.batch.operations;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import de.tu_berlin.math.coga.batch.input.reader.InputFileReader;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.GraphVisualizationResults;
import ds.PropertyContainer;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.z.BuildingPlan;
import ds.z.ConcreteAssignment;
import ds.z.Project;
import zet.tasks.GraphAlgorithmEnumeration;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BasicOptimization extends AbstractOperation implements Operation {
	InputFileReader<Project> input;
		AtomicOperation<BuildingPlan, NetworkFlowModel> transformationOperation;
		AtomicOperation<EarliestArrivalFlowProblem, PathBasedFlowOverTime> eafAlgorithm;

	public BasicOptimization() {
		// First, we go from zet to network flow model
		// then, we go from nfm to path based flow

		transformationOperation = new AtomicOperation<>( "Transformation", BuildingPlan.class, NetworkFlowModel.class );

		this.addOperation( transformationOperation );

		eafAlgorithm = new AtomicOperation<>( "Flow Computation", EarliestArrivalFlowProblem.class, PathBasedFlowOverTime.class );

		this.addOperation( eafAlgorithm );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public boolean consume( InputFileReader<?> o ) {

		if( o.getTypeClass() == Project.class ) {
			input = (InputFileReader<Project>)o;
			return true;
		}
		return false;
	}





	@Override
	public String toString() {
		return "Basic Optimization";
	}

	@Override
	public void run() {
		Project project = input.getSolution();

		System.out.println( project );



		if( !project.getBuildingPlan().isRastered() ) {
			System.out.print( "Building is not rasterized. Rastering... " );
			project.getBuildingPlan().rasterize();
			System.out.println( " done." );
		}

		if( transformationOperation.getSelectedAlgorithm() == null ) {
			System.out.println( "No algorithm selected!");
			return;
		}
		System.out.println( "Selected algorithm: " + transformationOperation.getSelectedAlgorithm() );

		//if( true ) return;


		// Convert
		//GraphConverterAlgorithms last = GraphConverterAlgorithms.NonGridGraph;
		final Algorithm<BuildingPlan,NetworkFlowModel> conv = transformationOperation.getSelectedAlgorithm();
		conv.setProblem( project.getBuildingPlan() );
		conv.run();
		NetworkFlowModel networkFlowModel;


		//if( networkFlowModel == null ) {
			conv.setProblem( project.getBuildingPlan() );
			conv.run();
			networkFlowModel = conv.getSolution();
		//}

		// convert and create the concrete assignment
		ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );

		GraphAssignmentConverter cav = new GraphAssignmentConverter( networkFlowModel );

		cav.setProblem( concreteAssignment );
		cav.run();
		networkFlowModel = cav.getSolution();


		// call the graph algorithm
		int maxTime = (int) PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		Algorithm<NetworkFlowModel, PathBasedFlowOverTime> gt;
		GraphAlgorithmEnumeration graphAlgorithm = GraphAlgorithmEnumeration.SuccessiveEarliestArrivalAugmentingPathOptimized;

		gt = graphAlgorithm.createTask( cav.getSolution(), maxTime );
		gt.setProblem( cav.getSolution() );
		//gt.addAlgorithmListener( this );
		gt.run();

		// create graph vis result
		GraphVisualizationResults gvr = new GraphVisualizationResults( cav.getSolution(), gt.getSolution() );
	}


}
