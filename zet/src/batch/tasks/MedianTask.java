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
 * Class MedianTask
 * Erstellt 22.07.2008, 00:43:43
 */

package batch.tasks;

import batch.BatchResultEntry;
import java.util.TreeMap;
import batch.tasks.AlgorithmTask;

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
