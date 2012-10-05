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
public class DoubleSequence implements Sequence<Double> {

    private double first;

    private double last;

    private double step;

    public DoubleSequence(double first, double last) {
        this(first, last, 1.0);
    }

    public DoubleSequence(double first, double last, double step) {
        this.first = first;
        this.last = last;
        this.step = step;
    }

    @Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>() {

            private double current = first - step;

            @Override
            public boolean hasNext() {
                return current != last;
            }

            @Override
            public Double next() {
                current += step;
                if (current >= last) {
                    current = last;
                }
                return current;
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
