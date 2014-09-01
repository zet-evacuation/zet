
package de.tu_berlin.math.coga.batch.operations;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonRandom;
import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import algo.ca.algorithm.evac.EvacuationSimulationResult;
import algo.ca.framework.EvacuationCellularAutomatonAlgorithm;
import algo.graph.exitassignment.Assignable;
import algo.graph.exitassignment.ShortestPathExitAssignment;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.netflow.ds.flow.PathBasedFlowOverTime;
import de.tu_berlin.coga.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import de.tu_berlin.coga.zet.model.AssignmentType;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;
import de.tu_berlin.coga.zet.model.Project;
import de.tu_berlin.math.coga.batch.input.reader.InputFileReader;
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
import evacuationplan.BidirectionalNodeCellMapping;
import exitdistributions.GraphBasedIndividualToExitMapping;
import io.visualization.CAVisualizationResults;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ExitAssignment extends AbstractOperation<Project,CAVisualizationResults> {
	InputFileReader<Project> input;
  CAVisualizationResults visResults;

  // Currently unused, we use default converter and algorithms
  AtomicOperation<BuildingPlan, NetworkFlowModel> transformationOperation;
	AtomicOperation<EarliestArrivalFlowProblem, PathBasedFlowOverTime> eafAlgorithm;

  @Override
  public boolean consume( InputFileReader<?> o ) {
		if( o.getTypeClass() == Project.class ) {
			input = (InputFileReader<Project>)o;
			return true;
		}
		return false;
  }

  @Override
  public Class<CAVisualizationResults> produces() {
    return CAVisualizationResults.class;
  }

  @Override
  public CAVisualizationResults getProduced() {
    return visResults;
  }

  @Override
  public void run() {
		Project project = input.getSolution();
    //todo
    // step 1: compute an exit assignment
    // here we use plugins to select type of exit assignment


		ShortestPathExitAssignment spExitAssignment = new ShortestPathExitAssignment ();

		final Algorithm<BuildingPlan,NetworkFlowModel> conv = new RectangleConverter();
		conv.setProblem( project.getBuildingPlan() );
		conv.run();
    //conv.setProblem( project.getBuildingPlan() );
    //conv.run();
    NetworkFlowModel networkFlowModel = conv.getSolution();

		// convert and create the concrete assignment
		ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
		GraphAssignmentConverter cav = new GraphAssignmentConverter( networkFlowModel );
		cav.setProblem( concreteAssignment );
		cav.run();
		networkFlowModel = cav.getSolution();




		//ZToGraphConverter.convertConcreteAssignment( concreteAssignments[runNumber], res.getNetworkFlowModel() );
		spExitAssignment.setProblem( networkFlowModel );
		spExitAssignment.run();
		Assignable exitAssignment = spExitAssignment;

    System.out.println( "Exit Assignment: " );

    System.out.println( exitAssignment.getExitAssignment() );



    
    
// step 2: put the exit assingment into a cellular automaton

    // We now have to create a cellular automaton, assign the concrete assignment and have to map the exits to the
    // chosen exits in our above exit computation
    EvacuationCellularAutomaton ca;
    ZToCAMapping mapping;
    ZToCARasterContainer container;

    final ZToCAConverter caConv = new ZToCAConverter();
    //final ExitDistributionZToCAConverter caConv2 = new ExitDistributionZToCAConverter();
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
    
    cca = cac.getSolution();
    
		BidirectionalNodeCellMapping.CAPartOfMapping caPartOfMapping = caConv.getLatestCAPartOfNodeCellMapping();
    
    
		BidirectionalNodeCellMapping nodeCellMapping = new BidirectionalNodeCellMapping(graphRaster, caPartOfMapping);
		GraphBasedIndividualToExitMapping graphBasedIndividualToExitMaping;
    graphBasedIndividualToExitMaping = new GraphBasedIndividualToExitMapping(ca, nodeCellMapping, exitAssignment.getExitAssignment() );
		graphBasedIndividualToExitMaping.calculate();
		ca.setIndividualToExitMapping( graphBasedIndividualToExitMaping );
    
    
    // step 3: simulate
    //EvacuationCellularAutomatonAlgorithm algo = new EvacuationCellularAutomatonRandom();
    Algorithm<EvacuationSimulationProblem, EvacuationSimulationResult> caAlgo = new EvacuationCellularAutomatonRandom();
    //Algorithm<EvacuationSimulationProblem,EvacuationSimulationResult> selected = caAlgorithm.getSelectedAlgorithm();
    //selected = cellularAutomatonAlgorithm;

    caAlgo.setProblem( new EvacuationSimulationProblem( (ca) ) );
    if( caAlgo instanceof EvacuationCellularAutomatonAlgorithm ) {
      double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
      ((EvacuationCellularAutomatonAlgorithm)caAlgo).setMaxTimeInSeconds( caMaxTime );
    }
    ca.startRecording();

    caAlgo.run();
    ca.stopRecording();

    System.out.println( "Recording stopped." );

    visResults = new CAVisualizationResults( VisualResultsRecorder.getInstance().getRecording(), mapping );    
  }

  @Override
  public String toString() {
    return "Exit Assignment";
  }

}
