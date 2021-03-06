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
package statistic.graph;

import org.zetool.statistic.Statistics;
import org.zetool.statistic.Statistic;

/**
 *
 * @author Martin Groß
 */
public enum StaticStatistic implements Statistic<Object, Double, GraphData> {

    TOTAL_TIME("Insgesamt benötigte Zeit") {

        public Double calculate(Statistics<GraphData> statistics, Object object) {
            return new Double(statistics.getData().getTimeHorizon());
        }
    };
    private String description;

    private StaticStatistic(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public Class<Double> range() {
        return Double.class;
    }
}
