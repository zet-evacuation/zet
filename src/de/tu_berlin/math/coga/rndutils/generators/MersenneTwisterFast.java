/**
 * Class MersenneTwisterFast
 * Erstellt 04.05.2008, 23:47:48
 */

package de.tu_berlin.math.coga.rndutils.generators;

import de.tu_berlin.math.coga.rndutils.generators.GeneralRandom;

/**
 * A wrapper class for the general use of random generators using {@link util.random.MersenneTwisterFast}.
 * @author Jan-Philipp Kappmeier
 */
public class MersenneTwisterFast extends de.tu_berlin.math.coga.rndutils.generators.original.MersenneTwisterFast implements GeneralRandom {
	/**
	 * @inheritDoc
	 */
	public MersenneTwisterFast() {
		super();
	}

	/**
	 * @inheritDoc
	 */
	public MersenneTwisterFast( final long seed ) {
		super( seed );
	}


	/**
	 * @inheritDoc
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
