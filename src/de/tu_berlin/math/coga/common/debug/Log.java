/**
 * Log.java
 * Created 03.03.2010, 15:28:52
 */

package de.tu_berlin.math.coga.common.debug;

import event.EventListener;
import event.EventServer;
import event.MessageEvent;
import java.util.ArrayList;
import java.util.Observable;

/**
 * A list of log entries. Entries can have two types (error and normal messages)
 * and are stored in two ways: in an array and as a string. The error messages
 * have additional html-tags to format them for logging out.
 * @author Jan-Philipp Kappmeier
 */
public class Log extends Observable implements EventListener<MessageEvent> {
	/** A list of all logged massages. */
	ArrayList<String> strings = new ArrayList<>();
	/** The complete log as string. */
	StringBuilder text = new StringBuilder();
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
		text = new StringBuilder();
		strings.clear();
	}

	/**
	 * Returns the current log text.
	 * @return the current log text
	 */
	public String getText() {
		return text.toString();
	}

	/**
	 * <p>Handles incoming events. An event comes, if some debugger/logger sends
	 * messages of type {@link event.MessageEvent.MessageType}. Only messages of
	 * the types {@link event.MessageEvent.MessageType#Log} and
	 * {@link event.MessageEvent.MessageType#LogError} are handled.</p>
	 * <p>{@code Log} events are displayed in normal font style while
	 * {@code LogError} events are displayed red.</p>
	 * @param event the event that occurred.
	 */
	public void handleEvent( MessageEvent event ) {
		String s = "";
		switch( event.getType() ) {
			case Log:
				s = event.getMessage();// + "<br>";
				s = s.replace( "<", "&lt;" );
				s = s.replace( ">", "&gt;" ) + "<br>";
				break;
			case LogError:
				s = event.getMessage();
				s = s.replace( "<", "&lt;" );
				s = s.replace( ">", "&gt;" );
				s = "<font color=\"red\">" + s + "</font><br>";
				//s = event.getMessage() + '\n';
				break;
		}
		s = s.replace( "\n", "<br>" );
		
		strings.add( s );
		text.append( s );
    super.setChanged(); // Markierung, daß sich der Text geändert hat
    super.notifyObservers(text); // ruft für alle Beobachter die update-Methode auf
	}
}
