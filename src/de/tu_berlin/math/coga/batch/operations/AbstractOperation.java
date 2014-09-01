
package de.tu_berlin.math.coga.batch.operations;

import java.util.Collections;
import java.util.LinkedList;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractOperation<Consume,Produce> implements Operation<Consume,Produce> {
	LinkedList<AtomicOperation<?,?>> operations = new LinkedList<>();

	void addOperation( AtomicOperation<?,?> operation ) {
		operations.add( operation );
	}

	@Override
	public Iterable<AtomicOperation<?, ?>> getAtomicOperations() {
		return Collections.unmodifiableCollection( operations );
	}
 }
