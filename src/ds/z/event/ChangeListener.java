/*
 * ChangeListener.java
 *
 * Created on 3. Dezember 2007, 15:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ds.z.event;

/** An object that processes ChangeEvents.
 *
 * @author Timon Kelter
 */
public interface ChangeListener {
	/** In this method the event must be processed by the implementing class.
	 * It will be called by any ChangeReporter at which the ChangeListener is 
	 * registered. */
	void stateChanged (ChangeEvent e);
}
