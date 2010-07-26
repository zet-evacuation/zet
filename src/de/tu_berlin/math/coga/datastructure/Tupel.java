/**
 * Tupel.java
 * Created 18.05.2010, 12:51:04
 */
package de.tu_berlin.math.coga.datastructure;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Tupel<U,V> {
	protected U u;
	protected V v;

	public Tupel( U u, V v ) {
		this.u = u;
		this.v = v;
	}

	public U getU() {
		return u;
	}

	public V getV() {
		return v;
	}
}
