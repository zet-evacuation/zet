/**
 * Class MersenneTwister
 * Erstellt 04.05.2008, 23:44:13
 */

package de.tu_berlin.math.coga.rndutils.generators;

/**
 * A wrapper class for the general use of random generators using {@link util.random.MersenneTwister}.
 * @author Jan-Philipp Kappmeier
 */
public class MersenneTwister extends de.tu_berlin.math.coga.rndutils.generators.original.MersenneTwister implements GeneralRandom {
	
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
