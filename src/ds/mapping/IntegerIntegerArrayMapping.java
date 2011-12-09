/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * IntegerIntegerMapping.java
 *
 */

package ds.mapping;

import ds.mapping.IntegerIntegerArrayMapping.TimeIntegerPair;
import java.util.Arrays;

/**
 * The {@code IntegerIntegerMapping} class represents a mapping from 
 * integers to integers. It is a specialized version of 
 * {@code IntegerObjectMapping} made for mappings from integers to 
 * integers. These mappings are particulary useful for functions taking
 * time as a parameter. Therefore values of this mapping's domain are referred
 * to as time henceforth.
 * Internally, the {@code IntegerIntegerMapping} is considered as a step
 * function. Consequently, the mapping is stored as a sorted collection of step
 * starts which is obviously sufficient to encode the mapping. 
 * The size needed to encode an {@code IntegerIntegerMapping} is therefore
 * linear in the number of steps required.
 * In order to access steps efficiently, a TreeSet is used which in turn is 
 * based on a red-black tree. This allows the addition, removal and search for
 * steps in O(log (number of steps)) time.
 * For mappings of integers to arbitrary values see
 * {@link IntegerObjectMapping}.
 */
public class IntegerIntegerArrayMapping {
    
    /**
     * Stores the mapping internally. Must not be null.
     */
    private final int[] mapping;
    
    /**
     * Creates a new {@code IntegerIntegerMapping} that is defined for all
     * integer values. Initially, all integers are mapped to 0. Runtime O(1).
     */
    public IntegerIntegerArrayMapping(int timeHorizon) {
        mapping = new int[timeHorizon];
    }
    
    public IntegerIntegerArrayMapping(int timeHorizon, int value) {
        this(timeHorizon);
        Arrays.fill(mapping, value);
    }
    
    
    public boolean lessEqual(int start, int end, int value) {
        for (int i = start; i < end; i++) {
            if (get(i) > value) return false;
        }
        return true;
    }

    public boolean greaterEqual(int start, int end, int value) {
        for (int i = start; i < end; i++) {
            if (get(i) < value) return false;
        }
        return true;
    }        
    
    /**
     * Returns the integer associated with the specified value. 
     * Runtime O(log (number of steps)).
     * @param time the value for which the associated integer is to be returned.
     * @return the integer associated with the specified value.
     */
    public int get(int time) {
        return mapping[time];
    }
    
    /**
     * Maps the integer {@code time} to the integer {@code value}.
     * Runtime O(log (number of steps)).
     * @param time the integer for which an association is to be made.
     * @param value the value to be associated with the integer.
     */
    public void set(int time, int value) {
        mapping[time] = value;
    }

    public void decrease(int time, int amount) {
        mapping[time] -= amount;
    }

    public void decrease(int start, int end, int amount) {
        for (int i = start; i < end; i++) {
            mapping[i] -= amount;
        }
    }    
    
    public void increase(int time, int amount) {
        mapping[time] += amount;
    }    

    public void increase(int start, int end, int amount) {
        for (int i = start; i < end; i++) {
            mapping[i] += amount;
        }
    }        
    
    public boolean isZero() {
        for (int i = 0; i < mapping.length; i++) {
            if (mapping[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns a string representation of this mapping.
     * Runtime O(number of steps).
     * @return the string representation of the underlying {@code TreeSet}.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < mapping.length; i++) {
            builder.append(i + " = " + mapping[i] + ", ");
        }
        builder.append("]");
        return builder.toString();
    }
    
    /**
     * A utility class used for the underlying {@code TreeSet}. A mapping
     * of a time {@code t} to an integer value {@code v} is stored by 
     * adding a {@code TimeIntegerPair (t,v)} to the tree set.
     */
    public class TimeIntegerPair implements Cloneable, Comparable<TimeIntegerPair> {
        
        /**
         * Stores the time component of the pair.
         */                
        protected int time;
        
        /**
         * Stores the integer component of this pair.
         */
        protected int value;
        
        /**
         * Constructs a new {@code TimeIntegerPair} with the specified
         * values. Runtime O(1).
         * @param time the time component of the pair.
         * @param value the integer component of the pair.
         */
        protected TimeIntegerPair(int time, int value) {
            this.time = time;
            this.value = value;
        }
        
        /**
         * Sets the value of this {@code TimeIntegerPair} to the specified
         * value. Runtime O(1).
         * @param newValue the new value of this time - integer pair.
         */
        public void set(int newValue) {
            value = newValue;
        }
        
        /**
         * Returns the time component of this {@code TimeIntegerPair}.
         * Runtime O(1).
         * @return the time component of this {@code TimeIntegerPair}.
         */
        public int time() {
            return time;
        }

        /**
         * Returns the integer component of this {@code TimeIntegerPair}.
         * Runtime O(1).
         * @return the integer component of this {@code TimeIntegerPair}.
         */
        public int value() {
            return value;
        }

        /**
         * Compares two {@code TimeIntegerPair}s by their time component.
         * Runtime O(1).
         * @param o the {@code TimeIntegerPair} to be compared.
         * @return 0 if this pair is equal to the specified pair; a value less
         * than 0 if this pair's time component is numerically less than the 
         * specified pair's time component; and a value greater than 0 if this 
         * pair's time component is numerically greater than the specified 
         * pair's time component.
         */
        public int compareTo(TimeIntegerPair o) {
            if (time > o.time) {
                return 1;
            } else if (time < o.time) {
                return -1;
            } else {
                return 0;
            }
            //long temp = time;
            //return Math.round(Math.signum(temp - o.time()));
        }
        
        /**
         * Creates a copy of this {@code TimeIntegerPair}. Runtime O(1).
         * @return a copy of this {@code TimeIntegerPair}.
         */
        @Override
        public TimeIntegerPair clone() {
            return new TimeIntegerPair(time, value);
        }
        
        /**
         * Compares this {@code TimeIntegerPair} to the specified object.
         * The result is true if and only if the argument is not null and is a
         * {@code TimeIntegerPair} which has the same time component. The
         * integer component is ignored. This is due to the fact that the
         * underlying tree set must not contain two 
         * {@code TimeIntegerPair}s with the same time component.
         * Runtime O(1).
         * @param o the object this mapping is to be compared with.
         * @return {@code true} if the given object represents an 
         * {@code TimeIntegerPair} equivalent to this pair, 
         * {@code false} otherwise.
         */
        @Override
        public boolean equals(Object o) {
            return (o != null) && (o instanceof TimeIntegerPair) && ((TimeIntegerPair) o).time() == time;
        }
        
        /**
         * Returns a hash code for this {@code TimeIntegerPair}. Since
         * this hash code should be consistent with {@link #equals} just the
         * time component of the pair is used. Runtime O(1).
         * @return the time component of this {@code TimeIntegerPair}.
         */
        @Override
        public int hashCode() {
            return time;
        }
        
        /**
         * Returns a string representation of this {@code TimeIntegerPair}.
         * This representation is of the form "time = value". Runtime O(1).
         * @return a string representation of this {@code TimeIntegerPair}.
         */
        @Override
        public String toString() {
            return String.format("%1$s = %2$s", time, value);
        }        
    }
}
