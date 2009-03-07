/*
 * StatisticRunOperationChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticRunOperationChangedEvent implements StatisticChangedEvent {
    
    private DisplayableStatistic statistic;

    public StatisticRunOperationChangedEvent(DisplayableStatistic statistic) {
        this.statistic = statistic;
    }

    public DisplayableStatistic getStatistic() {
        return statistic;
    }
}
