/*
 * IdentifiableIntegerMapping.java
 *
 */

package ds.graph;

/**
 * The <code>IdentifiableIntegerMapping</code> class represents a mapping from a
 * set of identifiable objects to integers. It is a specialized version of 
 * <code>IdentifiableObjectMapping</code> made for mappings to integers. 
 * An array is used for
 * storing the mapping internally. The ID of an identifiable object determines 
 * the position in the array where the object's value is stored. This approach 
 * allows a very efficient implementation of mappings. It is recommended that 
 * the objects' IDs are from the set <code>{0,...,#objects-1}</code> to ensure
 * the best performance. For mappings of objects to arbitrary values see
 * {@link IdentifiableObjectMapping}.
 * @param D the type of this mapping's domain, i.e. the type of the objects that
 * are to be mapped to integers. <code>D</code> must implement 
 * {@link Identifiable}.
 */
public class IdentifiableIntegerMapping<D extends Identifiable> implements Cloneable {
    
    /**
     * The array storing all associations. Must not be <code>null</code>.
     */
    protected int[] mapping;

    public IdentifiableIntegerMapping(Iterable<D> domain) {
        int maxId = -1;
        for (D x : domain) {
            if (maxId < x.id()) maxId = x.id();
        }
        mapping = new int[maxId+1];
    }

    public IdentifiableIntegerMapping(IdentifiableIntegerMapping<D> iim) {
        mapping = new int[iim.mapping.length];
        for (int i=0; i<mapping.length; i++) {
            mapping[i] = iim.mapping[i];
        }        
    }
    
    /**
     * Constructs a new <code>IdentifiableIntegerMapping</code> object with a
     * specified initial mapping. The
     * default association for an object is as specified by 
     * <code>mapping</code>. Runtime O(1).
     * @param mapping the array defining the initial mapping.
     * @exception NullPointerException if <code>mapping</code> is null.
     */
    protected IdentifiableIntegerMapping(int[] mapping) {
        this.mapping = mapping;
    }
    
    /**
     * Constructs a new <code>IdentifiableObjectMapping</code> object with a
     * domain of the specified size. The default association for an object is 
     * <code>0</code>. Runtime O(domainSize).
     * @param domainSize the initial size of the domain.
     * @exception NegativeArraySizeException if <code>value</code> is negative.
     */
    public IdentifiableIntegerMapping(int domainSize) {
        mapping = new int[domainSize];
    }

    /**
     * Returns the integer associated with <code>identifiableObject</code> in 
     * this mapping. Runtime O(1).
     * @param identifiableObject the object for which the associated value is to
     * be returned.
     * @return the integer associated with <code>identifiableObject</code> in 
     * this mapping.
     * @exception ArrayIndexOutOfBoundsException if 
     * <code>identifiableObject</code>'s ID is less then 0 or greater equal than 
     * the size of the domain.
     * @exception NullPointerException if <code>identifiableObject</code> is 
     * null.
     * @see #getDomainSize
     * @see #setDomainSize
     * @see Identifiable
     */
    public int get(D identifiableObject) {
        return mapping[identifiableObject.id()];
    }

    /**
     * Associates <code>identifiableObject</code> with <code>value</code> in 
     * this mapping. Any previously made association for 
     * <code>identifiableObject</code> is lost in the process. Calling 
     * <code>set</code> with an <code>identifiableObject</code> whose ID is 
     * greater equal than the current size of the domain will automatically 
     * increase the size of the domain to accommodate 
     * <code>identifiableObject</code>'s ID. Runtime O(1) (O(ID) if the domain 
     * is expanded).
     * @param identifiableObject the object for which an association is to be 
     * made.
     * @param value the integer to be associated with 
     * <code>identifiableObject</code>.
     * @exception ArrayIndexOutOfBoundsException if 
     * <code>identifiableObject</code>'s ID is less then 0.
     * @exception NullPointerException if <code>identifiableObject</code> is 
     * null.
     * @see #getDomainSize
     * @see #setDomainSize
     * @see Identifiable
     */
    public void set(D identifiableObject, int value) {
    	if (identifiableObject == null)
    		throw new RuntimeException("IdentifiableObject contains null, value contains "+value+".");
        if (identifiableObject.id() >= getDomainSize()) {
            setDomainSize(identifiableObject.id() + 1);
        }
        mapping[identifiableObject.id()] = value;
    }

    /**
     * A convenience method equaling to <code>set(identifiableObject, 
     * get(identifiableObject) + amount)</code>, with the exception that the
     * domain is to automatically expanded to accommodate to large ID.
     * Runtime O(1).
     * @param identifiableObject the object for which the value is to be 
     * increased.
     * @param amount the amount by which the integer currently associated with 
     * <code>identifiableObject</code> is to be increased.
     * @exception ArrayIndexOutOfBoundsException if 
     * <code>identifiableObject</code>'s ID is less then 0 or greater equal than 
     * the size of the domain.
     * @exception NullPointerException if <code>identifiableObject</code> is 
     * null.
     * @see #getDomainSize
     * @see #setDomainSize
     * @see Identifiable
     */
    public void increase(D identifiableObject, int amount) {
        mapping[identifiableObject.id()] += amount;
    }

