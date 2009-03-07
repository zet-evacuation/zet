/*
 * TimeHorizonBounds.java
 *
 */

package algo.graph.dynamicflow.eat;

/**
 *
 * @author Martin Groß
 */
public class TimeHorizonBounds {
    
    private int lowerBound;
    private int upperBound;

    public TimeHorizonBounds(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    @Override
    public String toString() {
        return "["+lowerBound+","+upperBound+"]";
    }    
}
