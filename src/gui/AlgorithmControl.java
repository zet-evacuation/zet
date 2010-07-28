/**
 * AlgorithmControl.java
 * Created: 27.07.2010 14:49:25
 */
package gui;

import converter.ZToCAConverter;
import converter.ZToCAConverter.ConversionNotSupportedException;
import ds.Project;
import ds.ca.CellularAutomaton;
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


}
