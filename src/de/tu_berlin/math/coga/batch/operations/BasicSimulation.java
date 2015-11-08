package de.tu_berlin.math.coga.batch.operations;

import org.zetool.components.batch.operations.AtomicOperation;
import org.zetool.components.batch.operations.AbstractOperation;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationResult;
import org.zet.cellularautomaton.algorithm.EvacuationCellularAutomatonAlgorithm;
import de.zet_evakuierung.model.AssignmentType;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Project;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import org.zetool.components.batch.input.reader.InputFileReader;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import ds.PropertyContainer;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.results.VisualResultsRecorder;
import io.visualization.EvacuationSimulationResults;
import java.util.Arrays;
import java.util.List;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblemImpl;
import org.zetool.common.algorithm.Algorithm;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BasicSimulation extends AbstractOperation<Project, EvacuationSimulationResults> {
  InputFileReader<Project> input;
  Project project;

  AtomicOperation<EvacuationSimulationProblem, EvacuationSimulationResult> caAlgorithm;
  EvacuationSimulationResults visResults;

  //EvacuationCellularAutomatonAlgorithm cellularAutomatonAlgorithm;
  EvacuationCellularAutomaton ca;
  ZToCAMapping mapping;
  ZToCARasterContainer container;

  public BasicSimulation() {
		// First, we go from zet to network flow model
    // then, we go from nfm to path based flow

    caAlgorithm = new AtomicOperation<>( "Cellular Automaton", EvacuationSimulationProblem.class, EvacuationSimulationResult.class );
    this.addOperation( caAlgorithm );
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
  public Class<EvacuationSimulationResults> produces() {
    return EvacuationSimulationResults.class;
  }

  @Override
  public List<Class<?>> getProducts() {
    return Arrays.asList( new Class<?>[] {produces(), BuildingPlan.class} );
  }

  @Override
  public Object getProduct( Class<?> productType ) {
    if( productType == BuildingPlan.class ) {
      return project.getBuildingPlan();
    } else {
      return super.getProduct( productType );
    }
  }
  
  
  @Override
  public EvacuationSimulationResults getProduced() {
    return visResults;
  }

  @Override
  public void run() {
    project = input.getSolution();

    System.out.println( project );

    final ZToCAConverter conv = new ZToCAConverter();
    conv.setProblem( project.getBuildingPlan() );
    conv.run();

    ca = conv.getCellularAutomaton();

    System.out.println( "The converted CA:\n" + ca );

    mapping = conv.getMapping();
    container = conv.getContainer();
    final ConvertedCellularAutomaton cca = new ConvertedCellularAutomaton( ca, mapping, container );

    // create and convert concrete assignment
    System.out.println( "Creating concrete Assignment." );
    for( AssignmentType at : project.getCurrentAssignment().getAssignmentTypes() ) {
      ca.setAssignmentType( at.getName(), at.getUid() );
    }
    ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment( project.getCurrentAssignment(), 400 );
    final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
    cac.setProblem( new AssignmentApplicationInstance( cca, concreteAssignment ) );
    cac.run();

    // set up simulation algorithm and compute
    System.out.println( "Performing simulation." );

    Algorithm<EvacuationSimulationProblem, EvacuationSimulationResult> caAlgo = caAlgorithm.getSelectedAlgorithm();
    //Algorithm<EvacuationSimulationProblem,EvacuationSimulationResult> selected = caAlgorithm.getSelectedAlgorithm();
    //selected = cellularAutomatonAlgorithm;

    caAlgo.setProblem( new EvacuationSimulationProblemImpl( (ca) ) );
    if( caAlgo instanceof EvacuationCellularAutomatonAlgorithm ) {
      double caMaxTime = PropertyContainer.getGlobal().getAsDouble( "algo.ca.maxTime" );
      ((EvacuationCellularAutomatonAlgorithm)caAlgo).setMaxTimeInSeconds( caMaxTime );
    }
    ca.startRecording();

    caAlgo.run();
    ca.stopRecording();

    System.out.println( "Recording stopped." );

    visResults = new EvacuationSimulationResults( VisualResultsRecorder.getInstance().getRecording(), mapping, ca );
  }

  @Override
  public String toString() {
    return "Basic Simulation (CA)";
  }
}
