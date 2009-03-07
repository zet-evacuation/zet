/*
 * DiagramTitleChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DiagramData;

/**
 *
 * @author Martin Groß
 */
public class DiagramTitleChangedEvent implements DiagramChangedEvent {
    
    private DiagramData diagram;
    private String newTitle;

    public DiagramTitleChangedEvent(DiagramData diagram, String newTitle) {
        this.diagram = diagram;
        this.newTitle = newTitle;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    @Override
    public String toString() {
        return "DiagramTitleChangedEvent: New title: " + newTitle;
    }
}
