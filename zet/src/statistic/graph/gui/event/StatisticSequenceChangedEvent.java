/*
 * StatisticSequenceChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticSequenceChangedEvent implements StatisticEvent {
    
    private DisplayableStatistic statistic;
    private DisplayableStatistic statistic2;

    public StatisticSequenceChangedEvent(DisplayableStatistic statistic, DisplayableStatistic statistic2) {
        this.statistic = statistic;
        this.statistic2 = statistic2;        
    }    

    public DisplayableStatistic getStatistic() {
        return statistic;
    }
    
    public DisplayableStatistic getStatistic2() {
        return statistic2;
    }
}
