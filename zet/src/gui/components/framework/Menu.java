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
 * MenuFramework.java
 * Created on 17. Dezember 2007, 02:06:21
 */

package gui.components.framework;

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
	private Menu() { }
	
	// Menuestuff
	private static JMenuItem processMnemonic( String s ) {
		return processMnemonic( new JMenuItem(), s );
	}
	
	private static JMenuItem processMnemonic( JMenuItem menu, String s ) {
		if( s.indexOf( "_" ) > -1 ) {
			int pos = s.indexOf( "_" );
			char c = s.charAt( pos + 1 );
			StringBuffer sb = new StringBuffer( s ).delete( pos, pos + 1 );
			menu.setText( sb.toString() );
			menu.setMnemonic( c );
		} else {
			menu.setText( s );
		}
		return menu;
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
	 * @param m the parent JMenu
	 * @param s the title
	 * @return the newly created menu
	 */
	public static JMenu addMenu( JMenu m, String s ) {
		if( s.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}
		JMenu menu = (JMenu)processMnemonic( new JMenu(), s );
		m.add( menu );
		return menu;
	}
	
	public static JMenu addMenu( JMenuBar b, String s ) {
		JMenu menu = (JMenu)processMnemonic( new JMenu(), s );
		b.add( menu );
		return menu;
	}
	
	public static JMenu addMenu( JPopupMenu p, String s ) {
		JMenu menu = new JMenu( s );
		p.add( menu );
		return menu;
	}
	
	/**
	 * Insert a JMenuItem to a given JMenu.
	 * @param m the JMenu
	 * @param s the menu title
	 * @param keyChar the shortcut character
	 * @param al an ActionListener
	 * @param commandString an action command
	 * @param inputEvent 
	 * @return   a JMenuItem
	 */
	public static JMenuItem addMenuItem( JMenu m, String s, char keyChar, ActionListener al, String commandString, int inputEvent ) { 
		if( s.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}

		JMenuItem menuItem = processMnemonic( s );
		m.add( menuItem );

		if( keyChar != 0 )
			menuItem.setAccelerator( KeyStroke.getKeyStroke( keyChar, inputEvent ) );
		if( al != null )
			menuItem.addActionListener( al );
		if( commandString != null )
			menuItem.setActionCommand( commandString );

		return menuItem;
	}

	/**
	 * Insert a JMenuItem to a given JMenu.
	 * @param m the JMenu
	 * @param s the menu title
	 * @param keyEvent the shortcut key code
	 * @param al an ActionListener
	 * @param commandString an action command
	 * @param inputEvent 
	 * @return   a JMenuItem
	 */
	public static JMenuItem addMenuItem( JMenu m, String s, int keyEvent, ActionListener al, String commandString, int inputEvent ) {
		if( s.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}

		JMenuItem menuItem = processMnemonic( s );
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
	 * Insert a JMenuItem to a given JMenu.
	 * @param m the JMenu
	 * @param s the menu title
	 * @param keyChar the shortcut character
	 * @param al an ActionListener
	 * @param commandString an action command
	 * @return   a JMenuItem
	 */
	public static JMenuItem addMenuItem( JMenu m, String s, char keyChar, ActionListener al, String commandString ) {
		return addMenuItem( m, s, keyChar, al, commandString, InputEvent.CTRL_DOWN_MASK);
	}

	public static JMenuItem addMenuItem( JMenu m, String s, char c, ActionListener al ) {
		return addMenuItem( m, s, c, al, null, InputEvent.CTRL_DOWN_MASK );
	}

	public static JMenuItem addMenuItem( JMenu m, String s, char c ) {
		return addMenuItem( m, s, c, null, null, InputEvent.CTRL_DOWN_MASK );
	}

	public static JMenuItem addMenuItem( JMenu m, String s ) {
		return addMenuItem( m, s, (char)0, null, null, 0 );
	}

	public static JMenuItem addMenuItem( JMenu m, String s, ActionListener al ) {
		return addMenuItem( m, s, (char)0, al, null, 0 );
	}

	public static JMenuItem addMenuItem( JMenu m, String s, ActionListener al, String commandString ) {
		return addMenuItem( m, s, (char)0, al, commandString, 0 );
	}
	
	/**
	 * Insert a JMenuItem to a given JMenu.
	 * @param m the JMenu
	 * @param s the menu title
	 * @param c the checked status
	 * @param al an ActionListener
	 * @param commandString 
	 * @return   a JMenuItem
	 */
	public static JCheckBoxMenuItem addCheckMenuItem( JMenu m, String s, boolean c, ActionListener al, String commandString ) {
		if( s.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}

		char mn = getMnemonic( s );
		JCheckBoxMenuItem menuItem;
		if( mn != 0 ) {
			menuItem = new JCheckBoxMenuItem( extractMnemonic( s ), c );
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
	
	public static JCheckBoxMenuItem addCheckMenuItem( JMenu m, String s, boolean c, ActionListener al ) {
		return addCheckMenuItem( m, s, c, al, null );
	}
	
	public static JCheckBoxMenuItem addCheckMenuItem( JMenu m, String s, boolean c ) {
		return addCheckMenuItem( m, s, c, null, null );
	}
	
	public static JRadioButtonMenuItem addRadioButtonMenuItem( JMenu m, String s, boolean c, ActionListener al, String commandString ) {
		if( s.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}

		char mn = getMnemonic( s );
		JRadioButtonMenuItem menuItem;
				if( mn != 0 ) {
			menuItem = new JRadioButtonMenuItem( extractMnemonic( s ), c );
			menuItem.setMnemonic( mn );
		} else
			menuItem = new JRadioButtonMenuItem( s, c );
		
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
	 * Insert a JMenuItem to a given JPopupMenu.
	 * @param m the JPopupMenu
	 * @param s the menu title
	 * @param al an ActionListener
	 * @param commandString an action command
	 * @return   a JMenuItem
	 */
	public static JMenuItem addMenuItem( JPopupMenu m, String s, ActionListener al, String commandString ) {
		if( s.startsWith( "-" ) ) {
			m.addSeparator();
			return null;
		}

		//JMenuItem menuItem = processMnemonic( s );
		JMenuItem menuItem = new JMenuItem( s );
		m.add( menuItem );
		
		if( commandString != null )
			menuItem.setActionCommand( commandString );
		if( al != null )
			menuItem.addActionListener( al );
		return menuItem;
	}

	public static JMenuItem addMenuItem( JPopupMenu m, String s ) {
		return addMenuItem( m, s, null, null );
	}

	public static JMenuItem addMenuItem( JPopupMenu m, String s, ActionListener al ) {
		return addMenuItem( m, s, al, null );
	}
	
	//public static void updateMenu( JMenu menu, String text ) {
	//	menu.setText( extractMnemonic( text ) );
	//	menu.setMnemonic( getMnemonic( text ) );
	//}

	public static void updateMenu( JMenuItem menu, String text ) {
		menu.setText( extractMnemonic( text ) );
		menu.setMnemonic( getMnemonic( text ) );
	}

}