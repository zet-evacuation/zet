package ds.z.exception;

import ds.z.*;

/**
 * This Exception has to be thrown, if the points of an edge are set via setPoints(), but the
 * points already have edges registered to them, that cannot be overwritten.
 * 
 */
public class PointsAlreadyConnectedException extends ValidationException {
	
	public PointsAlreadyConnectedException (PlanPoint connectedPoint) {
		super (connectedPoint);
	}
	
	public PointsAlreadyConnectedException (PlanPoint connectedPoint, String s) {
		super (connectedPoint, s);
	}
	
	/** @return The point that caused the exception because it already was connected to some edge
	 * so that the new edge could not be added to it. */
	public PlanPoint getConnectedPoint () {
		return (PlanPoint)getSource ();
	}
}