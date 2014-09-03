
package de.tu_berlin.math.coga.batch.output;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Output extends TreeListItem {
  boolean consumes( Class<?> c );

  void consume( Object o );

  default void consume( Object o, int run ) {
    consume( o );
  }
}
