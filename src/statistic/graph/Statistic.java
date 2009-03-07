/*
 * Statistic.java
 *
 */
package statistic.graph;

import statistic.common.Data;

/**
 *
 * @author Martin Groß
 */
public interface Statistic<O, R, D extends Data> {

    R calculate(Statistics<D> statistics, O object);

    Class<R> range();
}
