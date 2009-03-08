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
 *
 */
public class NanosecondTimeFormatter {
    
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
            case 0: return String.format("%1$s ns", time);
            case 1: return String.format("%1$s.%2$03d ms", time, last);// habe hier was geändet, ka was vorher da stand, gab einen utf-8-fehler also "...d?s"
            case 2: return String.format("%1$s.%2$03d ms", time, last);
            case 3: return String.format("%1$s.%2$03d s", time, last);
            default:
                throw new AssertionError("This should not happen.");
        }
    }
    
}
