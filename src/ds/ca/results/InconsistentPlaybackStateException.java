/*
 * Created on 01.05.2008
 *
 */
package ds.ca.results;

/**
 * @author Daniel Pluempe
 *
 */
public class InconsistentPlaybackStateException extends Exception {

    public InconsistentPlaybackStateException(){
        this("The recorded state is inconsistent.");
    }
    
    public InconsistentPlaybackStateException(String message){
        super(message);
    }
    
    public InconsistentPlaybackStateException(int timestamp, Action action, String message){
        super("There was an error replaying the following action in timestep " 
                + timestamp +
                ": " 
                + action + ": " + message);
    }
}
