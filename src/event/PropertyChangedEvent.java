/**
 * Class PropertyChangedEvent
 * Erstellt 19.06.2008, 10:16:20
 */

package event;

/**
 *
 * @param <S> 
 * @author Jan-Philipp Kappmeier
 */
public class PropertyChangedEvent<S> implements Event {
	public enum MessageType {
		/** Indicates that the options in the Options Dialog were changed. */
		Options,
		/** Indicates that the options in the Properties Dialog were changed. */
		Properties;
	}
	protected S source;
	private String msg;
	private MessageType type;

	public PropertyChangedEvent( S source, MessageType type, String msg ) {
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
