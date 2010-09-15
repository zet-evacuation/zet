/**
 * FlowData.java
 * Created: 30.08.2010 17:12:46
 */
package de.tu_berlin.math.coga.zet.viewer;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FlowData {
	public final int iteration;
	
	final public double startTime;
	final public double endTime;
	final public double inflow;
	final public double waittime;
	public final double globalStart;
	public final double globalEnd;

	final public double firstEnterExit;
	final public double firstLeaveExit;
	final public double firstAtHead;
	final public double lastEnterExit;
	final public double lastLeaveExit;
	final public double lastAtHead;
	final public double queueLengthForFirst;
	final public double queueLengthForLast;
	final public boolean capGreaterThanInflow;
	final public boolean waittimePositive;

	public FlowData( double startTime, double endTime, double inFlow, double waitTime, double capacity, double transitTime, double corridorCapacity, double exitPosition, int iteration, double globalStart, double globalEnd ) {
		this.globalStart = globalStart;
		this.globalEnd = globalEnd;
		this.startTime = startTime;
		this.endTime = endTime;
		this.inflow = inFlow;
		this.waittime = waitTime;
		this.iteration = iteration;

		capGreaterThanInflow = capacity > inflow;
		waittimePositive = waittime > 0;
		if( waittimePositive || !capGreaterThanInflow ) {
			firstEnterExit = this.startTime + exitPosition * transitTime;
			firstLeaveExit = firstEnterExit + waittime;
			firstAtHead = firstLeaveExit + (1 - exitPosition) * transitTime;
			lastEnterExit = this.endTime + exitPosition * transitTime;
			lastLeaveExit = lastEnterExit + (waittime + (this.endTime - this.startTime) * (inflow - capacity) / capacity);
			lastAtHead = lastLeaveExit + (1 - exitPosition) * transitTime;

			// queue stuff
			queueLengthForFirst = waittime * capacity / corridorCapacity;
			queueLengthForLast = queueLengthForFirst + (this.endTime - this.startTime) * (inflow - capacity) / corridorCapacity;
		} else {
			firstAtHead = this.startTime + transitTime;
			lastAtHead = this.endTime + transitTime;
			// not used in this case but must be initialized
			firstEnterExit = 0;
			firstLeaveExit = 0;
			lastEnterExit = 0;
			lastLeaveExit = 0;
			queueLengthForFirst = 0;
			queueLengthForLast = 0;
		}
	}
}
