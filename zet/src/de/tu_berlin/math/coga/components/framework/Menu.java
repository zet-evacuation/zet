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
 * MenuFramework.java
 * Created on 17. Dezember 2007, 02:06:21
 */

package de.tu_berlin.math.coga.components.framework;

import de.tu_berlin.math.coga.common.localization.DefaultLocalization;
import de.tu_berlin.math.coga.common.localization.AbstractLocalization;
import de.tu_berlin.math.coga.components.Localizer;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

/**
 * Creates a complete framework to create menus inclusive shortcuts and
 * {@link java.awt.event.ActionListener}. Currently only works with CTRL-shortcuts
 * @author Jan-Philipp Kappmeier, Christian Ullenboom (Java ist auch eine Insel)
 */
public class Menu {
	/** The localization object used to generate localized menu titles. */
	private static AbstractLocalization loc = DefaultLocalization.getSingleton();
	private Menu() { }

	public static AbstractLocalization getLocalization() {
		return loc;
	}
	
	/**
	 * Sets a {@code AbstractLocalization} object that is used to generate localized
	 * menu entries.
	 * @param localization the {@code AbstractLocalization} opbject.
	 */
	public static void setLoc( AbstractLocalization loc ) {
		Menu.loc = loc;
	}
	
	
	public static <T extends AbstractButton> T processMnemonic( T guiObject, String s ) {
		if( s.indexOf( "_" ) > -1 ) {
			int pos = s.indexOf( "_" );
			char c = s.charAt( pos + 1 );
			StringBuffer sb = new StringBuffer( s ).delete( pos, pos + 1 );
			guiObject.setText( sb.toString() );
			guiObject.setMnemonic( c );
		} else {
			guiObject.setText( s );
		}
		return guiObject;
	}
	
	/**
	 * Returns the mnemonic character of a string. This is the first character
	 * after the first "_".
	 * @param s the string
	 * @return the mnemonic character
	 */
	public static char getMnemonic( String s ) {
		if( s.indexOf( "_" ) > -1 ) {
			int pos = s.indexOf( "_" );
			char c = s.charAt( pos + 1 );
			return c;
		} else
			return 0;
	}

	/**
	 * Returns a "_" from a given string representing a menu title.
	 * @param s the string
	 * @return the string without the first "_"
	 */
	public static String extractMnemonic( String s ) {
		if( s.indexOf( "_" ) > -1 ) {
			int pos = s.indexOf( "_" );
			return (new StringBuffer( s ).delete( pos, pos + 1 )).toString();
		} else
			return s;
	}
	
	/**
	 * Creates a new menu entry to chreate herarchial menus
	 * @param m the parent JLocalizedMenu
	 * @param localizationString the title
	 * @return the newly created menu
	 */
	public static JMenu addMenu( JMenu m, String localizationString ) {
		if( localizationString.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}
		JMenu menu = processMnemonic( Localizer.instance().registerNewComponent( new JMenu(), localizationString ), loc.getString(localizationString) );
		m.add( menu );
		return menu;
	}
	
	public static JMenu addMenu( JMenuBar b, String localizationString ) {
		JMenu menu = processMnemonic( Localizer.instance().registerNewComponent( new JMenu(), localizationString ), loc.getString( localizationString ) );
		b.add( menu );
		return menu;
	}
	
	public static JMenu addMenu( JPopupMenu p, String localizationString ) {
		JMenu menu = Localizer.instance().registerNewComponent( new JMenu(), loc.getString( localizationString ) );
		p.add( menu );
		return menu;
	}
	
	/**
	 * Insert a JLocalizedMenuItem to a given JLocalizedMenu.
	 * @param m the JLocalizedMenu
	 * @param localizedString the localization string for the menu title, if "-" separator
	 * @param keyChar the shortcut character
	 * @param al an ActionListener
	 * @param commandString an action command
	 * @param inputEvent 
	 * @return   a JLocalizedMenuItem
	 */
	public static JMenuItem addMenuItem( JMenu m, String localizedString, char keyChar, ActionListener al, String commandString, int inputEvent ) { 
		if( localizedString.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}
		
		JMenuItem menuItem =  processMnemonic( Localizer.instance().registerNewComponent( new JMenuItem(), localizedString ), loc.getString( localizedString ) );
		m.add( menuItem );

		menuItem.setAccelerator( KeyStroke.getKeyStroke( inputEvent, inputEvent ));

		if( keyChar != 0 )
			menuItem.setAccelerator( KeyStroke.getKeyStroke( keyChar, inputEvent ) );
		if( al != null )
			menuItem.addActionListener( al );
		if( commandString != null )
			menuItem.setActionCommand( commandString );

		return menuItem;
	}

	/**
	 * Insert a JLocalizedMenuItem to a given JLocalizedMenu.
	 * @param m the JLocalizedMenu
	 * @param localizationString the menu title
	 * @param keyEvent the shortcut key code
	 * @param al an ActionListener
	 * @param commandString an action command
	 * @param inputEvent 
	 * @return   a JLocalizedMenuItem
	 */
	public static JMenuItem addMenuItem( JMenu m, String localizationString, int keyEvent, ActionListener al, String commandString, int inputEvent ) {
		if( localizationString.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}
		JMenuItem menuItem =  processMnemonic( Localizer.instance().registerNewComponent( new JMenuItem(), localizationString ), loc.getString( localizationString ) );
		m.add( menuItem );

		if( keyEvent != KeyEvent.VK_UNDEFINED )
			menuItem.setAccelerator( KeyStroke.getKeyStroke( keyEvent, inputEvent ) );
		if( al != null )
			menuItem.addActionListener( al );
		if( commandString != null )
			menuItem.setActionCommand( commandString );

		return menuItem;
	}
	
	
	/**
	 * Insert a JLocalizedMenuItem to a given JLocalizedMenu.
	 * @param m the JLocalizedMenu
	 * @param s the menu title
	 * @param keyChar the shortcut character
	 * @param al an ActionListener
	 * @param commandString an action command
	 * @return   a JLocalizedMenuItem
	 */
	public static JMenuItem addMenuItem( JMenu m, String localizedString, char keyChar, ActionListener al, String commandString ) {
		return addMenuItem( m, localizedString, keyChar, al, commandString, InputEvent.CTRL_DOWN_MASK);
	}

