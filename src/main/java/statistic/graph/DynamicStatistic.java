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
package statistic.graph;

import org.zetool.statistic.Statistics;
import org.zetool.statistic.Statistic;
import org.zetool.container.mapping.IntegerDoubleMapping;
import org.zetool.container.mapping.TimeValuePair;
import org.zetool.graph.Node;
import static statistic.graph.DynamicNodeStatistic.*;

/**
 *
 * @author Martin Groß
 */
public enum DynamicStatistic implements Statistic<Object, IntegerDoubleMapping, GraphData> {

    TIME_NEEDED_FOR_FLOW("Zeit benötigt für Individuen") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Object object) {
            IntegerDoubleMapping buffer = new IntegerDoubleMapping(true);
            for (Node sink : statistics.getData().getSinks()) {
                buffer = buffer.add(statistics.get(INCOMING_FLOW_AMOUNT, sink));
            }
            IntegerDoubleMapping result = new IntegerDoubleMapping();
            result.set(0, 0.0);
            int count = 0;
            TimeValuePair last = null;
            for (TimeValuePair tvp : buffer) {
                if (tvp.value() > count) {
                    for (int time = count + 1; time < tvp.value(); time++) {
                        double x = (time - last.value()) / (tvp.value() - last.value());
                        double value = last.time() + x * (tvp.time() - last.time());
                        result.set(time, Math.ceil(value));
                    }
                    result.set((int) tvp.value(), tvp.time());
                    count = (int) tvp.value();
                }
                last = tvp;
            }
            return result;
        }
    };

    public Class<IntegerDoubleMapping> range() {
        return IntegerDoubleMapping.class;
    }
    private String description;

    private DynamicStatistic(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
