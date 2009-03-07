/*
 * DiagramYAxisLabelChangedEvent.java
 *
 */

package statistic.graph.gui.event;

import statistic.graph.gui.DiagramData;

/**
 *
 * @author Martin Groß
 */
public class DiagramYAxisLabelChangedEvent implements DiagramChangedEvent {
    
    private DiagramData diagram;
    private String newYAxisLabel;

    public DiagramYAxisLabelChangedEvent(DiagramData diagram, String newYAxisLabel) {
        this.diagram = diagram;
        this.newYAxisLabel = newYAxisLabel;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }

    public String getNewYAxisLabel() {
        return newYAxisLabel;
    }

    public void setNewYAxisLabel(String newYAxisLabel) {
        this.newYAxisLabel = newYAxisLabel;
    }
}
