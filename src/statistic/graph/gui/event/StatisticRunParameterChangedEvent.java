/*
 * StatisticRunParameterChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticRunParameterChangedEvent implements StatisticChangedEvent {
    
    private DisplayableStatistic statistic;

    public StatisticRunParameterChangedEvent(DisplayableStatistic statistic) {
        this.statistic = statistic;
    }

    public DisplayableStatistic getStatistic() {
        return statistic;
    }
}
