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
 * StaticNodeStatistic.java
 *
 */
package statistic.graph;

import ds.graph.Node;
import static statistic.graph.DynamicNodeStatistic.*;

/**
 *
 * @author Martin Groß
 */
public enum StaticNodeStatistic implements Statistic<Node, Double, GraphData> {

    TOTAL_LOAD("Gemittelte Auslastung") {

        public Double calculate(Statistics<GraphData> statistics, Node node) {
            return statistics.get(STORED_FLOW_AMOUNT, node).get(statistics.getData().getTimeHorizon()) / (statistics.getData().getCapacity(node) * statistics.getData().getTimeHorizon());
        }
    };
    private String description;

    private StaticNodeStatistic(String description) {
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
