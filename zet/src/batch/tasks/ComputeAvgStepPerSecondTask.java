/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
import ds.ca.evac.EvacuationCellularAutomaton;

/**
 *
 * @author Timon Kelter
 */
public class ComputeAvgStepPerSecondTask implements Runnable {
	/** The batch object which stores the calculated results. */
	BatchResultEntry res;

	public ComputeAvgStepPerSecondTask( BatchResultEntry res ) {
		this.res = res;
	}

	public void run() {
		double tmpAverageStepsPerSecond = 0.0;
		double i = 0;
		
		if (res.getCa () != null) {
			for(EvacuationCellularAutomaton ca : res.getCa()){
				tmpAverageStepsPerSecond += ca.getStepsPerSecond();
				//AlgorithmTask.getInstance().publish( (int)Math.round( 100*(i++/res.getCa ().length) ) );
			}
			res.setAverageCAStepsPerSecond (tmpAverageStepsPerSecond / res.getCa().length);
		} else {
			res.setAverageCAStepsPerSecond (0.0);
		}
		//AlgorithmTask.getInstance().publish( 100 );
		
		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
	}
}
