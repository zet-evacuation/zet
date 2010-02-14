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
package util;

import java.text.NumberFormat;

/**
 * Some helper methods that are needed every now and then.
 * @author jan-Philipp Kappmeier
 */
public final class Helper {

	/** An array containing text for several file size measures. */
	static final String[] fileSizes = {"Bytes", "KiB", "MiB", "GiB"};

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
	 * Calculates the faculty of fileSizes given number.
	 * @param n the parameter
	 * @return the faculty of n
	 * @throws java.lang.IllegalArgumentException if n is negative
	 */
	public static final long faculty( int n ) {
		if( n < 0 )
			throw new java.lang.IllegalArgumentException( "Negative parameter value for faculty!" );
		if( n < 2 )
			return 1;
		else
			return (n * faculty( n - 1 ));
	}

	/**
	 * Formats a given number of bits to the largest possible unit. Supported
	 * units are "Bytes", "Kilobytes", "Megabytes" and "Gigabytes".
	 * @param bits the bits
	 * @return the string in the calculated unit with one decimal place and the shortcut for the unit
	 */
	public static final String bitToMaxFilesizeUnit( double bits ) {
		final NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits( 1 );
		double ret = bits / 8;	// rets is in bytes
		int i = 0;
		while(ret > 1024 && i++ < fileSizes.length - 1)
			ret /= 1024;
		return n.format( ret ) + " " + fileSizes[Math.min( i, fileSizes.length - 1 )];
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

	private static long N;

	/**
	 * Computes the factorial of a number.
	 * @param n
	 * @return
	 */
	public static long factorial( int n ) {
		if( n < 0 )
			throw new IllegalArgumentException( "n must not be at least zero." );
		if( n < 2 )
			return 1;

		long p = 1;
		long r = 1;
		N = 1;

		int log2n = 31 - Integer.numberOfLeadingZeros( n );
		int h = 0, shift = 0, high = 1;

		while( h != n ) {
			shift += h;
			h = n >>> log2n--;
			int len = high;
			high = (h & 1) == 1 ? h : h - 1;
			len = (high - len) / 2;

			if( len > 0 )
				r = r * (p *= product( len ));
		}
		return r << shift;
	}

	/**
	 * Private helping method used to compute the factorial.
	 * @param n
	 * @return
	 */
	private static long product( int n ) {
		int m = n / 2;
		if( m == 0 )
			return N += 2;
		if( n == 2 )
			return (N += 2) * (N += 2);
		return product( n - m ) * product( m );
	}
//	public static int bitLen( int n ) {
//		if( n > 0 )
//			if( bitTest2( n, 15 ) )
//				if( bitTest2( n, 23 ) )
//					if( bitTest2( n, 27 ) )
//						if( bitTest2( n, 29 ) )
//							if( bitTest2( n, 30 ) )
//								return 30;
//							else
//								return 29;
//						else
//							if( bitTest2( n, 28 ) )
//								return 28;
//							else
//								return 27;
//					else
//						if( bitTest2( n, 25 ) )
//							if( bitTest2( n, 26 ) )
//								return 26;
//							else
//								return 25;
//						else
//							if( bitTest2( n, 24 ) )
//								return 24;
//							else
//								return 23;
//				else
//					if( bitTest2( n, 19 ) )
//						if( bitTest2( n, 21 ) )
//							if( bitTest2( n, 22 ) )
//								return 22;
//							else
//								return 21;
//						else
//							if( bitTest2( n, 20 ) )
//								return 20;
//							else
//								return 19;
//					else
//						if( bitTest( n, 17 ) )
//							if( bitTest( n, 18 ) )
//								return 18;
//							else
//								return 17;
//						else
//							if( bitTest( n, 16 ) )
//								return 16;
//							else
//								return 15;
//			else
//				if( bitTest2( n, 7 ) )
//					if( bitTest2( n, 11 ) )
//						if( bitTest2( n, 13 ) )
//							if( bitTest2( n, 14 ) )
//								return 14;
//							else
//								return 13;
//						else
//							if( bitTest2( n, 12 ) )
//								return 12;
//							else
//								return 11;
//					else
//						if( bitTest2( n, 9 ) )
//							if( bitTest2( n, 10 ) )
//								return 10;
//							else
//								return 9;
//						else
//							if( bitTest2( n, 8 ) )
//								return 8;
//							else
//								return 7;
//				else
//					if( bitTest2( n, 3 ) )
//						if( bitTest2( n, 5 ) )
//							if( bitTest2( n, 6 ) )
//								return 6;
//							else
//								return 5;
//						else
//							if( bitTest2( n, 4 ) )
//								return 4;
//							else
//								return 3;
//					else
//						if( bitTest2( n, 1 ) )
//							if( bitTest2( n, 2 ) )
//								return 2;
//							else
//								return 1;
//						else
//							return 0;
//		else
//			return 31;
//	}


		public static int bitLen( int n ) {
		return n > 0 ?
			n >= 1 << 15 ?
				n >= 1 << 23 ?
					n >= 1 << 27 ?
						n >= 1 << 29 ?
							n >= 1 << 30 ? 30 : 29
						: n >= 1 << 28 ? 28 : 27
					: n >= 1 << 25 ?
							n >= 1 << 26 ? 26 : 25
						: n >= 1 << 24 ? 24 : 23
				: n >= 1 << 19 ?
						n >= 1 << 21 ?
							n >= 1 << 22 ? 22 : 21
						: n >= 1 << 20 ? 20 : 19
					: n >= 1 << 17 ?
							n >= 1 << 18 ? 18 : 17
						: n >= 1 << 16 ? 16 : 15
			: n >= 1 << 7 ?
					n >= 1 << 11 ?
						n >= 1 << 13 ?
							n >= 1 << 14 ? 14 : 13
						: n >= 1 << 12 ? 12 : 11
					: n >= 1 << 9 ?
							n >= 1 << 10 ? 10 : 9
						: n >= 1 << 8 ? 8 : 7
				: n >= 1 << 3 ?
						n >= 1 << 5 ?
							n >= 1 << 6 ? 6 : 5
						: n >= 1 << 4 ? 4 : 3
					: n >= 1 << 1 ?
							n >= 1 << 2 ? 2 : 1
						: 0
		: 31;
	}

	public static int bitLenFor( int n ) {
		if( n < 0 )
			return 31;
		if( (n & 1 << 30) != 0 )
			return 30;
		int l = 31;
		int r = 0;
		int mid;
		while( true ) {
			if( l - r <= 1 )
				return n >= 1 << l ? l : r;
			if( n >= 1 << (mid = (l + r) / 2) )
				r = mid;
			else
				l = mid - 1;
		}
	}

	public static int bitLenTrivial( int n ) {
		for( int i = 31; i >= 0; --i )
			if( (n & 1 << i) != 0 )
				return i;
		return 0;
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

	/**
	 * Computes the rounded down logarithm to the basis 2 of an integral number.
	 * @param n the integral number
	 * @return the logarithm to the basis 2 rounded down
	 */
	public static int log2Floor( int n ) {
		if( n <= 0 )
			throw new IllegalArgumentException( "n > 0 required" );
		return bitLen( n ) - 1;
	}

	public static void main( String[] args ) {

		System.out.println( factorial( 5 ) );


//		long count = 1 << 36;
//
//		long start, end;
//		// Test für die Optimierte variante:
//		count = 0;
//		do {//
//			++count;
//			start = System.nanoTime();
//			for( int j = 1; j < count; j++ )
//				for( int i = 0; i < (1 << 25); ++i ) {
//					bitLenTrivial( i );
//				}
//			end = System.nanoTime();
//			System.out.println( count + " Läufe: " + (end-start) + " Nanosekunden" );
//		} while( (end - start) < (1000000000L * 50) );
		//long end = System.currentTimeMillis();

//		long run1 = end - start;
//		System.out.println( "Run1: " + run1 );

//		start = System.currentTimeMillis();
//		// Test für die Optimierte variante:
//		for( int j = 1; j < count; j++ )
//			for( int i = 0; i < (1 << 31); ++i ) {
//				bitLenFor( i );
//			}
//		end = System.currentTimeMillis();
//
//		long run2 = end - start;
//		System.out.println( "Run2: " + run2 );

//		for( int i = 0; i <= 31; i++ )
//			System.out.println( bitLen( maxNumber( i ) ) + " soll sein: " + i + " --- " + bitLenFor( maxNumber( i ) ) );
//
//		System.out.println();
//		for( int i = 0; i <= 31; i++ )
//			System.out.println( bitLen( 1 << i ) + " soll sein: " + i + " --- " + bitLenFor( ( 1 << i ) ) );
//
//		System.out.println();
//		for( int i = 1; i <= 32; i++ )
//			System.out.println( bitLen( (1 << i) - 1 ) + " soll sein: " + (i - 1) + " --- " + bitLenFor( ( 1 << i ) - i ) );
	}
}



