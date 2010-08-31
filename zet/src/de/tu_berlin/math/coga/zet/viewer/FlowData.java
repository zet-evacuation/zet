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
		public double corCap = 4;
		public double exitPos = 0.75;
		public double taue;
		public double cap;
		public double firstAtTail;
		public double firstEnterExit;
		public double firstLeaveExit;
		public double firstAtHead;
		public double lastAtTail;
		public double lastEnterExit;
		public double lastLeaveExit;
		public double lastAtHead;
		public double tu = 30;
		public double queueLengthForFirst;
		public double queueLengthForLast;
		public double firstAfterTail;
		public double firstBeforeEnterExit;
		public double firstAfterEnterExit;
		public double firstBeforeLeaveExit;
		public double firstAfterLeaveExit;
		public double firstBeforeHead;
		public double lastAfterTail;
		public double lastBeforeEnterExit;
		public double lastAfterEnterExit;
		public double lastBeforeLeaveExit;
		public double lastAfterLeaveExit;
		public double lastBeforeHead;

		public FlowData( double startTime, double endTime, double inFlow, double waitTime, double cap, double tau, GLColor color ) {
			this.starttime = startTime;
			this.endtime = endTime;
			this.inflow = inFlow;
			this.waittime = waitTime;
			this.taue = tau;
			this.cap = cap;
			this.color = color;
			computeFixData();
		}

		private void computeFixData() {
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

			firstAfterTail = firstAtTail * tu + 1;
			firstBeforeEnterExit = firstEnterExit * tu + 1;
			firstAfterEnterExit = firstEnterExit * tu + 1;
			firstBeforeLeaveExit = firstLeaveExit * tu + 1;
			firstAfterLeaveExit = firstLeaveExit * tu + 1;
			firstBeforeHead = firstAtHead * tu + 1;

			lastAfterTail = lastAtTail * tu + 1;

			lastBeforeEnterExit = lastEnterExit * tu + 1;
			lastAfterEnterExit = lastEnterExit * tu + 1;
			lastBeforeLeaveExit = lastLeaveExit * tu + 1;
			lastAfterLeaveExit = lastLeaveExit * tu + 1;
			lastBeforeHead = lastAtHead * tu + 1;
		}
	}
