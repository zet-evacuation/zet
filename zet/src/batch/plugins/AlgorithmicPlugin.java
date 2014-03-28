/**
 * OptimizationPlugin.java
 * Created: 27.03.2014, 14:59:10
 */
package batch.plugins;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import net.xeoh.plugins.base.Plugin;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface AlgorithmicPlugin<U,V> extends Plugin {
	public String getName();

	public Class<U> accepts();


	public Class<V> generates();

	public default boolean canTake( Class<?> type ) {
		return type == accepts();
	}

	public default boolean canGenerate( Class<?> type ) {
		return type == generates();
	}

	/**
	 * Factory method generating an actual algorithm of the plugins type.
	 * @return an {@link Algorithm} instance
	 */
	public Algorithm<U,V> getAlgorithm();
}
