/**
 * Class MersenneTwisterFast
 * Erstellt 04.05.2008, 23:47:48
 */
package de.tu_berlin.math.coga.rndutils.generators;

/**
 * A wrapper class for the general use of random generators using {@link de.tu_berlin.math.coga.rndutils.generators.original.MersenneTwisterFast}.
 * @author Jan-Philipp Kappmeier
 */
public class MersenneTwisterFast extends de.tu_berlin.math.coga.rndutils.generators.original.MersenneTwisterFast implements GeneralRandom {

	/**
	 * Initializes the {@link de.tu_berlin.math.coga.rndutils.generators.original.MersenneTwisterFast} generator.
	 */
	public MersenneTwisterFast() {
		super();
	}

	/**
	 * Initializes the {@link de.tu_berlin.math.coga.rndutils.generators.original.MersenneTwisterFast} generator
	 * with a given seed.
	 */
	public MersenneTwisterFast( final long seed ) {
		super( seed );
	}

	/**
	 * Initializes the {@link de.tu_berlin.math.coga.rndutils.generators.original.MersenneTwisterFast} generator
	 * with an array of given seeds.
	 */
	public MersenneTwisterFast( final int[] array ) {
		super( array );
	}

	public String getName() {
		return "Mersene Twister MT19937 Fast";
	}

	public String getDesc() {
		return "";
	}
}
