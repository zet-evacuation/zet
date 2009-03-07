/*
 * StatisticAddedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticAddedEvent implements StatisticEvent {

    private DisplayableStatistic statistic;

    public StatisticAddedEvent(DisplayableStatistic statistic) {
        this.statistic = statistic;
    }
    
    public DisplayableStatistic getStatistic() {
        return statistic;
    }
}
