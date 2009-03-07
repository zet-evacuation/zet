/**
 * Class ProgressTaskEntry
 * Erstellt 19.07.2008, 18:05:08
 */

package gui.components.progress;

/**
 * A class representing a task used for progress display. It simply stores an
 * object implementing <code>Runnable</code> and its name.
 * @author Jan-Philipp Kappmeier
 */
public class ProgressTaskEntry {
	/** The name of the task */
	public String title = "";
	/** The task */
	public Runnable task = null;
}
