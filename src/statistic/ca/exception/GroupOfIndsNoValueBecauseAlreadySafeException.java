



package statistic.ca.exception;
import ds.ca.Individual;
import java.util.ArrayList;

/**
 *
 * @author Sylvie
 */

public class GroupOfIndsNoValueBecauseAlreadySafeException extends GroupOfIndividualsException {
	
	public GroupOfIndsNoValueBecauseAlreadySafeException (ArrayList<Individual> indgroup ) {
		super (indgroup);
	}
	
	public GroupOfIndsNoValueBecauseAlreadySafeException ( ArrayList<Individual> indgroup, String s ) {
		super (indgroup, s );
	}
	

}