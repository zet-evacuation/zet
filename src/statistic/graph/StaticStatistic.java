/*
 * StaticStatistic.java
 *
 */
package statistic.graph;

/**
 *
 * @author Martin Groß
 */
public enum StaticStatistic implements Statistic<Object, Double, GraphData> {

    TOTAL_TIME("Insgesamt benötigte Zeit") {

        public Double calculate(Statistics<GraphData> statistics, Object object) {
            return new Double(statistics.getData().getTimeHorizon());
        }
    };
    private String description;

    private StaticStatistic(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public Class<Double> range() {
        return Double.class;
    }
}
