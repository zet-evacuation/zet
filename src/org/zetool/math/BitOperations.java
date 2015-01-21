/* copyright 2010
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
 * BitOperations.java
 * Created 22.02.2010, 21:16:20
 */
package org.zetool.math;

/**
 * The class {@code BitOperations} is a utility class that provides some
 * bit operations.
 * @author Jan-Philipp Kappmeier
 */
public class BitOperations {
	/**
	 * Aviods instancint the utility class with combinatorial methods.
	 */
	private BitOperations() {
	}

	public static int bitLen( int n ) {
		return 31 - Integer.numberOfLeadingZeros( n );
	}

	/**
	 * Checks weather the {@code i}-th bit of a given number is set to 1.
	 * @param n the number which is tested
	 * @param i the bit position
	 * @return {@code true} if the {@code i}-th bit of {@code n} is 1, {@code false} otherwise
	 */
	public static boolean bitTest( int n, int i ) {
		return ((n & (1 << i)) != 0);
	}

	public static int maxNumber( int bits ) {
		int sum = 0;
		for( int i = 0; i <= bits; ++i )
			sum += 1 << i;
		return sum;
	}
}
