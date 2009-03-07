/*
 * Button.java
 * Created on 19.12.2007, 00:27:51
 */

package gui.components.framework;

import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Automatic creation of JButtons.
 * @author Kapman
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
	
	public static JButton newButton( String s, ActionListener al, String commandString, String toolTip ) {
		JButton b = processMnemonic( s );
		if( toolTip != null )
			b.setToolTipText( toolTip );
		return addActionListener( b, al, commandString );
	}

	public static JButton newButton( String s, ActionListener al, String commandString ) {
		JButton b = processMnemonic( s );
		return addActionListener( b, al, commandString );
	}
	
	public static JButton newButton( String s, ActionListener al ) {
		JButton b = processMnemonic( s );
		return addActionListener( b, al, null );
	}
	
	public static JButton newButton( String s, String toolTip ) {
		JButton b = processMnemonic( s );
		b.setToolTipText( toolTip );
		return b;
	}

	public static JButton newButton( String s ) {
		return processMnemonic( s );
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