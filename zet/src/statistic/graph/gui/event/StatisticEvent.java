/*
 * StatisticEvent.java
 *
 */
package statistic.graph.gui.event;

import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public interface StatisticEvent extends Event {
    
    DisplayableStatistic getStatistic();
}
