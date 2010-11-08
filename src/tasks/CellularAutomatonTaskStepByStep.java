/**
 * CellularAutomatonTaskStepByStep.java
 * Created: Nov 5, 2010, 4:40:09 PM
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
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonTaskStepByStep extends Algorithm<Project, Void> {
	CellularAutomatonAlgorithm  cellularAutomatonAlgorithm;
	CellularAutomaton ca;
	ZToCAMapping mapping;
	ZToCARasterContainer container;

	public void setCaAlgo( CellularAutomatonAlgorithm caAlgo ) {
		this.cellularAutomatonAlgorithm = caAlgo;
	}

	public CellularAutomatonAlgorithm getCellularAutomatonAlgorithm() {
		return cellularAutomatonAlgorithm;
	}

	@Override
	protected Void runAlgorithm( Project project ) {
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
		//caAlgo.getCellularAutomaton().startRecording ();

		caAlgo.setStepByStep( true );
		caAlgo.initialize();
		while( !caAlgo.isFinished() ) {
			try {
				Thread.sleep( 500 );
			} catch( InterruptedException ignore ) {
			}
			caAlgo.run();
			// fire event
			double progress1 = 1-(double)ca.getNotSafeIndividualsCount()/ca.individualCount();
			double progress2 = ca.getTimeStep()/caAlgo.getMaxTimeInSteps();
			this.fireProgressEvent( Math.max( progress2, progress1 ) );
		}

		//caAlgo.run();	// hier wird initialisiert
		//caAlgo.getCellularAutomaton().stopRecording();

		// create results
		//CAVisualizationResults visResults = new CAVisualizationResults( mapping, ca.getPotentialManager() );
		// TODO visualResultsRecorder normal class, no singleton.
		//CAVisualizationResults visResults = new CAVisualizationResults( VisualResultsRecorder.getInstance().getRecording(), mapping );

		//return visResults;
		return null;
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
