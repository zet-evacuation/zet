/**
 * Class DefaultJavaRandom
 * Erstellt 04.05.2008, 23:22:13
 */

package de.tu_berlin.math.coga.rndutils.generators;

import de.tu_berlin.math.coga.rndutils.generators.GeneralRandom;
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

	public String getName() {
		return "Default Java Random Number Generator";
	}

	public String getDesc() {
		return "";
	}
}
