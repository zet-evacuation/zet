/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * StaticFlowStatistic.java
 *
 */
package statistic.graph;

import org.zetool.netflow.ds.structure.FlowOverTimePath;
import org.zetool.graph.Edge;
import org.zetool.netflow.ds.structure.FlowOverTimeEdge;
import static statistic.graph.NodeFlowStatistic.*;

/**
 *
 * @author Martin Groß
 */
public enum StaticFlowStatistic implements Statistic<FlowOverTimePath, Double, GraphData> {

    TOTAL_MOVED_TIME("Gesamtfahrzeit") {

        public Double calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            double time = 0;
            for (Edge edge : flow.edges()) {
                time += statistics.getData().getTransitTime(edge);
            }
            return time;
        }
    },
    TOTAL_WAITED_TIME("Gewartete Zeit") {

        public Double calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            double time = 0;
            for (FlowOverTimeEdge edge : flow) {
                time += edge.getDelay();
            }
            return time;
        }
    },
    TOTAL_NEEDED_TIME("Gesamtzeit") {

        public Double calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            double time = 0;
            for (FlowOverTimeEdge edge : flow) {
                time += edge.getDelay();
                time += statistics.getData().getTransitTime(edge.getEdge());
            }
            return time;
        }
    },
    DISTANCE_TO_SINK("Entfernung zur Senke") {

        public Double calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            return new Double(statistics.getData().getDistance(statistics.get(SOURCE, flow), statistics.get(SINK, flow)));
        }
    },
    DISTANCE_TO_CLOSEST_SINK("Entfernung zur nächsten Senke") {

        public Double calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            return new Double(statistics.getData().getDistance(statistics.get(SOURCE, flow), statistics.get(CLOSEST_SINK, flow)));
        }
    };
    private String description;

    private StaticFlowStatistic(String description) {
        this.description = description;
    }

    public Class<Double> range() {
        return Double.class;
    }

    @Override
    public String toString() {
        return description;
    }
}
