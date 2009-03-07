/*
 * AlgorithmStoppedEvent.java
 *
 */

package sandbox;

/**
 *
 * @author Martin Groß
 */
public class AlgorithmStoppedEvent extends AlgorithmEvent {

    public AlgorithmStoppedEvent(Algorithm algorithm) {
        super(algorithm, algorithm.getStartTime(), algorithm.getRuntime());
    }
}
