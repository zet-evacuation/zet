/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Class JPropertyComboBox
 * Created 03.07.2008, 23:59:56
 */

package gui.editor.properties;

import java.io.File;
import javax.swing.JComboBox;

/**
 * A specialized {@link JComboBox} that contains an entry for each property type
 * that is known.
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class JPropertyComboBox extends JComboBox<PropertyListEntry> {
	PropertyFilesSelectionModel model;
	
	public JPropertyComboBox( PropertyFilesSelectionModel pfsm ) {
		super( pfsm );
		model = (PropertyFilesSelectionModel)getModel();
	}

	public File getSelectedFile() {
		if( getSelectedIndex() < 0 )
			return null;
		return model.getFile( getSelectedIndex() );
	}
}