	/**
	 * Insert a JLocalizedMenuItem to a given JLocalizedMenu.
	 * @param m the JLocalizedMenu
	 * @param s the menu title
	 * @param keyChar the shortcut character
	 * @param mask the shortcut mask, such as 'CTRL' or 'SHIFT+CTRL'
	 * @param al an ActionListener
	 * @param commandString an action command
	 * @return   a JLocalizedMenuItem
	 */
	public static JMenuItem addMenuItem( JMenu m, String localizedString, char keyChar, int mask, ActionListener al, String commandString ) {
		return addMenuItem( m, localizedString, keyChar, al, commandString, mask );
	}

	public static JMenuItem addMenuItem( JMenu m, String localizedString, char c, ActionListener al ) {
		return addMenuItem( m, localizedString, c, al, null, InputEvent.CTRL_DOWN_MASK );
	}

	public static JMenuItem addMenuItem( JMenu m, String localizedString, char c ) {
		return addMenuItem( m, localizedString, c, null, null, InputEvent.CTRL_DOWN_MASK );
	}

	public static JMenuItem addMenuItem( JMenu m, String localizedString ) {
		return addMenuItem( m, localizedString, (char)0, null, null, 0 );
	}

	public static JMenuItem addMenuItem( JMenu m, String localizedString, ActionListener al ) {
		return addMenuItem( m, localizedString, (char)0, al, null, 0 );
	}

	public static JMenuItem addMenuItem( JMenu m, String localizedString, ActionListener al, String commandString ) {
		return addMenuItem( m, localizedString, (char)0, al, commandString, 0 );
	}
	
	/**
	 * Insert a JLocalizedMenuItem to a given JLocalizedMenu.
	 * @param m the JLocalizedMenu
	 * @param localizationString the menu title
	 * @param c the checked status
	 * @param al an ActionListener
	 * @param commandString 
	 * @return   a JLocalizedMenuItem
	 */
	public static JCheckBoxMenuItem addCheckMenuItem( JMenu m, String localizationString, boolean c, ActionListener al, String commandString ) {
		if( localizationString.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}

		String s = loc.getString( localizationString );
		char mn = getMnemonic( s );
		JCheckBoxMenuItem menuItem;
		if( mn != 0 ) {
			menuItem = Localizer.instance().registerNewComponent( new JCheckBoxMenuItem( extractMnemonic( s ), c ), localizationString );
			menuItem.setMnemonic( mn );
		} else
			menuItem = new JCheckBoxMenuItem( s, c );

		if( al != null ) {
			menuItem.addActionListener( al );
		}
		if( commandString != null )
			menuItem.setActionCommand( commandString );

		m.add( menuItem );
		return menuItem;
	}
	
	public static JCheckBoxMenuItem addCheckMenuItem( JMenu m, String localizationString, boolean c, ActionListener al ) {
		return addCheckMenuItem( m, localizationString, c, al, null );
	}
	
	public static JCheckBoxMenuItem addCheckMenuItem( JMenu m, String localizationString, boolean c ) {
		return addCheckMenuItem( m, localizationString, c, null, null );
	}
	
	public static JRadioButtonMenuItem addRadioButtonMenuItem( JMenu m, String localizationString, boolean selected, ActionListener al, String commandString ) {
		if( localizationString.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}

		String text = loc.getString( localizationString );
		char mn = getMnemonic( text );
		JRadioButtonMenuItem menuItem;
		if( mn != 0 ) {
			menuItem = Localizer.instance().registerNewComponent( new JRadioButtonMenuItem( extractMnemonic( text ), selected ), localizationString );
			menuItem.setMnemonic( mn );
		} else
			menuItem = new JRadioButtonMenuItem( text, selected );
		
		if( al != null ) {
			menuItem.addActionListener( al );
		}
		if( commandString != null )
			menuItem.setActionCommand( commandString );

		m.add( menuItem );
		return menuItem;
	}
	
	// Popups

	/**
	 * Insert a JLocalizedMenuItem to a given JPopupMenu.
	 * @param m the JPopupMenu
	 * @param localizationKey the menu title
	 * @param al an ActionListener
	 * @param commandString an action command
	 * @return   a JLocalizedMenuItem
	 */
	public static JMenuItem addMenuItem( JPopupMenu m, String localizationKey, ActionListener al, String commandString ) {
		if( localizationKey.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}

		JMenuItem menuItem = Localizer.instance().registerNewComponent( new JMenuItem(), localizationKey );
		m.add( menuItem );
		
		if( commandString != null )
			menuItem.setActionCommand( commandString );
		if( al != null )
			menuItem.addActionListener( al );
		return menuItem;
	}

	public static JMenuItem addMenuItem( JPopupMenu m, String localizationKey ) {
		return addMenuItem( m, localizationKey, null, null );
	}

	public static JMenuItem addMenuItem( JPopupMenu m, String locallizationKey, ActionListener al ) {
		return addMenuItem( m, locallizationKey, al, null );
	}
}