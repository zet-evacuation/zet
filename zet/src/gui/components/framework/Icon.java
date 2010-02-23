/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
