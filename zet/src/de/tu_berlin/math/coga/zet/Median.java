/**
 * computeMedian.java Created: Jun 29, 2012, 3:30:16 PM
 */
package de.tu_berlin.math.coga.zet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @param <T> 
 * @author Jan-Philipp Kappmeier
 */
//public class Median<T extends Comparable<T>> {
public class Median<T extends Long> {
	private ArrayList<T> values;
			T median;// = values.get( values.size() / 2 - 1 );
			T upper;// = values.get( values.size() / 2 );

			T lowerQuartile;
			T upperQuartile;
			
			long iqr;
			
			ArrayList<T> outlieer = new ArrayList<>();
			ArrayList<T> valid = new ArrayList<>();
			
			
	public Median( ArrayList<T> values ) {
		this.values = values;
	}
	
	public void run() {
		computeMedian();
		int lower = (int)Math.ceil( 0.25 * values.size());
		int upper = (int)Math.ceil( 0.75 * values.size());
		lowerQuartile = values.get( lower-1 );
		upperQuartile = values.get( upper-1 );
		iqr = upperQuartile - lowerQuartile;
		for( T l : values ) {
			if( l < median - iqr )
				outlieer.add( l );
			else if( l > median + iqr )
				outlieer.add( l );
			else
				valid.add( l );
		}
	}
	
	public List<T> getOutlier() {
		return Collections.unmodifiableList( outlieer );
	}
	
	public int valid() {
		return valid.size(); //values.size() - outlieer.size();
	}
	
	public List<T> getValid() {
		return Collections.unmodifiableList( valid );
	}
	
	public int getNumberOfOutlier() {
		return outlieer.size();
	}
	
	
	public T computeMedian( ) {

		Collections.sort( values );

		if( values.size() % 2 == 1 )
			return median=values.get( (values.size() + 1) / 2 - 1 );
		else {
			median = values.get( values.size() / 2 - 1 );
			upper = values.get( values.size() / 2 );

			//return (T)((median. + upper) / 2.0);
			return median;
		}
	}

	public T getLower() {
		return median;
	}

	public T getUpper() {
		return upper;
	}
	
	
}
