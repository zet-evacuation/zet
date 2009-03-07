/**
 * Class DefaultJavaRandom
 * Erstellt 04.05.2008, 23:22:13
 */

package util.random.generators;

import java.util.Random;
import util.random.GeneralRandom;

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
