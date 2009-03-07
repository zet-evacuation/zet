/*
 * Flow.java
 *
 */

package ds.graph.flow;

import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;

/**
 *
 * @author Martin Groß
 */
public class Flow extends IdentifiableIntegerMapping<Edge> {

    public Flow(IdentifiableIntegerMapping<Edge> flow) {
        super(flow);
    }

    public Flow(Iterable<Edge> edges) {
        super(edges);
    }
}
