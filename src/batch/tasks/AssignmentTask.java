/**
 * Class AssignmentTask
 * Erstellt 24.11.2008, 00:25:05
 */

package batch.tasks;

import algo.graph.exitassignment.Assignable;
import algo.graph.exitassignment.ExitAssignment;

/**
 * An abstract task that calculates an exit assignment.
 * @author Jan-Philipp Kappmeier
 */
public abstract class AssignmentTask implements Runnable {
	/** The calculated object delivering an exit assignment. */
	protected Assignable exitAssignment;
	
	/**
	 * Returns an calculated exit assignment.
	 * @return an calculated exit assignment
	 */
	public ExitAssignment getExitAssignment() {
		return exitAssignment.getExitAssignment();
	}
}