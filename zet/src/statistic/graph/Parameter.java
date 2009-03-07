/*
 * Parameter.java
 *
 */

package statistic.graph;

/**
 *
 * @author Martin Groß
 */
public class Parameter {
    
    private String name;
    private double max;
    private double min;
    private double def;

    public Parameter() {
    }

    public Parameter(String name, double min, double max, double def) {
        this.name = name;
        this.max = max;
        this.min = min;
        this.def = def;
    }

    public double getDef() {
        return def;
    }

    public void setDef(double def) {
        this.def = def;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
