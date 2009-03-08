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
/**
 * Class TreeModel.java
 * Erstellt 21.02.2008, 23:36:35
 */
package gui.editor.properties;

import gui.editor.properties.converter.PropertyTreeConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Martin Groß, Jan-Philipp Kappmeier
 */
@XStreamAlias( "zp" )
@XStreamConverter( PropertyTreeConverter.class )
public class PropertyTreeModel extends DefaultTreeModel {
	private String propertyName = "";

  public PropertyTreeModel( PropertyTreeNode root ) {
    super( root );
  }

	public String getPropertyName() {
		return propertyName == null ? "" : propertyName;
	}

	public void setPropertyName( String propertyName ) {
		this.propertyName = propertyName;
	}

  @Override
  public PropertyTreeNode getRoot() {
    return( PropertyTreeNode ) root;
  }
}
