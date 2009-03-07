/**
 * Class MedianTask
 * Erstellt 22.07.2008, 00:43:43
 */

package batch.tasks;

import batch.BatchResultEntry;
import java.util.TreeMap;
import tasks.AlgorithmTask;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class MedianTask implements Runnable {
	/** The batch object which stores the calculated results. */
	BatchResultEntry res;
	/** A map storing statistics with run number as key. */
	TreeMap<Integer, Integer> median;

	public MedianTask( BatchResultEntry res, TreeMap<Integer, Integer> median ) {
		this.res = res;
		this.median = median;
	}

	public void run() {
		//Compute the median (concerning the evac time)
		//(The median will be the value of the first remaining entry)
		for(int j = 0; j < median.size() / 2; j++) {
			median.remove(median.firstKey());
			AlgorithmTask.getInstance().publish( (int)Math.round( 100*(j/((double)median.size()/2)) ) );
			//util.Helper.pause( 100 );
		}
		res.setMedianIndex( median.get( median.firstKey() ) );
		AlgorithmTask.getInstance().publish( 100 );
		
		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
	}
}
