/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
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
	public final double colorDifference;

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
		this.colorDifference = globalEnd - globalStart;
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
