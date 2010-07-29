/**
 * AlgorithmControl.java
 * Created: 27.07.2010 14:49:25
 */
package gui;

import algo.ca.EvacuationCellularAutomatonAlgorithm;
import batch.CellularAutomatonAlgorithm;
import converter.cellularAutomaton.AssignmentApplicationInstance;
import converter.cellularAutomaton.ConcreteAssignmentConverter;
import converter.cellularAutomaton.ConvertedCellularAutomaton;
import converter.cellularAutomaton.ZToCAConverter;
import converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException;
import converter.cellularAutomaton.ZToCAMapping;
import converter.cellularAutomaton.ZToCARasterContainer;
import ds.Project;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.z.AssignmentType;
import ds.z.ConcreteAssignment;
import io.visualization.BuildingResults;
import io.visualization.CAVisualizationResults;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import tasks.CellularAutomatonTask;
import tasks.SerialTask;
import tasks.conversion.BuildingPlanConverter;


/**
 * A class that starts, stops and pauses the algorithms that can be used in
 * zet.
 * @author Jan-Philipp Kappmeier
 */
public class AlgorithmControl implements PropertyChangeListener {

	BuildingResults buildingResults;
	Project project;
	CellularAutomaton cellularAutomaton;
	ConcreteAssignment concreteAssignment;
	EvacuationCellularAutomatonAlgorithm caAlgo;
	ZToCAMapping mapping;
	ZToCARasterContainer container;
	CAVisualizationResults caVisResults;


	public AlgorithmControl( Project project ) {
		this.project = project;
	}

	void setProject( Project project ) {
		this.project = project;
	}

	public void convertBuildingPlan( ) {
		convertBuildingPlan( null );
	}
	
	public void convertBuildingPlan( PropertyChangeListener pcl ) {
		final BuildingPlanConverter bpc = new BuildingPlanConverter();
		bpc.setProblem( project.getBuildingPlan() );

		final SerialTask st = new SerialTask();
		st.add( bpc );
		st.addPropertyChangeListener( new PropertyChangeListener() {

			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() )
					buildingResults = bpc.getSolution();
			}
		});
		if( pcl != null)
			st.addPropertyChangeListener( pcl );
		st.execute();
	}

	public BuildingResults getBuildingResults() {
		return buildingResults;
	}

	public void convertCellularAutomaton( ) {
		convertCellularAutomaton( null );
	}

	void convertCellularAutomaton( PropertyChangeListener propertyChangeListener ) {
		final ZToCAConverter conv = new ZToCAConverter();
		conv.setProblem( project.getBuildingPlan() );

		final SerialTask st = new SerialTask( conv );
		st.addPropertyChangeListener( new PropertyChangeListener() {

			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					cellularAutomaton = conv.getCellularAutomaton();
					mapping = conv.getMapping();
					container = conv.getContainer();
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
		st.execute();
	}

	public CellularAutomaton getCellularAutomaton() {
		return cellularAutomaton;
	}

	public ZToCARasterContainer getContainer() {
		return container;
	}

	public ZToCAMapping getMapping() {
		return mapping;
	}

	void performSimulation() {
		performSimulation( null );
	}

	void performSimulation( PropertyChangeListener propertyChangeListener ) {
		final CellularAutomatonTask cat = new CellularAutomatonTask();
		cat.setCaAlgo( CellularAutomatonAlgorithm.RandomOrder );
		cat.setProblem( project );

		final SerialTask st = new SerialTask( cat );
		st.addPropertyChangeListener( new PropertyChangeListener() {

			public void propertyChange( PropertyChangeEvent pce ) {
				if( st.isDone() ) {
					cellularAutomaton = cat.getCa();
					mapping = cat.getMapping();
					container = cat.getContainer();
					caVisResults = cat.getSolution();
				}
			}
		});
		if( propertyChangeListener != null )
			st.addPropertyChangeListener( propertyChangeListener );
		st.execute();
	}

	public CAVisualizationResults getCaVisResults() {
		return caVisResults;
	}

	void createConcreteAssignment() throws IllegalArgumentException, ConversionNotSupportedException {
		for( AssignmentType at : project.getCurrentAssignment().getAssignmentTypes() )
			cellularAutomaton.setAssignmentType( at.getName(), at.getUid() );
		concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
		final ConcreteAssignmentConverter cac = new ConcreteAssignmentConverter();
		cac.setProblem( new AssignmentApplicationInstance( new ConvertedCellularAutomaton( cellularAutomaton, mapping, container ), concreteAssignment ) );
		cac.run();
	}

	void setUpSimulationAlgorithm() {
		CellularAutomatonAlgorithm cellularAutomatonAlgo = CellularAutomatonAlgorithm.RandomOrder;
		caAlgo = cellularAutomatonAlgo.createTask( cellularAutomaton );
		double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		caAlgo.setMaxTimeInSeconds( caMaxTime );
	}

	boolean caInitialized = false;

	boolean performOneStep() throws ConversionNotSupportedException {
		if( !caInitialized ) {
			final ZToCAConverter conv = new ZToCAConverter( project );
			conv.run();
			cellularAutomaton = conv.getCellularAutomaton();
			mapping = conv.getMapping();
			container = conv.getContainer();
			createConcreteAssignment();
			setUpSimulationAlgorithm();
			caAlgo.setStepByStep( true );
			caAlgo.initialize();
			caInitialized = true;
			return true;
		} else {
			caAlgo.run();
			if( caAlgo.isFinished() )
				caInitialized = false;
			return false;
		}
	}


	public void propertyChange( PropertyChangeEvent pce ) {
		System.out.println( pce.getPropertyName() );
	}
}
