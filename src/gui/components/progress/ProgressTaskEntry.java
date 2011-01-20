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
 * Class ProgressTaskEntry
 * Erstellt 19.07.2008, 18:05:08
 */

package gui.components.progress;

/**
 * A class representing a task used for progress display. It simply stores an
 * object implementing {@code Runnable} and its name.
 * @author Jan-Philipp Kappmeier
 */
public class ProgressTaskEntry {
	/** The name of the task */
	public String title = "";
	/** The task */
	public Runnable task = null;
}
