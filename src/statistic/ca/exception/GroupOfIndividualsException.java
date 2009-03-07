


package statistic.ca.exception;
import ds.ca.Individual;
import java.util.ArrayList;
/**
 *
 * @author Sylvie
 */

public class GroupOfIndividualsException extends RuntimeException {
	protected ArrayList<Individual> indgroup;
	
	public GroupOfIndividualsException () {
		this (null, null);
	}
	/** @param ind The Individual that caused the exception. */
	public GroupOfIndividualsException (ArrayList<Individual> indgroup) {
		this (indgroup, null);
	}
	/** @param message A message that further explains this exception. */
	public GroupOfIndividualsException ( String message ) {
		this (null, message);
	}
	/** @param ind The Individual that caused the exception.
	 * @param message A message that further explains this exception. */
	public GroupOfIndividualsException (ArrayList<Individual> indgroup, String message) {
		super (message);
		this.indgroup = indgroup;
	}
	
	/** @return ind The Individual that caused the exception. */
	public ArrayList<Individual> getGroupOfIndividuals () {
		return indgroup;
	}
}