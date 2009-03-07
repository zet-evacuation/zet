/*
 * DiagramEvent.java
 *
 */
package statistic.graph.gui.event;

import statistic.graph.gui.DiagramData;

/**
 *
 * @author Martin Groß
 */
public interface DiagramEvent extends Event {

    DiagramData getDiagram();
}
