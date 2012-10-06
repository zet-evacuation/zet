/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

import java.util.Iterator;

/**
 * A class that represents an implicitly given sequence of doubles, that is 
 * uniformly distributed between a start and an end value, with a specified step 
 * in between two doubles. Notice that the step does not need to be an double
 * - the returned doubles are rounded values of an internal floating point 
 * number. The two boundary values are always part of the returned sequence, 
 * even when starting from the first value with the specified step would 
 * normally not stop at the end value. If start and end value are the same, the 
 * sequence contains just this one double.
 * 
 * @author Martin Groß
 */
public class DoubleSequence implements Sequence<Double> {

    /**
     * The start value of the sequence. 
     */
    private double first;
    /**
     * The end value of the sequence.
     */
    private double last;
    /**
     * The step value of the sequence, by which the current value is increased
     * in an iteration.
     */
    private double step;

    /**
     * Creates an instance of double steps with the given parameters and a step
     * of 1.
     * @param first the first double to be iterated.
     * @param last the last double to be iterated.
     * @throws IllegalArgumentException if last is smaller than first.
     */
    public DoubleSequence(double first, double last) {
        this(first, last, 1.0);
    }

    /**
     * Creates an instance of double steps with the given parameters and the
     * step increment.
     * @param first the first double to be iterated.
     * @param last the last double to be iterated.
     * @param step the increment by which the current value increases in an 
     * iteration.
     * @throws IllegalArgumentException if last is smaller than first.
     */
    public DoubleSequence(double first, double last, double step) {
        if (last < first) {
            throw new IllegalArgumentException(last + " is smaller than " + first + ".");
        }
        this.first = first;
        this.last = last;
        this.step = step;
    }

    /**
     * {@InheritDoc}
     */
    @Override
    public Iterator<Double> iterator() {
        return new Iterator<>() {

            /**
             * Stores the current value of this iterator.
             */
            private double current = first - step;

            /**
             * {@InheritDoc}
             */
            @Override
            public boolean hasNext() {
                return current != last;
            }

            /**
             * {@InheritDoc}
             */
            @Override
            public Double next() {
                current += step;
                if (current >= last) {
                    current = last;
                }
                return current;
            }

            /**
             * {@InheritDoc}
             */
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

    /**
     * {@InheritDoc}
     */
    @Override
    public int size() {
        return 1 + (int) Math.ceil((last - first) / step);
    }
}
