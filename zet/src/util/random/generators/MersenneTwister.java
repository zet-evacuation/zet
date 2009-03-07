/**
 * Class MersenneTwister
 * Erstellt 04.05.2008, 23:44:13
 */

package util.random.generators;

import util.random.GeneralRandom;

/**
 * A wrapper class for the general use of random generators using {@link util.random.MersenneTwister}.
 * @author Jan-Philipp Kappmeier
 */
public class MersenneTwister extends util.random.MersenneTwister implements GeneralRandom {
	
	/**
	 * @inheritDoc
	 */
	public MersenneTwister() {
		super();
	}
    
	/**
	 * @inheritDoc
	 */
	public MersenneTwister( final long seed ) {
		super( seed );
	}
    
	/**
	 * @inheritDoc
	 */
	public MersenneTwister( final int[] array ) {
		super( array );
		}

	public String getName() {
		return "Mersenne Twister MT19937";
	}

	public String getDesc() {
		return "";
	}
}
