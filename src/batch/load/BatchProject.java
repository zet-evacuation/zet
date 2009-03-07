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
