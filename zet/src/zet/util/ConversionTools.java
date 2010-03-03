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

/*
 * ConversionTools.java
 * Created on 3. Dezember 2007, 23:34
 */
package zet.util;

/**
 * A class that provides methods to convert length into the internal z-format
 * values. That means all meters are rounded to 3 decimal places.
 * @author Jan-Philipp Kappmeier
 */
public final class ConversionTools {

	/**
	 * No instantiating of <code>ConversationTools</code> possible.
	 */
	private ConversionTools() {
	}

	/**
	 * <p>Translates a specified integer value to a double value. The integer
	 * value is assumed to have the millimeter unit and the returned value has
	 * the unit meter.</p>
	 * <p>The resulting value is rounded to three decimal places, which is the
	 * default accurancy in <code>z</code>-format.</p>
	 * @param mm the millimeter value
	 * @return the meter value for the millimeter
	 */
	public final static double toMeter( int mm ) {
		return roundScale3( mm / 1000.0 );
	}

	/**
	 * Converts a floating point  value to an int value, using three decimal
	 * places. The floating point values are rounded fair using
	 * {@link #roundScale3( double )}. This represents the accurancy used by the
	 * {@code z}-format.
	 * @param x the floating point value
	 * @return the same value as {@code z}-format precision integer value
	 */
	public final static int floatToInt( double x ) {
		return (int) Math.round( roundScale3( x ) * 1000.0f );
	}

	/**
	 * Rounds a given Double to three valid decimal places. The used rounding-method
	 * is {@link Math#rint( double )}, which rounds fair. That means, the functions
	 * rounds normal in most cases. If the last rounding-significant place is 5, it
	 * rounds up if the next number is odd and it rounds down if the next number is
	 * even.
	 * @param d the value to be rounded
	 * @return a fair rounded value to three valid decimal places
	 */
	public final static double roundScale3( double d ) {
		return Math.rint( d * 1000 ) / 1000.0f;
	}
}
