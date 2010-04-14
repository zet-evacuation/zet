/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * AlgorithmEvent.java
 *
 */
package de.tu_berlin.math.coga.common.algorithm;

import java.text.DateFormat;
import java.util.Date;

/**
 * The abstract base class for algorithm events. It records the algorithm the
 * event occurred in and the time at which the event occured. The time should be
 * given in milliseconds elapsed since midnight, January 1, 1970 UTC.
 *
 * @author Martin Groß
 */
public abstract class AlgorithmEvent {

    /**
     * The algorithm the event occurred in.
     */
    private Algorithm algorithm;
    /**
     * The time at which the event occurred in milliseconds elapsed since
     * midnight, January 1, 1970 UTC.
     */
    private long eventTime;

    /**
     * A protected constructor for subclasses to initialize the basic fields of
     * this class.
     * @param algorithm the algorithm the event occurred in.
     * @param eventTime the time at which the event occurred in milliseconds
     * elapsed since midnight, January 1, 1970 UTC.
     */
    protected AlgorithmEvent(Algorithm algorithm, long eventTime) {
        this.algorithm = algorithm;
        this.eventTime = eventTime;
    }

    /**
     * Returns the algorithm the event occurred in.
     * @return the algorithm the event occurred in.
     */
    public final Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Returns the time at which the event occurred in milliseconds elapsed
     * since midnight, January 1, 1970 UTC.
     * @return the time at which the event occurred.
     */
    public final long getEventTime() {
        return eventTime;
    }

    /**
     * Returns the event time formatted as a string by using the class
     * java.util.DateFormat.
     * @return the event time formatted as a string.
     */
    public final String getFormattedEventTime() {
        return DateFormat.getDateInstance().format(new Date(eventTime));
    }
	@Override
		public String toString() {
			return "a";
		}
}
