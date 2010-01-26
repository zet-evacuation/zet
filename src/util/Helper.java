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
/**
 * Class Helper
 * Erstellt 08.07.2008, 19:04:34
 */

package util;

import java.text.NumberFormat;

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
		} catch( InterruptedException ignore ) { }
	}
	
	/**
	 * Calculates the faculty of fileSizes given number.
	 * @param n the parameter
	 * @return the faculty of n
	 * @throws java.lang.IllegalArgumentException if n is negative
	 */
	public static final long faculty( int n ) {
		if( n < 0 )
			throw new java.lang.IllegalArgumentException( "Negative parameter value for faculty!" );
		if( n == 0 )
			return 1;
		else
			return( n * faculty(n-1));
	}
	
	/**
	 * Formats a given number of bits to the largest possible unit. Supported
	 * units are "Bytes", "Kilobytes", "Megabytes" and "Gigabytes".
	 * @param bits the bits
	 * @return the string in the calculated unit with one decimal place and the shortcut for the unit
	 */
	static final String[] fileSizes = {"Bytes", "KB", "MB", "GB"};
	public static final String bitToMaxFilesizeUnit( double bits ) {
		NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits( 1 );
		double ret = bits/8;	// rets is in bytes
		int i = 0;
		while( ret > 1024 && i++ < fileSizes.length-1 )
			ret /= 1024;
		return n.format( ret ) + " " + fileSizes[Math.min( i, fileSizes.length-1 )];
	}

	public final static boolean isBetween( char value, char lower, char upper ) {
		return value >= lower &&  value <= upper;
	}
}