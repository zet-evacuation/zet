/*
 * ProfileSelectionChangedEvent.java
 *
 */
package statistic.graph.gui.event;

import statistic.graph.gui.DisplayProfile;

/**
 *
 * @author Martin Groß
 */
public class ProfileSelectionChangedEvent implements ProfileEvent {

    private DisplayProfile profile;

    public ProfileSelectionChangedEvent(DisplayProfile profile) {
        this.profile = profile;
    }

    public DisplayProfile getProfile() {
        return profile;
    }
}
