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
 * ProfileType.java
 *
 */
package statistic.graph.gui;

import statistic.graph.DynamicEdgeStatistic;
import statistic.graph.DynamicFlowStatistic;
import statistic.graph.DynamicNodeStatistic;
import statistic.graph.DynamicStatistic;
import statistic.graph.StaticEdgeStatistic;
import statistic.graph.StaticFlowStatistic;
import statistic.graph.StaticNodeStatistic;
import statistic.graph.StaticStatistic;

/**
 *
 * @author Martin Groß
 */
public enum ProfileType {

    GLOBAL("Globale Statistiken", StaticStatistic.class, DynamicStatistic.class),
    EDGE("Kanten Statistiken", StaticEdgeStatistic.class, DynamicEdgeStatistic.class),
    NODE("Knoten Statistiken", StaticNodeStatistic.class, DynamicNodeStatistic.class),
    FLOW("Fluss Statistiken", StaticFlowStatistic.class, DynamicFlowStatistic.class);
    private String description;
    private Class<? extends Enum>[] statistics;

    private ProfileType(String description, Class<? extends Enum>... statistics) {
        this.description = description;
        this.statistics = statistics;
    }

    public Class<? extends Enum>[] getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return description;
    }
}
