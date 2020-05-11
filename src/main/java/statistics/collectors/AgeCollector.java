/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
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
