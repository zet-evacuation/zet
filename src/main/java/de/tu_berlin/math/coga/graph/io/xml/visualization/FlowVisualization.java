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
package de.tu_berlin.math.coga.graph.io.xml.visualization;

import java.util.List;

import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.graph.visualization.NodePositionMapping;
import org.zetool.math.vectormath.Vector3;
import org.zetool.netflow.ds.flow.EdgeBasedFlowOverTime;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FlowVisualization extends GraphVisualization {

    private EdgeBasedFlowOverTime flow;
    int timeHorizon;
    int maxFlowRate;
    boolean edgesDoubled = false;

    public FlowVisualization(DirectedGraph network, NodePositionMapping<Vector3> nodePositionMapping, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Node> supplies, List<Node> sources, List<Node> sinks) {
        super(network, nodePositionMapping, edgeCapacities, nodeCapacities, transitTimes, supplies, sources, sinks);
        flow = new EdgeBasedFlowOverTime(getNetwork());
    }

    public FlowVisualization(EarliestArrivalFlowProblem eafp, NodePositionMapping nodePositionMapping) {
        super(eafp, nodePositionMapping);
        flow = new EdgeBasedFlowOverTime(getNetwork());
    }

    public FlowVisualization(GraphVisualization gv) {
        super(gv);
        flow = new EdgeBasedFlowOverTime(getNetwork());
    }

    public EdgeBasedFlowOverTime getFlow() {
        return flow;
    }

    public void setFlow(EdgeBasedFlowOverTime flow) {
        this.flow = flow;
    }

    public void setFlow(EdgeBasedFlowOverTime flow, int maxFlowRate) {
        setFlow(flow);
        setMaxFlowRate(maxFlowRate);
    }

    public int getTimeHorizon() {
        return timeHorizon;
    }

    public void setTimeHorizon(int timeHorizon) {
        this.timeHorizon = timeHorizon;
    }

    public int getMaxFlowRate() {
        return maxFlowRate;
    }

    protected void setMaxFlowRate(int maxFlowRate) {
        this.maxFlowRate = maxFlowRate;
    }

    public boolean isEdgesDoubled() {
        return edgesDoubled;
    }

    public void setEdgesDoubled(boolean edgesDoubled) {
        this.edgesDoubled = edgesDoubled;
    }
}
