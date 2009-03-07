/**
 * Class EarliestArrivalAssignmentTask
 * Erstellt 24.11.2008, 00:16:08
 */
package batch.tasks;

import algo.graph.exitassignment.ReducedEarliestArrivalTransshipmentExitAssignment;
import batch.BatchResultEntry;
import converter.ZToGraphConverter;
import converter.ZToNonGridGraphConverter;
import ds.Project;
import ds.z.Assignment;
import ds.z.ConcreteAssignment;

/**
 *
 * @author Martin Groß
 */
public class ReducedEarliestArrivalAssignmentTask extends AssignmentTask {

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

    public ReducedEarliestArrivalAssignmentTask(Project project, Assignment assignment, BatchResultEntry res, ConcreteAssignment[] concreteAssignments, int runNumber) {
        this.project = project;
        this.assignment = assignment;
        this.res = res;
        this.concreteAssignments = concreteAssignments;
        this.runNumber = runNumber;
    }

    public void run() {
        ReducedEarliestArrivalTransshipmentExitAssignment eatAssignment;
        eatAssignment = new ReducedEarliestArrivalTransshipmentExitAssignment();
        ZToGraphConverter.convertConcreteAssignment(concreteAssignments[runNumber], res.getNetworkFlowModel());
        eatAssignment.setProblem(res.getNetworkFlowModel());
        eatAssignment.run();
        exitAssignment = eatAssignment;
		
		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
    }
}
