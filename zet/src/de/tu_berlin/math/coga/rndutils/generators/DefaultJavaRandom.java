/**
 * Class DefaultJavaRandom
 * Created 04.05.2008, 23:22:13
 */
package de.tu_berlin.math.coga.rndutils.generators;

import java.util.Random;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultJavaRandom extends Random implements GeneralRandom {

	public DefaultJavaRandom() {
		super();
	}

	public DefaultJavaRandom( long seed ) {
		super( seed );
	}

	@Override
	public byte nextByte() {
		return (byte) (next( 8 ));
	}

	@Override
	public char nextChar() {
		return (char) (next( 16 ));
	}

	@Override
	public short nextShort() {
		return (short) (next( 16 ));
	}

	@Override
	public String getName() {
		return "Default Java Random Number Generator";
	}

	@Override
	public String getDesc() {
		return "A linear congruential pseudorandom number generator. Uses the method defined by D. H. Lehmer and described by Donald E. Knuth in The Art of Computer Programming, Volume 2: Seminumerical Algorithms, section 3.2.1.";
	}
}
