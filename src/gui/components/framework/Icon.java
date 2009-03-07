/*
 * Icon.java
 * Created on 19.12.2007, 00:28:00
 */

package gui.components.framework;

import javax.swing.ImageIcon;

/**
 * A static class that allows storing and saving icons.
 * @author Jan-Philipp Kappmeier
 */
public class Icon {
	private static String path = "./icons/";
	
	/**
	 * Private constructor avoids instantiation.
	 */
	private Icon() { }
	
	public static javax.swing.Icon newIcon( IconSet icon ) {
		return new ImageIcon( path + icon.getName() );
	}
	
	public static javax.swing.Icon newIcon( String path ) {
		return new ImageIcon( path );
	}
}
