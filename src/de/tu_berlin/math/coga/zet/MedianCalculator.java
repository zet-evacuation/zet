/**
 * computeMedian.java Created: Jun 29, 2012, 3:30:16 PM
 */
package de.tu_berlin.math.coga.zet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @param <T> 
 * @author Jan-Philipp Kappmeier
 */
public class MedianCalculator<T extends Comparable<T>> {
	int dimension = 1;
	private ArrayList<T>[] values;
	private ArrayList<T>[] sorted;
	private ArrayList<ArrayList<Integer>> inRange;
	T[] median;
	T[] upper;
	T[] lowerQuartile;
	T[] upperQuartile;
	long[] iqr;
	int invalid = 0;
	ArrayList<T> outlier = new ArrayList<>();
	ArrayList<T> valid = new ArrayList<>();

	public MedianCalculator( int dimension ) {
		if( dimension <= 0 )
			throw new IllegalArgumentException( "Dimension must be positive" );
		this.dimension = dimension;
		this.values = (ArrayList<T>[]) Array.newInstance( ArrayList.class, dimension );
		this.sorted = (ArrayList<T>[]) Array.newInstance( ArrayList.class, dimension );
		for( int i = 0; i < dimension; ++i ) {
			values[i] = new ArrayList<>();
			sorted[i] = new ArrayList<>();
		}
		inRange = new ArrayList<>();
		median = (T[]) Array.newInstance( Long.class, dimension );
		upper = (T[]) Array.newInstance( Long.class, dimension );
		lowerQuartile = (T[]) Array.newInstance( Long.class, dimension );
		upperQuartile = (T[]) Array.newInstance( Long.class, dimension );
		iqr = new long[dimension];
	}

	public void addData( T[] data ) {
		if( data.length != values.length )
			throw new IllegalArgumentException( "Data dimenson does not fit to dimension of median collector" );
		for( int i = 0; i < data.length; ++i ) {
			values[i].add( data[i] );
			sorted[i].add( data[i] );
		}
		inRange.add( new ArrayList<Integer>() );
	}

	public void run() {
		computeMedian();

	}

	public List<T> getValues( int d ) {
		return Collections.unmodifiableList( values[d] );
	}

	public List<T> getOutlier( int d ) {
		outlier = new ArrayList<>( invalid );
		for( int i = 0; i < values[d].size(); ++i )
			if( inRange.get( i ).contains( d ) )
				outlier.add( values[d].get( i ) );
		return Collections.unmodifiableList( outlier );
	}

	public int valid() {
		return values[0].size() - invalid;
	}

	public List<T> getValid( int d ) {
		valid = new ArrayList<>( values[d].size() - invalid );
		for( int i = 0; i < values[d].size(); ++i )
			if( inRange.get( i ).isEmpty() )
				valid.add( values[d].get( i ) );
		return Collections.unmodifiableList( valid );
	}

	public int getNumberOfOutlier() {
		return invalid;
	}

	public T computeMedian() {
		for( int j = 0; j < inRange.size(); ++j )
			//inRange.set( j, -1 );
			inRange.get( j ).clear();
		invalid = 0;

		for( int i = 0; i < dimension; ++i )
			computeMedian( i );
		return null;
	}

	private void computeMedian( int i ) {
		Collections.sort( sorted[i] ); // resort as we may have inserted some data

		if( sorted[i].size() % 2 == 1 )
			median[i] = sorted[i].get( (sorted[i].size() + 1) / 2 - 1 );
		else {
			median[i] = sorted[i].get( sorted[i].size() / 2 - 1 );
			upper[i] = sorted[i].get( sorted[i].size() / 2 );
		}
		int lower = (int) Math.ceil( 0.25 * sorted[i].size() );
		int upper = (int) Math.ceil( 0.75 * sorted[i].size() );
		lowerQuartile[i] = sorted[i].get( lower - 1 );
		upperQuartile[i] = sorted[i].get( upper - 1 );
		if( upperQuartile[i] instanceof Long ) {
			long uQ = (Long) upperQuartile[i];
			long lQ = (Long) lowerQuartile[i];
			iqr[i] = uQ - lQ;

			for( int j = 0; j < values[i].size(); ++j ) {
				long l = (Long) values[i].get( j );
				if( l == 10 )
					l = 20;
				if( l < (Long) median[i] - 1.5 * (Long) iqr[i] ) {

					if( !inRange.get( j ).contains( i )) {
						if( inRange.get( j ).isEmpty() )
							invalid++;
						inRange.get( j ).add( i );
					}
				} else if( l > (Long) median[i] + 1.5 * (Long) iqr[i] ) {
					if( !inRange.get( j ).contains( i )) {
						if( inRange.get( j ).isEmpty() )
							invalid++;
						//inRange.set( j, i );
						inRange.get( j ).add( i );
					}
				} else {

				}
			}
		}
	}

	public T getLower( int dimension ) {
		return median[dimension];
	}

	public T getUpper( int dimension ) {
		return upper[dimension];
	}
}
