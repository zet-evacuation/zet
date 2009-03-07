/**
 * Class OptionsChangedEvent
 * Erstellt 28.10.2008, 00:35:04
 */

package event;

/**
 * An event that is thrown if the program optons are changed, so that the
 * values can be reloaded. Especially used for the visualization.
 * @param <S> 
 * @author Jan-Philipp Kappmeier
 */
public class OptionsChangedEvent<S> implements Event {
	protected S source;

	public OptionsChangedEvent( S source ) {
		this.source = source;
	}

	public S getSource() {
		return source;
	}
}
