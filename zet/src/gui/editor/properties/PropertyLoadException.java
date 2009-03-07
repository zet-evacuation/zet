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