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
package de.tu_berlin.math.coga.components;

import info.clearthought.layout.TableLayout;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * <p>The <code>JStatusBar</code> implements a status bar, that means an panel
 * usually positioned at the button of a window that contains some fields
 * containing text information.</p>
 * <p>In this class the fields are considered as "elements". The elements can
 * be added and deleted separately and the status bar can be edited, but to
 * the changes are not displayed until {@code reset()} is called.</p>
 * <p>The appearance of the status bar can not be changed, the elements look
 * just like bordered components, however, it is possible to assign a percentaged
 * size to each element. If no size is set, the space is divided equally to
 * the elements.</p>
 * @author Jan-Philipp Kappmeier
 */
public class JStatusBar extends JPanel {

	/** The array containing the components visible in the status bar. */
	protected ArrayList<JComponent> components = new ArrayList<JComponent>( 3 );

	/**
	 * Creates a new instance of the {@code JStatusBar} containing one
	 * empty element.
	 */
	public JStatusBar() {
		super();
		addElement();
		rebuild();
	}

	/**
	 * Creates a new instance of the {@code JStatusBar} containing one
	 * element with a specified text.
	 * @param text the specified text.
	 */
	public JStatusBar( String text ) {
		this();
		setStatusText( 0, text );
	}

	/**
	 * Adds an empty element to the status bar.
	 */
	public void addElement() {
		addElement( "" );
	}

	/**
	 * Adds a new element to the status bar.
	 * @param text the initial text of the new element
	 */
	public void addElement( String text ) {
		JLabel label = new JLabel( text );
		label.setBorder( new EtchedBorder() );
		components.add( label );
	}

	public void addElement( JComponent component ) {
		components.add( component );
	}

	/**
	 * Resets the status bar, that means all elements are deleted. A status bar
	 * like this will use no space.
	 */
	public void clear() {
		components.clear();
	}

	/**
	 * Rebuilds the panel, that means a new layout is set. This method has to be
	 * called if elements where added or deleted.
	 */
	protected void rebuild() {
		if( components.isEmpty() ) {
			setLayout( new TableLayout() );
			return;
		}

		// set array		
		double size2[][] = new double[2][];
		size2[0] = new double[components.size()];
		size2[1] = new double[1];
		size2[1][0] = TableLayout.PREFERRED;
		for( int i = 0; i < components.size(); i++ )
			size2[0][i] = TableLayout.FILL;
		setLayout( new TableLayout( size2 ) );

		// for all components, add them
		for( int i = 0; i < components.size(); i++ )
			this.add( components.get( i ), i + ", 0" );
	}

	/**
	 * Sets a new text on a specified status bar element. The text is also set as
	 * tool tip, so that the text can be read if the space is to small. Converts
	 * the component to {@link JLabel} without testing.
	 * @param index the index of the element, begins with 0
	 * @param text the new text
	 */
	public void setStatusText( int index, String text ) {
		((JLabel) components.get( index )).setText( text );
		components.get( index ).setToolTipText( text );
	}
}
