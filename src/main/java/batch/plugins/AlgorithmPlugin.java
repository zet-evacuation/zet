package batch.plugins;

import net.xeoh.plugins.base.Plugin;
import org.zetool.components.batch.plugins.BatchAlgorithm;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @param <U>
 * @param <V>
 */
public interface AlgorithmPlugin<U,V> extends BatchAlgorithm<U, V>, Plugin {
  
}
