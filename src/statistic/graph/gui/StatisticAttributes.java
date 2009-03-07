/*
 * StatisticAttributes.java
 *
 */
package statistic.graph.gui;

import java.awt.Color;

/**
 *
 * @author Martin Groß
 */
public class StatisticAttributes {

    private Color color;
    private DiagramData diagram;
    private String name;

    public StatisticAttributes() {
        name = "";
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public DiagramData getDiagram() {
        return diagram;
    }

    public void setDiagram(DiagramData diagram) {
        this.diagram = diagram;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public StatisticAttributes clone() {
        StatisticAttributes clone = new StatisticAttributes();
        clone.setColor(color);
        clone.setDiagram(diagram);
        clone.setName(name);
        return clone;
    }
}
