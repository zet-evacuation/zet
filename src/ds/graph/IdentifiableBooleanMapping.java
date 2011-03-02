/**
 * IdentifiableBooleanMapping.java
 * Created: 25.02.2011, 18:44:13
 */
package ds.graph;

import java.util.Arrays;


/**
 *
 * @param <D>
 * @author Jan-Philipp Kappmeier
 */
public class IdentifiableBooleanMapping<D extends Identifiable> implements Cloneable {
	/** The array storing all associations. Must not be {@code null}. */
	protected boolean[] mapping;

	protected IdentifiableBooleanMapping() {
	}

	public IdentifiableBooleanMapping( Iterable<D> domain ) {
		int maxId = -1;
		for( D x : domain )
			if( maxId < x.id() )
				maxId = x.id();
		mapping = new boolean[maxId + 1];
	}

	public IdentifiableBooleanMapping( IdentifiableBooleanMapping<D> iim ) {
		mapping = new boolean[iim.getDomainSize()];
		System.arraycopy( iim.mapping, 0, mapping, 0, mapping.length );
	}

	/**
	 * Constructs a new {@code IdentifiableBooleanMapping} object with a
	 * specified initial mapping. The
	 * default association for an object is as specified by
	 * {@code mapping}. Runtime O(1).
	 * @param mapping the array defining the initial mapping.
	 * @throws NullPointerException if {@code mapping} is null.
	 */
	protected IdentifiableBooleanMapping( boolean[] mapping ) {
		this.mapping = mapping;
	}

	/**
	 * Constructs a new {@code IdentifiableObjectMapping} object with a
	 * domain of the specified size. The default association for an object is
	 * {@code 0}. Runtime O(domainSize).
	 * @param domainSize the initial size of the domain.
	 * @throws NegativeArraySizeException if {@code value} is negative.
	 */
	public IdentifiableBooleanMapping( int domainSize ) {
		mapping = new boolean[domainSize];
	}

	/**
	 * Returns the integer associated with {@code identifiableObject} in
	 * this mapping. Runtime O(1).
	 * @param identifiableObject the object for which the associated value is to
	 * be returned.
	 * @return the integer associated with {@code identifiableObject} in
	 * this mapping.
	 * @throws ArrayIndexOutOfBoundsException if
	 * {@code identifiableObject}'s ID is less then 0 or greater equal than
	 * the size of the domain.
	 * @throws NullPointerException if {@code identifiableObject} is null.
	 * @see #getDomainSize
	 * @see #setDomainSize
	 * @see Identifiable
	 */
	public boolean get( D identifiableObject ) {
		return mapping[identifiableObject.id()];
	}

	/**
	 * Associates {@code identifiableObject} with {@code value} in
	 * this mapping. Any previously made association for
	 * {@code identifiableObject} is lost in the process. Calling
	 * {@code set} with an {@code identifiableObject} whose ID is
	 * greater equal than the current size of the domain will automatically
	 * increase the size of the domain to accommodate
	 * {@code identifiableObject}'s ID. Runtime O(1) (O(ID) if the domain
	 * is expanded).
	 * @param identifiableObject the object for which an association is to be
	 * made.
	 * @param value the integer to be associated with
	 * {@code identifiableObject}.
	 * @throws ArrayIndexOutOfBoundsException if
	 * {@code identifiableObject}'s ID is less then 0.
	 * @throws NullPointerException if {@code identifiableObject} is null.
	 * @see #getDomainSize
	 * @see #setDomainSize
	 * @see Identifiable
	 */
	public void set( D identifiableObject, boolean value ) {
		if( identifiableObject == null )
			throw new RuntimeException( "IdentifiableObject contains null, value contains " + value + "." );
		if( identifiableObject.id() >= getDomainSize() )
			setDomainSize( identifiableObject.id() + 1 );
		mapping[identifiableObject.id()] = value;
	}

	/**
	 * A convenience method equaling to {@code set(identifiableObject,
	 * get(identifiableObject) + amount)}, with the exception that the
	 * domain is to automatically expanded to accommodate to large ID.
	 * Runtime O(1).
	 * @param identifiableObject the object for which the value is to be
	 * increased.
	 * @throws ArrayIndexOutOfBoundsException if
	 * {@code identifiableObject}'s ID is less then 0 or greater equal than
	 * the size of the domain.
	 * @throws NullPointerException if {@code identifiableObject} is null.
	 * @see #getDomainSize
	 * @see #setDomainSize
	 * @see Identifiable
	 */
	public void toggle( D identifiableObject ) {
		if( identifiableObject == null )
			throw new RuntimeException( "IdentifiableObject contains null." );
		if( identifiableObject.id() >= getDomainSize() )
			setDomainSize( identifiableObject.id() + 1 );
		mapping[identifiableObject.id()] = !mapping[identifiableObject.id()];
	}

