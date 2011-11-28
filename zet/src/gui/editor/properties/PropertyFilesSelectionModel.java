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
 * Class PropertyFilesSelectionModel.java
 * Created 03.07.2008, 19:22:25
 */

package gui.editor.properties;

import gui.propertysheet.PropertyTreeModel;
import ds.PropertyContainer;
import gui.editor.properties.PropertyFilesSelectionModel.PropertyListEntry;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.DefaultComboBoxModel;

/**
 * A model for {@link JPropertyComboBox} that contains each property and their
 * paths and names.
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class PropertyFilesSelectionModel extends DefaultComboBoxModel<PropertyListEntry> {
	public class PropertyListEntry implements Comparable<PropertyListEntry> {
		public String name;
		public Path path;
		PropertyListEntry ( String name, Path path ) {
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
				return p.path.equals( path );// && p.name.equals( name );
			} else return false;
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
			return name.compareTo( (p.getName() ) );
		}
	}
	
	private Path path;
		
	/**
	 * A constructor using the default path "./properties".
	 */
	public PropertyFilesSelectionModel() {
		this( Paths.get( "./properties" ) );
	}
	
	public PropertyFilesSelectionModel( Path path ) {
		super();
		this.path = path;
		loadPath();
	}

	/**
	 * Loads all property files in a specified path. PropertyListEntry files are assumed to
	 * be all {@code .xml} files. The properties are sorted with respect to their
	 * name.
	 */
	private void loadPath() {
		
		ArrayList<PropertyListEntry> properties = new ArrayList<>();
		try (DirectoryStream<Path> files = Files.newDirectoryStream( path, "*.xml" )) {
			for( Path p : files ) {
				System.out.println( "Property file found: " + p.getFileName() );
				PropertyTreeModel ptm = PropertyContainer.loadConfigFile( p.toFile() );
				properties.add( new PropertyListEntry( ptm.getPropertyName(), p ) );
			}
		} catch( PropertyLoadException ex ) {
			System.err.println( "Illegale Property-Datei" + (ex.getFile() == null ? " " : " '" + ex.getFile().getName() + "' ") + "wird übersprungen." );
		} catch( IOException ex ) {
			System.err.println( "ERROR: " + ex.toString() );
		}

		// sort properties and add them to the model
		Collections.sort( properties );
		for( PropertyListEntry p : properties )
			addElement( p );
	}
	
  /**
	 * Returns the file in which the property with the given index is stored.
	 * @param index the index of the property
	 * @return the file in which the property with the given index is stored.
	 */
	public File getFile( int index ) {
		return getElementAt( index ).getFile();
	}
	
	public PropertyListEntry getProperty ( String filename ) {
		for (int i = 0; i < getSize (); i++) {
			PropertyListEntry p = getElementAt( i );
			if( p.getFile ().getName ().equals( filename ) ) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Returns a property that has the specified name.
	 * @param name the name of the property that is searched
	 * @return a property with the given name
	 */
	public PropertyListEntry getPropertyByName( String name ) {
		for (int i = 0; i < getSize (); i++) {
			PropertyListEntry p = getElementAt( i );
			if (p.getName().equals( name ) ) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Sets the selected item according to the submitted path. The new property 
	 * is the one, whose file equals this path.
	 * @param loadPath 
	 */
	public void setSelectedItem( Path loadPath ) {
		for( int i = 0; i < this.getSize(); ++i ) { // No iterator available!
			try {
				if( Files.isSameFile( loadPath, getElementAt( i ).path ) ) {
					super.setSelectedItem( getElementAt( i ) );
					break;
				}
			} catch( IOException ex ) {
				ex.printStackTrace( System.err );
			}
		}
	}
	
	
}
