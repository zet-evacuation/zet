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
 * Class PropertyTreeNode
 * Erstellt 22.02.2008, 01:36:06
 */

package gui.editor.properties;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import gui.editor.properties.framework.PropertyElement;
import gui.editor.properties.framework.AbstractPropertyValue;
import gui.editor.properties.converter.DefaultPropertyTreeNodeConverter;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import localization.Localization;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias("treeNode")
@XStreamConverter(DefaultPropertyTreeNodeConverter.class)
public class PropertyTreeNode extends DefaultMutableTreeNode implements PropertyElement {
  boolean useAsLocString = false;
  String name;
	Vector<AbstractPropertyValue> properties;

	public void addProperty( AbstractPropertyValue property ) {
		properties.add( property );
	}
	
	public void clearProperties() {
		properties.clear();
	}
	
public List<AbstractPropertyValue> getProperties() {
	return Collections.unmodifiableList( properties );
}	
	
  public PropertyTreeNode( String name ) {
    super( name );
    this.name = name;
		properties = new Vector<AbstractPropertyValue>();
  }

  /**
   * 
   * @return
   */
  public boolean isUsedAsLocString() {
    return useAsLocString;
  }

  /**
   * 
   * @param useAsLocString
   */
  public void useAsLocString( boolean useAsLocString ) {
    this.useAsLocString = useAsLocString;
    setUserObject( getName() );
  }
  
	/**
	 * 
	 * @return
	 */
	public String getName() {
		if( isUsedAsLocString() )
			return Localization.getInstance().getString( name );
		else
			return name;
  }
  
	/**
	 * 
	 * @param name
	 */
	public void setName( String name ) {
			this.name = name;
			setUserObject( getName() );
  }
  
  @Override
  public void setUserObject( Object userObject ) {
    if( !( userObject instanceof String) )
      throw new IllegalArgumentException( Localization.getInstance().getString( "gui.propertyselector.DefaultPropertyTreeNodeConverter.noStringException" ) );
    super.setUserObject( userObject );
  }

	/**
	 * 
	 * @return
	 */
	public String getNameTag() {
		return name;
	}
}
