/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class CreateNetworkFlowModelTask
 * Erstellt 23.11.2008, 22:44:03
 */

package batch.tasks;

import batch.BatchResultEntry;
import converter.ZToGraphConverter;
import ds.Project;
import ds.graph.NetworkFlowModel;

/**
 * A task that creates a network flow model, convertes a building plan and
 * stores it in some batch results.
 * @author Jan-Philipp Kappmeier
 */
public class CreateNetworkFlowModelTask implements Runnable {
	/** The batch object which stores the calculated results. */
	private BatchResultEntry res;
	/** The {@link ds.z.Project} */
	private Project project;
	/** The calculated network flow. */
	private NetworkFlowModel nfo;

	/**
	 * Creates a new instance of the task. The project and the batch results are
	 * needed.
	 * @param project the project that is converted.
	 * @param res the project result
	 */
	public CreateNetworkFlowModelTask( Project project, BatchResultEntry res ) {
		this.project = project;
		this.res = res;
	}
	
	/**
	 * Executes the task, e.g. converts the plan anc creates a network flow model.
	 */
	public void run() {
		nfo = new NetworkFlowModel();
		res.setNetworkFlowModel( nfo );
		ZToGraphConverter.convertBuildingPlan( project.getPlan(), nfo );
		
		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
	}

	/**
	 * Returns the calculated network flow model (after {@link #run()} is called).
	 * @return the calculated network flow model.
	 */
	public NetworkFlowModel getNfo() {
		return nfo;
	}
}