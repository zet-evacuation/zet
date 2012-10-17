/*
 * AssignmentException.java
 * Created 22.01.2010, 21:01:31
 */

package ds.z.exception;

import java.io.IOException;

/**
 * The class {@code AssignmentException} ...
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class AssignmentException extends RuntimeException {
	public static enum State {
		GeneralAssignmentError,
		NoAssignmentCreated,
		NoAssignmentSelected
	}

	State state;

	/**
	 * Creates a new instance of {@code AssignmentException}.
	 */
	public AssignmentException() {
		super();
		state = State.GeneralAssignmentError;
	}

	public AssignmentException( State state ) {
		super();
		this.state = state;
	}

	public AssignmentException( State state, String message ) {
		super( message );
		this.state = state;
	}

	public AssignmentException( String message ) {
		super( message );
		this.state = State.GeneralAssignmentError;
	}

	/**
	 * Returns the special information for assignment errors.
	 * @return the special information for assignment errors
	 */
	public State getState() {
		return state;
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "AssignmentException: " + super.getMessage();
	}
	
	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}
