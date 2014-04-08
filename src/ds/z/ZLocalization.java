/**
 * ZLocalization.java
 * Created: Nov 12, 2010, 2:32:12 PM
 */
package ds.z;

import de.tu_berlin.coga.common.localization.AbstractLocalization;
import java.util.MissingResourceException;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ZLocalization extends AbstractLocalization {
	private volatile static ZLocalization singleton;

	private ZLocalization() throws MissingResourceException {
		super( "ds.z.ZLocalization" );
	}

	// synchronized keyword has been removed from here
	public static ZLocalization getSingleton() {
		// needed because once there is singleton available no need to acquire
		// monitor again & again as it is costly
		if( singleton == null )
			synchronized( ZLocalization.class ) {
				// this is needed if two threads are waiting at the monitor at the
				// time when singleton was getting instantiated
				if( singleton == null )
					singleton = new ZLocalization();
			}
		return singleton;
	}
}
