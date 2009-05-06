/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * Button.java
 * Created on 19.12.2007, 00:27:51
 */

package gui.components.framework;

import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Automatic creation of JButtons.
 * @author Jan-Philipp Kappmeier
 */
public class Button {

	/** Private constructor avoids instantiation. */
	private Button() {}
	
	// Create mnemonic shortcut
	private static JButton processMnemonic( String s ) {
		if( s.indexOf( "_" ) > -1 ) {
			int pos = s.indexOf( "_" );
			char c = s.charAt( pos + 1 );
			StringBuffer sb = new StringBuffer( s ).delete( pos, pos + 1 );
			JButton b = new JButton();
			b.setMnemonic(c);
			return b;
		} else {
			return new JButton( s );
		}
	}

	/**
	 * Adds Actionlistener to the instance and returns the same instance.
	 * @param b
	 * @param al
	 * @param commandString
	 * @return
	 */
	private static JButton addActionListener( JButton b, ActionListener al, String commandString ) {
		if( al != null )
			b.addActionListener( al );
		if( commandString != null )
			b.setActionCommand(commandString );
		return b;
	}
	
	public static JButton newButton( String title, ActionListener al, String commandString, String toolTip ) {
		JButton b = processMnemonic( title );
		if( toolTip != null )
			b.setToolTipText( toolTip );
		return addActionListener( b, al, commandString );
	}

	public static JButton newButton( String title, ActionListener al, String commandString ) {
		JButton b = processMnemonic( title );
		return addActionListener( b, al, commandString );
	}
	
	public static JButton newButton( String title, ActionListener al ) {
		JButton b = processMnemonic( title );
		return addActionListener( b, al, null );
	}
	
	public static JButton newButton( String title, String toolTip ) {
		JButton b = processMnemonic( title );
		b.setToolTipText( toolTip );
		return b;
	}

	public static JButton newButton( String title ) {
		return processMnemonic( title );
	}

	public static JButton newButton( javax.swing.Icon i, ActionListener al, String commandString ) {
		JButton b = new JButton( i );
		return addActionListener( b, al, commandString );
	}

	public static JButton newButton( IconSet is, ActionListener al, String commandString, String toolTip ) {
		JButton b = new JButton( Icon.newIcon( is ) );
		if( toolTip != null )
			b.setToolTipText( toolTip );
		return addActionListener( b, al, commandString );
	}

	public static JButton newButton( IconSet is, ActionListener al, String commandString ) {
		JButton b = new JButton( Icon.newIcon( is ) );
		return addActionListener( b, al, commandString );
	}

	public static JButton newButton( IconSet is, ActionListener al) {
		return newButton( is, al, null );
	}

	public static JButton newButton( IconSet is, String toolTip ) {
		return newButton( is, null, null, toolTip );
	}

	public static JButton newButton( IconSet is ) {
		return newButton( is, null, null );
	}
}