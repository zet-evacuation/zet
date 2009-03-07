/*
 * DynamicStatistic.java
 *
 */
package statistic.graph;

import ds.graph.Node;
import statistic.graph.IntegerDoubleMapping.TimeValuePair;
import static statistic.graph.DynamicNodeStatistic.*;

/**
 *
 * @author Martin Groß
 */
public enum DynamicStatistic implements Statistic<Object, IntegerDoubleMapping, GraphData> {

    TIME_NEEDED_FOR_FLOW("Zeit benötigt für Individuen") {

        public IntegerDoubleMapping calculate(Statistics<GraphData> statistics, Object object) {
            IntegerDoubleMapping buffer = new IntegerDoubleMapping(true);
            for (Node sink : statistics.getData().getSinks()) {
                buffer = buffer.add(statistics.get(INCOMING_FLOW_AMOUNT, sink));
            }
            IntegerDoubleMapping result = new IntegerDoubleMapping();
            result.set(0, 0.0);
            int count = 0;
            TimeValuePair last = null;
            for (TimeValuePair tvp : buffer) {
                if (tvp.value() > count) {
                    for (int time = count + 1; time < tvp.value(); time++) {
                        double x = (time - last.value()) / (tvp.value() - last.value());
                        double value = last.time() + x * (tvp.time() - last.time());
                        result.set(time, Math.ceil(value));
                    }
                    result.set((int) tvp.value(), tvp.time());
                    count = (int) tvp.value();
                }
                last = tvp;
            }
            return result;
        }
    };

    public Class<IntegerDoubleMapping> range() {
        return IntegerDoubleMapping.class;
    }
    private String description;

    private DynamicStatistic(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
