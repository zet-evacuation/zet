/**
 * AgeCollector.java
 * input:
 * output:
 *
 * method:
 *
 * Created: May 12, 2010,3:34:18 PM
 */
package statistics.collectors;

import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.stat.TallyStore;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AgeCollector extends Tally {
	TallyStore completeDataset;
	boolean storeCompleteData;

	public AgeCollector() {

	}

	public void storeCompleteData( boolean storeCompleteData ) {
		if( !this.storeCompleteData && storeCompleteData )
			completeDataset = new TallyStore();
		this.storeCompleteData = storeCompleteData;
	}

	public void clear() {
		if( completeDataset != null )
			completeDataset = new TallyStore();
	}
	
	@Override
	public void add( double x ) {
		super.add(x);
		if( storeCompleteData )
			completeDataset.add( x );
	}

	public int[] getHistogram( double width ) {
		completeDataset.quickSort();
		double[] values = completeDataset.getArray();

		int n = (int)Math.floor( values[values.length-1]/width ) + 2;
		int[] histogram = new int[n];
		int start = (int)Math.floor(values[0]/width);
		double bp = (start+1)*width;
		for( int i = 0; i < values.length; ++i ) {
			if( values[i] > bp ) {
				start++;
				bp = (start+1)*width;
			}
			histogram[start]++;
		}
		return histogram;
	}

	public TallyStore getCompleteDataSet() {
		return completeDataset;
	}


}
