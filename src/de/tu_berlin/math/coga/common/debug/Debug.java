/**
 * Debug.java
 * Created: 22.07.2010 17:30:35
 */
package de.tu_berlin.math.coga.common.debug;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.swing.JOptionPane;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Debug {

	/**
	 * A helper method that gives out an exception in a message window to the
	 * screen and also gives it out to the default {@code err} stream.
	 * @param ex the exception
	 */
	public static void printException( Exception ex ) {
		System.err.println( "Eine Exception trat auf:" );
		System.err.println( "Message: " + ex.getMessage() );
		System.err.println( "Localized: " + ex.getLocalizedMessage() );
		System.err.println( "Cause: " + ex.getCause() );
		ex.printStackTrace( System.err );
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ex.printStackTrace( new PrintStream( bos ) );
		JOptionPane.showMessageDialog( null, bos.toString(), "Error", JOptionPane.ERROR_MESSAGE );
	}
}
