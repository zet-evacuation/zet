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
 * Class NewClass
 * Erstellt 03.07.2008, 19:22:25
 */

package gui.editor.properties;

import ds.PropertyContainer;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PropertyFilesSelectionModel extends DefaultComboBoxModel {
	public class Property {
		public String name;
		public String path;
		Property ( String name, String path ) {
			this.name = name;
			this.path = path;
		}
		
		@Override
		public String toString() {
			return name;
		}
	
		public File getFile() {
			return new File( path );
		}
		
		@Override
		public boolean equals( Object o ) {
			if( o instanceof Property ) {
				Property p = (Property)o;
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

		public String getName() {
			return name;
		}
	}
	
	private String path;
	
	private static final PropertyFilesSelectionModel instance = new PropertyFilesSelectionModel ("./properties/");
	public static PropertyFilesSelectionModel getInstance () { return instance; }
	
	private PropertyFilesSelectionModel( String path ) {
		super();
		this.path = path;
		loadPath();
	}
	
	public void loadPath() {
		File dir = new File( path );
		File[] propertyFiles = dir.listFiles( new XMLFilenameFilter() );
		for( File file : propertyFiles ) {
			try {
				PropertyTreeModel ptm = PropertyContainer.loadConfigFile( file );
				addElement( new Property(ptm.getPropertyName(), file.getPath() ) );
			} catch( PropertyLoadException ex ) {
				System.err.println( "Illegale Property-Datei" + (ex.getFile() == null ? " " : " '" + ex.getFile().getName() + "' ") + "wird übersprungen." );
			}
			//System.out.println( a.getPropertyName() );
			//entries.add( new Property(ptm.getPropertyName(), file.getPath() ) );
			//System.out.println( file.getPath() );
		}
	}
	
	public File getFile( int index ) {
		return ((Property)getElementAt( index )).getFile();
	}
	
	public Property getProperty ( String filename ) {
		for (int i = 0; i < getSize (); i++) {
			Property p = (Property)getElementAt( i );
			if (p.getFile ().getName ().equals(filename)) {
				return p;
			}
		}
		return null;
	}
	
	public Property getPropertyByName( String name ) {
		for (int i = 0; i < getSize (); i++) {
			Property p = (Property)getElementAt( i );
			if (p.getName().equals( name ) ) {
				return p;
			}
		}
		return null;
	}
	
	private class XMLFilenameFilter implements FilenameFilter {
		public boolean accept( File f, String s ) {
			return new File(f, s).isFile() && s.toLowerCase().endsWith( ".xml" );
	  }
	}
}
