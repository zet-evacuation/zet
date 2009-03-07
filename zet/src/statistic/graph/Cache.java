/*
 * Cache.java
 *
 */
package statistic.graph;

import java.util.HashMap;

import statistic.common.Data;

/**
 *
 * @author Martin Groß
 */
@SuppressWarnings("unchecked")
public class Cache {

    private HashMap<Statistic, HashMap> cache;

    public Cache() {
        cache = new HashMap<Statistic, HashMap>();
    }

    public <O, R, D extends Data> boolean contains(Statistic<O, R, D> statistic, O object) {
        return cache.containsKey(statistic) && cache.get(statistic).containsKey(object);
    }

    public <O, R, D extends Data> R get(Statistic<O, R, D> statistic, O object) {
        return (R) cache.get(statistic).get(object);
    }

    public <O, R, D extends Data> void put(Statistic<O, R, D> statistic, O object, R value) {
        if (!cache.containsKey(statistic)) {
            cache.put(statistic, new HashMap<O, R>());
        }
        cache.get(statistic).put(object, value);
    }
}
