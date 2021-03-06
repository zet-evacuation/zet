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
package de.tu_berlin.math.coga.batch.operations;

import org.zetool.components.batch.operations.AtomicOperation;
import org.zetool.components.batch.operations.AbstractOperation;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationResult;
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
import org.zet.cellularautomaton.results.VisualResultsRecorder;
import io.visualization.EvacuationSimulationResults;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.InitialConfiguration;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblemImpl;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
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
  MultiFloorEvacuationCellularAutomaton ca;
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
      //ca.setAssignmentType( at.getName(), at.getUid() );
    }
    ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment( project.getCurrentAssignment(), 400 );
    final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
    cac.setProblem( new AssignmentApplicationInstance( cca, concreteAssignment ) );
    cac.run();

    // set up simulation algorithm and compute
    System.out.println( "Performing simulation." );

    VisualResultsRecorder recorder = new VisualResultsRecorder(null, null);
    Algorithm<EvacuationSimulationProblem, EvacuationSimulationResult> caAlgo = caAlgorithm.getSelectedAlgorithm();

    List<Individual> individuals = Collections.emptyList();
    Map<Individual, EvacCellInterface> individualStartPositions = Collections.emptyMap();
    
      InitialConfiguration ic = new InitialConfiguration(ca, individuals, individualStartPositions);
    EvacuationSimulationProblemImpl esp = new EvacuationSimulationProblemImpl(ic);
    double caMaxTime = PropertyContainer.getGlobal().getAsDouble( "algo.ca.maxTime" );
    esp.setEvacuationTimeLimit((int)caMaxTime);
    caAlgo.setProblem( esp );
    //ca.startRecording();
    //VisualResultsRecorder recorder = new VisualResultsRecorder(esp.getInitialConfiguration(), caAlgo);

    caAlgo.run();
    //ca.stopRecording();

    System.out.println( "Recording stopped." );

        EvacuationState es = null; //caAlgo.getSolution().getEvacuationState();
        EvacuationSimulationSpeed ess = null; //caAlgo.getEvacuationSimulationSpeed();
    visResults = new EvacuationSimulationResults(es, ess, recorder.getRecording());
  }

  @Override
  public String toString() {
    return "Basic Simulation (CA)";
  }
}
