/*
 * IntegerDoubleMapping.java
 *
 */
package statistic.graph;

import ds.graph.IntegerIntegerMapping;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;
import statistic.graph.IntegerDoubleMapping.TimeValuePair;

/**
 * The <code>IntegerDoubleMapping</code> class represents a mapping from 
 * integers to integers. It is a specialized version of 
 * <code>IntegerObjectMapping</code> made for mappings from integers to 
 * integers. These mappings are particulary useful for functions taking
 * time as a parameter. Therefore values of this mapping's domain are referred
 * to as time henceforth.
 * Internally, the <code>IntegerDoubleMapping</code> is considered as a step
 * function. Consequently, the mapping is stored as a sorted collection of step
 * starts which is obviously sufficient to encode the mapping. 
 * The size needed to encode an <code>IntegerDoubleMapping</code> is therefore
 * linear in the number of steps required.
 * In order to access steps efficiently, a TreeSet is used which in turn is 
 * based on a red-black tree. This allows the addition, removal and search for
 * steps in O(log (number of steps)) time.
 * For mappings of integers to arbitrary values see
 * {@link IntegerObjectMapping}.
 * 
 * @author Martin Groß
 */
public class IntegerDoubleMapping implements Cloneable, Iterable<TimeValuePair> {

    /**
     * Stores the mapping internally. Must not be null.
     */
    protected TreeSet<TimeValuePair> mapping;
    /*
     * Stores whether the mapping should be interpreted as piecewise constant or
     * piecewise linear.
     */
    protected boolean linear;

    /**
     * Creates a new <code>IntegerDoubleMapping</code> that is defined for all
     * integer values. Initially, all integers are mapped to 0. Runtime O(1).
     */
    public IntegerDoubleMapping() {
        mapping = new TreeSet<TimeValuePair>();
        set(Integer.MIN_VALUE, 0);
        set(Integer.MAX_VALUE, 0);
    }

    /**
     * Creates a new <code>IntegerDoubleMapping</code> that is defined for all
     * integer values. Initially, all integers are mapped to 0. Runtime O(1).
     * @param linear if <code>true</code> this mapping is interpreted as 
     * piecewise linear by the get-function instead of piecewise constant. 
     */
    public IntegerDoubleMapping(boolean linear) {
        mapping = new TreeSet<TimeValuePair>();
        this.linear = linear;
        set(Integer.MIN_VALUE, 0);
        set(Integer.MAX_VALUE, 0);
    }

    public IntegerDoubleMapping(IntegerIntegerMapping iim) {
        mapping = new TreeSet<TimeValuePair>();
        for (IntegerIntegerMapping.TimeIntegerPair tip : iim) {
            mapping.add(new TimeValuePair(tip.time(), tip.value()));
        }
    }

    /**
     * Checks how the internally stored values are interpreted by the get 
     * method.
     * @return <code>true</code> if this mapping is considered to represent a
     * piecewise linear function, <code>false</code> if it is interpreted as
     * piecewise constant.
     */
    public boolean isPiecewiseLinear() {
        return linear;
    }

    /**
     * Returns the integer associated with the specified value. 
     * Runtime O(log (number of steps)).
     * @param time the value for which the associated integer is to be returned.
     * @return the integer associated with the specified value.
     */
    public double get(int time) {
        if (!linear) {
            return mapping.floor(new TimeValuePair(time, 0)).value();
        } else {
            TimeValuePair reference = new TimeValuePair(time, 0);
            TimeValuePair floor = mapping.floor(reference);
            if (floor.time() == time) {
                return floor.value();
            }
            TimeValuePair basement = mapping.lower(floor);
            TimeValuePair ceiling = mapping.ceiling(reference);
            double ascent = 0.0;
            if (ceiling != null) {
                ascent = (ceiling.value() - floor.value()) / (ceiling.time() - floor.time());
            } else if (basement != null) {
                ascent = (floor.value() - basement.value()) / (floor.time() - basement.time());
            }
            return floor.value() + ascent * (time - floor.time());
        }
    }

    public TimeValuePair getFirst() {
        return mapping.higher(new TimeValuePair(Integer.MIN_VALUE,0));
    }        
    
    public TimeValuePair getLast() {
        return mapping.lower(new TimeValuePair(Integer.MAX_VALUE,0));
    }    
    
    public double getLastValue() {
        return mapping.lower(new TimeValuePair(Integer.MAX_VALUE,0)).value();
    }

