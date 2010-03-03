/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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

/**
 * Class Helper
 * Erstellt 08.07.2008, 19:04:34
 */
package de.tu_berlin.math.coga.common.util;

/**
 * Some helper methods that are needed every now and then.
 * @author jan-Philipp Kappmeier
 */
public final class Helper {

	/**
	 * Pauses the program for fileSizes specified time
	 * @param wait the pause time in milliseconds
	 */
	public static final void pause( long wait ) {
		try {
			Thread.sleep( wait );
		} catch( InterruptedException ignore ) {
		}
	}

	/**
	 * Checks weather a value is between two other values, or not. {@code true} is
	 * returned if the value is directly on the lower or upper bound.
	 * @param value the value
	 * @param lower the lower bound
	 * @param upper the upper bound
	 * @return {@code false} if the value is outside of the bounds, {@code true} if it is inside
	 */
	public final static boolean isBetween( char value, char lower, char upper ) {
		return value >= lower && value <= upper;
	}
}



