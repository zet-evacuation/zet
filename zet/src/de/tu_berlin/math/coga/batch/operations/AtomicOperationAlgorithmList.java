/**
 * AtomicOperationAlgorithmList.java
 * Created: 28.03.2014, 16:28:28
 */
package de.tu_berlin.math.coga.batch.operations;

import java.util.LinkedList;
import net.xeoh.plugins.base.Plugin;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AtomicOperationAlgorithmList {
	AtomicOperation atomic;
	LinkedList<Plugin> available = new LinkedList<>();
	int index = -1;

	public AtomicOperationAlgorithmList( AtomicOperation atomic ) {
		this.atomic = atomic;
	}


}
