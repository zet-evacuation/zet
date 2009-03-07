/*
 * StatisticBaseValueChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticBaseValueChangedEvent implements StatisticChangedEvent {
    
    private DisplayableStatistic statistic;

    public StatisticBaseValueChangedEvent(DisplayableStatistic statistic) {
        this.statistic = statistic;
    }

    public DisplayableStatistic getStatistic() {
        return statistic;
    }
}
