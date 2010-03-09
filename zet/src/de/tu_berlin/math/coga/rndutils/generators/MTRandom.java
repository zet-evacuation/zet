/**
 * MTRandom.java
 * Created 04.05.2008, 23:51:13
 */
package de.tu_berlin.math.coga.rndutils.generators;

/**
 * A wrapper class for the general use of random generators using
 * {@link de.tu_berlin.math.coga.rndutils.generators.original.MTRandom}.
 * @author Jan-Philipp Kappmeier
 */
public class MTRandom extends de.tu_berlin.math.coga.rndutils.generators.original.MTRandom implements GeneralRandom {

	/**
	 * Initializes the random generator.
	 * @see de.tu_berlin.math.coga.rndutils.generators.original.MTRandom
	 */
	public MTRandom() {
		super();
	}

	/**
	 * Initializes the random generator in a special compatibility mode.
	 * @see de.tu_berlin.math.coga.rndutils.generators.original.MTRandom
	 */
	public MTRandom( boolean compatible ) {
		super( compatible );
	}

	/**
	 * Initializes the random generator with a given seed.
	 * @see de.tu_berlin.math.coga.rndutils.generators.original.MTRandom
	 */
	public MTRandom( long seed ) {
		super( seed );
	}

	/**
	 * Initializes the random generator with an array of seeds.
	 * @see de.tu_berlin.math.coga.rndutils.generators.original.MTRandom
	 */
	public MTRandom( byte[] buf ) {
		super( buf );
	}

	/**
	 * Initializes the random generator with an array of seeds.
	 * @see de.tu_berlin.math.coga.rndutils.generators.original.MTRandom
	 */
	public MTRandom( int[] buf ) {
		super( buf );
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

	public String getName() {
		return "Mersene Twister MT19937";
	}

	public String getDesc() {
		return "Mersenne Twister in the LGPL Licence";
	}
}
