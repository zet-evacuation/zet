/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.gui;

import de.tu_berlin.coga.common.localization.Localization;
import de.tu_berlin.coga.common.localization.LocalizationManager;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GUILocalization {
	public final static Localization loc = LocalizationManager.getSingleton().getLocalization( "zet.gui.GUILocalization" );
					
//	private volatile static GUILocalization singleton;
//
//	private GUILocalization() throws MissingResourceException {
//		super( "zet.gui.GUILocalization" );
//	}
//
//	public static GUILocalization getSingleton() {
//		if( singleton == null )
//			synchronized( GUILocalization.class ) {
//				// thread safe: check again if pointer is null
//				if( singleton == null )
//					singleton = new GUILocalization();
//			}
//		return singleton;
//	}
}
