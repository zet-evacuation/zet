/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class VisualizationDataStructureTask
 * Erstellt 27.06.2008, 00:45:14
 */

package batch.tasks;

import ds.CompareVisualizationResults;
import ds.GraphVisualizationResults;
import gui.visualization.control.ZETGLControl;
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
	private GraphVisualizationResults graphRes;
	private BuildingResults buildingRes;
	private ZETGLControl control;
	private CAStatistic caStatistic;
        private CompareVisualizationResults compVisRes;

	/**
	 * Initializes the task with the needed objects.
	 * @param caVisResults the results of the cellular automaton
	 * @param graphVisResults the results of a flow optimization on a graph
	 * @param buildingResults the structure of the building
	 * @param caStatistic the statistic for the current run
	 */
	public VisualizationDataStructureTask( CAVisualizationResults caVisResults, GraphVisualizationResults graphVisResults, BuildingResults buildingResults, CAStatistic caStatistic ) {
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
	 * Creates the ZETGLControl object
	 */

	public void run() {
		control = new ZETGLControl( caRes, graphRes, buildingRes, caStatistic, compVisRes );
	}

	/**
	 * Returns the created control object
	 * @return the control object
	 */
	public ZETGLControl getControl() {
		return control;
	}
}
