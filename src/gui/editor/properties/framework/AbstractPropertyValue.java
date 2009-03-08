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
 * Class AbstractPropertyValue
 * Erstellt 09.04.2008, 21:51:23
 */
package gui.editor.properties.framework;

import javax.swing.JPanel;
import localization.Localization;

/**
 *
 * @param T 
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractPropertyValue<T> implements PropertyValue<T>, PropertyElement {
  boolean useAsLocString = false;
  String name = "";
	String description = "";
	String information = "";
	String property = "";
	T defaultValue;
  
	public String getDescription() {
		if( isUsedAsLocString() )
			return Localization.getInstance().getString( description );
		else
			return description;
	}

	public void setDescription( String text ) {
		this.description = text;
	}

	public T getValue() {
		return defaultValue;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue( T value) {
		defaultValue = value;
	}

	public String getInformation() {
		if( isUsedAsLocString() )
			return Localization.getInstance().getString( information );
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
			return Localization.getInstance().getString( name );
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

	public String getPropertyName() {
		return property;
	}

	public void setPropertyName( String property ) {
		this.property = property;
	}
	
	
}
