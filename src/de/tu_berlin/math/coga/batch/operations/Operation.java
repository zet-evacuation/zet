/**
 * Operation.java
 * Created: 27.03.2014, 16:27:14
 */
package de.tu_berlin.math.coga.batch.operations;

import de.tu_berlin.math.coga.batch.algorithm.AlgorithmList;


/**
 * An operation defines a list of algorithms that are executed to convert one
 * input to an output via steps in between.
 * @author Jan-Philipp Kappmeier
 */
public interface Operation {

	public Iterable<AtomicOperation<?, ?>> getAtomicOperations();

}
