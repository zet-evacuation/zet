/*
 * ArraySet.java
 * 
 */

package ds.graph;

import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * The <code>ArraySet</code> class represents a set of
 * <code>Identifiable</code> objects. They are internally stored in an
 * array by their IDs. Therefore they are ordered by the IDs.
 * The class implements the interface <code>IdentifiableCollection</code> 
 * and thus provides all specified methods. Especialle the methods that
 * can be implemented using the IDs are very effient as storing an element
 * or looking for an element with a specified ID.
 */
public class ArraySet<E extends Identifiable> implements IdentifiableCollection<E> {
    
    /**
     * The concret type replacing the generics.
     * Is needed because Java does not support generic arrays.
     */
    private Class<? extends Identifiable> elementType;
    
    /**
     * The intern array to store the elements by their ID.
     */
    private E[] elements;
    
    /**
     * A variable to store the current size of the datastructure,
     * where size means the number of stored elements.
     */
    private int size;
   
    /**
     * Constructs an <code>ArraySet</code> containing the elements in the 
     * given array. The elements must be stored in the field corrisponding
     * to their ID, elsewise an <code>IllegalArgumentException</code> 
     * is thrown.
     * @param elements an array with elements that shall be contained in this
     *        <code>ArraySet</code>.
     */
    public ArraySet(E[] elements) {
        this.elementType = elements[0].getClass();
        this.elements = elements;
        for (int i = 0; i < elements.length; i++){
            E e = elements[i];
            if (e == null || e.id() != i)
                throw new IllegalArgumentException();
        }
        size =  elements.length;
    }    
    
    /**
     * Constructs an <code>ArraySet</code>, 
     * typed to <code>elementType</code>, but containing no elements and
     * with zero capacity.
     * The capacity must be set by <code>public void setCapacity(int capacity)</code>
     * before storing elements in the <code>ArraySet</code>.
     * @param elementType the type the elements in this <code>ArraySet</code>
     *                    will have.
     */
    public ArraySet(Class<E> elementType) {
        this(elementType, 0);
    }    
        
    /**
     * Constructs an <code>ArraySet</code> containing no elemens, but typed
     * to <code>elementType</code> and with a capacity to store elements
     * with IDs from zero to <code>capacity-1</code>.
     * @param elementType the type the elements in this <code>ArraySet</code>
     *                    will have.
     * @param capacity the highest possible ID for elements plus one.
     */
    @SuppressWarnings("unchecked")
    public ArraySet(Class<E> elementType, int capacity) {
        this.elementType = elementType;
        this.elements = (E[]) Array.newInstance(elementType,capacity);    
    }    

    /**
     * Adds an element to the <code>ArraySet</code> and returns
     * whether the insertion was successful. The insertion fails
     * if the ID of the element is negative outside the range of this
     * <code>ArraySet</code>.
     * Elsewise the element will be stored at the appropriate array position.
     * @param element element to be add.
     */
    public boolean add(E element) {
        if (element.id() < 0 || element.id() >= this.getCapacity())
            return false;
        if (elements[element.id()] == null) size++;
        elements[element.id()] = element;        
        return true;
    }
    
    /**
     * Removes the element from the <code>ArraySet</code> having the
     * same ID as the element <code>element</code>.  
     * If there is no such element in the <code>ArraySet</code>,
     * nothing happens.
     * Due to the array based implementation this operation is efficient.
     * Runtime O(1).
     * @param element element element to be removed.
     */
    public void remove(E element) {
        if (element.id() >= 0 && element.id() <= elements.length -1) {
            if(elements[element.id()] != null)
                size--;
            elements[element.id()] = null;
        }
    }
    
    /**
     * Removes and returns the last element of this <code>ListSequence</code>.
     * The last element is the element with the highest ID.
     * If the <code>ArraySet</ArraySet> is empty, nothing happens.
     * @return the last element of this <code>ArraySet</code>.
     */
    public E removeLast(){
        E e = last();
        if (e == null)
            return null;
        else {
            remove(e);
            return e;
        }            
    }
    
    /**
     * Returns whether the element is contained in this <code>ArraySet</code>.
     * The test checks for containedness of the specified element (not for
     * containedness of an element having the same ID).
     * The test is efficent because of the array based implementation. 
     * Runtime O(1).
     * @param element the element that shall be checked for containedness.
     * @return whether the element <code>element</code> contained in this 
     *         <code>ArraySet</code>.
     */
    public boolean contains(E element) {
        if (element.id() < 0 || element.id() >= elements.length)
            return false;
        return elements[element.id()] == element;
    }

