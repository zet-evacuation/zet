/*
 * ProfileEvent.java
 *
 */
package statistic.graph.gui.event;

import statistic.graph.gui.DisplayProfile;

/**
 *
 * @author Martin Groß
 */
public interface ProfileEvent extends Event {

    DisplayProfile getProfile();
}
