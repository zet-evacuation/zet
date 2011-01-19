/**
 * ZModelErrorException.java
 * Created: 19.01.2011, 17:41:58
 */
package ds.z.exception;


/**
 * An abstract base class for all exceptions thrown in the Z model.
 * @author Jan-Philipp Kappmeier
 */
public abstract class ZModelErrorException extends RuntimeException {

	/**
	 * Call to the super constructor with an error message.
	 * @param message the error message
	 */
	public ZModelErrorException( String message ) {
		super( message );
	}

}
