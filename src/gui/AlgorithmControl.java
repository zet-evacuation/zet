/**
 * AlgorithmControl.java
 * Created: 27.07.2010 14:49:25
 */
package gui;

import algo.ca.EvacuationCellularAutomatonAlgorithm;
import batch.CellularAutomatonAlgorithm;
import converter.ZToCAConverter;
import converter.ZToCAConverter.ConversionNotSupportedException;
import ds.Project;
import ds.PropertyContainer;
import ds.ca.CellularAutomaton;
import ds.z.ConcreteAssignment;
import io.visualization.BuildingResults;


/**
 * A class that starts, stops and pauses the algorithms that can be used in
 * zet.
 * @author Jan-Philipp Kappmeier
 */
public class AlgorithmControl {

	BuildingResults buildingResults;
	Project project;
	CellularAutomaton cellularAutomaton;
	ConcreteAssignment concreteAssignment;
	EvacuationCellularAutomatonAlgorithm caAlgo;

	public AlgorithmControl( Project project ) {
		this.project = project;
	}

	void setProject( Project project ) {
		this.project = project;
	}

	public void convertBuildingPlan( ) {
		buildingResults = new BuildingResults( project.getBuildingPlan() );
	}

	public BuildingResults getBuildingResults() {
		return buildingResults;
	}

	public void convertCellularAutomaton( ) throws ConversionNotSupportedException {
		cellularAutomaton = ZToCAConverter.getInstance().convert( project.getBuildingPlan() );
	}

	public CellularAutomaton getCellularAutomaton() {
		return cellularAutomaton;
	}

	void createConcreteAssignment() throws IllegalArgumentException, ConversionNotSupportedException {
		concreteAssignment = project.getCurrentAssignment().createConcreteAssignment( 400 );
		ZToCAConverter.applyConcreteAssignment( concreteAssignment );
	}

	void simulate() {

//		long start;
//		long end;

		//Run the CA
//		start = System.currentTimeMillis();
//		caAlgo.getCellularAutomaton().startRecording();
		caAlgo.run();	// hier wird initialisiert
//		caAlgo.getCellularAutomaton().stopRecording();
//		end = System.currentTimeMillis();
		System.out.println( "Lauf fertig." );
	}

	void setUpSimulationAlgorithm() {
		CellularAutomatonAlgorithm cellularAutomatonAlgo = CellularAutomatonAlgorithm.RandomOrder;
		caAlgo = cellularAutomatonAlgo.createTask( cellularAutomaton );
		double caMaxTime = PropertyContainer.getInstance().getAsDouble( "algo.ca.maxTime" );
		caAlgo.setMaxTimeInSeconds( caMaxTime );
	}

	void performOneStep() {
		caAlgo.setStepByStep( true );
		caAlgo.run();
	}

	public EvacuationCellularAutomatonAlgorithm getCaAlgo() {
		return caAlgo;
	}





}
