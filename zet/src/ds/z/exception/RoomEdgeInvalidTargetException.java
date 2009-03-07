package ds.z.exception;

import ds.z.*;

/**
 * This Exception has to be thrown, if a room edge is connected to another room edge on a different floor.
 */
public class RoomEdgeInvalidTargetException extends ValidationException {
	
	public RoomEdgeInvalidTargetException (RoomEdge invalidEdge) {
		super (invalidEdge);
	}
	
	public RoomEdgeInvalidTargetException (RoomEdge invalidEdge, String s) {
		super (invalidEdge, s);
	}
	
	public RoomEdge getInvalidEdge () {
		return (RoomEdge)getSource ();
	}
}