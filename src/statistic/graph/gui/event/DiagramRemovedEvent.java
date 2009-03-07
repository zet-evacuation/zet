/*
 * DiagramRemovedEvent.java
 *
 */
package statistic.graph.gui.event;

import statistic.graph.gui.DiagramData;

/**
 *
 * @author Martin Groß
 */
public class DiagramRemovedEvent implements DiagramEvent {

    private DiagramData diagram;

    public DiagramRemovedEvent(DiagramData diagram) {
        this.diagram = diagram;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }
}
