/*
 * StatisticColorChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticColorChangedEvent implements StatisticChangedEvent {
    
    private DisplayableStatistic statistic;

    public StatisticColorChangedEvent(DisplayableStatistic statistic) {
        this.statistic = statistic;
    }    

    public DisplayableStatistic getStatistic() {
        return statistic;
    }
}
