/*
 * EventListener.java
 *
 */

package event;

/**
 *
 * @param <T> 
 */
public interface EventListener<T extends Event> {
    
    void handleEvent(T event);

}
