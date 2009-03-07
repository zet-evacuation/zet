/**
 * Class MedianTask
 * Erstellt 22.07.2008, 00:43:43
 */

package batch.tasks;

import batch.BatchResultEntry;
import ds.ca.CellularAutomaton;
import java.util.TreeMap;
import tasks.AlgorithmTask;

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
			for(CellularAutomaton ca : res.getCa()){
				tmpAverageStepsPerSecond += ca.getStepsPerSecond();
				AlgorithmTask.getInstance().publish( (int)Math.round( 100*(i++/res.getCa ().length) ) );
			}
			res.setAverageCAStepsPerSecond (tmpAverageStepsPerSecond / res.getCa().length);
		} else {
			res.setAverageCAStepsPerSecond (0.0);
		}
		AlgorithmTask.getInstance().publish( 100 );
		
		// Forget the used batch result entry. This is necessary in case that the batch entries
		// are stored on disk. Then this reference will inhibit the deletion of the batch result entry
		res = null;
	}
}
