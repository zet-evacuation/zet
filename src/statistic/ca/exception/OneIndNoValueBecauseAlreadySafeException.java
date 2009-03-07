

package statistic.ca.exception;
import ds.ca.Individual;

/**
 *
 * @author Sylvie
 */
public class OneIndNoValueBecauseAlreadySafeException extends OneIndividualException {
    	public OneIndNoValueBecauseAlreadySafeException (Individual ind ) {
		super (ind);
	}
	
	public OneIndNoValueBecauseAlreadySafeException ( Individual ind, String s ) {
		super (ind, s );
	}

}
