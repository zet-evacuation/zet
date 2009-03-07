/*
 * DisplayableStatistic.java
 *
 */
package statistic.graph.gui;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import statistic.common.Data;
import statistic.graph.ComplexStatistic;

/**
 *
 * @author Martin Groß
 */
@XStreamAlias("statistic")
public class DisplayableStatistic<O, R, D extends Data> {

    private ComplexStatistic<O, R, D> statistic;
    private StatisticAttributes attributes;

    public DisplayableStatistic() {
        statistic = new ComplexStatistic<O, R, D>();
        attributes = new StatisticAttributes();
    }

    public StatisticAttributes getAttributes() {
        return attributes;
    }

    public boolean isInitialized() {
        boolean result = getStatistic().getStatistic() != null && getAttributes().getDiagram() != null;
        boolean compareObjects = getStatistic().getObjectOperation() == null || getStatistic().getObjectOperation().isComparing();
        boolean compareRuns = getStatistic().getRunOperation() == null || getStatistic().getRunOperation().isComparing();
        boolean singleValue = !compareObjects && !compareRuns;
        boolean nestedLists = compareObjects && compareRuns;
        if (nestedLists) {
            return result && getAttributes().getDiagram().getType().supportsNestedLists();
        } else if (singleValue) {
            return result && getAttributes().getDiagram().getType().supportsSingleValues();
        } else {
            return result;
        }
    }
    
    public String getReason() {
        StringBuilder reason = new StringBuilder();
        if (getStatistic().getStatistic() == null) {
            reason.append("Modellgröße");
        }
        if (getAttributes().getDiagram() == null) {
            if (reason.length() > 0) {
                reason.append(", ");
            }
            reason.append("Diagramm");
        }
        boolean compareObjects = getStatistic().getObjectOperation() == null || getStatistic().getObjectOperation().isComparing();
        boolean compareRuns = getStatistic().getRunOperation() == null || getStatistic().getRunOperation().isComparing();
        boolean singleValue = !compareObjects && !compareRuns;
        boolean nestedLists = compareObjects && compareRuns;        
        if (singleValue && getAttributes().getDiagram() != null && !getAttributes().getDiagram().getType().supportsSingleValues()) {
            if (reason.length() > 0) {
                reason.append(", ");
            }
            reason.append("Zu wenig Werte für das Diagramm");
        }        
        if (nestedLists && getAttributes().getDiagram() != null && !getAttributes().getDiagram().getType().supportsNestedLists()) {
            if (reason.length() > 0) {
                reason.append(", ");
            }
            reason.append("Zu viele Werte für das Diagramm");
        }                
        return reason.toString();
    }

    public void setAttributes(StatisticAttributes attributes) {
        this.attributes = attributes;
    }

    public ComplexStatistic<O, R, D> getStatistic() {
        return statistic;
    }

    public void setStatistic(ComplexStatistic<O, R, D> statistic) {
        this.statistic = statistic;
    }

    @Override
    public DisplayableStatistic<O, R, D> clone() {
        DisplayableStatistic<O, R, D> clone = new DisplayableStatistic<O, R, D>();
        clone.setAttributes(attributes.clone());
        clone.setStatistic(statistic.clone());
        return clone;
    }

    @Override
    public String toString() {
        return attributes.getName();
    }
}
