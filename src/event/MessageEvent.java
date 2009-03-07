/*
 * MessageEvent.java
 * Created on 19.12.2007, 02:09:37
 */
package event;

/**
 * This is a simple messaging event to submit status messages or error messages.
 * @param <S> 
 * @author Jan-Philipp Kappmeier
 */
public class MessageEvent<S> implements Event {
	public enum MessageType {
		Status,
		Error,
		MousePosition,
		EditMode;
	}
	protected S source;
	private String msg;
	private MessageType type;

	public MessageEvent( S source, MessageType type, String msg ) {
		this.source = source;
		this.msg = msg;
		this.type = type;
	}

	public String getMessage() {
		return msg;
	}

	public S getSource() {
		return source;
	}

	public MessageType getType() {
		return type;
	}
}
