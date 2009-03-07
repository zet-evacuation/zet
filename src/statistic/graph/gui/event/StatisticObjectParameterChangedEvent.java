/*
 * StatisticObjectParameterChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticObjectParameterChangedEvent implements StatisticChangedEvent {
    
    private DisplayableStatistic statistic;

    public StatisticObjectParameterChangedEvent(DisplayableStatistic statistic) {
        this.statistic = statistic;
    }

    public DisplayableStatistic getStatistic() {
        return statistic;
    }
}
