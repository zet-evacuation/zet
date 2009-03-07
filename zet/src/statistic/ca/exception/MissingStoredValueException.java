

package statistic.ca.exception;

/**
 *
 * @author Sylvie
 */
public class MissingStoredValueException extends RuntimeException{
    
    	public MissingStoredValueException () {
		super();
	}    
    	public MissingStoredValueException ( String message ) {
		super(message);
	}

}
