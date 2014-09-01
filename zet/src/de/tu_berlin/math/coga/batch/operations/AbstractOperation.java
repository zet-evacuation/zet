
package de.tu_berlin.math.coga.batch.operations;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


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

  @Override
  public List<Class<?>> getProducts() {
    return Collections.singletonList( this.produces() );
  }

  @Override
  public Object getProduct( Class<?> productType ) {
    if( productType == this.produces() ) {
      return this.getProduced();
    }
    throw new AssertionError( "Not overridden for different types then default product!" );
  }
}
