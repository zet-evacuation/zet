/**
 * Log.java
 * Created 03.03.2010, 15:28:52
 */

package de.tu_berlin.math.coga.common.debug;

import event.EventListener;
import event.EventServer;
import event.MessageEvent;
import java.util.ArrayList;

/**
 * A list of log entries. Entries can have two types (error and normal messages)
 * and are stored in two ways: in an array and as a string. The error messages
 * have additional html-tags to format them for logging out.
 * @author Jan-Philipp Kappmeier
 */
public class Log implements EventListener<MessageEvent> {
	/** A list of all logged massages. */
	ArrayList<String> strings = new ArrayList<String>();
	/** The complete log as string. */
	String text = "";
	// todo: store texts only in array

	/**
	 * Creates a new instance and registeres at the event server.
	 */
	public Log() {
		EventServer.getInstance().registerListener( this, MessageEvent.class );
	}

	/**
	 * Clears all logged data.
	 */
	public void clear() {
		text = "";
		strings.clear();
	}

	/**
	 * Returns the current log text.
	 * @return the current log text
	 */
	public String getText() {
		return text;
	}

	/**
	 * <p>Handles incoming events. An event comes, if some debugger/logger sends
	 * messages of type {@link event.MessageEvent.MessageType}. Only messages of
	 * the types {@link event.MessageEvent.MessageType#Log} and
	 * {@link event.MessageEvent.MessageType#LogError} are handled.</p>
	 * <p>{@code Log} events are displayed in normal font style while
	 * {@code LogError} events are displayed red.</p>
	 * @param event the event that occured.
	 */
	public void handleEvent( MessageEvent event ) {
		String s = "";
		switch( event.getType() ) {
			case Log:
				s = event.getMessage() + "<br>";
				//s = event.getMessage() + '\n';
				break;
			case LogError:
				s = "<font color=\"red\">" + event.getMessage() + "</font><br>";
				//s = event.getMessage() + '\n';
				break;
		}
		strings.add( s );
		text += s;
	}
}