    /**
     * Returns whether this <code>ArraySet</code> is empty. Runtime O(1).
     * @return whether this <code>ArraySet</code> is empty.
     */    
    public boolean empty() {
        return size() == 0;
    }

    /**
     * Returns the size of this <code>ArraySet</code>. 
     * The size means the number of stored elements, not the capacity of the 
     * internal array.
     * Runtime O(1).
     * @return the size of this <code>ArraySet</code>.
     */
    public int size() {
        return size;
    }

    /**
     * Returns the element with the ID <code>id</code> that is stored in this 
     * <code>ArraySet</code> or null if no element with this ID is stored.
     * An <code>ArraySet</code> is especially a set, i.e. the returned
     * element is uniquely defined.
     * The test is efficent because of the array based implementation. 
     * Runtime O(1).
     * @param id the ID that shall be checked
     * @return the element with the ID <code>id</code> that is stored in this 
     * <code>ArraySet</code>, <code>null</code> if no element with this
     * ID is stored.
     */
    public E get(int id) {
        return elements[id];
    }    
    
    
    /**
     * Returns the first element stored in this <code>ArraySet</code>.
     * The order in <code>ArraySet</code> depends on the IDs, 
     * thus the first element is the element with the smallest ID.
     * If the <code>ArraySet</code> is empty, <code>null</code> is returned.
     * Runtime O(ID_first), where ID_first is the ID of the first element.
     * @return the first element stored in this <code>ArraySet</code>,
     *         null if no element is stored.
     */
    public E first() {
        int index = 0;
        while (index < elements.length && elements[index] == null) {
            index++;
        }
        if (index < elements.length) {
            return elements[index];
        } else {
            return null;
        }
    }
     
    /**
     * Returns the last element stored in this <code>ArraySet</code>.
     * The order in <code>ArraySet</code> depends on the IDs, 
     * thus the last element is the element with the highest ID.
     * If the <code>ArraySet</code> is empty, <code>null</code> is returned.
     * Runtime O(ID_last), where ID_first is the ID of the last element.
     * @return the last element stored in this <code>ArraySet</code>,
     *         null if no element is stored.
     */
    public E last() {
        int index = elements.length-1;
        while (index > -1 && elements[index] == null ) {
            index--;
        }
        if (index > -1) {
            return elements[index];
        } else {
            return null;
        }
    }

    /**
     * Returns the predecessor of the element <code>element</code>.
     * Returns null if the  <code>element</code> is the first in the 
     * <code>ArraySet</code> or if it is not stored in the 
     * <code>ArraySet</code>. 
     * The order in <code>ArraySet</code> depends on the IDs, 
     * thus the predecessor of the element <code>element</code> is the element
     * with the highest ID smaller than the ID of <code>element</code>. 
     * Runtime O(ID_element) where ID_element is the ID of <code>element</code>.
     * @param element the element which predecessor is wanted
     * @return the predecessor of <code>element<\code> or null if the element 
     * is the first in the <code>ArraySet</code> or is not contained 
     * in the <code>ArraySet</code>.
     */
    public E predecessor(E element) {
        if (contains(element)) {
            int index = element.id()-1;
            while (index > -1 && elements[index] == null) {
                index--;
            }
            if (index > -1) {
                return elements[index];
            } else {
                return null;
            }            
        } else {
            return null;
        }
    }
    
