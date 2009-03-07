/*
 * AlgorithmProgressEvent.java
 *
 */
package sandbox;

/**
 *
 * @author Martin Groß
 */
public class AlgorithmProgressEvent extends AlgorithmEvent {

    private double progress;

    public AlgorithmProgressEvent(Algorithm algorithm, long startTime, long eventTime, double progress) {
        super(algorithm, startTime, eventTime);
        this.progress = progress;
    }

    public double getProgress() {
        return progress;
    }

    public int getProgressAsInteger() {
        return (int) Math.round(progress);
    }
}
