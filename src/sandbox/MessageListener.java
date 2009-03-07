/*
 * MessageListener.java
 *
 */
package sandbox;

import java.util.EventListener;

/**
 *
 */
public interface MessageListener extends EventListener {

    void messageChanged(MessageEvent e);
}
