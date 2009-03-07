
package statistic.ca.exception;
import ds.ca.Individual;
        
/**
 *
 * @author Sylvie
 */

public class OneIndNoPotentialException extends OneIndividualException {
	
	public OneIndNoPotentialException (Individual ind ) {
		super (ind);
	}
	
	public OneIndNoPotentialException ( Individual ind, String s ) {
		super (ind, s );
	}
	

}