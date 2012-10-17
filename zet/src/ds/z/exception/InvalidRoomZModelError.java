/**
 * InvalidRoomZModelError.java
 * Created: 24.01.2011, 15:30:10
 */
package ds.z.exception;

import ds.z.Room;
import java.io.IOException;


/**
 * Indicates that an error occurred in some room. Mostly this is because an
 * invalid polygon shape, e. g. if it only contains points at one coordinate.
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class InvalidRoomZModelError extends ZModelErrorException {
	private Room room;
	private StackTraceElement[] stack;


	public InvalidRoomZModelError( String message, Room room ) {
		super( message );
		this.room = room;
	}

	/**
	 * Returns an exception that was thrown.
	 * @return an exception that was thrown
	 */
	public Room getRoom() {
		return room;
	}
	
	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}
