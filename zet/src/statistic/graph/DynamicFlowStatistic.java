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
 * DynamicFlowStatistic.java
 *
 */
package statistic.graph;

import org.zetool.netflow.ds.structure.FlowOverTimeEdge;
import org.zetool.netflow.ds.structure.FlowOverTimePath;

/**
 *
 * @author Martin Groß
 */
public enum DynamicFlowStatistic implements Statistic<FlowOverTimePath, IntegerDoubleMapping, GraphData> {

    MOVED_TIME("Gesamtfahrzeit") {

        @Override
        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            IntegerDoubleMapping result = new IntegerDoubleMapping(true);
            int time = 0;
            int value = 0;
            for (FlowOverTimeEdge edge : flow) {
                time += edge.getDelay();
                result.set(time, value);
                time += statistics.getData().getTransitTime(edge.getEdge());
                value += statistics.getData().getTransitTime(edge.getEdge());
                result.set(time, value);
            }
            return result;
        }
    },
    WAITED_TIME("Gewartete Zeit") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, FlowOverTimePath flow) {
            IntegerDoubleMapping result = new IntegerDoubleMapping(true);
            int time = 0;
            int value = 0;
            result.set(time, value);
            for (FlowOverTimeEdge edge : flow) {
                time += edge.getDelay();
                value += statistics.getData().getTransitTime(edge.getEdge());
                result.set(time, value);
                time += statistics.getData().getTransitTime(edge.getEdge());
                result.set(time, value);
            }
            return result;
        }
    };
    private String description;

    private DynamicFlowStatistic(String description) {
        this.description = description;
    }

    public Class<IntegerDoubleMapping> range() {
        return IntegerDoubleMapping.class;
    }

    @Override
    public String toString() {
        return description;
    }
}
