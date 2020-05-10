/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui.editor.properties;

import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PropertyListEntry implements Comparable<PropertyListEntry> {
	public String name;
	public Path path;

	PropertyListEntry( String name, Path path ) {
		this.name = name;
		this.path = path;
	}

	@Override
	public String toString() {
		return name;
	}

	public Path getPath() {
		return path;
	}

	public File getFile() {
		return path.toFile();
	}

	@Override
	public boolean equals( Object o ) {
		if( o instanceof PropertyListEntry ) {
			PropertyListEntry p = (PropertyListEntry)o;
			return p.path.equals( path ); // && p.name.equals( name );
		}
		else
			return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		//hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 53 * hash + (this.path != null ? this.path.hashCode() : 0);
		return hash;
	}

	/**
	 * Returns the name of the property.
	 * @return the name of the property
	 */
	public String getName() {
		return name;
	}

	/**
	 * Compares two properties with respect to their name.
	 * @param p the property that is compared to this instance.
	 * @return -1 if this property has a lexicographically smaller name, 0 if they are equal or 1.
	 */
	public int compareTo( PropertyListEntry p ) {
		return name.compareTo( p.getName() );
	}
	
}
