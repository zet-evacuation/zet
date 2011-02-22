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
import ds.z.Project;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonTaskStepByStep extends Algorithm<Project, Void> {
	private CellularAutomatonAlgorithm  cellularAutomatonAlgorithm;
	private boolean performConversion = true;
	private ConvertedCellularAutomaton cca;// = new ConvertedCellularAutomaton( ca, mapping, container );

	public CellularAutomatonTaskStepByStep() {
		super();
	}

	public CellularAutomatonTaskStepByStep( ConvertedCellularAutomaton cca ) {
		this.cca = cca;
		performConversion = false;
	}

	public void setCaAlgo( CellularAutomatonAlgorithm caAlgo ) {
		this.cellularAutomatonAlgorithm = caAlgo;
	}

	public CellularAutomatonAlgorithm getCellularAutomatonAlgorithm() {
		return cellularAutomatonAlgorithm;
	}

	@Override
	protected Void runAlgorithm( Project project ) {
		// convert cellular automaton, if not provided already
		if( performConversion ) {
			final ZToCAConverter conv = new ZToCAConverter();
			conv.setProblem( project.getBuildingPlan() );
			conv.run();
			cca = new ConvertedCellularAutomaton( conv.getCellularAutomaton(), conv.getMapping(), conv.getContainer() );
		}

		// create and convert concrete assignment
		for( AssignmentType at : project.getCurrentAssignment().getAssignmentTypes() )
			cca.getCellularAutomaton().setAssignmentType( at.getName(), at.getUid() );
		ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
		final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
		cac.setProblem( new AssignmentApplicationInstance( cca, concreteAssignment ) );
		cac.run();

		// set up simulation algorithm and compute
		EvacuationCellularAutomatonAlgorithm caAlgo = cellularAutomatonAlgorithm.createTask( cca.getCellularAutomaton() );
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
			double progress1 = 1-(double)cca.getCellularAutomaton().getNotSafeIndividualsCount()/cca.getCellularAutomaton().individualCount();
			double progress2 = cca.getCellularAutomaton().getTimeStep()/caAlgo.getMaxTimeInSteps();
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
		return cca.getCellularAutomaton();
	}

	public ZToCARasterContainer getContainer() {
		return cca.getContainer();
	}

	public ZToCAMapping getMapping() {
		return cca.getMapping();
	}
}
