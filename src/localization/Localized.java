/**
 * Interface Localized
 * Erstellt 30.04.2008, 09:03:00
 */

package localization;

/**
 * Classes that support localization can implement this interface. The
 * {@link localize()} method is called if the language is changed.
 * @author Jan-Philipp Kappmeier
 */
public interface Localized {
	public void localize();
}
