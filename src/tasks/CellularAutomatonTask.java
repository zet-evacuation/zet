/**
 * CellularAutomatonTask.java
 * Created 29.07.2010 12:12:03
 */
package tasks;

import algo.ca.EvacuationCellularAutomatonAlgorithm;
import batch.CellularAutomatonAlgorithm;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.Project;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.ca.results.VisualResultsRecorder;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;
import io.visualization.CAVisualizationResults;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonTask extends Algorithm<Project, CAVisualizationResults> {
	CellularAutomatonAlgorithm  cellularAutomatonAlgorithm;
	CellularAutomaton ca;
	ZToCAMapping mapping;
	ZToCARasterContainer container;

	public void setCaAlgo( CellularAutomatonAlgorithm caAlgo ) {
		this.cellularAutomatonAlgorithm = caAlgo;
	}

	@Override
	protected CAVisualizationResults runAlgorithm( Project project ) {
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
		ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
		final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
		cac.setProblem( new AssignmentApplicationInstance( cca, concreteAssignment ) );
		cac.run();

		// set up simulation algorithm and compute
		EvacuationCellularAutomatonAlgorithm caAlgo = cellularAutomatonAlgorithm.createTask( ca );
		double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		caAlgo.setMaxTimeInSeconds( caMaxTime );
		caAlgo.getCellularAutomaton().startRecording ();
		caAlgo.run();	// hier wird initialisiert
		caAlgo.getCellularAutomaton().stopRecording();

		// create results
		//CAVisualizationResults visResults = new CAVisualizationResults( mapping, ca.getPotentialManager() );
		// TODO visualResultsRecorder normal class, no singleton.
		CAVisualizationResults visResults = new CAVisualizationResults( VisualResultsRecorder.getInstance().getRecording(), mapping );

		return visResults;
	}

	public CellularAutomaton getCa() {
		return ca;
	}

	public ZToCARasterContainer getContainer() {
		return container;
	}

	public ZToCAMapping getMapping() {
		return mapping;
	}
}
