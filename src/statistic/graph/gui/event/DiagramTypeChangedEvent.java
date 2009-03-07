/*
 * DiagramTypeChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DiagramData;
import statistic.graph.gui.DiagramType;

/**
 *
 * @author Martin Groß
 */
public class DiagramTypeChangedEvent implements DiagramEvent {
    
    private DiagramData diagram;
    private DiagramType type;

    public DiagramTypeChangedEvent(DiagramData diagram, DiagramType type) {
        this.diagram = diagram;
        this.type = type;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }

    public DiagramType getType() {
        return type;
    }

    public void setType(DiagramType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DiagramTypeChangedEvent: New type: " + type;
    }
}
