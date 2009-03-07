package ds.z.exception;

import ds.z.*;

/**
 * This Exception has to be thrown, if a teleport edge is connected to another teleport edge where
 * the two edges have different lengths (which is illegal).
 */
public class TeleportEdgeTargetLengthException extends ValidationException {
	
	public TeleportEdgeTargetLengthException (TeleportEdge invalidEdge) {
		super (invalidEdge);
	}
	
	public TeleportEdgeTargetLengthException (TeleportEdge invalidEdge, String s) {
		super (invalidEdge, s);
	}
	
	public TeleportEdge getInvalidEdge () {
		return (TeleportEdge)getSource ();
	}
}