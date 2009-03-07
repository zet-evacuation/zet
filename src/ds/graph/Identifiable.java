/*
 * Identifiable.java
 *
 */

package ds.graph;

/**
 * The <code>Identifiable</code> interface defines what we mean by
 * saying that an object is identifiable: the object must be able
 * to return its ID, an integer value, that is used to identify the object.
 * The ID is used to store identifiable objects efficiently in arraybased
 * datastructures.
 */
public interface Identifiable extends Cloneable {
    
    /**
     * Returns an integer value, called ID, that can be used to identify the
     * corresponding object. The ID is usually used for equality checks and as a
     * simple, efficient hash function in conjuction with mappings 
     * ({@link IdentifiableObjectMapping}).
     * @return an integer value identifing the corresponding object.
     */
    int id();
    
    /**
     * Returns a clone of this <code>Identifiable</code> object.
     * @return a clone of this <code>Identifiable</code> object.
     */
    Identifiable clone();

}
