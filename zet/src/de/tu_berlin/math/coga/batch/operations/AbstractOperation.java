/**
 * AbstractOperation.java
 * Created: 27.03.2014, 16:29:04
 */
package de.tu_berlin.math.coga.batch.operations;

import java.util.Collections;
import java.util.LinkedList;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AbstractOperation implements Operation {
	LinkedList<AtomicOperation<?,?>> operations = new LinkedList<>();

	void addOperation( AtomicOperation<?,?> operation ) {
		operations.add( operation );
	}

	@Override
	public Iterable<AtomicOperation<?, ?>> getAtomicOperations() {
		return Collections.unmodifiableCollection( operations );
	}
}
