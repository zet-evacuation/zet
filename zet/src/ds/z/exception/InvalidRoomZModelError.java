/**
 * InvalidRoomZModelError.java
 * Created: 24.01.2011, 15:30:10
 */
package ds.z.exception;

import ds.z.Room;


/**
 * Indicates that an error occurred in some room. Mostly this is because an
 * invalid polygon shape, e. g. if it only contains points at one coordinate.
 * @author Jan-Philipp Kappmeier
 */
public class InvalidRoomZModelError extends ZModelErrorException {
	private Room room;
	private StackTraceElement[] stack;


	public InvalidRoomZModelError( String message, Room room ) {
		super( message );
		room = room;
	}

	/**
	 * Returns an exception that was thrown.
	 * @return an exception that was thrown
	 */
	public Room getRoom() {
		return room;
	}
}
