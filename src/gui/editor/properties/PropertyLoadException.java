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
 * Class PropertyLoadException
 * Erstellt 21.11.2008, 19:19:50
 */

package gui.editor.properties;

import java.io.File;
import java.io.IOException;

/**
 * A <code>PropertyLoadException</code> is thrown if an error during loading
 * of a property file occured. It is possible to submit the file that created
 * the error.
 * @author Jan-Philipp Kappmeier
 */
public class PropertyLoadException extends IOException {
	/** The file that created the error. */
	private File file;
	
	/**
	 * Creates a new <code>PropertyLoadException</code> with default error string.
	 */
	public PropertyLoadException() {
		this( "Error loading properties." );
	}
	
	/**
	 * Creates a new <code>PropertyLoadException</code> with an error string.
	 * @param s the error string
	 */
	public PropertyLoadException ( String s ) {
		super( s );
	}

	/**
	 * Creates a new <code>PropertyLoadException</code>, sets the file and sets
	 * an error message containing the filename.
	 * @param f the file
	 */
	public PropertyLoadException ( File f ) {
		this( f == null ? "" : "Error loading properties from file '" + f.getName() + "'" );
		file = f;
	}

	/**
	 * Returns the file that created the error. Can be <code>null</code>.
	 * @return the file that created the error.
	 */
	public File getFile() {
		return file;
	}
}