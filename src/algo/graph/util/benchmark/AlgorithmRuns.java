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
/*
 * AlgorithmRuns.java
 *
 */
package algo.graph.util.benchmark;

import de.tu_berlin.math.coga.common.util.Formatter;
import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 */
public class AlgorithmRuns<T extends AlgorithmRunData> extends LinkedList<T> {
	protected long deviation;
	protected long lowerQuartile;
	protected long max;
	protected long mean;
	protected long median;
	protected long min;
	protected long upperQuartile;
	protected long variance;

	public void analyse() {
		AlgorithmRunData[] runs = toArray( new AlgorithmRunData[0] );
		Arrays.sort( runs );
		int last = runs.length - 1;
		min = runs[0].getRuntime();
		max = runs[last].getRuntime();
		if( last % 2 == 0 )
			median = runs[last / 2].getRuntime();
		else
			median = (runs[last / 2].getRuntime() + runs[last / 2 + 1].getRuntime()) / 2;
		long sum = 0;
		for( AlgorithmRunData data : runs )
			sum += data.getRuntime();
		mean = sum / runs.length;
		sum = 0;
		for( AlgorithmRunData data : runs )
			sum += (data.getRuntime() / 1000 - mean) * (data.getRuntime() / 1000 - mean);
		variance = sum / runs.length * 1000000;
		deviation = Math.round( Math.sqrt( variance ) );
	}

	public String results() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Minimum: " + Formatter.formatTimeUnit( min, Formatter.TimeUnits.NanoSeconds ) + "\n" );
		builder.append( "Maximum: " + Formatter.formatTimeUnit( max, Formatter.TimeUnits.NanoSeconds ) + "\n" );
		builder.append( "Mittelwert: " + Formatter.formatTimeUnit( mean, Formatter.TimeUnits.NanoSeconds ) + "\n" );
		builder.append( "Varianz: " + Formatter.formatTimeUnit( variance, Formatter.TimeUnits.NanoSeconds ) + "\n" );
		builder.append( "Standardabweichung: " + Formatter.formatTimeUnit( deviation, Formatter.TimeUnits.NanoSeconds ) + "\n" );
		builder.append( "Median: " + Formatter.formatTimeUnit( median, Formatter.TimeUnits.NanoSeconds ) + "\n" );
		return builder.toString();
	}
}
