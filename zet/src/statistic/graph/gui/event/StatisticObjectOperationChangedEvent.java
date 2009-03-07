/*
 * StatisticObjectOperationChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticObjectOperationChangedEvent implements StatisticChangedEvent {
    
    private DisplayableStatistic statistic;

    public StatisticObjectOperationChangedEvent(DisplayableStatistic statistic) {
        this.statistic = statistic;
    }

    public DisplayableStatistic getStatistic() {
        return statistic;
    }

}
