
package de.tu_berlin.math.coga.batch.operations;

import org.zetool.components.batch.operations.AtomicOperation;
import org.zetool.components.batch.operations.AbstractOperation;
import algo.ca.algorithm.evac.EvacuationCellularAutomatonRandom;
import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import algo.ca.algorithm.evac.EvacuationSimulationResult;
import algo.ca.algorithm.evac.SwapCellularAutomaton;
import algo.ca.framework.EvacuationCellularAutomatonAlgorithm;
import algo.graph.exitassignment.ExitAssignment;
import org.zetool.common.algorithm.Algorithm;
import de.tu_berlin.coga.zet.model.AssignmentType;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;
import de.tu_berlin.coga.zet.model.Project;
import org.zetool.components.batch.input.reader.InputFileReader;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.RectangleConverter;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.results.VisualResultsRecorder;
import evacuationplan.BestResponseDynamics;
import evacuationplan.BidirectionalNodeCellMapping;
import exitdistributions.GraphBasedExitToCapacityMapping;
import exitdistributions.GraphBasedIndividualToExitMapping;
import io.visualization.EvacuationSimulationResults;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BestResponseOperation extends AbstractOperation<Project, EvacuationSimulationResults> {
	InputFileReader<Project> input;
  EvacuationSimulationResults visResults;

  // Currently unused, we use default converter and algorithms
	AtomicOperation<NetworkFlowModel, ExitAssignment> assignmentOperation;

  public BestResponseOperation() {
//  	assignmentOperation = new AtomicOperation<>( "Assignment computation", NetworkFlowModel.class, ExitAssignment.class );
//		this.addOperation( assignmentOperation );
}


  @Override
  public boolean consume( InputFileReader<?> o ) {
		if( o.getTypeClass() == Project.class ) {
			input = (InputFileReader<Project>)o;
			return true;
		}
		return false;
  }

  @Override
  public Class<EvacuationSimulationResults> produces() {
    return EvacuationSimulationResults.class;
  }

  @Override
  public EvacuationSimulationResults getProduced() {
    return visResults;
  }

  @Override
  public void run() {
		Project project = input.getSolution();
    //todo
    // step 1: compute an exit assignment
    // here we use plugins to select type of exit assignment

    final Algorithm<BuildingPlan,NetworkFlowModel> conv = new RectangleConverter();
    conv.setProblem( project.getBuildingPlan() );
		conv.run();
    NetworkFlowModel networkFlowModel = conv.getSolution();

		// convert and create the concrete assignment
		ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );

    GraphAssignmentConverter cav = new GraphAssignmentConverter( networkFlowModel );
		cav.setProblem( concreteAssignment );
		cav.run();
		networkFlowModel = cav.getSolution();

    // We do not need the actual assignment, network flow model is enough!
//    if( assignmentOperation.getSelectedAlgorithm() == null ) {
//			System.out.println( "No algorithm selected!");
//			return;
//		}
//    final Algorithm<NetworkFlowModel,ExitAssignment> assignmentAlgorithm = assignmentOperation.getSelectedAlgorithm();
//    System.out.println( "Selected algorithm: " + assignmentAlgorithm );
//
//    assignmentAlgorithm.setProblem( networkFlowModel );
//    assignmentAlgorithm.run();
//    ExitAssignment exitAssignment = assignmentAlgorithm.getSolution();
//
//    System.out.println( "Exit Assignment: " );
//    System.out.println( exitAssignment );

    // step 2: put the exit assingment into a cellular automaton

    // We now have to create a cellular automaton, assign the concrete assignment and have to map the exits to the
    // chosen exits in our above exit computation
    EvacuationCellularAutomaton ca;
    ZToCAMapping mapping;
    ZToCARasterContainer container;

    final ZToCAConverter caConv = new ZToCAConverter();
    //final ExitDistributionZToCAConverter caConv = new ExitDistributionZToCAConverter();
    caConv.setProblem( project.getBuildingPlan() );
    caConv.run();

    ca = caConv.getCellularAutomaton();

    System.out.println( "The converted CA:\n" + ca );

    mapping = caConv.getMapping();
    container = caConv.getContainer();
    ConvertedCellularAutomaton cca = new ConvertedCellularAutomaton( ca, mapping, container );

    // here we have to assign the concrete assignment
    // create and convert concrete assignment
    System.out.println( "Creating concrete Assignment." );
    for( AssignmentType at : project.getCurrentAssignment().getAssignmentTypes() ) {
      ca.setAssignmentType( at.getName(), at.getUid() );
    }

    //ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
    final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
    cac.setProblem( new AssignmentApplicationInstance( cca, concreteAssignment ) );
    cac.run();

    ZToGraphRasterContainer graphRaster = networkFlowModel.getZToGraphMapping().getRaster();

//		EvacuationCellularAutomaton ca = super.convert( buildingPlan );
		BidirectionalNodeCellMapping.CAPartOfMapping caPartOfMapping = caConv.getLatestCAPartOfNodeCellMapping();

//		applyConcreteAssignment( concreteAssignment );
//		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping( graphRaster, caPartOfMapping );
		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping( graphRaster, caPartOfMapping );

    GraphBasedExitToCapacityMapping graphBasedExitToCapacityMapping = new GraphBasedExitToCapacityMapping( ca, nodeCellMapping, networkFlowModel );
		graphBasedExitToCapacityMapping.calculate();
		ca.setExitToCapacityMapping( graphBasedExitToCapacityMapping.getExitCapacity() );
//		return ca;



    cca = cac.getSolution();

//		BidirectionalNodeCellMapping.CAPartOfMapping caPartOfMapping = caConv.getLatestCAPartOfNodeCellMapping();
//		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);
//    GraphBasedIndividualToExitMapping graphBasedIndividualToExitMaping;
//    graphBasedIndividualToExitMaping = new GraphBasedIndividualToExitMapping(
//            ca, nodeCellMapping, exitAssignment );
//    graphBasedIndividualToExitMaping.calculate();
//    System.out.println( "Graph based individual to exit mapping:" );
//    System.out.println( graphBasedIndividualToExitMaping );
//		ca.setIndividualToExitMapping( graphBasedIndividualToExitMaping );


    // step 3: simulate
    //EvacuationCellularAutomatonAlgorithm algo = new EvacuationCellularAutomatonRandom();
    Algorithm<EvacuationSimulationProblem, EvacuationSimulationResult> caAlgo = new EvacuationCellularAutomatonRandom();
    //Algorithm<EvacuationSimulationProblem,EvacuationSimulationResult> selected = caAlgorithm.getSelectedAlgorithm();
    //selected = cellularAutomatonAlgorithm;

    SwapCellularAutomaton swapAlgo = new SwapCellularAutomaton();

    caAlgo = swapAlgo;

    caAlgo.setProblem( new EvacuationSimulationProblem( (ca) ) );
    if( caAlgo instanceof EvacuationCellularAutomatonAlgorithm ) {
      double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
      ((EvacuationCellularAutomatonAlgorithm)caAlgo).setMaxTimeInSeconds( caMaxTime );
    }
    ca.startRecording();

    // The best response part
    //caAlgo.initialize();

    // perform initial best response dynamics exit selection
    BestResponseDynamics brd = new BestResponseDynamics();
    brd.computeAssignmentBasedOnBestResponseDynamics( ca, ca.getIndividuals() );
    System.out.println( "Best Responses: " );
    System.out.println( brd );

    caAlgo.run();
    ca.stopRecording();

    System.out.println( "Recording stopped." );

    visResults = new EvacuationSimulationResults( VisualResultsRecorder.getInstance().getRecording(), mapping, ca );
  }

  @Override
  public String toString() {
    return "Exit Assignment";
  }
}
