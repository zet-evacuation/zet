/**
 * GraphLocalization.java
 * Created: Nov 12, 2010, 3:13:18 PM
 */
package zz_old_ds.graph;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphLocalization {
	final public static Localization loc = LocalizationManager.getManager().getLocalization( "ds.graph.GraphLocalization" );
//private volatile static GraphLocalization singleton;
//
//	private GraphLocalization() throws MissingResourceException {
//		super( "" );
//	}
//
//	// synchronized keyword has been removed from here
//	public static GraphLocalization getSingleton() {
//		// needed because once there is singleton available no need to acquire
//		// monitor again & again as it is costly
//		if( singleton == null )
//			synchronized( ZLocalization.class ) {
//				// this is needed if two threads are waiting at the monitor at the
//				// time when singleton was getting instantiated
//				if( singleton == null )
//					singleton = new GraphLocalization();
//			}
//		return singleton;
//	}
}
