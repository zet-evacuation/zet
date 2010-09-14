/**
 * FlowData.java
 * Created: 30.08.2010 17:12:46
 */
package de.tu_berlin.math.coga.zet.viewer;

import opengl.drawingutils.GLColor;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FlowData {
	public GLColor color;
	public double starttime = 2;
	public double endtime = 3.5;
	public double inflow = 1.5;
	public double waittime = 1;
	public double corCap = 5;
	public double exitPos = 0.75;
	public double taue;
	public double cap;
	final public double firstAtTail;
	final public double firstEnterExit;
	final public double firstLeaveExit;
	final public double firstAtHead;
	final public double lastAtTail;
	final public double lastEnterExit;
	final public double lastLeaveExit;
	final public double lastAtHead;
	final public double queueLengthForFirst;
	final public double queueLengthForLast;
	final public double firstAfterTail;
	final public double firstBeforeEnterExit;
	final public double firstAfterEnterExit;
	final public double firstBeforeLeaveExit;
	final public double firstAfterLeaveExit;
	final public double firstBeforeHead;
	final public double lastAfterTail;
	final public double lastBeforeEnterExit;
	final public double lastAfterEnterExit;
	final public double lastBeforeLeaveExit;
	final public double lastAfterLeaveExit;
	final public double lastBeforeHead;
	final public boolean capGreaterThanInflow;
	final public boolean waittimePositive;

	public FlowData( double startTime, double endTime, double inFlow, double waitTime, double cap, double tau, GLColor color ) {
		this.starttime = startTime;
		this.endtime = endTime;
		this.inflow = inFlow;
		this.waittime = waitTime;
		this.taue = tau;
		this.cap = cap;
		this.color = color;

		capGreaterThanInflow = cap > inflow;
		waittimePositive = waittime > 0;
		if( waittimePositive || !capGreaterThanInflow ) {
			firstAtTail = starttime;
			firstEnterExit = firstAtTail + exitPos * taue;
			firstLeaveExit = firstEnterExit + waittime;
			firstAtHead = firstLeaveExit + (1 - exitPos) * taue;
			lastAtTail = endtime;
			lastEnterExit = lastAtTail + exitPos * taue;
			lastLeaveExit = lastEnterExit + (waittime + (endtime - starttime) * (inflow - cap) / cap);
			lastAtHead = lastLeaveExit + (1 - exitPos) * taue;

			// queue stuff
			queueLengthForFirst = waittime * cap / corCap;
			queueLengthForLast = queueLengthForFirst + (endtime - starttime) * (inflow - cap) / corCap;

			firstAfterTail = firstAtTail;
			firstBeforeEnterExit = firstEnterExit;
			firstAfterEnterExit = firstEnterExit;
			firstBeforeLeaveExit = firstLeaveExit;
			firstAfterLeaveExit = firstLeaveExit;
			firstBeforeHead = firstAtHead;

			lastAfterTail = lastAtTail;

			lastBeforeEnterExit = lastEnterExit;
			lastAfterEnterExit = lastEnterExit;
			lastBeforeLeaveExit = lastLeaveExit;
			lastAfterLeaveExit = lastLeaveExit;
			lastBeforeHead = lastAtHead;
		} else {
			firstAtTail = starttime;
			firstAtHead = firstAtTail + taue;
			lastAtTail = endtime;
			lastAtHead = lastAtTail + taue;
			firstAfterTail = firstAtTail;
			firstBeforeHead = firstAtHead;
			lastAfterTail = lastAtTail;
			lastBeforeHead = lastAtHead;



			firstEnterExit = 0;
			firstLeaveExit = 0;
			lastEnterExit = 0;
			lastLeaveExit = 0;
			queueLengthForFirst = 0;
			queueLengthForLast = 0;
			firstBeforeEnterExit = 0;
			firstAfterEnterExit = 0;
			firstBeforeLeaveExit = 0;
			firstAfterLeaveExit = 0;
			lastBeforeEnterExit = 0;
			lastAfterEnterExit = 0;
			lastBeforeLeaveExit = 0;
			lastAfterLeaveExit = 0;

		}
	}
}
