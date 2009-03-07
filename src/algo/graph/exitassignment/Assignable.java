/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algo.graph.exitassignment;

/**
 * An interface that allows returning an exit assingment. Used by some
 * algorithms that calculate an exit assingment.
 * @author Jan-Philipp Kappmeier
 */
public interface Assignable {
	/**
	 * Returns the calculated exit assignment.
	 * @return the calculated exit assignment
	 */
	public ExitAssignment getExitAssignment();
}
