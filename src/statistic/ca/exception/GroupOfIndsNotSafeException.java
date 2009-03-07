

package statistic.ca.exception;
import ds.ca.Individual;
import java.util.ArrayList;

/**
 *
 * @author Sylvie
 */

public class GroupOfIndsNotSafeException extends GroupOfIndividualsException {
	
	public GroupOfIndsNotSafeException (ArrayList<Individual> indgroup ) {
		super (indgroup);
	}
	
	public GroupOfIndsNotSafeException ( ArrayList<Individual> indgroup, String s ) {
		super (indgroup, s );
	}
	

}