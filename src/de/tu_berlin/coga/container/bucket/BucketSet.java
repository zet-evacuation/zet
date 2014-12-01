
package de.tu_berlin.coga.container.bucket;

import de.tu_berlin.coga.container.mapping.Identifiable;
import de.tu_berlin.coga.container.mapping.IdentifiableObjectMapping;
import java.lang.reflect.Array;
import java.util.Objects;

/**
 *
 * @param <E>
 * @author Jan-Philipp Kappmeier
 */
public class BucketSet<E extends Identifiable> {
	IdentifiableObjectMapping<E, E> next;
	IdentifiableObjectMapping<E, E> prev;
	protected Class<E> rangeType;
	public E[] buckets;
	public boolean[] inactive;
	int domainSize;
	protected int[] distanceLabels;

	public BucketSet( int domainSize, Class<E> rangeType ) {
		next = new IdentifiableObjectMapping<>( domainSize );
		prev = new IdentifiableObjectMapping<>( domainSize );
		this.rangeType = rangeType;
		this.buckets = (E[]) Array.newInstance( rangeType, domainSize );
		inactive = new boolean[domainSize];
		this.domainSize = domainSize;
	}

	public void setDistanceLabels( int[] distanceLabels ) {
		this.distanceLabels = distanceLabels;
	}

	public void reset( int upTo ) {
		for( int i = 0; i <= upTo; ++i ) {
			buckets[i] = null;
			inactive[i] = false;
		}

		for( int i = upTo; i < domainSize; ++i )
			inactive[i] = false;
	}

	public E get( int n ) {
		return buckets[n];
	}

	public final void addInactive( int distance, E node ) {
//		if( node.id() == 665020 ) {
//			System.out.println( "Try to add " + node + " with distance " + distance );
//			if( inactive[node.id()] )
//				System.out.println( "Will FAIL!!!" );
//
//		}
		if( inactive[node.id()] )
			return;
		inactive[node.id()] = true;

		if( buckets[distance] != null ) {
			final E next_t = buckets[distance];
			next.set( node, next_t );
			prev.set( node, null );
			prev.set( next_t, node );
		} else
			next.set( node, null );
		buckets[distance] = node;
	}

	public final void deleteInactive( int distance, E node ) {
//		if( node.id() == 665020 ) {
//			System.out.println( "Try to remove " + node + " with distance " + distance );
//		}

		if( !inactive[node.id()])
			return;

		assert inactive[node.id()];
		inactive[node.id()] = false;

		Objects.requireNonNull( node, "Node is null" );
		Objects.requireNonNull( buckets[distance], "Buckets distance" );

		final E next_t = next.get( node );
		if( buckets[distance].id() == node.id() ) {
			buckets[distance] = next_t;
			if( next_t != null )
				prev.set( next_t, null );
		} else {
			final E prev_t = prev.get( node );
			next.set( prev_t, next.get( node ) );
			if( next_t != null )
				prev.set( next_t, prev_t );

		}
	}

	public void printInactiveBucket( int distance ) {
		E node = buckets[distance];
		System.out.print( "IBucket " + distance + ": " );
		while( node != null ) {
			System.out.print( node.id() );
			if( next.get( node ) != null && prev.get( next.get( node ) ) != null )
				System.out.print( "<" );
			if( next.get( node ) != null )
				System.out.print( "->" );
			node = next.get( node );
		}
		System.out.println();
	}

	public void set( int l, E object ) {
		buckets[l] = object;
	}

	public E next( E e ) {
		return next.get( e );
	}
}