    /**
     * Returns the successor of the element <code>element</code>.
     * Returns null if the  <code>element</code> is the last in the 
     * <code>ArraySet</code> or if it is not stored in the 
     * <code>ArraySet</code>. 
     * The order in <code>ArraySet</code> depends on the IDs, 
     * thus the successor of the element <code>element</code> ist the element
     * with the smallest ID higher than the ID of <code>element</code>. 
     * Runtime O(n-ID_element) where ID_element is the ID of <code>element</code>
     * and n is the number of possible IDs.
     * @param element the element which successor is wanted
     * @return the successor of <code>element<\code> or null if the element 
     * is the first in the <code>ArraySet</code> or is not contained 
     * in the <code>ArraySet</code>.
     */
    public E successor(E element) {
        if (contains(element)) {
            int index = element.id();
            while (index < elements.length && elements[index] == null) {
                index++;
            }
            if (index < elements.length) {
                return elements[index];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns an iterator for the elements of this <code>ArraySet</code>.
     * With the iterator one can iterate comfortable through all elements.
     * @return an iterator for the elements of this <code>ArraySet</code>.
     */
    public Iterator<E> iterator() {
        return new ArrayIterator<E>(elements);
    }
    
    /**
     * Returns the capacity of this <code>ArraySet</code>.
     * The capacity is one higher than highest accepted ID.
     * @return the capacity of this <code>ArraySet</code>.
     */
    public int getCapacity() {
        return elements.length;
    }
    
    /**
     * Sets the the capacity of this <code>ArraySet</code>.
     * The capacity is one higher than the highest accepted ID.
     * Should only be used if the constructor 
     * <code>public ArraySet(Class<E> elementType)</code>
     * was used. Dynamic resizing is not recommended!
     * Elements with IDs greater or equal to <code>capacity</code> will be
     * cut off.
     * @param capacity the capacity to be set.
     */
    @SuppressWarnings("unchecked")
    public void setCapacity(int capacity) {
        E[] newElements = (E[]) Array.newInstance(elementType,capacity);
        for (int i = elements.length - 1; i >= capacity; i--) {
            if (elements[i] != null) size--;
        }
        System.arraycopy(elements, 0, newElements, 0, Math.min(elements.length,capacity));
        elements = newElements;
    }
    
    /**
     * Returns a String describing the <code>ArraySet</code>.
     * The String contains the indices of the internal array that have
     * a stored element. As the elements have the same ID as the position
     * they are stored in, die output String is also a list of the IDs
     * of all elements stored in the <code>ArraySet</code>.
     * @return a String containing the indices of the internal array that have a stored element.
     */
    @Override
    public String toString(){
        String s = "[";
        Iterator<E> it = this.iterator();
        if (it.hasNext()){
            E e = it.next();
            s += e.id();
        }
            
        while (it.hasNext()) {
            E e = it.next();
            s += " ";
            s += e.id();
        }
        s += "]";
        return s;
    }

    /**
     * Returns the hash code of this array set.
     * The hash code is calculated by computing the arithmetic mean
     * of the hash codes of the contained elements.
     * Therefore the hash code is equal for array sets equal according to
     * the <code>equals</code>-method, but not necessarily different
     * for array sets different according to the <code>equals</code>-method
     * If hashing of array sets is heavily used,
     * the implementation of this method should be reconsidered.
     * @return the hash code of this node.
     */
    @Override
    public int hashCode() {
        int h = 0;
        for (E e : this) {
            h += Math.floor(e.hashCode() / this.size());
        }
        return h;
    }
    
    /**
     * Returns whether an object is equal to this array set.
     * The result is true if and only if the argument is not null and is a
     * <code>ArraySet</code> object including the same number of
     * elements where all the elements are pairwise equal according
     * to their <code>equals</code>-Method.
     * @param o object to compare.
     * @return <code>true</code> if the given object represents a
     * <code>ArraySet</code> equivalent to this object, <code>false</code> otherwise.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        boolean eq;
        if (o == null || !(o instanceof ArraySet)) {
            eq = false;
        } else {
            ArraySet arraysetu = (ArraySet) o;
            ArraySet<E> arrayset = null;
            if (arraysetu.getClass() != this.getClass()) {
            	return false;
            } else {
            	arrayset = (ArraySet<E>) arraysetu;
            }
            eq = (this.size() == arrayset.size());
            if (eq) {
                Iterator<E> i1 = this.iterator();
                Iterator<E> i2 = arrayset.iterator();
                while (i1.hasNext()) {
                    eq &= (i1.next().equals(i2.next()));
                }
            }
        }
        return eq;
    }
    
    /**
     * Clones this array set by cloning the elements and creating a new 
     * <code>ArraySet</code> object with the clones.
     * @return a <code>ArraySet</code> object with clones of the elements
     *         of this object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public ArraySet<E> clone() {
    	E[] c = (E[]) Array.newInstance(elementType, elements.length);
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] == null) {
                c[i] = null;
            } else {
                c[i] = (E) elements[i].clone();
            }
        }
        return new ArraySet<E>(c);
    }
}
