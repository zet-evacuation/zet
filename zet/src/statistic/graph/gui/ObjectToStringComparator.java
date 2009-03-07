/*
 * ObjectToStringComparator.java
 *
 */
package statistic.graph.gui;

import java.util.Comparator;

/**
 *
 * @author Martin Groß
 */
public class ObjectToStringComparator implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
        return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
    }
}
