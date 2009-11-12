/**
 * Class MTRandom
 * Erstellt 04.05.2008, 23:51:13
 */

package de.tu_berlin.math.coga.rndutils.generators;

import de.tu_berlin.math.coga.rndutils.generators.GeneralRandom;

/**
 * A wrapper class for the general use of random generators using {@link util.random.MTRandom}.
 * @author Jan-Philipp Kappmeier
 */
public class MTRandom extends de.tu_berlin.math.coga.rndutils.generators.original.MTRandom implements GeneralRandom {
	/**
	 * @inheritDoc
	 */	
	public MTRandom() {
		super();
	}

	/**
	 * @inheritDoc
	 */
	public MTRandom( boolean compatible ) {
		super( compatible );
	}

	/**
	 * @inheritDoc
	 */
	public MTRandom( long seed ) {
		super( seed );
	}

	/**
	 * @inheritDoc
	 */
	public MTRandom( byte[] buf ) {
		super( buf );
	}

	/**
	 * @inheritDoc
	 */
	public MTRandom( int[] buf ) {
		super( buf );
	}

	public String getName() {
		return "Mersene Twister MT19937";
	}

	public String getDesc() {
		return "Mersenne Twister in the LGPL Licence";
	}
}
