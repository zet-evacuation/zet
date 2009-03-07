/*
 * PolygonNotClosedException.java
 *
 * Created on 01. December 2007, 23:06
 */

package ds.z.exception;

import ds.z.*;

/**
 * The exception <code>TeleportEdgeNotconnected</code> indicates that a
 * teleport has not an associated teleport edge. It can also occur if
 * it is connected to an teleport edge, which is not connected
 * to the original one.
 * @author Jan-Philipp Kappmeier
 */
public class TeleportEdgeNotConnected extends ValidationException {
	
	public TeleportEdgeNotConnected (TeleportEdge invalidEdge) {
		super (invalidEdge);
	}
	
	public TeleportEdgeNotConnected (TeleportEdge invalidEdge, String s) {
		super (invalidEdge, s);
	}
	
	public TeleportEdge getInvalidEdge () {
		return (TeleportEdge)getSource ();
	}
}