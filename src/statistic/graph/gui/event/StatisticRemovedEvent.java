/*
 * StatisticRemovedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticRemovedEvent implements StatisticEvent {

    private DisplayableStatistic statistic;

    public StatisticRemovedEvent(DisplayableStatistic statistic) {
        this.statistic = statistic;
    }
    
    public DisplayableStatistic getStatistic() {
        return statistic;
    }
}
