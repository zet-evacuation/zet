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
 * Button.java
 * Created on 19.12.2007, 00:27:51
 */

package de.tu_berlin.math.coga.components.framework;

import de.tu_berlin.math.coga.common.localization.DefaultLocalization;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.components.Localizer;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

/**
 * Automatic creation of {@link JButton}s.
 * @author Jan-Philipp Kappmeier
 */
public class Button {
	/** The localization object used to generate localized menu titles. */
	private static Localization loc = DefaultLocalization.getSingleton();

	/** Private constructor avoids instantiation. */
	private Button() {}
	
	/**
	 * Adds {@link ActionListener} to the instance and returns the same instance.
	 * @param b
	 * @param al
	 * @param commandString
	 * @return
	 */
	private static AbstractButton addActionListener( AbstractButton b, ActionListener al, String commandString ) {
		if( al != null )
			b.addActionListener( al );
		if( commandString != null )
			b.setActionCommand( commandString );
		return b;
	}
	
	public static JButton newButton( String localizationString, ActionListener al, String commandString, String toolTip ) {
		JButton b = Menu.processMnemonic( Localizer.instance().registerNewComponent( new JButton(), localizationString ), loc.getString( localizationString ) );

		if( toolTip != null )
			b.setToolTipText( toolTip );
		return (JButton)addActionListener( b, al, commandString );
	}

	public static JButton newButton( String title, ActionListener al, String commandString ) {
		JButton b = Menu.processMnemonic( new JButton(), title );
		return (JButton)addActionListener( b, al, commandString );
	}
	
	public static JButton newButton( String title, ActionListener al ) {
		JButton b = Menu.processMnemonic( new JButton(), title );
		return (JButton)addActionListener( b, al, null );
	}
	
	public static JButton newButton( String title, String toolTip ) {
		JButton b = Menu.processMnemonic( new JButton(), title );
		b.setToolTipText( toolTip );
		return b;
	}

	public static JButton newButton( String title ) {
		return Menu.processMnemonic( new JButton(), title );
	}

	public static JButton newButton( javax.swing.Icon i, ActionListener al, String commandString ) {
		JButton b = new JButton( i );
		return (JButton)addActionListener( b, al, commandString );
	}

	public static JButton newButton( Icon ic, ActionListener al, String commandString, String toolTip ) {
		JButton b = new JButton( ic );
		if( toolTip != null )
			b.setToolTipText( toolTip );
		return (JButton)addActionListener( b, al, commandString );
	}

	public static JToggleButton newButton( Icon ic, ActionListener al, String commandString, String toolTip, boolean initialState ) {
		JToggleButton b = new JToggleButton( ic, initialState );
		if( toolTip != null )
			b.setToolTipText( toolTip );
		return (JToggleButton)addActionListener( b, al, commandString );
	}

	public static JButton newButton( Icon ic, ActionListener al) {
		return newButton( ic, al, null );
	}

	public static JButton newButton( Icon ic, String toolTip ) {
		return newButton( ic, null, null, toolTip );
	}

	public static JButton newButton( Icon ic ) {
		return newButton( ic, null, null );
	}
}