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
