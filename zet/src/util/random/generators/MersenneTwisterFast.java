/**
 * Class MersenneTwisterFast
 * Erstellt 04.05.2008, 23:47:48
 */

package util.random.generators;

import util.random.GeneralRandom;

/**
 * A wrapper class for the general use of random generators using {@link util.random.MersenneTwisterFast}.
 * @author Jan-Philipp Kappmeier
 */
public class MersenneTwisterFast extends util.random.MersenneTwisterFast implements GeneralRandom {
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
