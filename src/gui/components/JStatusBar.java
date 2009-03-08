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

package gui.components;

import info.clearthought.layout.TableLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * The <code>JStatusBar</code> implements a status bar, that means an panel
 * usually positioned at the button of a window that contains some fields
 * containing text information.
 * <p>In this class the fields are considered as "elements". The elements can
 * be added and deleted separately and the status bar can be edited, but to
 * the changes are not displayed until {@code reset()} is called.</p>
 * <p>The appearance of the status bar can not be changed, the elements look
 * just like bordered labels, however, it is possible to assign a percential
 * size to each element. If no size is set, the space is divided equally to
 * the elements.</p>
 * @author Jan-Philipp Kappmeier
 */
public class JStatusBar extends JPanel {
	protected ArrayList<JLabel> labels = new ArrayList<JLabel>(3);
  
	/**
	 * Creates a new instance of the <code>JStatusBar</code> containing one
	 * empty element.
	 */
	public JStatusBar() {
    super();
		addElement();
		rebuild();
	}
  
	/**
	 * Creates a new instance of the <code>JStatusBar</code> containing one
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
		labels.add( label );
	}

	/**
	 * Resets the status bar, that means all elements are deleted. A status bar
	 * like this will use no space.
	 */
	public void clear() {
		labels.clear();
	}
	
	/**
	 * Rebuilds the panel, that means a new layout is set. This method has to be
	 * called if elements where added or deleted.
	 */
	protected void rebuild() {
		if( labels.size() == 0 ) {
			setLayout( new TableLayout() );
			return;
		}
		
		// set array		
		double size2[][] = new double[2][];
		size2[0] = new double[labels.size() ];
		size2[1] = new double[ 1 ];
		size2[1][0] = TableLayout.PREFERRED;
		for( int i=0; i < labels.size(); i++ ) {
			size2[0][i] = TableLayout.FILL;
		}
		setLayout( new TableLayout( size2 ) );
		
		// for all labels, add them
		for( int i=0; i < labels.size(); i++ ) {
			this.add( labels.get( i ), i + ", 0" );
		}
	}
	
  /**
	 * Sets a new text on a specified status bar element. The text is also set as
	 * tooltip, so that the text can be read if the space is to small.
	 * @param index the index of the element, begins with 0
	 * @param text the new text
	 */
  public void setStatusText( int index, String text ) {
    labels.get( index ).setText( text );
	labels.get( index ).setToolTipText (text);
  }
}