    /**
     * A convenience method equaling to <code>set(identifiableObject, 
     * get(identifiableObject) - amount)</code>, with the exception that the
     * domain is to automatically expanded to accommodate to large ID.
     * Runtime O(1).
     * @param identifiableObject the object for which the value is to be 
     * decreased.
     * @param amount the amount by which the integer currently associated with 
     * <code>identifiableObject</code> is to be decreased.
     * @exception ArrayIndexOutOfBoundsException if 
     * <code>identifiableObject</code>'s ID is less then 0 or greater equal than 
     * the size of the domain.
     * @exception NullPointerException if <code>identifiableObject</code> is 
     * null.
     * @see #getDomainSize
     * @see #setDomainSize
     * @see Identifiable
     */
    public void decrease(D identifiableObject, int amount) {
        mapping[identifiableObject.id()] -= amount;
    }
    
    /**
     * Returns the minimum over all values assigned to the specified set of
     * objects.
     * @return the minimum over all values assigned to the specified set of
     * objects.
     */
    public int minimum(Iterable<D> identifiableObjects) {
        int minimum = Integer.MAX_VALUE;
        for (D identifiableObject : identifiableObjects) {
            int value = get(identifiableObject);
            if (value < minimum) {
                minimum = value;
            }
        }
        return minimum;
    }
    
    /**
     * Returns the size of this mapping's domain. Associations of objects and 
     * integers can only be made for objects with an ID between <code>0</code> 
     * and <code>getDomainSize()-1</code>. Runtime O(1).
     * @return the size of this mapping's domain.
     */
    public int getDomainSize() {
        return mapping.length;
    }

    /**
     * Sets the size of this mapping's domain to <code>value</code>.
     * Runtime O(value).
     * @param value the new size of this mapping's domain.
     * @exception NegativeArraySizeException if <code>value</code> is negative.
     */
    public void setDomainSize(int value) {
        int[] newMapping = new int[value];
        System.arraycopy(
                mapping, 0,
                newMapping, 0,
                Math.min(mapping.length, newMapping.length));
        mapping = newMapping;
    }

    /**
     * Checks whether <code>identifiableObject</code> has been defined in this
     * mapping, i.e. whether its ID fits the size of the domain. Runtime O(1).
     * @param identifiableObject the object to check for whether it is defined
     * in this mapping.
     * @return true if <code>get(identifiableObject)</code> would return a
     * non-<code>null</code> value and false otherwise.
     * @exception NullPointerException if <code>identifiableObject</code> is 
     * null.
     */
    public boolean isDefinedFor(D identifiableObject) {
        return 0 <= identifiableObject.id()
                && identifiableObject.id() < getDomainSize();
    }
    
    /**
     * Creates a copy of this mapping. Runtime O(number of values).
     * @return a copy of this mapping.
     */
    @Override
    public IdentifiableIntegerMapping<D> clone() {
        int[] newMapping = new int[mapping.length];
        System.arraycopy(mapping, 0, newMapping, 0, mapping.length);
        return new IdentifiableIntegerMapping<D>(newMapping);
    }
    
    /**
     * Compares this mapping to the specified object. The result is true if and
     * only if the argument is not null and is an 
     * <code>IdentifiableIntegerMapping</code> object which has an domain of 
     * equal size and makes exactly the same object - integer
     * associations. Runtime O(size of the domain).
     * @param o the object this mapping is to be compared with.
     * @return <code>true</code> if the given object represents an 
     * <code>IdentifiableIntegerMapping</code> equivalent to this mapping, 
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof IdentifiableIntegerMapping)) {
            return false;
        }
        IdentifiableIntegerMapping iom = (IdentifiableIntegerMapping) o;
        if (iom.mapping.length != mapping.length) {
            return false;
        }
        for (int i = 0; i < mapping.length; i++) {
            if (iom.mapping[i] != mapping[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a hash code for this <code>IdentifiableIntegerMapping</code>.
     * Runtime O(size of the domain).
     * @return the sum of the integers associated with objects 
     * in this mapping.
     */
    @Override
    public int hashCode() {
        int sum = 0;
        for (int i = 0; i < mapping.length; i++) {
            sum += mapping[i];
        }
        return sum;
    }

    /**
     * Return a <code>String</code> object representing this mapping. The 
     * returned <code>String</code> will consist of a list of all object - 
     * integer associations made in this mapping. Runtime O(size of the domain).
     * @return a string representation of this mapping.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        int counter = 0;
        for (int i = 0; i < mapping.length; i++) {
            if (counter == 10) {
                counter = 0;
                builder.append("\n");
            }            
            if (mapping[i] != 0) {
                builder.append(i);
                builder.append(" = ");
                if (mapping[i] == Integer.MAX_VALUE) {
                    builder.append("MAX");
                } else {
                    builder.append(mapping[i]);
                }
                builder.append(", ");
                counter++;
            }            
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(']');
        return builder.toString();
    }
}
