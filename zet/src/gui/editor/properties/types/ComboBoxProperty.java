/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Class ListProperty
 * Erstellt 05.11.2009, 17:31:50
 */

package gui.editor.properties.types;

import gui.editor.properties.framework.AbstractPropertyValue;
import java.util.ArrayList;
import javax.swing.JComboBox;

/**
 * An abstract class that provides a combo box and allows to add several
 * entries provided as a list of strings.
 * @author Jan-Philipp Kappmeier
 */
public abstract class ComboBoxProperty<T> extends AbstractPropertyValue<T> {
	JComboBox comboBox = new JComboBox();
	ArrayList<String> entries = new ArrayList<String>();


	/**
	 * Returns the available entries for the combo box.
	 * @return a list of the available entries for the combo box
	 */
	public ArrayList<String> getEntries() {
		return entries;
	}

	/**
	 * Sets the available entries for the combo box.
	 * @param entries a list of entries.
	 */
	public void setEntries( ArrayList<String> entries ) {
		this.entries = entries;
	}
}
