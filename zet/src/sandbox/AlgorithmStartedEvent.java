/*
 * AlgorithmStartedEvent.java
 *
 */

package sandbox;

/**
 *
 * @author Martin Groß
 */
public class AlgorithmStartedEvent extends AlgorithmEvent {

    public AlgorithmStartedEvent(Algorithm algorithm) {
        super(algorithm, algorithm.getStartTime(), algorithm.getStartTime());
    }
}
