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

	public void reloadFromPropertyContainer() {
		for( AbstractPropertyValue apv : properties ) {
			apv.reloadFromPropertyContainer();
		}
		if( children != null)
			for( Object ptn : children ) {
				((PropertyTreeNode)ptn).reloadFromPropertyContainer();
			}
	}

	public PropertyTreeNode( String name ) {
		super( name );
		this.name = name;
		properties = new Vector<AbstractPropertyValue>();
	}

	/**
	 * Returns {@code true} if the strings for the name, information and
	 * description in the XML-file shall be tags used for localization.
	 * @return {@code true} if the XML-file contains localization tags, {@code false} otherwise
	 * @see Localization
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
	 * Returns the name of the property stored in this node. If it
	 * {@link #isUsedAsLocString()}, the localized string is returned.
	 * @return the name of the property stored in this node
	 */
	public String getName() {
		if( isUsedAsLocString() )
			return Localization.getInstance().getString( name );
		else
			return name;
	}

	/**
	 * Assigns a new name to the property stored in this node.
	 * @param name the new name
	 */
	public void setName( String name ) {
		this.name = name;
		setUserObject( getName() );
	}

	@Override
	public void setUserObject( Object userObject ) {
		if( !(userObject instanceof String) )
			throw new IllegalArgumentException( Localization.getInstance().getString( "gui.propertyselector.DefaultPropertyTreeNodeConverter.noStringException" ) );
		super.setUserObject( userObject );
	}

	/**
	 * Returns the string stored in the XML-file. This can be either a name or
	 * a tag used for localization.
	 * @return the name stored in the XML-file
	 * @see #isUsedAsLocString()
	 */
	public String getNameTag() {
		return name;
	}
}
