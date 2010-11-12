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
 * Class AbstractPropertyValue
 * Erstellt 09.04.2008, 21:51:23
 */
package gui.editor.properties.framework;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import ds.PropertyContainer;
import javax.swing.JPanel;
import de.tu_berlin.math.coga.common.localization.Localization;

/**
 * <p>A class representing a property value. Properties are stored in an XML-file
 *	and contain the following information: the property, that is the string by
 *	which it can be accessed using the {@link PropertyContainer}. A name for the
 *	given property and some information and description texts. The description
 *	is a detailed information while information should be rather short.</p>
 * <p>The name, description and information can be used as tags for
 *	localization, as well. In that case the strings in the XML-file does not
 *	contain the name, information etc. but some tags used by
 *	{@link Localization} to read localized strings. If it is the case can be
 *	checked via {@link #isUsedAsLocString()} and is stored as a boolean flag in
 *	the XML-file.</p>
 * @param <T> The type of the property information. Specialized behaviour for
 *	the types shall be implemented in derived classes.
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractPropertyValue<T> implements PropertyValue<T>, PropertyElement {
  boolean useAsLocString = false;
  String name = "";
	String description = "";
	String information = "";
	String property = "";
	T value;
  
	/**
	 * Returns the detailed description for the property.
	 * @return the detailed description for the property
	 */
	public String getDescription() {
		if( isUsedAsLocString() )
			return DefaultLoc.getSingleton().getString( description );
		else
			return description;
	}

	/**
	 * Sets the description for the property. Note that you cannot change the
	 * description if it shall be used as a tag for localizated string. In that
	 * case, you can only change the tag.
	 * @param text the description
	 */
	public void setDescription( String text ) {
		this.description = text;
	}

	/**
	 * Returns the value for the property.
	 * @return the value for the property
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Sets the value for the property.
	 * @param value the value for the property
	 */
	public void setValue( T value) {
		this.value = value;
	}

	public String getInformation() {
		if( isUsedAsLocString() )
			return DefaultLoc.getSingleton().getString( information );
		else
			return information;
	}

	public void setInformation( String text ) {
		this.information = text;
	}

	public boolean isUsedAsLocString() {
		return useAsLocString;
	}

	public void useAsLocString( boolean useAsLocString ) {
		this.useAsLocString = useAsLocString;
	}

	public String getName() {
		if( isUsedAsLocString() )
			return DefaultLoc.getSingleton().getString( name );
		else
			return name;
	}

	public void setName( String name ) {
    this.name = name;
	}

	public String getDescriptionTag() {
		return description;
	}

	public String getInformationTag() {
		return information;
	}

	public String getNameTag() {
		return name;
	}
	
	public abstract JPanel getPanel();
	
	protected abstract void updateModel();

	/**
	 * Reloads the values for the property from the {@link PropertyContainer}.
	 * This can be useful, if some properties were changed and should be stored.
	 * @see PropertyContainer#saveConfigFile(PropertyTreeModel, File)
	 */
	@SuppressWarnings("unchecked")
	public void reloadFromPropertyContainer() {
		setValue( (T)PropertyContainer.getInstance().get( property ) );
	}

	/**
	 * Returns the name of the property. That is the name by which it can be
	 * accessed in the {@link PropertyContainer}.
	 * @return the name of the property
	 */
	public String getPropertyName() {
		return property;
	}

	/**
	 * Sets the name of the property. That is the name by which it can be accessed
	 * using the {@link PropertyContainer}.
	 * @param property the name of the property
	 */
	public void setPropertyName( String property ) {
		this.property = property;
	}
}
