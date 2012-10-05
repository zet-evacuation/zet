/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.common.algorithm.parameter;

import java.util.Iterator;

/**
 *
 * @author gross
 */
public class IntegerSequence implements Sequence<Integer> {

    private int first;

    private int last;

    private double step;

    /**
     * Creates an instance of integer steps with the given parameters and a step
     * of 1.
     * @param first the first integer to be iterated.
     * @param last the last integer to be iterated.
     */
    public IntegerSequence(int first, int last) {
        this(first, last, 1.0);
    }

    public IntegerSequence(int first, int last, double step) {
        this.first = first;
        this.last = last;
        this.step = step;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {

            private double current = first - step;

            @Override
            public boolean hasNext() {
                return current != last;
            }

            @Override
            public Integer next() {
                current += step;
                if (current >= last) {
                    current = last;
                }
                return (int) Math.round(current);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

    @Override
    public int size() {
        return 1 + (int) Math.ceil((last - first) / step);
    }
}
