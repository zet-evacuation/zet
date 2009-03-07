/*
 * ChangeReporter.java
 * Created on 3. Dezember 2007, 15:02
 */
package ds.z.event;

/**
 * An interface that defines the methods necessary for classes who intend to
 * report changes that are imposed on them to some Listeners. Those must previously
 * have been registered at the <code>ChangeReporter</code>.
 *
 * @author Timon Kelter
 */
public interface ChangeReporter {
	/** Calls <code>stateChanged(e)</code> at all registered listeners and sends
	 * a specified event.
	 * @param e the event
	 */
	void throwChangeEvent( ChangeEvent e );

	/**
	 * Adds a {@link ChangeListener} to this reporter. The Listener is only
	 * added, if he hasn't been registered previously as no Listener sall have the
	 * possibility to register twice at the same <code>ChangeReporter</change>.
	 * @param c the added listener
	 */
	void addChangeListener( ChangeListener c );

	/**
	 * Removes a specified {@link ChangeListener} (de-registers).
	 * @param c the change listener
	 */
	void removeChangeListener( ChangeListener c );
}
