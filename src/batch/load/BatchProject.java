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
 * Class BatchProject
 * Erstellt 25.11.2008, 20:10:57
 */

package batch.load;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A class representing some batch tasks. The tasks can be loaded into the
 * batch window and stored in a file.
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "batchProject" )
public class BatchProject implements Iterable<BatchProjectEntry> {
	/** A list containing the tasks. */
	private ArrayList<BatchProjectEntry> batchEntries = new ArrayList<BatchProjectEntry>();

	/**
	 * Adds a new task to the list.
	 * @param bpe the new batch task
	 */
	public void add( BatchProjectEntry bpe ) {
		batchEntries.add( bpe );
	}
	
	/**
	 * Returns a batch tasks with a given index.
	 * @param i the index of the batch task.
	 * @return the batch task at the given index
	 */
	public BatchProjectEntry get( int i ) {
		return batchEntries.get( i );
	}

	/**
	 * An iterator over all stored batch tasks.
	 * @return the iterator over all stored batch tasks
	 */
	public Iterator<BatchProjectEntry> iterator() {
		return batchEntries.iterator();
	}
}
