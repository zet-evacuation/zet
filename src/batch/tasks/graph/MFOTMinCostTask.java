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

import algo.graph.dynamicflow.maxflow.MaxFlowOverTime;
import algo.graph.dynamicflow.maxflow.MaximumFlowOverTimeProblem;
import ds.NetworkFlowModelAlgorithm;
import ds.NetworkFlowModel;
import ds.graph.flow.PathBasedFlowOverTime;

public class MFOTMinCostTask extends NetworkFlowModelAlgorithm {

    private int th;

    public MFOTMinCostTask(int timeHorizon) {
        this.th = timeHorizon;
    }

    @Override
    protected PathBasedFlowOverTime runAlgorithm(NetworkFlowModel model) {
        MaxFlowOverTime maxFlowOverTimeAlgo = new MaxFlowOverTime();
        maxFlowOverTimeAlgo.setProblem(new MaximumFlowOverTimeProblem(model.getNetwork(), model.getEdgeCapacities(), model.getTransitTimes(), model.getSinks(), model.getSources(), th));
        maxFlowOverTimeAlgo.run();
        return maxFlowOverTimeAlgo.getSolution();
    }
}
