/**
 * DefaultLoc.java
 * Created: Nov 12, 2010, 2:49:05 PM
 */
package de.tu_berlin.math.coga.common.localization;

import java.util.MissingResourceException;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultLoc extends Localization {
	private volatile static DefaultLoc singleton;

	private DefaultLoc() throws MissingResourceException {
		super( "de.tu_berlin.math.coga.common.localization.zevacuate" );
	}

	public static DefaultLoc getSingleton() {
		// needed because once there is singleton available no need to acquire
		// monitor again & again as it is costly
		if( singleton == null )
			synchronized( ZLocalization.class ) {
				// this is needed if two threads are waiting at the monitor at the
				// time when singleton was getting instantiated
				if( singleton == null )
					singleton = new DefaultLoc();
			}
		return singleton;
	}

}
