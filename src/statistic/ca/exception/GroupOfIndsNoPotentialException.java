


package statistic.ca.exception;
import ds.ca.Individual;
import java.util.ArrayList;

/**
 *
 * @author Sylvie
 */

public class GroupOfIndsNoPotentialException extends GroupOfIndividualsException {
	
	public GroupOfIndsNoPotentialException (ArrayList<Individual> indgroup ) {
		super (indgroup);
	}
	
	public GroupOfIndsNoPotentialException ( ArrayList<Individual> indgroup, String s ) {
		super (indgroup, s );
	}
	

}