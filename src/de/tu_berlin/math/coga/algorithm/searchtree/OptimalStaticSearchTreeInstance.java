/**
 * OptimalStaticSearchTreeInstance.java Created: 05.07.2012, 11:23:37
 */
package de.tu_berlin.math.coga.algorithm.searchtree;

import de.tu_berlin.coga.common.datastructure.KeyValuePair;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @param <T>
 * @author Jan-Philipp Kappmeier
 */
public class OptimalStaticSearchTreeInstance<T extends Comparable<T>> {
  double total = 0;
  ArrayList<KeyValuePair<T, Double>> keys;

  public OptimalStaticSearchTreeInstance() {
    keys = new ArrayList<>();
  }

  public void addKeyValuePair( T key, double probability ) {
    //this.key.add( key );
    keys.add( new KeyValuePair<>( key, probability ) );
    total += probability;
    if( total > 1 ) {
      throw new IllegalStateException( "Total probability > 1" );
    }
  }

  public int size() {
    return keys.size();
  }

  public double getProbability( int i ) {
    return keys.get( i ).getValue();
  }

  public void sort() {
    Collections.sort( keys );
  }
}
