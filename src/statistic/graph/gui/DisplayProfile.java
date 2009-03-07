/*
 * DisplayProfile.java
 *
 */
package statistic.graph.gui;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
@XStreamAlias("profile")
public class DisplayProfile {

    private String name;
    private ProfileType type;
    private List<DiagramData> diagrams;
    private List<DisplayableStatistic> statistics;

    public DisplayProfile() {
        diagrams = new LinkedList<DiagramData>();
        statistics = new LinkedList<DisplayableStatistic>();
    }

    public List<DiagramData> getDiagrams() {
        return diagrams;
    }

    public void setDiagrams(List<DiagramData> diagrams) {
        this.diagrams = diagrams;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DisplayableStatistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<DisplayableStatistic> statistics) {
        this.statistics = statistics;
    }

    public ProfileType getType() {
        return type;
    }

    public void setType(ProfileType type) {
        this.type = type;
    }

    @Override
    public DisplayProfile clone() {
        DisplayProfile clone = new DisplayProfile();
        clone.setName(name);
        for (DiagramData diagram : diagrams) {
            clone.getDiagrams().add(diagram.clone());
        }        
        for (DisplayableStatistic statistic : statistics) {
            clone.getStatistics().add(statistic.clone());
        }
        clone.setType(type);
        return clone;
    }

    @Override
    public String toString() {
        return name;
    }
}
