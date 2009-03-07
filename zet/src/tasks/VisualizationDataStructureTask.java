/**
 * Class VisualizationDataStructureTask
 * Erstellt 27.06.2008, 00:45:14
 */

package tasks;

import ds.graph.GraphVisualizationResult;
import gui.visualization.control.GLControl;
import io.visualization.BuildingResults;
import io.visualization.CAVisualizationResults;
import statistic.ca.CAStatistic;

/**
 * A task that starts creation of the visualization data structure used for the
 * visualization of flows and cellular automaton.
 * @author Jan-Philipp Kappmeier
 */
public class VisualizationDataStructureTask implements Runnable {
	private CAVisualizationResults caRes;
	private GraphVisualizationResult graphRes;
	private BuildingResults buildingRes;
	private GLControl control;
	private CAStatistic caStatistic;

	/**
	 * Initializes the task with the needed objects.
	 * @param caVisResults the results of the cellular automaton
	 * @param graphVisResults the results of a flow optimization on a graph
	 * @param buildingResults the structure of the building
	 * @param caStatistic the statistic for the current run
	 */
	public VisualizationDataStructureTask( CAVisualizationResults caVisResults, GraphVisualizationResult graphVisResults, BuildingResults buildingResults, CAStatistic caStatistic ) {
		//if( caVisResults == null )
		//	throw new java.lang.IllegalArgumentException( "Cellular Automaton results are null." );
		//if( graphVisResults == null )
		//	throw new java.lang.IllegalArgumentException( "Graph results are null." );
		if( buildingResults == null )
			throw new java.lang.IllegalArgumentException( "Building results are null." );
		caRes = caVisResults;
		graphRes = graphVisResults;
		buildingRes = buildingResults;
		this.caStatistic = caStatistic;
	}
	
	/**
	 * Creates the GLControl object
	 */
	public void run() {
		control = new GLControl( caRes, graphRes, buildingRes, caStatistic );
	}

	/**
	 * Returns the created control object
	 * @return the control object
	 */
	public GLControl getControl() {
		return control;
	}
}
