/**
 * Tuple.java
 * Created 18.05.2010, 12:51:04
 */
package de.tu_berlin.math.coga.datastructure;


/**
 *
 * @param <U> 
 * @param <V>
 * @author Jan-Philipp Kappmeier
 */
public class Tuple<U,V> {
	
	private U u;
	private V v;

	public Tuple( U u, V v ) {
		this.u = u;
		this.v = v;
	}

	public U getU() {
		return u;
	}

	public V getV() {
		return v;
	}

	@Override
	public String toString() {
		return "(" +  u + ',' + v + ')';
	}
}
