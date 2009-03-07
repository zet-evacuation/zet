/*
 * StatisticDescriptionChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticDescriptionChangedEvent implements StatisticChangedEvent {
    
    private DisplayableStatistic statistic;

    public StatisticDescriptionChangedEvent(DisplayableStatistic statistic) {
        this.statistic = statistic;
    }

    public DisplayableStatistic getStatistic() {
        return statistic;
    }
}
