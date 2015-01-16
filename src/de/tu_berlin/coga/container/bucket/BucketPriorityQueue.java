/**
 * BucketPriorityQueue.java Created: Oct 21, 2010, 11:50:07 AM
 */
package de.tu_berlin.coga.container.bucket;

import org.zetool.container.mapping.Identifiable;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import java.lang.reflect.Array;
import java.util.HashSet;

/**
 *
 * @param <E>
 * @author Jan-Philipp Kappmeier
 */
public class BucketPriorityQueue<E extends Identifiable> {

	IdentifiableObjectMapping<E, E> next;
	protected Class<E> rangeType;
	E[] buckets;
	public boolean[] active;
	public HashSet<E> activeHash;
	int maxIndex;
	int minIndex;
	int dMax;
	int domainSize;
	protected IdentifiableIntegerMapping<E> distanceLabels;

	//@SuppressWarnings( "unchecked" )
	public BucketPriorityQueue( int domainSize, Class<E> rangeType ) {
		next = new IdentifiableObjectMapping<>( domainSize );
		this.rangeType = rangeType;
		this.buckets = (E[])Array.newInstance( rangeType, domainSize );
		active = new boolean[domainSize];
		this.domainSize = domainSize;
		maxIndex = 0;
		minIndex = domainSize - 1;
		activeHash = new HashSet<>( (int)(domainSize * 0.1) );
	}

	public void setDistanceLabels( IdentifiableIntegerMapping<E> distanceLabels ) {
		this.distanceLabels = distanceLabels;
	}

	public void reset() {
		for( int i = 0; i <= dMax; ++i ) {
			buckets[i] = null;
			active[i] = false;
		}

		for( int i = dMax + 1; i < domainSize; ++i ) {
			active[i] = false;
		}
		dMax = maxIndex = 0;
		minIndex = domainSize - 1;
	}

	public E get( int n ) {
		return buckets[n];
	}

	public final int addActive( int distance, E node ) {
//		if( node.id() == 3457 && distance == 10899 ) {
//			int i = 3;
//			i++;
//		}
//		if( node.id() == 3457 ) {
//			System.out.println( "Activate 3457 with distance " + distance );
//		}
		//if( node.id() == 57262 ) {
		//	System.out.println( "Activate 57262 with distance " + distance );
		//}

		activeHash.add( node );

		if( active[node.id()] == true ) {
			return maxIndex;	// was already active
		}
		active[node.id()] = true;

		next.set( node, buckets[distance] );
		buckets[distance] = node;
		buckets[distance] = node;

		final int dist = distanceLabels.get( node );
		if( dist < minIndex ) {
			minIndex = dist;
		}
		if( dist > maxIndex ) {
			maxIndex = dist;
		}
		if( dMax < maxIndex ) {
			dMax = maxIndex;
		}
		return maxIndex;
	}

	/**
	 * Removes the first element in the bucket list of the given distance. Only
	 * works if {@code node} is the first element.
	 * @param distance
	 * @param node
	 */
	public final void removeActive( int distance, E node ) {
		assert active[node.id()];
		//if( node.id() == 3457 ) {
		//	System.out.println( "Deactivate 3457 with distance " + distance );
		//}
		if( node.id() == 57262 ) {
			//	System.out.println( "Dectivate 57262 with distance " + distance );
		}

		activeHash.remove( node );

		active[node.id()] = false;
		buckets[distance] = next.get( node );
	}

	public void printActiveBucket( int distance ) {
		E node = buckets[distance];
		System.out.print( "Bucket " + distance + ": " );
		while( node != null ) {
			System.out.print( node.id() );
			//if( prev.get( node ) != null ) {
			//	System.out.print( "<");
			//}
			if( next.get( node ) != null ) {
				System.out.print( "->" );
			}
			node = next.get( node );
		}
		System.out.println();
	}

	public E next( E e ) {
		return next.get( e );
	}

	public int getMaxIndex() {
		return maxIndex;
	}

	public int getMinIndex() {
		return minIndex;
	}

	public void setMaxIndex( int aMax ) {
		this.maxIndex = aMax;
		dMax = aMax;
	}

	public E max() {
		while( maxIndex >= 0 && buckets[maxIndex] == null ) {
			maxIndex--;
		}
		return maxIndex >= 0 ? buckets[maxIndex] : null;
	}

	public int getdMax() {
		return dMax;
	}

	public void setdMax( int dMax ) {
		this.dMax = dMax;
	}

}
