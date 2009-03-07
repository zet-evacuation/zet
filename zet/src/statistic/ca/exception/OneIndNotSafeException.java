

package statistic.ca.exception;
import ds.ca.Individual;
        
/**
 *
 * @author Sylvie
 */

public class OneIndNotSafeException extends OneIndividualException {
	
	public OneIndNotSafeException (Individual ind ) {
		super (ind);
	}
	
	public OneIndNotSafeException ( Individual ind, String s ) {
		super (ind, s );
	}
	

}