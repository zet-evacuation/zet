/*
 * AlgorithmEvent.java
 *
 */
package sandbox;

/**
 *
 * @author Martin Groß
 */
public abstract class AlgorithmEvent {

    private Algorithm algorithm;
    private long eventTime;
    private long startTime;

    protected AlgorithmEvent(Algorithm algorithm, long startTime, long eventTime) {
        this.algorithm = algorithm;
        this.startTime = startTime;
        this.eventTime = eventTime;
    }

    public final Algorithm getAlgorithm() {
        return algorithm;
    }

    public final long getEventTime() {
        return eventTime;
    }

    public final long getStartTime() {
        return startTime;
    }
}
