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
 * Class EarliestArrivalAssignmentTask
 * Erstellt 24.11.2008, 00:16:08
 */
package batch.tasks.assignment;

import batch.tasks.*;
import algo.graph.exitassignment.ShortestPathGraphEarliestArrivalTransshipmentExitAssignment;
import batch.BatchResultEntry;
import converter.ZToGraphConverter;
import ds.Project;
import ds.z.Assignment;
import ds.z.ConcreteAssignment;

/**
 *
 * @author Martin Groß
 */
public class ShortestPathGraphEarliestArrivalAssignmentTask extends AssignmentTask {

    /** The {@link ds.z.Project} */
    private Project project;
    /** The used assignment for the ca run. */
    private Assignment assignment;
    /** The batch object which stores the calculated results. */
    private BatchResultEntry res;
    /** The concrete assignments. */
    private ConcreteAssignment[] concreteAssignments;
    /** The run number. */
    private int runNumber;

    public ShortestPathGraphEarliestArrivalAssignmentTask(Project project, Assignment assignment, BatchResultEntry res, ConcreteAssignment[] concreteAssignments, int runNumber) {
        this.project = project;
        this.assignment = assignment;
        this.res = res;
        this.concreteAssignments = concreteAssignments;
        this.runNumber = runNumber;
    }

    public void run() {
        ShortestPathGraphEarliestArrivalTransshipmentExitAssignment eatAssignment;
        eatAssignment = new ShortestPathGraphEarliestArrivalTransshipmentExitAssignment();
        ZToGraphConverter.convertConcreteAssignment(concreteAssignments[runNumber], res.getNetworkFlowModel());
        eatAssignment.setProblem(res.getNetworkFlowModel());
        eatAssignment.run();
        exitAssignment = eatAssignment;
		
		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
    }
}
