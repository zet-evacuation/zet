/**
 * CellularAutomatonTask.java
 * Created 29.07.2010 12:12:03
 */
package tasks;

import algo.ca.EvacuationCellularAutomatonAlgorithm;
import batch.CellularAutomatonAlgorithm;
import converter.cellularAutomaton.AssignmentApplicationInstance;
import converter.cellularAutomaton.ConcreteAssignmentConverter;
import converter.cellularAutomaton.ConvertedCellularAutomaton;
import converter.cellularAutomaton.ZToCAConverter;
import converter.cellularAutomaton.ZToCAMapping;
import converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.Project;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
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
		final ConcreteAssignmentConverter cac = new ConcreteAssignmentConverter();
		cac.setProblem( new AssignmentApplicationInstance( cca, concreteAssignment ) );
		cac.run();

		// set up simulation algorithm and compute
		EvacuationCellularAutomatonAlgorithm caAlgo = cellularAutomatonAlgorithm.createTask( ca );
		double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		caAlgo.setMaxTimeInSeconds( caMaxTime );
		caAlgo.run();	// hier wird initialisiert

		// create results
		CAVisualizationResults visResults = new CAVisualizationResults( mapping, ca.getPotentialManager() );
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
