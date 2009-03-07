/*
 * ProgressListener.java
 *
 */
package sandbox;

import java.util.EventListener;

/**
 *
 */
public interface ProgressListener extends EventListener {

    void progressChanged(AlgorithmProgressEvent event);
}
