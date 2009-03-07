/**
 * Class ToManyPeopleException
 * Erstellt 19.05.2008, 14:32:11
 */

package ds.z.exception;

import ds.z.PlanPolygon;

/**
 * An exception, that is thrown when too many persons are in a {@link ds.z.Room} or {@link ds.z.Area}.
 * @author Jan-Philipp Kappmeier
 */
public class TooManyPeopleException extends ValidationException {

	/**
	 * @param polygon The Poylgon which contains too much persons. 
	 * @param message A message that further describes the exception. */
	public TooManyPeopleException (PlanPolygon polygon, String message) {
		super (polygon, message);
	}
        
	/**
	 * @param polygon the Poylgon which contains too much persons.
	 */
	public TooManyPeopleException ( PlanPolygon polygon ) {
		super ( polygon );
	}
	
	/**
	 * @return The Poylgon which contains too much persons.
	 */
	public PlanPolygon getPolygon () {
		return (PlanPolygon)getSource ();
	}
}
