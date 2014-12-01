/**
 * KeyValuePair.java Created: 27.07.2012, 15:21:39
 */
package de.tu_berlin.coga.common.datastructure;

import java.util.AbstractMap.SimpleEntry;

/**
 * Simple Key Value Pair that can be sorted by the keys.
 * @param <K> The key type
 * @param <V> The value type
 * @author Jan-Philipp Kappmeier
 */
public class KeyValuePair<K extends Comparable<K>, V> extends SimpleEntry<K, V> implements Comparable<KeyValuePair<K, V>> {
	private static final long serialVersionUID = 1L;

	/**
	 * @param key key to be set
	 * @param value value to be set
	 */
	public KeyValuePair( final K key, final V value ) {
		super( key, value );
	}

	/**
	 * Compares two Key Value Pairs according to the keys.
	 * @param o the other {@code KeyValuePair} with which this object is compared
	 * @return the result of the comparison between {@code this.getKey()} and {o.getKey()}
	 */
	@Override
	public int compareTo( KeyValuePair<K, V> o ) {
		return this.getKey().compareTo( o.getKey() );
	}
}