    /**
     * Maps the integer <code>time</code> to the integer <code>value</code>.
     * Runtime O(log (number of steps)).
     * @param time the integer for which an association is to be made.
     * @param value the value to be associated with the integer.
     */
    public void set(int time, double value) {
        TimeValuePair tip = new TimeValuePair(time, value);
        TimeValuePair floor = mapping.floor(tip);
        if (floor != null && floor.equals(tip)) {
            floor.set(value);
        } else {
            mapping.add(tip);
        }
    }

    /**
     * A convenience method for increasing the value associated with a single
     * integer. It is equivalent to <code>increase(time, time+1, amount)</code>.
     * Runtime O(log (number of steps)).
     * @param time the integer for which the associated value is to be 
     * increased.
     * @param amount the amount by which the value is to be increased.
     */
    public void increase(int time, double amount) {
        increase(time, time + 1, amount);
    }

    /**
     * A convenience method for increasing the values associated with a range of
     * integers from <code>fromTime</code> (inclusively) to <code>toTime</code>
     * (exclusively). It is equivalent to calling 
     * <code>set(time, get(time) + amount)</code> for all integers in the range
     * defined above. Runtime O(log (number of steps) + number of steps changed).
     * @param fromTime the first integer for which the associated value is to be
     * increased.
     * @param toTime the first integer after <code>fromTime</code> for which 
     * the associated value is <b>not</b> to be increased.
     * @param amount the amount by which the values are to be increased.
     * @exception IllegalArgumentException if <code>toTime</code> is less equal
     * than <code>fromTime</code>.
     */
    public void increase(int fromTime, int toTime, double amount) {
        if (toTime <= fromTime) {
            throw new IllegalArgumentException("toTime must be greater than fromTime.");
        }
        TimeValuePair from = new TimeValuePair(fromTime, 0);
        TimeValuePair to = new TimeValuePair(toTime, 0);
        TimeValuePair first = mapping.floor(from);
        double lastBefore = mapping.lower(to).value();
        TimeValuePair last = mapping.ceiling(to);
        if (first.time() < fromTime) {
            mapping.add(new TimeValuePair(fromTime, first.value() + amount));
        } else {
            first.set(first.value() + amount);
        }
        if (toTime < last.time()) {
            mapping.add(new TimeValuePair(toTime, lastBefore));
        }
        NavigableSet<TimeValuePair> subSet = mapping.subSet(mapping.floor(from), false, mapping.ceiling(to), false);
        for (TimeValuePair tip : subSet) {
            tip.set(tip.value() + amount);
        }
        if (mapping.lower(first) != null && mapping.lower(first).value() == first.value()) {
            mapping.remove(first);
        }
    }

    /**
     * A convenience method for decreasing the value associated with a single
     * integer. It is equivalent to 
     * <code>increase(time, time+1, -amount)</code>.
     * Runtime O(log (number of steps)).
     * @param time the integer for which the associated value is to be 
     * decreased.
     * @param amount the amount by which the value is to be decreased.
     */
    public void decrease(int time, double amount) {
        increase(time, -amount);
    }

    /**
     * A convenience method for decreasing the values associated with a range of
     * integers from <code>fromTime</code> (inclusively) to <code>toTime</code>
     * (exclusively). It is equivalent to calling 
     * <code>increase(fromTime, toTime, -amount)</code>.
     * defined above. Runtime O(log (number of steps) + number of steps changed).
     * @param fromTime the first integer for which the associated value is to be
     * decreased.
     * @param toTime the first integer after <code>fromTime</code> for which 
     * the associated value is <b>not</b> to be decreased.
     * @param amount the amount by which the values are to be decreased.
     */
    public void decrease(int fromTime, int toTime, double amount) {
        decrease(fromTime, toTime, -amount);
    }

    /**
     * Adds the specified mapping to this mapping. 
     * <code>IntegerDoubleMapping</code> objects are treated as mathematical
     * functions Z -> Z for this purpose.
     * Runtime(number of steps in <code>mapping</code>).
     * @param mapping the mapping to be added to this mapping.
     * @exception NullPointerException if mapping is null.
     */
    public IntegerDoubleMapping add(IntegerDoubleMapping map) {
        IntegerDoubleMapping result = new IntegerDoubleMapping(linear || map.isPiecewiseLinear());
        SortedSet<TimeValuePair> set = new TreeSet<TimeValuePair>();
        set.addAll(mapping);
        set.addAll(map.mapping);
        for (TimeValuePair tip : set) {
            result.mapping.add(new TimeValuePair(tip.time(), get(tip.time()) + map.get(tip.time())));
        }
        return result;
    }

