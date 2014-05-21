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
 * Class MinCostAssignmentTask
 * Erstellt 23.11.2008, 22:17:59
 */

package batch.tasks.assignment;

import batch.tasks.*;
import algo.graph.exitassignment.ShortestPathExitAssignment;
import batch.BatchResultEntry;
import de.tu_berlin.coga.zet.model.Project;
import de.tu_berlin.coga.zet.model.Assignment;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;

/**
 * A task that calculates an exit assignment using shortest paths.
 * @author Timon Kelter
 */
public class ShortestPathAssignmentTask extends AssignmentTask {
	/** The {@link de.tu_berlin.coga.zet.model.Project} */
	private Project project;
	/** The used assignment for the ca run. */
	private Assignment assignment;
	/** The batch object which stores the calculated results. */
	private BatchResultEntry res;
	/** The concrete assignments. */
	private ConcreteAssignment[] concreteAssignments;
	/** The run number. */
	private int runNumber;
				
	/**
	 * Initializes a new instance of this task.
	 * @param res the object containing which stores the results
	 * @param runNumber the number of the run, used to access the results
	 * @param project the project on which the ca runs
	 * @param assignment the selected assignment
	 * @param concreteAssignments the concrete assignments that were already calculated for the cellular automaton. can be null.
	 */
	public ShortestPathAssignmentTask( Project project, Assignment assignment, BatchResultEntry res, ConcreteAssignment[] concreteAssignments, int runNumber ) {
		this.project = project;
		this.assignment = assignment;
		this.res = res;
		this.concreteAssignments = concreteAssignments;
		this.runNumber = runNumber;
	}
	
	/**
	 * Calculates the exit assignment using shortest paths. After that it can be
	 * delivered using {@link #getExitAssignment()}.
	 */
	public void run() {
		ShortestPathExitAssignment spExitAssignment = new ShortestPathExitAssignment ();
		//ZToGraphConverter.convertConcreteAssignment( concreteAssignments[runNumber], res.getNetworkFlowModel() );
		spExitAssignment.setProblem( res.getNetworkFlowModel() );
		spExitAssignment.run();
		exitAssignment = spExitAssignment;
		
		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
	}
}


