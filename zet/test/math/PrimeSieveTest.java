/*
 * PrimeSieveTest.java
 * Created 22.02.2010, 19:49:43
 */

package math;

import de.tu_berlin.math.coga.math.PrimeSieve;
import junit.framework.TestCase;

/**
 * The class <code>PrimeSieveTest</code> tests different prime sieve variants
 * and compares them.
 * @author Jan-Philipp Kappmeier
 */
public class PrimeSieveTest extends TestCase {
	int[] pl2 = new int[] {2};
	int[] pl3 = new int[] {2, 3};
	int[] pl5 = new int[] {2, 3, 5};
	int[] pl7 = new int[] {2, 3, 5, 7};
	int[] pl11 = new int[] {2, 3, 5, 7, 11};
	int[] pl13 = new int[] {2, 3, 5, 7, 11, 13};
	int[] pl17 = new int[] {2, 3, 5, 7, 11, 13, 17};
	int[] pl19 = new int[] {2, 3, 5, 7, 11, 13, 17, 19};
	int[] pl23 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23};
	int[] pl29 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};
	int[] pl31 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31};
	int[] pl37 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
	int[] pl41 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41};
	int[] pl43 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43};
	int[] pl47 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47};
	int[] pl53 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
	int[] pl59 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59};
	int[] pl61 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};
	int[] pl67 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67};
	int[] pl71 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71};
	int[] pl73 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73};
	int[] pl79 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79};
	int[] pl83 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83};
	int[] pl89 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89};
	int[] pl97 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97};
	int[] pl101 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};
	int[] pl103 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103};
	int[] pl107 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107};
	int[] pl109 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109};
	int[] pl113 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113};
	int[] pl117 = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 117};
	// 127 is next prime
	
	int[][] parray = new int[][] { pl2, pl3, pl3, pl5, pl5, pl7, pl7, pl7, pl7, pl11,
	pl11,pl13,pl13,pl13,pl13,pl17,pl17,pl19,pl19,pl19,pl19,pl23,pl23,pl23,pl23,pl23,pl23,pl29,pl29,
	pl31,pl31,pl31,pl31,pl31,pl31,pl37,pl37,pl37,pl37,pl41,pl41,pl43,pl43,pl43,pl43,pl47,pl47,pl47,pl47,
	pl47,pl47,pl53,pl53,pl53,pl53,pl53,pl53,pl59,pl59,pl61,pl61,pl61,pl61,pl61,pl61,pl67,pl67,pl67,pl67,
	pl71,pl71,pl73,pl73,pl73,pl73,pl73,pl73,pl79,pl79,pl79,pl79,pl83,pl83,pl83,pl83,pl83,pl83,pl89,pl89,
	pl89,pl89,pl89,pl89,pl89,pl89,pl97,pl97,pl97,pl97,pl101,pl101,pl103,pl103,pl103,pl103,pl107,pl107,pl109,
	pl109,pl109,pl109,pl113,pl113,pl113,pl113,pl113,pl113,pl113,pl113,pl113,pl113,pl113,pl113,pl113,pl113
	};

	  public void testPrimeSieve() throws Exception {
			System.out.println( "Testing prime sieve for n = 16..." );
			int n = 16;
			PrimeSieve p = new PrimeSieve( n );
			p.computeThird();
			giveOutPrimes( p );
			check( p, pl13 );

			System.out.println( "Test correct number of primes..." );
			for( int i = 2; i <= 126; ++i ) {
				System.out.print( i + " " );
				p = new PrimeSieve( i );
				p.computeThird();
				check( p, parray[i-2] );
			}
			System.out.println();
			System.out.println();
		}

	  public void testPrimeSieveLowMem() throws Exception {
			System.out.println( "Testing Low Mem Algorithmus" );
			System.out.println( "Testing prime sieve for n = 16..." );
			int n = 16;
			PrimeSieve p = new PrimeSieve( n );
			p.computeLowMem();
			giveOutPrimes( p );
			check( p, pl13 );

			System.out.println( "Test correct number of primes..." );
			for( int i = 2; i <= 126; ++i ) {
				System.out.print( i + " " );
				p = new PrimeSieve( i );
				p.compute();
				check( p, parray[i-2] );
			}
			System.out.println();
			System.out.println();
		}

	  public void testADW3() throws Exception {
			System.out.println( "Testing Algorithmus der Woche Optimiert3" );
			System.out.println( "Testing prime sieve for n = 16..." );
			int n = 16;
			PrimeSieve p = new PrimeSieve( n );
			p.computeADW3();
			giveOutPrimes( p );
			check( p, pl13 );

			System.out.println( "Test correct number of primes..." );
			for( int i = 2; i <= 126; ++i ) {
				System.out.print( i + " " );
				p = new PrimeSieve( i );
				p.computeADW3();
				check( p, parray[i-2] );
			}
			System.out.println();
			System.out.println();
		}

	  public void testADW3Half() throws Exception {
			System.out.println( "Testing Algorithmus der Woche Optimiert3-half" );
			System.out.println( "Testing prime sieve for n = 16..." );
			int n = 16;
			PrimeSieve p = new PrimeSieve( n );
			p.computeADW3Half();
			giveOutPrimes( p );
			check( p, pl13 );

			System.out.println( "Test correct number of primes..." );
			for( int i = 2; i <= 126; ++i ) {
				System.out.print( i + " " );
				p = new PrimeSieve( i );
				p.computeADW3();
				check( p, parray[i-2] );
			}
			System.out.println();
			System.out.println();
		}

	  public void testADW3Third() throws Exception {
			System.out.println( "Testing Algorithmus der Woche Optimiert3-third" );
			System.out.println( "Testing prime sieve for n = 16..." );
			int n = 16;
			PrimeSieve p = new PrimeSieve( n );
			p.computeADW3Third();
			giveOutPrimes( p );
			check( p, pl13 );

			System.out.println( "Test correct number of primes..." );
			for( int i = 2; i <= 126; ++i ) {
				System.out.print( i + " " );
				p = new PrimeSieve( i );
				p.computeADW3Third();
				check( p, parray[i-2] );
			}
			System.out.println();
			System.out.println();
		}

	  public void testADW3ThirdLowMem() throws Exception {
			System.out.println( "Testing Algorithmus der Woche Optimiert3-third" );
			System.out.println( "Testing prime sieve for n = 16..." );
			int n = 16;
			PrimeSieve p = new PrimeSieve( n );
			p.computeADW3ThirdLowMem();
			giveOutPrimes( p );
			check( p, pl13 );

			System.out.println( "Test correct number of primes..." );
			for( int i = 2; i <= 126; ++i ) {
				System.out.print( i + " " );
				p = new PrimeSieve( i );
				p.computeADW3ThirdLowMem();
				check( p, parray[i-2] );
			}
			System.out.println();
			System.out.println();
		}

		public void testOptPrimeSieve() throws Exception {
			System.out.println( "Testing Luschnys PrimeSieve" );
			System.out.println( "Testing prime sieve for n = 16..." );
			int n = 16;
			PrimeSieve p = new PrimeSieve( n );
			p.computeLuschny();
			giveOutPrimes( p );
			check( p, pl13 );

			System.out.println( "Test correct number of primes..." );
			for( int i = 2; i <= 126; ++i ) {
				System.out.print( i + " " );
				p = new PrimeSieve( i );
				p.computeLuschny();
				check( p, parray[i-2] );
			}
			System.out.println();
			System.out.println();
		}

		public void testAtkin() throws Exception {
			System.out.println( "Testing Atkin sieve" );
			System.out.println( "Testing prime sieve for n = 16..." );
			int n = 16;
			PrimeSieve p = new PrimeSieve( n );
			p.computeAtkin();
			giveOutPrimes( p );
			check( p, pl13 );

			System.out.println( "Test correct number of primes..." );
			for( int i = 2; i <= 126; ++i ) {
				System.out.print( i + " " );
				p = new PrimeSieve( i );
				p.computeLuschny();
				check( p, parray[i-2] );
			}
			System.out.println();
			System.out.println();
		}

		private void giveOutPrimes( PrimeSieve p ) {
			for( int i = 0; i < p.getPrimeCount(); ++i )
				System.out.print( p.getPrime( i+1 ) + ' ' );
			System.out.println();
		}

		private void check( PrimeSieve p, int[] compare ) {
			assertEquals( "Check correct number of primes: ", compare.length, p.getPrimeCount() );
			for( int i = 1; i <= p.getPrimeCount(); ++i ) {
				assertEquals( "Check prime " + i + " of " + p.getPrimeCount() + ": ", compare[i-1], p.getPrime(i) );
			}
		}
}