/*
 * ZETProperties.java
 * Created 09.09.2009, 14:56:29
 */

package gui;

import ds.PropertyContainer;

/**
 * The class <code>ZETProperties</code> ...
 * @author Kap
 */
public class ZETProperties {

	/**
	 * Creates a new instance of <code>ZETProperties</code>.
	 */
	private ZETProperties() { }

	public static boolean isDefaultFloorHidden() {
		return PropertyContainer.getInstance().getAsBoolean( "editor.options.view.hideDefaultFloor" );
	}

	public static void setDefaultFloorHidden( boolean defaultFloorHidden ) {
		PropertyContainer.getInstance().set( "editor.options.view.hideDefaultFloor", defaultFloorHidden );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "ZETProperties";
	}
}
