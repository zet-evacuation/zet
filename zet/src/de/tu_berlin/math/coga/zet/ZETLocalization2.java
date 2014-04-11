/**
 * DefaultLoc.java
 * Created: Nov 12, 2010, 2:49:05 PM
 */
package de.tu_berlin.math.coga.zet;

import de.tu_berlin.coga.common.localization.AbstractLocalization;
import de.tu_berlin.coga.common.localization.Localization;
import de.tu_berlin.coga.common.localization.LocalizationManager;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ZETLocalization2 {
	//private volatile static ZETLocalization singleton;
	public final static Localization loc = LocalizationManager.getSingleton().getLocalization( "de.tu_berlin.math.coga.zet.zevacuate" );

//
//	public static ZETLocalization getSingleton() {
//		// needed because once there is singleton available no need to acquire
//		// monitor again & again as it is costly
//		if( singleton == null )
//			synchronized( ZLocalization.class ) {
//				// this is needed if two threads are waiting at the monitor at the
//				// time when singleton was getting instantiated
//				if( singleton == null )
//					singleton = new ZETLocalization();
//			}
//		return singleton;
//	}
}
