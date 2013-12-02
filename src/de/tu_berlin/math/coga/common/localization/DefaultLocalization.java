/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.common.localization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultLocalization extends Localization {
	/** The singleton instance. */
	private volatile static DefaultLocalization singleton = null;
	
	public DefaultLocalization() {
		super( "de.tu_berlin.math.coga.common.localization.default" );
	}	

	public static DefaultLocalization getSingleton() {
		// needed because once there is singleton available no need to acquire
		// monitor again & again as it is costly
		if( singleton == null )
			synchronized( DefaultLocalization.class ) {
				// this is needed if two threads are waiting at the monitor at the
				// time when singleton was getting instantiated
				if( singleton == null )
					singleton = new DefaultLocalization();
			}
		return singleton;
	}
}

