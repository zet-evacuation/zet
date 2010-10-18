/**
 * FileTypeException.java
 * Created: Oct 5, 2010,12:17:26 PM
 */
package io;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FileTypeException extends IllegalArgumentException {

	public FileTypeException( Throwable thrwbl ) {
		super( thrwbl );
	}

	public FileTypeException( String string, Throwable thrwbl ) {
		super( string, thrwbl );
	}

	public FileTypeException( String string ) {
		super( string );
	}

	public FileTypeException() {
		super();
	}

}
