/*
 * IntegerObjectMapping.java
 *
 */

package ds.graph;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * The <code>IntegerObjectMapping</code> class represents a mapping from 
 * integers to arbitrary. Values of this mapping's domain are referred
 * to as time henceforth, since time being the domain will be the primary
 * application for such mappings.
 * Internally, the <code>IntegerObjectMapping</code> is considered as a step
 * function. Consequently, the mapping is stored as a sorted collection of step
 * starts which is obviously sufficient to encode the mapping. 
 * The size needed to encode an <code>IntegerObjectMapping</code> is therefore
 * linear in the number of steps required.
 * In order to access steps efficiently, a TreeSet is used which in turn is 
 * based on a red-black tree. This allows the addition, removal and search for
 * steps in O(log (number of steps)) time.
 * For mappings of integers to integers see
 * {@link IntegerIntegerMapping}.
 */
public class IntegerObjectArrayMapping<R> {
    
    /**
     * Stores the mapping internally. Must not be null.
     */
    private final R[] mapping;
    
    private int highestIndex;
    
    /**
     * Creates a new <code>IntegerObjectMapping</code> that is defined for all
     * integer values. Initially, all integers are mapped to null. Runtime O(1).
     */
    public IntegerObjectArrayMapping(int timeHorizon, Class<R> type) {
        mapping = (R[]) Array.newInstance(type, timeHorizon);
    }

    /**
     * Returns the value associated with the specified integer. 
     * Runtime O(log (number of steps)).
     * @param time the integer for which the associated value is to be returned.
     * @return the value associated with the specified integer.
     */
    public R get(int time) {
        return mapping[time];
    }

    public int getLastTime() {
        return highestIndex;
    }
    
    /**
     * Maps the integer <code>time</code> to the object <code>value</code>.
     * Runtime O(log (number of steps)).
     * @param time the integer for which an association is to be made.
     * @param value the value to be associated with the integer.
     */
    public void set(int time, R value) {
        mapping[time] = value;
        if (time > highestIndex) {
            highestIndex = time;
        }
    }
    
    /**
     * Returns a string representation of this mapping.
     * Runtime O(number of steps).
     * @return the string representation of the underlying <code>TreeSet</code>.
     */
    @Override
    public String toString() {
        return Arrays.deepToString(mapping);
    }
}