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