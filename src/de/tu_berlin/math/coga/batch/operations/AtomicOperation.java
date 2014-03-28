/**
 * AtomicOperation.java
 * Created: 27.03.2014, 16:34:10
 */
package de.tu_berlin.math.coga.batch.operations;

import batch.plugins.AlgorithmicPlugin;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import gui.ZETMain;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import net.xeoh.plugins.base.util.PluginManagerUtil;


/**
 * An atomic operation transforms an input to an output and uses an algorithm
 * for that. The algorithm is undefined in the beginning and can be set.
 * @author Jan-Philipp Kappmeier
 */
public class AtomicOperation<U,V> implements Iterable<AlgorithmicPlugin<U,V>> {
	private Class<U> accepts;
	private Class<V> generates;
	private LinkedList<AlgorithmicPlugin<U,V>> availableAlgorithms = new LinkedList<>();
	int index = -1;

	public AtomicOperation( String name, Class<U> accepts, Class<V> generates ) {
		this.name = name;
		this.accepts = accepts;
		this.generates = generates;

		checkForPlugins();
	}

	@SuppressWarnings( "unchecked" )
	private void checkForPlugins() {
		PluginManagerUtil pmu = new PluginManagerUtil( ZETMain.pm );
		Collection<AlgorithmicPlugin> plugins = pmu.getPlugins( AlgorithmicPlugin.class );

		for( AlgorithmicPlugin<?,?> plugin : plugins ) {
			if( servedBy( plugin ) ) {
				System.out.println( plugin );
				availableAlgorithms.add( (AlgorithmicPlugin<U,V>)plugin);
			}
		}
	}

	private boolean servedBy( AlgorithmicPlugin<?,?> plugin ) {
		if( plugin.canTake( accepts() ) ) {
			System.out.println( "The plugin can take the input from the atomic operation" );
			if( plugin.canGenerate( generates() ) ) {
				System.out.println( "Plugin " + plugin + " accepts " + toString() );
				return true;
			} else {
				System.out.println( "The plugin cannot generate the output!" );
			}
		} else {
			System.out.println( "The plugin cannot take the input!" );
		}
		return false;
	}

	public void setIndex( int index ) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public Iterator<AlgorithmicPlugin<U,V>> iterator() {
		return availableAlgorithms.iterator();
	}

	public void addAvailable( AlgorithmicPlugin<U,V> plugin ) {
		availableAlgorithms.add( plugin );
	}

	private String name;

	private Algorithm<U,V> selectedAlgorithm;
	private U instance;
	private V solution;

	public Algorithm<U, V> getSelectedAlgorithm() {
		return selectedAlgorithm;
	}

	public void setSelectedAlgorithm( Algorithm<U, V> selectedAlgorithm ) {
		this.selectedAlgorithm = selectedAlgorithm;
	}

	public void setInstance( U instance ) {
		this.instance = instance;
	}

	public V getSolution() {
		return solution;
	}

	public V run() {
		selectedAlgorithm.setProblem( instance );
		selectedAlgorithm.run();
		return selectedAlgorithm.getSolution();
	}

	public Class<U> accepts() {
		return accepts;
	}

	public Class<V> generates() {
		return generates;
	}



	@Override
	public String toString() {
		return name;
	}

	public int indexOf( AlgorithmicPlugin<?, ?> selectedPlugin ) {
		return availableAlgorithms.indexOf( selectedPlugin );
	}

}
