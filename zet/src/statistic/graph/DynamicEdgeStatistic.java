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
 * DynamicEdgeStatistic.java
 *
 */
package statistic.graph;

import ds.graph.Edge;

/**
 *
 * @author Martin Groß
 */
public enum DynamicEdgeStatistic implements Statistic<Edge, IntegerDoubleMapping, GraphData> {

    FLOW_RATE("Flussrate") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Edge edge) {
            return statistics.getData().getEdgeFlow(edge);
        }
    },
    FLOW_AMOUNT("Flussmenge") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Edge edge) {
            return statistics.get(FLOW_RATE, edge).integral();
        }
    },
    LOAD("Auslastung") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Edge edge) {
            return statistics.get(FLOW_RATE, edge).divide(statistics.getData().getCapacity(edge));
        }
    };
    private String description;

    private DynamicEdgeStatistic(String description) {
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
