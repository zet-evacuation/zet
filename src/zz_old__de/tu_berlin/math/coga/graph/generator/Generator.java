/**
 * Generator.java
 * Created: 08.10.2010, 10:32:55
 */

package zz_old__de.tu_berlin.math.coga.graph.generator;

import org.zetool.graph.Graph;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Generator {

	public Graph getGraph();

	public int getEdgeCount();

	public int getNodeCount();

}
