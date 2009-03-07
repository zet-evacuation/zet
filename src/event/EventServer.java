/*
 * EventServer.java
 *
 */

package event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class EventServer {
    
    private static EventServer instance;

    public static EventServer getInstance() {
        if (instance == null) {
            instance = new EventServer();
        }
        return instance;
    }
    
    protected Map<Class<? extends Event>, List<EventListener>> listeners;
    
    private EventServer() {
        listeners = new HashMap<Class<? extends Event>, List<EventListener>>();
    }
    
    public <T extends Event> void registerListener(EventListener<? super T> listener, Class<T> eventType) {
        if (!listeners.containsKey(eventType)) {
            listeners.put(eventType, new LinkedList<EventListener>());
        }
        if (!listeners.get(eventType).contains(listener)) {
            listeners.get(eventType).add(listener);
        }
    }
    
    public <T extends Event> void unregisterListener(EventListener<? super T> listener, Class<T> eventType) {        
        if (!listeners.containsKey(eventType)) {
            return;
        }
        if (listeners.get(eventType).contains(listener)) {
            listeners.get(eventType).remove(listener);
        }        
    }
    
    public void dispatchEvent(Event e) {
        Class<?> eventType = e.getClass();
        Map<EventListener, Boolean> notified = new HashMap<EventListener, Boolean>();
        do {            
            notifyListeners(e, eventType, notified);
            for (Class<?> cl : eventType.getInterfaces()) {
                notifyListeners(e, cl, notified);
            }
            eventType = eventType.getSuperclass();
        } while (eventType != null);
    }
    
    protected void notifyListeners(Event e, Class<?> eventType, Map<EventListener, Boolean> notified) {
        if (listeners.containsKey(eventType)) {
            for (EventListener listener : listeners.get(eventType)) {
                if (notified.containsKey(listener) && notified.get(listener)) continue;
                listener.handleEvent(e);
                notified.put(listener, Boolean.TRUE);
            }
        }        
    }

}
