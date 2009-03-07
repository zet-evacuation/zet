/*
 * StatisticsCollection.java
 *
 */
package statistic.graph;

import java.util.LinkedList;
import java.util.List;

import statistic.common.Data;

/**
 *
 * @author Martin Groß
 */
public class StatisticsCollection<D extends Data> extends LinkedList<Statistics<D>> {

    private List<String> names;

    public StatisticsCollection() {
        super();
        names = new LinkedList<String>();
    }

    @Override
    public boolean add(Statistics e) {
        names.add("Durchlauf " + (size() + 1));
        return super.add(e);
    }
    
    public List<String> getNames() {
        return names;
    }

    /*
    public boolean add(Statistics statistic) {
        statisticsList.add(statistic);
    }
/*
    public <O, R> List<R> get(Statistic<O, R> statistic) {
        List<R> result = new LinkedList<R>();
        for (Statistics statistics : statisticsList) {
            result.add(statistics.get(statistic));
        }
        return result;
    }

    public <O, R> List<R> get(Statistic<O, R> statistic, O object) {
        List<R> result = new LinkedList<R>();
        for (Statistics statistics : statisticsList) {
            result.add(statistics.get(statistic, object));
        }
        return result;
    }

    public <O, R> R get(Statistic<O, R> statistic, Operation<R> operation, Object... parameters) {
        return operation.execute(get(statistic), parameters);
    }

    public <O, R> R get(Statistic<O, R> statistic, O object, Operation<R> operation, Object... parameters) {
        return operation.execute(get(statistic, object), parameters);
    }*/
}
