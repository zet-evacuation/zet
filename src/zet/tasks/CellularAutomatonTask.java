/**
 * CellularAutomatonTask.java
 * Created 29.07.2010 12:12:03
 */
package zet.tasks;

import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import algo.ca.framework.EvacuationCellularAutomatonAlgorithm;
import org.zetool.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.results.VisualResultsRecorder;
import de.zet_evakuierung.model.AssignmentType;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Project;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import io.visualization.EvacuationSimulationResults;
import statistic.ca.CAStatistic;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonTask extends Algorithm<Project, EvacuationSimulationResults> {
	EvacuationCellularAutomatonAlgorithm cellularAutomatonAlgorithm;
	EvacuationCellularAutomaton ca;
	ZToCAMapping mapping;
	ZToCARasterContainer container;

	public void setCaAlgo( EvacuationCellularAutomatonAlgorithm caAlgo ) {
		this.cellularAutomatonAlgorithm = caAlgo;
	}

	public EvacuationCellularAutomatonAlgorithm getCellularAutomatonAlgorithm() {
		return cellularAutomatonAlgorithm;
	}

	@Override
	protected EvacuationSimulationResults runAlgorithm( Project project ) {
		// convert cellular automaton
		final ZToCAConverter conv = new ZToCAConverter();
		conv.setProblem( project.getBuildingPlan() );
		conv.run();
		ca = conv.getCellularAutomaton();
		mapping = conv.getMapping();
		container = conv.getContainer();
		final ConvertedCellularAutomaton cca = new ConvertedCellularAutomaton( ca, mapping, container );

		// create and convert concrete assignment
		for( AssignmentType at : project.getCurrentAssignment().getAssignmentTypes() )
			ca.setAssignmentType( at.getName(), at.getUid() );
		ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment( project.getCurrentAssignment(), 400 );
		final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
		cac.setProblem( new AssignmentApplicationInstance( cca, concreteAssignment ) );
		cac.run();

		// set up simulation algorithm and compute
		EvacuationCellularAutomatonAlgorithm caAlgo = cellularAutomatonAlgorithm;
		caAlgo.setProblem( new EvacuationSimulationProblem( ( ca ) ) );
		double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		caAlgo.setMaxTimeInSeconds( caMaxTime );
		ca.startRecording ();

		caAlgo.run();
		ca.stopRecording();
    
		EvacuationSimulationResults visResults = new EvacuationSimulationResults( VisualResultsRecorder.getInstance().getRecording(), mapping, ca );
    
    visResults.statistic = new CAStatistic (caAlgo.getProblem().caStatisticWriter.getStoredCAStatisticResults ());
    
		return visResults;
	}

	public EvacuationCellularAutomaton getCa() {
		return ca;
	}

	public ZToCARasterContainer getContainer() {
		return container;
	}

	public ZToCAMapping getMapping() {
		return mapping;
	}
}
