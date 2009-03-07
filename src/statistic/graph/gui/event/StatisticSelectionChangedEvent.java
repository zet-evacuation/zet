/*
 * StatisticSelectionChangedEvent.java
 *
 */
package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 *  @author Martin Groß
 */
public class StatisticSelectionChangedEvent implements StatisticEvent {

    private DisplayableStatistic statistic;

    public StatisticSelectionChangedEvent(DisplayableStatistic displayableStatistic) {
        this.statistic = displayableStatistic;
    }

    public DisplayableStatistic getStatistic() {
        return statistic;
    }

    public void setStatistic(DisplayableStatistic statistic) {
        this.statistic = statistic;
    }
}
