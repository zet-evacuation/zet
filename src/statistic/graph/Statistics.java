/*
 * Statistics.java
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
public class Statistics<D extends Data> {

    private Cache cache;
    private D data;

    public Statistics(D data) {
        this.cache = new Cache();
        this.data = data;
    }

    protected Cache getCache() {
        return cache;
    }

    public D getData() {
        return data;
    }

    public <O, R> R get(Statistic<O, R, D> statistic) {
        if (!cache.contains(statistic, null)) {
            R value = statistic.calculate(this, null);
            cache.put(statistic, null, value);
        }
        return cache.get(statistic, null);
    }

    public <O, R> R get(Statistic<O, R, D> statistic, O object) {
        if (!cache.contains(statistic, object)) {
            R value = statistic.calculate(this, object);
            cache.put(statistic, object, value);
        }
        return cache.get(statistic, object);
    }

    public <O, R> List<R> get(Statistic<O, R, D> statistic, O o, O o2, O... objects) {
        List<R> results = new LinkedList<R>();
        results.add(get(statistic, o));
        results.add(get(statistic, o2));
        for (O object : objects) {
            results.add(get(statistic, object));
        }
        return results;
    }

    public <O, R> R get(Statistic<O, R, D> statistic, Operation<R> operation, O... objects) {
        if (objects.length == 0) {
            return get(statistic, null);
        } else if (objects.length == 1) {
            return get(statistic, objects[0]);
        } else {
            List<R> results = new LinkedList<R>();
            for (O object : objects) {
                results.add(get(statistic, object));
            }
            return operation.execute(results);
        }
    }
}