    /**
     * Subtracts the specified mapping from this mapping. 
     * <code>IntegerDoubleMapping</code> objects are treated as mathematical
     * functions Z -> Z for this purpose.
     * Runtime(number of steps in <code>mapping</code>).
     * @param mapping the mapping to be subtracted to this mapping.
     * @exception NullPointerException if mapping is null.
     */
    public void subtractMapping(IntegerDoubleMapping mapping) {
        TimeValuePair last = null;
        for (TimeValuePair tip : mapping) {
            if (last == null) {
                last = tip;
                continue;
            }
            decrease(last.time(), tip.time(), last.value());
        }
    }

    public IntegerDoubleMapping add(double scalar) {
        IntegerDoubleMapping result = new IntegerDoubleMapping();
        for (TimeValuePair tip : mapping) {
            result.set(tip.time(), tip.value() + scalar);
        }
        return result;
    }

    public IntegerDoubleMapping subtract(double scalar) {
        return add(-scalar);
    }

    public IntegerDoubleMapping multiply(double scalar) {
        IntegerDoubleMapping result = new IntegerDoubleMapping();
        for (TimeValuePair tip : mapping) {
            result.set(tip.time(), tip.value() * scalar);
        }
        return result;
    }

    public IntegerDoubleMapping divide(double scalar) {
        return multiply(1.0 / scalar);
    }

    public IntegerDoubleMapping shift(int amount) {
        IntegerDoubleMapping result = new IntegerDoubleMapping();
        for (TimeValuePair tip : mapping) {
            if (tip.time() == Integer.MIN_VALUE || tip.time() == Integer.MAX_VALUE) {
                result.set(tip.time(), tip.value());
            } else {
                result.set(tip.time() + amount, tip.value());
            }
        }
        return result;
    }

    public IntegerDoubleMapping invert() {
        IntegerDoubleMapping result = new IntegerDoubleMapping();
        for (TimeValuePair tip : mapping) {
            result.set((int) Math.round(tip.value()), tip.time());
        }
        result.set(0, 0.0);
        return result;
    }

    /**
     * Computes the integral of this mapping. 
     * <code>IntegerDoubleMapping</code> is considered a step function with 
     * step starts defined by its mapping for this purpose. The result (a
     * piecewise linear function) is interpreted as an 
     * <code>IntegerDoubleMapping</code> by defining a map for each start point
     * of a linear segment. Runtime(number of steps).
     * @return the integral of this mapping. 
     */
    public IntegerDoubleMapping integral() {
        IntegerDoubleMapping summatedMapping = new IntegerDoubleMapping(true);
        int sum = 0;
        int lastTime = 0;
        double lastValue = 0;
        boolean first = true;
        for (TimeValuePair tip : mapping) {
            first = (tip.time() == Integer.MIN_VALUE);
            if (!first) {
                sum += (tip.time() - lastTime) * lastValue;
                summatedMapping.set(tip.time(), sum);
            }
            lastTime = tip.time();
            lastValue = tip.value();
        }
        return summatedMapping;
    }

    /**
     * Returns an iterator over the time - integer mappings in this
     * <code>IntegerDoubleMapping</code>. Runtime O(1).
     * @return an iterator over the time - integer mappings in this
     * <code>IntegerDoubleMapping</code>.
     */
    @Override
    public Iterator<TimeValuePair> iterator() {
        return mapping.iterator();
    }

    /**
     * Returns a copy of this mapping. Runtime O(number of steps).
     * @return a copy of this mapping.
     */
    @Override
    public IntegerDoubleMapping clone() {
        IntegerDoubleMapping clone = new IntegerDoubleMapping();
        for (TimeValuePair tip : mapping) {
            clone.set(tip.time(), tip.value());
        }
        return clone;
    }

