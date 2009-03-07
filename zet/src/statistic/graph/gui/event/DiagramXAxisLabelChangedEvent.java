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
public class DiagramXAxisLabelChangedEvent implements DiagramChangedEvent {
    
    private DiagramData diagram;
    private String newXAxisLabel;

    public DiagramXAxisLabelChangedEvent(DiagramData diagram, String newXAxisLabel) {
        this.diagram = diagram;
        this.newXAxisLabel = newXAxisLabel;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }

    public String getNewXAxisLabel() {
        return newXAxisLabel;
    }

    public void setNewXAxisLabel(String newXAxisLabel) {
        this.newXAxisLabel = newXAxisLabel;
    }
}
