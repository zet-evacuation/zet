
package de.tu_berlin.coga.common.datastructure;

import java.util.Objects;

/**
 * Represents two values that belong together. The values may be of different
 * type.
 * @param <U> the type of the first value
 * @param <V> the type of the second value
 * @author Jan-Philipp Kappmeier
 */
public class Tuple<U,V> {

	private U u;
	private V v;

	/**
	 *
	 * @param u
	 * @param v
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public Tuple( U u, V v ) throws NullPointerException {
		this.u = Objects.requireNonNull( u , "u must not be null" );
		this.v = v; //Objects.requireNonNull( v , "v must not be null" );
	}

	public U getU() {
		return u;
	}

	public V getV() {
		return v;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 47 * hash + u.hashCode(); // u and v are not null
		hash = 47 * hash + v.hashCode();
		return hash;
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == this )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		@SuppressWarnings( "unchecked" ) // safe here due to class check
		final Tuple<U, V> other = (Tuple<U, V>)obj;
		if( !this.u.equals( other.u ) ) // u and v are not null
			return false;
		if( !this.v.equals( other.v ) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" +  u + ',' + v + ')';
	}
}
