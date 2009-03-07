

package statistic.ca.exception;
import ds.ca.Individual;
/**
 *
 * @author Sylvie
 */

public class OneIndividualException extends RuntimeException {
	protected Individual ind;
	
	public OneIndividualException () {
		this (null, null);
	}
	/** @param ind The Individual that caused the exception. */
	public OneIndividualException (Individual ind) {
		this (ind, null);
	}
	/** @param message A message that further explains this exception. */
	public OneIndividualException ( String message ) {
		this (null, message);
	}
	/** @param ind The Individual that caused the exception.
	 * @param message A message that further explains this exception. */
	public OneIndividualException (Individual ind, String message) {
		super (message);
		this.ind = ind;
	}
	
	/** @return ind The Individual that caused the exception. */
	public Object getIndividual () {
		return ind;
	}
}