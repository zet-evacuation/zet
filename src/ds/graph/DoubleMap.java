/*
 * DoubleMap.java
 *
 */
package ds.graph;

import ds.graph.network.DynamicNetwork;
import ds.mapping.Identifiable;

/**
 *
 * @param <D> 
 * @author Martin Groß
 */
public class DoubleMap<D extends Identifiable> {
	private double[] mapping;

	public DoubleMap( DynamicNetwork graph ) {
		mapping = new double[graph.numberOfEdges()];
	}

	public DoubleMap( int numberOfEdges ) {
		mapping = new double[numberOfEdges];
	}

	public DoubleMap( Iterable<D> domain ) {
		int maxId = -1;
		for( D x : domain )
			if( maxId < x.id() )
				maxId = x.id();
		mapping = new double[maxId + 1];
	}

	public DoubleMap( D[] domain ) {
		int maxId = -1;
		for( D x : domain )
			if( maxId < x.id() )
				maxId = x.id();
		mapping = new double[maxId + 1];
	}

	public DoubleMap( DoubleMap<D> doubleMap ) {
		mapping = new double[doubleMap.mapping.length];
		System.arraycopy( doubleMap.mapping, 0, mapping, 0, mapping.length );
	}

	public DoubleMap( DoubleMap<D> doubleMap, double d ) {
		mapping = new double[doubleMap.mapping.length + 1];
		System.arraycopy( doubleMap.mapping, 0, mapping, 0, doubleMap.mapping.length );
		mapping[doubleMap.mapping.length] = d;
	}

	public double get( D x ) {
		return mapping[x.id()];
	}

	public void decrease( D x, double amount ) {
		mapping[x.id()] -= amount;
	}

	public void increase( D x, double amount ) {
		mapping[x.id()] += amount;
	}

	public void set( D x, double value ) {
		if( x.id() >= mapping.length ) {
			double[] n = new double[x.id() + 1];
			System.arraycopy( mapping, 0, n, 0, mapping.length );
			mapping = n;
		}
		mapping[x.id()] = value;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append( "(" );
		for( int i = 0; i < mapping.length; i++ ) {
			buffer.append( mapping[i] );
			if( i < mapping.length - 1 )
				buffer.append( ", " );
		}
		buffer.append( ")" );
		return buffer.toString();
	}
}
