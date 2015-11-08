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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
/*
 * StaticEdgeStatistic.java
 *
 */
package statistic.graph;

import org.zetool.statistic.Statistics;
import org.zetool.statistic.Statistic;
import org.zetool.graph.Edge;
import static statistic.graph.DynamicEdgeStatistic.*;

/**
 *
 * @author Martin Groß
 */
public enum StaticEdgeStatistic implements Statistic<Edge, Double, GraphData> {

    CAPACITY("Kapazität") {

        public Double calculate(Statistics<GraphData> statistics, Edge edge) {
            return new Double(statistics.getData().getCapacity(edge));
        }        
    },
    TOTAL_LOAD("Gemittelte Auslastung") {

        public Double calculate(Statistics<GraphData> statistics, Edge edge) {
            return statistics.get(FLOW_AMOUNT, edge).get(statistics.getData().getTimeHorizon()) / (statistics.getData().getCapacity(edge) * statistics.getData().getTimeHorizon());
        }
    };
    private String description;

    private StaticEdgeStatistic(String description) {
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
