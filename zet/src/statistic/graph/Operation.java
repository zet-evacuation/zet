/*
 * Operation.java
 *
 */
package statistic.graph;

import java.util.List;

/**
 * 
 * @author Martin Groß
 */
public interface Operation<R> {

    R execute(List<R> objects, Object... parameters);

    boolean isComparing();
    
    Parameter[] parameters();
}
