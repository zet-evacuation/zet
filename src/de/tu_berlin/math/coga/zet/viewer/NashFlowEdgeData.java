/**
 * NashFlowEdgeData.java
 * Created: 30.08.2010 17:11:13
 */
package de.tu_berlin.math.coga.zet.viewer;

import java.util.ArrayList;
import java.util.Iterator;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class NashFlowEdgeData implements Iterable<FlowData> {

	// fixed properties for the edge:
	ArrayList<FlowData> flowDatas = new ArrayList<FlowData>();
	double capacity = 1;
	double transitTime = 1;
	double corCapacity = 4;
	double exitPositionRelative = 0.8;

	public NashFlowEdgeData( double capacity, double transitTime ) {
		this( capacity, transitTime, 3.9, 0.77 );
	}

	public NashFlowEdgeData( double capacity, double transitTime, double corridorCapacity, double exitPosition ) {
		this.capacity = capacity;
		this.transitTime = transitTime;
		this.corCapacity = corridorCapacity;
		this.exitPositionRelative = exitPosition;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity( double capacity ) {
		this.capacity = capacity;
	}

	public double getTransitTime() {
		return transitTime;
	}

	public void setTransitTime( double transitTime ) {
		this.transitTime = transitTime;
	}

	private void add( FlowData flowData ) {
		flowDatas.add( flowData );
	}

	void add( double start, double end, double inflow, double wait, int iteration, double globalStart, double globalEnd ) {
		add( new FlowData( start, end, inflow, wait, capacity, transitTime, corCapacity, exitPositionRelative, iteration, globalStart, globalEnd ) );
	}

	public Iterator<FlowData> iterator() {
		return flowDatas.iterator();
	}

	/**
	 * Returns the width of the corridor.
	 * @return the corridor capacity
	 */
	public double getCorridorCapacity() {
		return corCapacity;
	}

	/**
	 * Sets the capacity of the corridor (which is basically the width of the
	 * corridor in the visualization).
	 * @param corridorCapacity the capacity of the corridor.
	 * @throws IllegalArgumentException if the capacity is not positive
	 *
	 */
	public void setCorridorCapacity( double corridorCapacity ) throws IllegalArgumentException {
		if( corridorCapacity <= 0 )
			throw new IllegalArgumentException( "Corridor capacity must be positive.");
		this.corCapacity = corridorCapacity;
	}

	/**
	 * Returns the relative position of the exit on the edge.
	 * @return the relative position of the exit on the edge
	 */
	public double getExitPosition() {
		return exitPositionRelative;
	}

	/**
	 * Sets the relative exit position of the edge. Must be a value between 0 and
	 * 1.
	 * @param exitPosition the relative position of the exit on the edge
	 * @throws IllegalArgumentException if the exit position is out of range
	 */
	public void setExitPosition( double exitPosition ) throws IllegalArgumentException {
		if( exitPosition <= 0 || exitPosition >= 1 )
			throw new IllegalArgumentException( "Exit position must be in ]0,1[" );
		this.exitPositionRelative = exitPosition;
	}

}
