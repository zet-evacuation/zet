/**
 * NashFlowEdgeData.java
 * Created: 30.08.2010 17:11:13
 */
package de.tu_berlin.math.coga.zet.viewer;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * <p>Collects all necessary data for the Nash flow visualization for one edge.
 * Basic edge data is {@code transitTime} and {@code capacity}. Basic
 * visualization parameters are {@code corridorCapacity} and the relative
 * {@code exitPosition}.</p>
 * <p>For each iteration one can {@link #add()} the flow on the edge. This
 * includes the {@code inflow}, the {@code waitTime}, the {@code startTime}
 * and {@code endTime} of flow flowing into the edge.</p>
 * <p>Additionally, for each peace of Nash flow the {@code iteration} is needed
 * and the information at what time the flow started flowing and ended doing so
 * at the source.</p>
 * @author Jan-Philipp Kappmeier
 */
public class NashFlowEdgeData implements Iterable<FlowData> {
	/** A list of the Nash flows on the edge. One element for each iteration. */
	ArrayList<FlowData> flowDatas = new ArrayList<>();
	/** The capacity of the edge. */
	double capacity = 1;
	/** The capacity of the edge. */
	double transitTime = 1;
	/** The transit time of the edge. */
	double corridorCapacity = 4;
	/** The relative exit position on the edge (during visualization) */
	double exitPositionRelative = 0.8;

	/**
	 * Creates a new edge data for an edge with given {@code capacity} and
	 * {@code transitTime}.
	 * @param capacity the capacity of the edge
	 * @param transitTime the transit time of the edge
	 */
	public NashFlowEdgeData( double capacity, double transitTime ) {
		this( capacity, transitTime, 3.9, 0.77 );
	}

	/**
	 * Creates a new edge data for an edge with given {@code capacity} and
	 * {@code transitTime}. Additionally the visualization values for the
	 * {@code corridorCapacity} and the relative {@code exitPosition} are needed.
	 * @param capacity the capacity of the edge
	 * @param transitTime the transit time of the edge
	 * @param corridorCapacity the capacity of the corridor. should be greater than the capacity
	 * @param exitPosition the relative position of the exit on the edge.
	 */
	public NashFlowEdgeData( double capacity, double transitTime, double corridorCapacity, double exitPosition ) {
		if( exitPosition <= 0 || exitPosition >= 1 )
			throw new IllegalArgumentException( "Exit position must be in ]0,1[" );
		this.capacity = capacity;
		this.transitTime = transitTime;
		this.corridorCapacity = corridorCapacity;
		this.exitPositionRelative = exitPosition;
	}

	/**
	 * Returns the {@code capacity} of the edge.
	 * @return the {@code capacity} of the edge
	 */
	public double getCapacity() {
		return capacity;
	}

	/**
	 * Returns the {@code transitTime} of the edge.
	 * @return the transit time of the edge
	 */
	public double getTransitTime() {
		return transitTime;
	}


	/**
	 * Returns the width of the corridor.
	 * @return the corridor capacity
	 */
	public double getCorridorCapacity() {
		return corridorCapacity;
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
		this.corridorCapacity = corridorCapacity;
		rebuild();
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
		rebuild();
	}

	/**
	 * Adds a new piece of Nash flow to the edge. The piece equals one iteration
	 * of the algorithm.
	 * @param start the time at which flow enters the edge first time
	 * @param end the time at which flow enters the edge last time
	 * @param inflow the rate with that flow enters the edge
	 * @param wait the wait time
	 * @param iteration the iteration in which the piece of flow was computed
	 * @param globalStart the time at which the flow started leaving the source
	 * @param globalEnd the time at which the flow stopped leaving the source
	 */
	public void add( double start, double end, double inflow, double wait, int iteration, double globalStart, double globalEnd ) {
		flowDatas.add( new FlowData( start, end, inflow, wait, capacity, transitTime, corridorCapacity, exitPositionRelative, iteration, globalStart, globalEnd ) );
	}

	/**
	 * Recomputes the flow data. This needs to be done when corridor capacity or
	 * exit position was changed.
	 */
	private void rebuild() {
		ArrayList<FlowData> newData = new ArrayList<FlowData>();
		for( FlowData f : flowDatas )
			newData.add( new FlowData( f.startTime, f.endTime, f.inflow, f.waittime, capacity, transitTime, corridorCapacity, exitPositionRelative, f.iteration, f.globalStart, f.globalEnd ) );
		flowDatas.clear();
		flowDatas = newData;
	}

	/**
	 * Returns an iterator which allows to iterate over all flow data (for each
	 * iteration of the Nash flow algorithm).
	 * @return an iterator for all flow data
	 */
	public Iterator<FlowData> iterator() {
		return flowDatas.iterator();
	}
}
