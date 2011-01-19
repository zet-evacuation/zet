/**
 * UnknownZModelError.java
 * Created: 19.01.2011, 17:46:31
 */
package ds.z.exception;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class UnknownZModelError extends ZModelErrorException {
	private Exception exception;
	private StackTraceElement[] stack;

	public UnknownZModelError( String message, Exception exception ) {
		super( message );
		stack = exception.getStackTrace();
	}

	/**
	 * Returns an exception that was thrown.
	 * @return an exception that was thrown
	 */
	public Exception getException() {
		return exception;
	}
	public void printOriginalStackTrace() {
		for( StackTraceElement element : stack )
			System.err.println( element );
	}
}
