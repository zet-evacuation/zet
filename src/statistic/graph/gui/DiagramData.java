/*
 * DiagramData.java
 *
 */
package statistic.graph.gui;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author Martin Groß
 */
@XStreamAlias("diagram")
public class DiagramData implements Cloneable {

    private String title;
    private DiagramType type;
    private String xAxisLabel;
    private String yAxisLabel;

    public DiagramData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DiagramType getType() {
        return type;
    }

    public void setType(DiagramType type) {
        this.type = type;
    }

    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    @Override
    protected DiagramData clone() {
        DiagramData clone = new DiagramData();
        clone.setTitle(title);
        clone.setType(type);
        clone.setXAxisLabel(xAxisLabel);
        clone.setYAxisLabel(yAxisLabel);
        return clone;
    }

    @Override
    public String toString() {
        return title;
    }
}
