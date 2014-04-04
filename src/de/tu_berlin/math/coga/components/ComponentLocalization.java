/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.components;

import de.tu_berlin.math.coga.common.localization.AbstractLocalization;
import java.util.MissingResourceException;

/**
 * The localization class for gui components present in the package
 * {@link de.tu_berlin.math.coga.components}.
 * @author Jan-Philipp Kappmeier
 */
public class ComponentLocalization extends AbstractLocalization {

	private volatile static ComponentLocalization singleton;

	private ComponentLocalization() throws MissingResourceException {
		super( "de.tu_berlin.math.coga.components.ComponentLocalization" );
	}

	public static ComponentLocalization getSingleton() {
		if( singleton == null )
			synchronized( ComponentLocalization.class ) {
				// thread safe: check again if pointer is null
				if( singleton == null )
					singleton = new ComponentLocalization();
			}
		return singleton;
	}
	
}
