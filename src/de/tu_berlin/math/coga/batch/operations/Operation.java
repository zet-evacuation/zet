
package de.tu_berlin.math.coga.batch.operations;

import de.tu_berlin.math.coga.batch.input.reader.InputFileReader;


/**
 * An operation defines a list of algorithms that are executed to convert one
 * input to an output via steps in between.
 * @author Jan-Philipp Kappmeier
 */
public interface Operation extends Runnable {

	public Iterable<AtomicOperation<?, ?>> getAtomicOperations();

	/**
	 * Returns true, if the object can be consumed. The object is stored as
	 * input until consume is called again.
	 * @param o
	 * @return
	 */
	public boolean consume( InputFileReader<?> o );

}
