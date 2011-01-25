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

/**
 * ComboBoxRenderer.java
 * Created on 15.12.2007, 16:31
 */
package zet.gui.components.model;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * A generic ComboBox renderer.
 * @author Timon Kelter
 */
public class ComboBoxRenderer extends JLabel implements ListCellRenderer {

	public ComboBoxRenderer() {
		setOpaque( true );
		setHorizontalAlignment( CENTER );
		setVerticalAlignment( CENTER );
	}

	/**
	 * This method finds the image and text corresponding
	 * to the selected value and returns the label, set up
	 * to display the text and image.
	 */
	@Override
	public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
		if( isSelected ) {
			setBackground( list.getSelectionBackground() );
			setForeground( list.getSelectionForeground() );
		} else {
			setBackground( list.getBackground() );
			setForeground( list.getForeground() );
		}

		setHorizontalAlignment( LEFT );

		return this;
	}
}