    /**
     * Checks whether the specified object is equivalent to this mapping. This
     * is the case if and only if the specified object is not <code>null</code,
     * of type <code>IntegerDoubleMapping</code> and makes exactly the same
     * time - integer associations. Runtime O(number of steps).
     * @param o the object to be compared with this mapping.
     * @return true if the specified object is an 
     * <code>IntegerDoubleMapping</code> that is equivalent with this one,
     * <code>false</code>  otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return (o != null) && (o instanceof IntegerDoubleMapping) && ((IntegerDoubleMapping) o).mapping.equals(mapping);
    }

    /**
     * Returns a hash code for this mapping. Runtime O(number of steps).
     * @return the hash code of the underlying <code>TreeSet</code>.
     */
    @Override
    public int hashCode() {
        return mapping.hashCode();
    }

    /**
     * Returns a string representation of this mapping.
     * Runtime O(number of steps).
     * @return the string representation of the underlying <code>TreeSet</code>.
     */
    @Override
    public String toString() {
        String result = mapping.toString().replace(", " + Integer.MAX_VALUE + " = 0.0", "");
        result = result.replace(Integer.MIN_VALUE + " = 0.0, ", "");
        return result;
    }

    /**
     * A utility class used for the underlying <code>TreeSet</code>. A mapping
     * of a time <code>t</code> to an value <code>v</code> is stored by 
     * adding a <code>TimeValuePair (t,v)</code> to the tree set.
     */
    public class TimeValuePair implements Cloneable, Comparable<TimeValuePair> {

        /**
         * Stores the time component of the pair.
         */
        protected int time;
        /**
         * Stores the value component of this pair.
         */
        protected double value;

        /**
         * Constructs a new <code>TimeValuePair</code> with the specified
         * values. Runtime O(1).
         * @param time the time component of the pair.
         * @param value the value component of the pair.
         */
        protected TimeValuePair(int time, double value) {
            this.time = time;
            this.value = value;
        }

        /**
         * Sets the value of this <code>TimeValuePair</code> to the specified
         * value. Runtime O(1).
         * @param newValue the new value of this time - value pair.
         */
        public void set(double newValue) {
            value = newValue;
        }

        /**
         * Returns the time component of this <code>TimeValuePair</code>.
         * Runtime O(1).
         * @return the time component of this <code>TimeValuePair</code>.
         */
        public int time() {
            return time;
        }

        /**
         * Returns the value component of this <code>TimeValuePair</code>.
         * Runtime O(1).
         * @return the value component of this <code>TimeValuePair</code>.
         */
        public double value() {
            return value;
        }

        /**
         * Compares two <code>TimeValuePair</code>s by their time component.
         * Runtime O(1).
         * @param o the <code>TimeValuePair</code> to be compared.
         * @return 0 if this pair is equal to the specified pair; a value less
         * than 0 if this pair's time component is numerically less than the 
         * specified pair's time component; and a value greater than 0 if this 
         * pair's time component is numerically greater than the specified 
         * pair's time component.
         */
        public int compareTo(TimeValuePair o) {
            long temp = time;
            return Math.round(Math.signum(temp - o.time()));
        }

        /**
         * Creates a copy of this <code>TimeValuePair</code>. Runtime O(1).
         * @return a copy of this <code>TimeValuePair</code>.
         */
        @Override
        public TimeValuePair clone() {
            return new TimeValuePair(time, value);
        }

        /**
         * Compares this <code>TimeValuePair</code> to the specified object.
         * The result is true if and only if the argument is not null and is a
         * <code>TimeValuePair</code> which has the same time component. The
         * value component is ignored. This is due to the fact that the
         * underlying tree set must not contain two 
         * <code>TimeValuePair</code>s with the same time component.
         * Runtime O(1).
         * @param o the object this mapping is to be compared with.
         * @return <code>true</code> if the given object represents an 
         * <code>TimeValuePair</code> equivalent to this pair, 
         * <code>false</code> otherwise.
         */
        @Override
        public boolean equals(Object o) {
            return (o != null) && (o instanceof TimeValuePair) && ((TimeValuePair) o).time() == time;
        }

        /**
         * Returns a hash code for this <code>TimeValuePair</code>. Since
         * this hash code should be consistent with {@link #equals} just the
         * time component of the pair is used. Runtime O(1).
         * @return the time component of this <code>TimeValuePair</code>.
         */
        @Override
        public int hashCode() {
            return time;
        }

        /**
         * Returns a string representation of this <code>TimeValuePair</code>.
         * This representation is of the form "time = value". Runtime O(1).
         * @return a string representation of this <code>TimeValuePair</code>.
         */
        @Override
        public String toString() {
            return String.format("%1$s = %2$s", time, value);
        }
    }
}

