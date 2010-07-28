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

/*
 * CreateCellularAutomatonTask.java
 * Created 27.11.2009, 19:09:50
 */

package batch.tasks;

import batch.BatchResultEntry;
import converter.ZToCAConverter;
import converter.ZToCAConverter.ConversionNotSupportedException;
import ds.Project;
import ds.ca.CellularAutomaton;

/**
 * The class <code>CreateCellularAutomatonTask</code> transforms the z-model
 * to a cellular automaton.
 * @author Jan-Philipp Kappmeier
 */
public class CreateCellularAutomatonTask implements Runnable {
	/** The batch object which stores the calculated results. */
	private BatchResultEntry res;
	/** The number of the run, used for accessing the result in {@link res} */
	private int runNumber;
	/** The {@link ds.z.Project} */
	private Project project;

	/**
	 * @param res the object containing which stores the results.
	 * @param runNumber the number of the run, used to access the results
	 * @param project the project on which the ca runs
	 */
	public CreateCellularAutomatonTask( BatchResultEntry res, int runNumber, Project project ) {
		this.res = res;
		this.runNumber = runNumber;
		this.project = project;
	}

	/**
	 * Converts a cellular automaaton. The automaton is stored in the array of
	 * automatons at the specified position.
	 */
	public void run() {
		CellularAutomaton ca;
		try {
			ca = ZToCAConverter.getInstance().convert( project.getBuildingPlan() );
		} catch( ConversionNotSupportedException e ) {
			e.printStackTrace();
			return;
		}
		res.setCellularAutomaton( runNumber, ca );
		res = null;
	}
}