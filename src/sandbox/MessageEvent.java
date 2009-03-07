/*
 * MessageEvent.java
 *
 */

package sandbox;

/**
 *
 * @author Martin Groß
 */
public class MessageEvent extends AlgorithmEvent {
    
    private String message;

    public MessageEvent(Algorithm algorithm, long startTime, long eventTime, String message) {
        super(algorithm, startTime, eventTime);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
