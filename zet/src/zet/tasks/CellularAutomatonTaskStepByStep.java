/**
 * CellularAutomatonTaskStepByStep.java
 * Created: Nov 5, 2010, 4:40:09 PM
 */
package zet.tasks;

import algo.ca.algorithm.evac.EvacuationCellularAutomatonAlgorithm;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.AssignmentApplicationInstance;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.CellularAutomatonAssignmentConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ConvertedCellularAutomaton;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.z.Project;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonTaskStepByStep extends Algorithm<Project, Void> {
	private CellularAutomatonAlgorithmEnumeration  cellularAutomatonAlgorithm;
	private boolean performConversion = true;
	private ConvertedCellularAutomaton cca;// = new ConvertedCellularAutomaton( ca, mapping, container );

	private boolean performOneStep;
	private boolean stopMode = false;
	
	public CellularAutomatonTaskStepByStep() {
		super();
	}

	public CellularAutomatonTaskStepByStep( ConvertedCellularAutomaton cca ) {
		this.cca = cca;
		performConversion = false;
	}

	public void setCaAlgo( CellularAutomatonAlgorithmEnumeration caAlgo ) {
		this.cellularAutomatonAlgorithm = caAlgo;
	}

	public CellularAutomatonAlgorithmEnumeration getCellularAutomatonAlgorithm() {
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
			System.out.println( "CCA created" );
		}

		// create and convert concrete assignment
		for( AssignmentType at : project.getCurrentAssignment().getAssignmentTypes() )
			cca.getCellularAutomaton().setAssignmentType( at.getName(), at.getUid() );
		ConcreteAssignment concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
		final CellularAutomatonAssignmentConverter cac = new CellularAutomatonAssignmentConverter();
		cac.setProblem( new AssignmentApplicationInstance( cca, concreteAssignment ) );
		cac.run();

		// set up simulation algorithm and compute
		//EvacuationCellularAutomatonAlgorithm caAlgo = cellularAutomatonAlgorithm.createTask( cca.getCellularAutomaton() );
		EvacuationCellularAutomatonAlgorithm caAlgo = cellularAutomatonAlgorithm.getAlgorithm();
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
			if( !stopMode ) {
				caAlgo.run();
				// fire event
				double progress1 = (double)(cca.getCellularAutomaton().getInitialIndividualCount()-cca.getCellularAutomaton().getNotSafeIndividualsCount())/cca.getCellularAutomaton().getInitialIndividualCount();
				double progress2 = cca.getCellularAutomaton().getTimeStep()/caAlgo.getMaxTimeInSteps();

				this.fireProgressEvent( Math.max( progress2, progress1 ) );
			} else {
				// stop mode. Do nothing except oneStep is true
				if( performOneStep ) {
					caAlgo.run();
					performOneStep = false;
				}
			}
		}
		return null;
	}

	public EvacuationCellularAutomaton getCa() {
		return cca == null ? null : cca.getCellularAutomaton();
	}

	public ZToCARasterContainer getContainer() {
		return cca.getContainer();
	}

	public ZToCAMapping getMapping() {
		return cca.getMapping();
	}

	public void setPerformOneStep( boolean performOneStep ) {
		this.performOneStep = performOneStep;
	}

	public void setStopMode( boolean stopMode ) {
		this.stopMode = stopMode;
	}
}