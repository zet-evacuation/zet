/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * MaxFlowTask.java
 * 
 */

package batch.tasks.graph;

import batch.tasks.*;
import ds.NetworkFlowModel;
import algo.graph.dynamicflow.maxflow.TimeExpandedMaximumFlowOverTime;

/**
 *
 */
public class MFOTimeExpandedTask extends GraphAlgorithmTask {
	private int th;
	public MFOTimeExpandedTask( NetworkFlowModel model, int timeHorizon ) {
		super (model);
		this.th = timeHorizon;
	}
	
	@Override
	public void run() {
            
		TimeExpandedMaximumFlowOverTime maxFlowOverTimeAlgo =
			new TimeExpandedMaximumFlowOverTime(model.getNetwork(), model.getEdgeCapacities(), model.getTransitTimes(), model.getSources(), model.getSinks(), th);
		maxFlowOverTimeAlgo.run();
		df = maxFlowOverTimeAlgo.getSolution();
                
                //TransshipmentBoundEstimator tbe = new TransshipmentBoundEstimator();
                //int bound = tbe.calculateBound(model.getNetwork(), model.getTransitTimes(), model.getEdgeCapacities(), model.getCurrentAssignment());
                //System.out.println(bound);
	}
}
