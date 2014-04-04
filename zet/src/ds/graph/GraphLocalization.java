/**
 * GraphLocalization.java
 * Created: Nov 12, 2010, 3:13:18 PM
 */
package ds.graph;

import de.tu_berlin.math.coga.common.localization.AbstractLocalization;
import ds.z.ZLocalization;
import java.util.MissingResourceException;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphLocalization extends AbstractLocalization {
private volatile static GraphLocalization singleton;

	private GraphLocalization() throws MissingResourceException {
		super( "ds.graph.GraphLocalization" );
	}

	// synchronized keyword has been removed from here
	public static GraphLocalization getSingleton() {
		// needed because once there is singleton available no need to acquire
		// monitor again & again as it is costly
		if( singleton == null )
			synchronized( ZLocalization.class ) {
				// this is needed if two threads are waiting at the monitor at the
				// time when singleton was getting instantiated
				if( singleton == null )
					singleton = new GraphLocalization();
			}
		return singleton;
	}
}
