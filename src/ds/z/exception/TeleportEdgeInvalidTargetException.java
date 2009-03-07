package ds.z.exception;

import ds.z.*;

/**
 * This Exception has to be thrown, if a teleport edge is connected to another teleport edge on the same floor.
 */
public class TeleportEdgeInvalidTargetException extends ValidationException {
	
	public TeleportEdgeInvalidTargetException (TeleportEdge invalidEdge) {
		super (invalidEdge);
	}
	
	public TeleportEdgeInvalidTargetException (TeleportEdge invalidEdge, String s) {
		super (invalidEdge, s);
	}
	
	public TeleportEdge getInvalidEdge () {
		return (TeleportEdge)getSource ();
	}
}