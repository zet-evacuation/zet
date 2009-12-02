/*
 * AlgorithmDetailedProgressEvent.java
 *
 */

package algo.graph;

/**
 * An algorithm event that is fired by the algorithm when progress occurs. It
 * contains detailed information about the current task.
 * @author Martin Groß
 */
public class AlgorithmDetailedProgressEvent extends AlgorithmProgressEvent {

    /**
     * A short description of the task currently performed by the algorithm.
     */
    private String taskName;

    private String progressInformation;

    private String detailedProgressInformation;

    public AlgorithmDetailedProgressEvent(Algorithm algorithm, double progress) {
        super(algorithm, progress);
    }

    public AlgorithmDetailedProgressEvent(Algorithm algorithm, long eventTime, double progress) {
        super(algorithm, eventTime, progress);
    }

    public AlgorithmDetailedProgressEvent(Algorithm algorithm, double progress, String message) {
        super(algorithm, progress);
    }

    /**
     * Returns a short description of the task currently performed by the
     * algorithm.
     * @return a short description of the task currently performed by the
     * algorithm.
     */
    public String getTaskName() {
        return taskName;
    }

}