	/**
	 * Associates {@code identifiableObject} with {@code value} in
	 * this mapping. Any previously made association for
	 * {@code identifiableObject} is lost in the process. Calling
	 * {@code add} with an {@code identifiableObject} whose ID is
	 * greater equal than the current size of the domain will automatically
	 * increase the size of the domain to accommodate
	 * {@code identifiableObject}'s ID, at least the capacity is doubled.
	 * Runtime O(1) (O(min{ID, 2*oldDomainSize}) if the domain is expanded).
	 * @param identifiableObject the object for which an association is to be
	 * made.
	 * @param value the integer to be associated with
	 * {@code identifiableObject}.
	 * @throws ArrayIndexOutOfBoundsException if
	 * {@code identifiableObject}'s ID is less then 0.
	 * @throws NullPointerException if {@code identifiableObject} is null.
	 * @see #getDomainSize
	 * @see #setDomainSize
	 * @see Identifiable
	 */
	public void add( D identifiableObject, boolean value ) {
		if( identifiableObject == null )
			throw new RuntimeException( "IdentifiableObject contains null, value contains " + value + "." );
		if( identifiableObject.id() >= getDomainSize() )
			setDomainSize( Math.min( identifiableObject.id() + 1, getDomainSize() * 2 ) );
		mapping[identifiableObject.id()] = value;
	}

	public void initializeWith( boolean value ) {
		Arrays.fill( mapping, value );
	}

	/**
	 * Returns the size of this mapping's domain. Associations of objects and
	 * integers can only be made for objects with an ID between {@code 0}
	 * and {@code getDomainSize()-1}. Runtime O(1).
	 * @return the size of this mapping's domain.
	 */
	public int getDomainSize() {
		return mapping.length;
	}

	/**
	 * Sets the size of this mapping's domain to {@code value}.
	 * Runtime O(value).
	 * @param value the new size of this mapping's domain.
	 * @throws NegativeArraySizeException if {@code value} is negative.
	 */
	public void setDomainSize( int value ) {
		boolean[] newMapping = new boolean[value];
		System.arraycopy( mapping, 0, newMapping, 0, Math.min( mapping.length, newMapping.length ) );
		mapping = newMapping;
	}

	/**
	 * Checks whether {@code identifiableObject} has been defined in this
	 * mapping, i.e. whether its ID fits the size of the domain. Runtime O(1).
	 * @param identifiableObject the object to check for whether it is defined
	 * in this mapping.
	 * @return true if {@code get(identifiableObject)} would return a non-{@code null} value and false otherwise.
	 * @throws NullPointerException if {@code identifiableObject} is {@code null}.
	 */
	public boolean isDefinedFor( D identifiableObject ) {
		return 0 <= identifiableObject.id() && identifiableObject.id() < getDomainSize();
	}

	/**
	 * Creates a copy of this mapping. Runtime O(number of values).
	 * @return a copy of this mapping.
	 */
	@Override
	public IdentifiableBooleanMapping<D> clone() {
		boolean[] newMapping = new boolean[mapping.length];
		System.arraycopy( mapping, 0, newMapping, 0, mapping.length );
		return new IdentifiableBooleanMapping<D>( newMapping );
	}

	/**
	 * Compares this mapping to the specified object. The result is true if and
	 * only if the argument is not null and is an
	 * {@code IdentifiableBooleanMapping} object which has an domain of
	 * equal size and makes exactly the same object - integer
	 * associations. Runtime O(size of the domain).
	 * @param o the object this mapping is to be compared with.
	 * @return {@code true} if the given object represents an
	 * {@code IdentifiableBooleanMapping} equivalent to this mapping,
	 * {@code false} otherwise.
	 */
	@Override
	public boolean equals( Object o ) {
		if( o == null || !(o instanceof IdentifiableBooleanMapping) )
			return false;
		IdentifiableBooleanMapping iom = (IdentifiableBooleanMapping) o;
		if( iom.mapping.length != mapping.length )
			return false;
		for( int i = 0; i < mapping.length; i++ )
			if( iom.mapping[i] != mapping[i] )
				return false;
		return true;
	}

	/**
	 * Returns a hash code for this {@code IdentifiableBooleanMapping}.
	 * Runtime O(size of the domain).
	 * @return the sum of the integers associated with objects in this mapping.
	 */
	@Override
	public int hashCode() {
		int sum = 0;
		for( int i = 0; i < mapping.length; i++ ) {
			sum += i;
		}
		return sum;
	}

	/**
	 * Return a {@code String} object representing this mapping. The
	 * returned {@code String} will consist of a list of all object -
	 * integer associations made in this mapping. Runtime O(size of the domain).
	 * @return a string representation of this mapping.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( '[' );
		int counter = 0;
		for( int i = 0; i < mapping.length; i++ ) {
			if( counter == 10 ) {
				counter = 0;
				builder.append( "\n" );
			}
//            if (mapping[i] != 0) {
			builder.append( i );
			builder.append( " = " );
			builder.append( mapping[i] );
			builder.append( ", " );
			counter++;
//            }
		}
		if( builder.length() > 2 )
			builder.delete( builder.length() - 2, builder.length() );
		builder.append( ']' );
		return builder.toString();
	}
}
