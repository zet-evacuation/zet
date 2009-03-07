/**
 * Class JavaSecureRandom
 * Erstellt 04.05.2008, 23:58:07
 */

package util.random.generators;

import java.security.SecureRandom;
import util.random.GeneralRandom;

/**
 * A wrapper for the {@link java.security.SecureRandom} class.
 * @author Jan-Philipp Kappmeier
 */
public class JavaSecureRandom extends SecureRandom implements GeneralRandom {
	/**
	 * @inheritDoc
	 */
	public JavaSecureRandom() {
		super();
  }

	/**
	 * @inheritDoc
	 */
	public JavaSecureRandom( byte seed[]) {
		super( seed );
	}

	public String getName() {
		return "Java Secure Random Number Generator";
	}

	public String getDesc() {
		return "";
	}
}