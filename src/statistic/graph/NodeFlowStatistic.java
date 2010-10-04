/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * NodeFlowStatistic.java
 *
 */
package statistic.graph;

import ds.graph.flow.FlowOverTimePath;
import ds.graph.Node;

/**
 *
 * @author Martin Groß
 */
public enum NodeFlowStatistic implements Statistic<FlowOverTimePath, Node, GraphData> {

    SOURCE {

        public Node calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            return flow.firstEdge().start();
        }
    },
    SINK {

        public Node calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            return flow.lastEdge().end();
        }
    },
    CLOSEST_SINK {

        public Node calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            int min = Integer.MAX_VALUE;
            Node result = null;
            for (Node sink : statistics.getData().getSinks()) {
                if (statistics.getData().getDistance(statistics.get(SOURCE, flow), sink) < min) {
                    min = statistics.getData().getDistance(statistics.get(SOURCE, flow), sink);
                    result = sink;
                }
            }
            return result;
        }
    };

    public Class<Node> range() {
        return Node.class;
    }
}
