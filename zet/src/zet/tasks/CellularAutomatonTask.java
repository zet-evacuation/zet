/**
 * CellularAutomatonTask.java
 * Created 29.07.2010 12:12:03
 */
package zet.tasks;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonAlgorithm;
import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmEvent;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.algorithm.AlgorithmListener;
import ds.z.Project;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.results.VisualResultsRecorder;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;
import io.visualization.CAVisualizationResults;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonTask extends Algorithm<Project, CAVisualizationResults> implements AlgorithmListener {
	CellularAutomatonAlgorithmEnumeration  cellularAutomatonAlgorithm;
	EvacuationCellularAutomaton ca;
	ZToCAMapping mapping;
	ZToCARasterContainer container;

	public void setCaAlgo( CellularAutomatonAlgorithmEnumeration caAlgo ) {
		this.cellularAutomatonAlgorithm = caAlgo;
	}

	public CellularAutomatonAlgorithmEnumeration getCellularAutomatonAlgorithm() {
		return cellularAutomatonAlgorithm;
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
		//EvacuationCellularAutomatonAlgorithm caAlgo = cellularAutomatonAlgorithm.createTask( ca );
		EvacuationCellularAutomatonAlgorithm caAlgo = cellularAutomatonAlgorithm.getAlgorithm();
		caAlgo.setProblem( new EvacuationSimulationProblem( ( ca ) ) );
		double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		caAlgo.setMaxTimeInSeconds( caMaxTime );
		caAlgo.getCellularAutomaton().startRecording ();
		
		caAlgo.addAlgorithmListener( this );
		
		caAlgo.run();	// hier wird initialisiert
		caAlgo.getCellularAutomaton().stopRecording();

		// create results
		//CAVisualizationResults visResults = new CAVisualizationResults( mapping, ca.getPotentialManager() );
		// TODO visualResultsRecorder normal class, no singleton.
		CAVisualizationResults visResults = new CAVisualizationResults( VisualResultsRecorder.getInstance().getRecording(), mapping );

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

	@Override
	public void eventOccurred( AlgorithmEvent event ) {
		//throw new UnsupportedOperationException( "Not supported yet." );
	}
}
