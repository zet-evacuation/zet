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
 * NanosecondTimeFormatter.java
 *
 */
package util;

/**
 * An utility class to convert and format a time specified in nanoseconds into 
 * a string using an appropriate time unit. This class is mainly used to convert
 * algorithm runtimes measured by <code>System.nanoTime()</code> calls into a 
 * more human readable format.
 * 
 * @author Martin Groß
 */
public class NanosecondTimeFormatter {

    /**
     * Converts a time specified in nanoseconds into a time unit appropriate for 
     * the length of the specified time and formats it as a human readable 
     * string. For example, 1445997106 ns would be converted and formatted to
     * "1.445 s".
     * @param timeInNanoseconds the time in nanoseconds.
     * @return the converted and formatted string.
     */
    public static String formatTime(long timeInNanoseconds) {
        long time = timeInNanoseconds;
        int counter = 0;
        int last = 0;
        while (time >= 1000 && counter < 3) {
            last = (int) (time % 1000);
            time /= 1000;
            counter++;
        }
        switch (counter) {
            case 0:
                return String.format("%1$s ns", time);
            case 1:
                return String.format("%1$s.%2$03d µs", time, last);
            case 2:
                return String.format("%1$s.%2$03d ms", time, last);
            case 3:
                if (time <= 60) {
                    return String.format("%1$s.%2$03d s", time, last);
                } else if (time > 60) {
                    last = (int) (time % 60);
                    time /= 60;
                    return String.format("%1$s.%2$02d min", time, last);
                } else {
                    throw new AssertionError("This should not happen.");
                }
            default:
                throw new AssertionError("This should not happen.");
        }
    }
}
