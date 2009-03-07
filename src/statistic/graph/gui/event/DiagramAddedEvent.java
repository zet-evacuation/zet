/*
 * DiagramAddedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DiagramData;

/**
 *
 * @author Martin Groß
 */
public class DiagramAddedEvent implements DiagramEvent {
    
    private DiagramData diagram;

    public DiagramAddedEvent(DiagramData newDiagram) {
        this.diagram = newDiagram;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }

    @Override
    public String toString() {
        return "DiagramAddedEvent: " + diagram;
    }
}
