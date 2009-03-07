/*
 * StatisticDiagramChangedEvent.java
 *
 */
package statistic.graph.gui.event;

import statistic.graph.gui.DiagramData;
import statistic.graph.gui.DisplayableStatistic;

/**
 *
 * @author Martin Groß
 */
public class StatisticDiagramChangedEvent implements StatisticEvent {

    private DiagramData diagram;
    private DiagramData oldDiagram;
    private DisplayableStatistic statistic;

    public StatisticDiagramChangedEvent(DisplayableStatistic statistic, DiagramData oldDiagram, DiagramData diagram) {
        this.statistic = statistic;
        this.oldDiagram = oldDiagram;
        this.diagram = diagram;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public DiagramData getOldDiagram() {
        return oldDiagram;
    }

    public DisplayableStatistic getStatistic() {
        return statistic;
    }
}
