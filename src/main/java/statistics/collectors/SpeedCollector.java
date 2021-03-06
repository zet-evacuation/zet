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

import org.zetool.common.datastructure.Tuple;
import java.util.ArrayList;
import umontreal.iro.lecuyer.stat.TallyStore;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SpeedCollector {
	TallyStore completeDataset;
	ArrayList<Tuple<Double, Double>> data = new ArrayList<Tuple<Double, Double>>();
	ArrayList<Double> binBounds;
	ArrayList<Integer> binSizes;
	ArrayList<TallyStore> bins;


	public SpeedCollector() {
		binBounds = new ArrayList<Double>( 2 );
		binBounds.add( 30. );
		binBounds.add( 50. );
		binSizes = new ArrayList<Integer>( 3 );
		binSizes.add( 0 );
		binSizes.add( 0 );
		binSizes.add( 0 );
		bins = new ArrayList<TallyStore>( 3 );
		bins.add( new TallyStore() );
		bins.add( new TallyStore() );
		bins.add( new TallyStore() );
		completeDataset = new TallyStore();
	}

	int min = 5;
	int max = 85;

	public void setDetailed( ) {
		// One dataset for each year
		binBounds = new ArrayList<Double>( max-min );
		bins = new ArrayList<TallyStore>( max-min+1 );
		binSizes = new ArrayList<Integer>( max-min+1 );
		for( int i = 10; i <= 90; ++i ) {
			binBounds.add( (double)i );
			binSizes.add( 0 );
			bins.add( new TallyStore() );
		}
		binSizes.add( 0 );
		for( Tuple t : data ) {
			findBin( t );
		}
	}

	public int getNumberOfBins() {
		return bins.size();
	}

	public void add( Tuple<Double, Double> t ) {
		data.add( t );
		completeDataset.add( t.getV() );
		findBin( t );
	}

	private void findBin( Tuple<Double, Double> t ) {
		for( int i = 0; i < binBounds.size(); ++i ) {
			if( t.getU() < binBounds.get( i ) ) {
				binSizes.set( i, binSizes.get( i ) + 1 );
				bins.get( i ).add( t.getV() );
				return;
			}
		}
		binSizes.set( binBounds.size(), binSizes.get( binBounds.size() ) + 1 );
		bins.get( binBounds.size() ).add( t.getV() );
	}

	public void printBin( int bin ) {
		System.out.println( binSizes.get(bin) );
	}

	public double[] getValues( int bin ) {
		return bins.get( bin ).getArray();
	}

	public double[] getValues() {
		return completeDataset.getArray();
	}
}
