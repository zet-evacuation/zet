
package ds.z.exception;

/** An exception that is thrown during the execution of the check-methods in
 * Floor and Room. It indicates that something is wrong with the current
 * configuration of the z-model. Details can be extracted from the exception
 * message.
 *
 * @author Timon Kelter
 */
public class ValidationException extends RuntimeException {
	protected Object source;
	
	public ValidationException () {
		this (null, null);
	}
	/** @param source The 'ds.z.'- object that caused the exception. */
	public ValidationException (Object source) {
		this (source, null);
	}
	/** @param message A message that further explains this exception. */
	public ValidationException ( String message ) {
		this (null, message);
	}
	/** @param source The 'ds.z.'- object that caused the exception.
	 * @param message A message that further explains this exception. */
	public ValidationException (Object source, String message) {
		super (message);
		this.source = source;
	}
	
	/** @return The 'ds.z.'- object that caused the exception. */
	public Object getSource () {
		return source;
	}
}