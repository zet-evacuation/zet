/*
 * DiagramSequenceChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DiagramData;

/**
 *
 * @author Martin Groß
 */
public class DiagramSequenceChangedEvent implements DiagramEvent {
    
    private DiagramData diagram;
    private DiagramData diagram2;

    public DiagramSequenceChangedEvent(DiagramData diagram, DiagramData diagram2) {
        this.diagram = diagram;
        this.diagram2 = diagram2;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }

    public DiagramData getDiagram2() {
        return diagram2;
    }

    public void setDiagram2(DiagramData diagram2) {
        this.diagram2 = diagram2;
    }

    @Override
    public String toString() {
        return "DiagramSequenceChanged: " + diagram + " <-> " + diagram2;
    }
}
