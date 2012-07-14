/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * HidingSet.java
 * 
 */

package ds.collection;

import ds.graph.ArraySet;
import ds.graph.GraphLocalization;
import ds.graph.IdentifiableCollection;
import ds.mapping.Identifiable;
import java.util.Iterator;

/**
 * The {@code HidingSet} class extends the {@link ArraySet} class by the 
 * functionality of hiding elements.
 * A hidden element remains in the set but is not counted in {@code size(){@code 
 * and will also be skipped in methods like {@code first()} or
 * {@code predecessor(E element)}. The iterator of the class will also
 * jump over hidden elements.
 * @param <E> 
 */
public class HidingSet<E extends Identifiable> extends ArraySet<E> implements IdentifiableCollection<E> {

    /** The array to store which elements are currently hidden. */
    private boolean[] hidden;
    
    /** Number of currently hidden elements. */
    private int numberOfHiddenElements;
    
    /** The type of the elements stored in the {@code HidingSet}. */
    private Class<? extends Identifiable> elementType;
    
    /**
     * Creates a new {@code HidingSet} that can store elements
     * of type {@code elementType} with capacity to store elements
     * with IDs between zero and {@code capacity-1}.
     * No elements are hidden at the start.
     * @param elementType the type the elements in this {@code HidingSet}
     *                    will have.
     * @param capacity the highest possible ID for elements plus one.
     */
    public HidingSet(Class<E> elementType, int capacity){
			super(elementType, capacity);
        this.elementType = elementType;
        hidden = new boolean[capacity];
        numberOfHiddenElements = 0;
    }
    
    /**
     * Constructs a {@code HidingSet}, 
     * typed to {@code elementType}, but containing no elements and
     * with zero capacity.
     * The capacity must be set by {@code public void setCapacity(int capacity)}
     * before storing elements in the {@code HidingSet}.
     * @param elementType the type the elements in this {@code HidingSet}
     *                    will have.
     */
    public HidingSet(Class<E> elementType) {
        this(elementType, 0);
        numberOfHiddenElements = 0;
    }

    /**
     * Constructs a {@code HidingSet} containing the elements in the 
     * given array. The elements must be stored in the field corresponding
     * to their ID, elsewise an {@code IllegalArgumentException} 
     * is thrown. No elements are hidden at the start.
     * @param elements an array with elements that shall be contained in this
     *        {@code ArraySet}.
     */
    public HidingSet(E[] elements) {
        super(elements);
        hidden = new boolean[elements.length];
        numberOfHiddenElements = 0;
    } 
    
    /**
     * Returns whether the element is contained in this {@code HidingSet}.
     * The test checks for containedness of the specified element (not for
     * containedness of an element having the same ID).
     * The test is efficient because of the array based implementation. 
     * Hidden elements are skipped.
     * Runtime O(1).
     * @param element the element that shall be checked for containedness.
     * @return whether the element {@code element} contained in this 
     *         {@code ArraySet}, returns {@code false} if the
     *         element is stored but hidden.
     */
    @Override
    public boolean contains(E element) {
        return super.contains(element) && !hidden[element.id()];
    }
		
		public boolean containsEvenIfHidden( E element ) {
        return super.contains( element );
		}
						

    /**
     * Returns whether this {@code ArraySet} contains no non hidden
     * elements. Runtime O(1).
     * @return {@code false} if there is a stored element that is not 
     *         hidden, {@code true} else.
     */    
    @Override
    public boolean empty() {
        return (size() == 0);
    }
    
    /**
     * Returns the number of non hidden elements. Runtime O(1).
     * @return the number of non hidden elements.
     */
    @Override
    public int size(){
        return super.size() - numberOfHiddenElements;
    }
    
    /**
     * Returns the number of all stored elements including hidden elements.
     * Runtime O(1).
     * @return the number of all stored elements including hidden elements.
     */
    public int numberOfAllElements(){
        return super.size();
    }
    
    /**
     * Returns the element with the ID {@code id} that is stored in this 
     * {@code HidingSet} or null if no element with this ID is stored or
     * if the stored element is hidden.
     * A {@code HidingSet}  is especially a set, i.e. the returned
     * element is uniquely defined.
     * The test is efficient because of the array based implementation. 
     * Runtime O(1).
     * @param id the ID that shall be checked
     * @return the element with the ID {@code id} that is stored in this 
     * {@code ArraySet}, {@code null} if no element with this
     * ID is stored or the stored element is hidden.
     */
    @Override
    public E get(int id) {
        if (hidden[id])
            return null;
        else return super.get(id);
    }
    
    /**
     * Returns the element with the ID {@code id} that is stored in this 
     * {@code HidingSet} or null if no element with this ID is stored.
     * The element is returned even if it is hidden.
     * A {@code HidingSet} is especially a set, i.e. the returned
     * element is uniquely defined.
     * The test is efficient because of the array based implementation. 
     * Runtime O(1).
     * @param id the ID that shall be checked
     * @return the element with the ID {@code id} that is stored in this 
     * {@code ArraySet} (even if hidden), {@code null} if no element with this
     * ID is stored.
     */    
    public E getEvenIfHidden(int id){
    	return super.get(id);
    }

    /**
     * Returns the first non hidden element stored in this {@code HidingSet}.
     * The order in {@code HidingSet} depends on the IDs, 
     * thus the first element is the non hidden element with the smallest ID.
     * If the {@code HidingSet} is empty or all elements are hidden,
     * {@code null} is returned.
     * Runtime O(ID_first), where ID_first is the ID of the first non hidden element.
     * @return the first non hidden element stored in this {@code HidingSet},
     *         null if no element is stored or all elements are hidden.
     */
    @Override
    public E first() {
        int index = 0;
        while (super.get(index)==null || hidden[index]) {
            index++;
            if (index == hidden.length) return null;
        }
        return get(index);
    }

    /**
     * Returns the last non hidden element stored in this {@code HidingSet}.
     * The order in {@code HidingSet} depends on the IDs, 
     * thus the last element is the non hidden element with the highest ID.
     * If the {@code HidingSet} is empty of all elements are hidden,
     * {@code null} is returned.
     * Runtime O(ID_last), where ID_first is the ID of the last non hidden element.
     * @return the last non hidden element stored in this {@code HidingSet},
     *         null if no element is stored or all elements are hidden.
     */
    @Override
    public E last() {
        if(size()<=0) return null;
        int index = size()-1;
        while (super.get(index)==null || hidden[index]) {
            index--;
            if (index == -1) return null;
        }
        return get(index);
    }

    /**
     * Returns the (non hidden) predecessor of the element {@code element}.
     * Returns null if the  {@code element} is the first non hidden element
     * in the {@code HidingSet} or if it is not stored in the 
     * {@code HidingSet}. 
     * The order in {@code HidingSet} depends on the IDs, 
     * thus the predecessor of the element {@code element} is the non
     * hidden element with the highest ID smaller than the ID of {@code element}. 
     * Runtime O(ID_element) where ID_element is the ID of {@code element}.
     * @param element the element which predecessor is wanted
     * @return the predecessor of {@code element<\code> or null if the element 
     * is the first non hidden element in the {@code HidingSet} or is not contained 
     * in the {@code HidingSet}.
     */
    @Override
    public E predecessor(E element) {
        int index = element.id();
        do {
            index--;
            if (index == -1) {
                return null;
            }
        } while (hidden[index]);
        return get(index);
    }

    /**
     * Returns the (non hidden) successor of the element {@code element}.
     * Returns null if the  {@code element} is the last non hidden element
     * in the {@code HidingSet} or if it is not stored in the 
     * {@code HidingSet}. 
     * The order in {@code ArraySet} depends on the IDs, 
     * thus the successor of the element {@code element} ist the non hidden
     * element with the smallest ID higher than the ID of {@code element}. 
     * Runtime O(n-ID_element) where ID_element is the ID of {@code element}
     * and n is the number of possible IDs.
     * @param element the element which successor is wanted
     * @return the successor of {@code element<\code> or null if the element 
     * is the first non hidden element in the {@code HidingSet} or is not contained 
     * in the {@code HidingSet}.
     */
    @Override
    public E successor(E element) {
        int index = element.id();
        do {
            index++;
            if (index == hidden.length) {
                return null;
            }
        } while (hidden[index]);
        return get(index);
    }

    /**
     * Removes the element from the {@code HidingSet} having the
     * same ID as the element {@code element}.  
     * If there is no such element in the {@code ArraySet},
     * nothing happens.
     * Due to the array based implementation this operation is efficient.
     * If a new element with the same ID
     * will be added later, it will not be hidden, regardless whether
     * this element was hidden in the {@code HidingSet}.
     * Runtime O(1).
     * @param element element element to be removed.
     */
    @Override
    public void remove(E element) {
        super.remove(element);
        if (element.id() >= 0 && element.id() >= size() - 1) {
            hidden[element.id()] = false;
        }
    }
    
    /**
     * Adds an element to the {@code HidingSet} and returns
     * whether the insertion was successful. 
     * The new inserted element will not be hidden.
     * The insertion fails
     * if the ID of the element is negative outside the range of this
     * {@code HidingSet}.
     * Elsewise the element will be stored at the appropriate array position.
		 * @param element element to be add.
		 * @return {@code true} if adding was successful, {@code false} otherwise
     */
    @Override
    public boolean add(E element){
        boolean successful = super.add(element);
        if (successful){
            setHidden(element,false);
            return true;
        } else
            return false;
    }
    
    /**
     * Returns an iterator for the elements of this {@code HidingSet}.
     * With the iterator one can iterate comfortable through all elements.
     * @return an iterator for the elements of this {@code HidingSet}.
     */
    @Override
    public Iterator<E> iterator() {
        return new HidingSetIterator();
    }
    
    /**
     * Returns the number of currently hidden elements.
     * @return the number of currently hidden elements.
     */
    public int numberOfHiddenElements() {
        return numberOfHiddenElements;
    }
    
    /**
     * Returns whether an element is hidden. 
     * If the element is not stored in the {@code HidingSet}
     * {@code false} will be returned.
     * @param element the element that shall be checked
     * @return {@code true} it the element is contained in the set
     *         and hidden, {@code false} else.
     */
    public boolean isHidden(E element) {
			return !super.contains(element) ? false : hidden[element.id()];
    }
    
    /**
     * Returns whether there is an element with
     * ID {@code id} that is hidden. 
     * If there is no element with this ID
     * stored in the {@code HidingSet}
     * {@code false} will be returned.
     * @param id the ID that shall be checked
     * @return {@code true} if an element
     *         with the given ID is contained in the set
     *         and hidden, {@code false} else.
     */
    public boolean isHidden(int id){
    	final E element = super.get(id);
			return element == null ? false : isHidden(element);
    }
    
    /**
     * Sets whether the element {@code element} is hidden from now on.
     * If {@code hidden} contains {@code true} the element will
     * be hidden, else it will be visible.
     * If the element is not contained in the {@code HidingSet}
     * nothing happens.
     * @param element the element that shall be made hidden or visible.
     * @param hidden whether the element will be visible from now on.
     */
    public void setHidden(E element, boolean hidden) {
        if (super.contains(element)) {
            if (isHidden(element) != hidden) {
                this.hidden[element.id()] = hidden;
                if (hidden) {
                    numberOfHiddenElements++;
                } else {
                    numberOfHiddenElements--;
                }
            }
        }
    }
    
    /**
     * Marks all contained elements as visible.
     */
    public void showAll(){
    	for (int i = 0; i < hidden.length; i++)
    		hidden[i] = false;
      numberOfHiddenElements = 0;
    }
    
    /**
     * Sets whether the element with ID {@code id} is hidden from now on.
     * If {@code hidden} contains {@code true} the element will
     * be hidden, else it will be visible.
     * If the element is not contained in the {@code HidingSet}
     * nothing happens.
     * @param id the id of the element that shall be made hidden or visible.
     * @param hidden whether the element will be visible from now on.
     */
    public void setHidden(int id, boolean hidden){
    	E element = super.get(id);
    	if (element==null)
    		return;
    	setHidden(element,hidden);
    }
    
    /**
     * Sets the the capacity of this {@code HidingSet}.
     * The capacity is one higher than the highest accepted ID.
     * Should only be used if the constructor 
     * {@code public HidingSet(Class<E> elementType)}
     * was used. Dynamic resizing is not recommended!
     * Elements with IDs greater or equal to {@code capacity} will be
     * cut off.
     * @param capacity the capacity to be set.
     */
    @Override
    public void setCapacity(int capacity) {
		// Keep track of how many hidden elements are possibly deleted here
		for (int i = super.getCapacity () - 1; i >= capacity; i--) {
            if (super.get (i) != null && isHidden (i)) numberOfHiddenElements--;
        }
		
        super.setCapacity(capacity);
        boolean[] newHidden = new boolean[capacity];
        if (hidden != null)
            System.arraycopy(hidden, 0, newHidden, 0, Math.min(hidden.length,capacity));
        hidden = newHidden;
    }    
         
    /**
     * Returns an {@code ArraySet} where those elements hidden in this
     * {@code HidingSet} are not contained.
     * @return an {@code ArraySet} without hidden elements. 
     */
    @SuppressWarnings("unchecked")
    public ArraySet<E> getSetWithoutHiddenElements(){
        ArraySet<E> result = new ArraySet(elementType,getCapacity());        
        for (int i = 0; i < this.getCapacity();i++)
            if (!hidden[i])
                result.add(get(i));
        return result;
    }
	  
		
		public ArraySet<E> getAll() {
			return new ArraySet<>( this );
		}
						
    public Iterator<E> iteratorAll() {
        return super.iterator();
    }
    
		
    /**
     * An iterator to comfortably iterate through the elements of a
     * {@code HidingSet}. The elements in an {@code HidingSet}
     * are ordered by their IDs. Hidden elements will be skipped.
     */
    public class HidingSetIterator implements Iterator<E> {

        /**
         * The element that was last returned by the iterator, 
         * {@code null} at the start.
         */
        private E current;
        
        /**
         * The element that will be returned next by the iterator,
         * {@code null} at the start and after the last element
         * was returned.
         */
        private E next;
        
        /**
         * The next index to be checked when the next element shall be
         * returned.
         */
        int nextIndex;       
        
        /**
         * Creates a new iterator.
         */
        public HidingSetIterator() {
            nextIndex = 0;
            next = null;
            current = null;
        }

        /**
         * Checks whether there are more non returned elements in this
         * {@code HidingSet}.
         * @return {@code true} if there are elements not yet returned,
         *         {@code false} else.
         */
		@Override
        public boolean hasNext() {
            if (next != null)
                return true;
            int cap = getCapacity();
            while (nextIndex < cap && (get(nextIndex) == null || hidden[nextIndex]))
                nextIndex++;
            if (nextIndex >= cap)
                return false;
            else {
                next = get(nextIndex); 
                nextIndex++;
                return true;
            }
        }

        /**
         * Returns the next non returned element in  this
         * {@code HidingSet}. If there are no more elements,
         * {@code null} is returned.
         * @return the next element if there are more, {@code null} else.
         */
		@Override
        public E next() {
            if (next == null) hasNext();
            current = next;
            next = null;
            return current;
        }

        /**
         * Removes the element from this {@code HidingSet} that was
         * last returned. If no element has been returned yet,
         * an {@code IllegalStateException} will be thrown.
         */
		@Override
        public void remove() {
            if (current == null)
                throw new IllegalStateException(GraphLocalization.getSingleton (
    			).getString ("ds.graph.IterateFirstException"));
            else {
                HidingSet.this.remove(current);
                current = null;
            }
        }
        
    }   

    /**
     * Returns a String describing the {@code HidingSet}.
     * The String contains the indices of the internal array that have
     * a stored and non hidden element. 
     * As the elements have the same ID as the position
     * they are stored in, die output String is also a list of the IDs
     * of all elements stored in the {@code HidingSet} and not hidden.
     * @return a String containing the indices of the internal array that have a stored
     *         and non hidden element.
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
     * Clones this {@code HidingSet} by cloning the elements 
     * and the hiding array and creating a new 
     * {@code HidingSet} object with the clones.
     * @return a {@code HidingSet} object with clones of the elements
     * and the hiding array of this object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public HidingSet<E> clone() {
        HidingSet<E> h = new HidingSet(elementType, getCapacity());
        Iterator<E> it = super.iterator();
        while (it.hasNext()){
            E e = it.next();
            h.add(e);
            h.setHidden(e,hidden[e.id()]);
        }
        return h;
    }
    
    /**
     * Returns whether an object is equal to this hiding set.
     * The result is true if and only if the argument is not null and is a
     * {@code HidingSet} object including the same number of
     * elements where all the elements are pairwise equal according
     * to their {@code equals}-Method.
     * Only visible elements are taken into account.
     * @param o object to compare.
     * @return {@code true} if the given object represents a
     * {@code HidingSet} equivalent to this object, {@code false} otherwise.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        boolean eq;
        if (o == null || !(o instanceof HidingSet)) {
            eq = false;
        } else {
        	HidingSet hset = (HidingSet)o;
            if (hset.getClass() != this.getClass()) {
            	return false;
            }
            HidingSet<E> hidingSet = (HidingSet<E>)hset;
            eq = (this.size() == hidingSet.size());
            if (eq) {
                Iterator<E> i1 = this.iterator();
                Iterator<E> i2 = hidingSet.iterator();
                while (i1.hasNext()) {
                    eq &= (i1.next().equals(i2.next()));
                }
            }
        }
        return eq;
    }

    /**
     * Returns the hash code of this hiding set.
     * The hash code is calculated by computing the arithmetic mean
     * of the hash codes of the contained elements, where
     * hidden elements are skipped.
     * Therefore the hash code is equal for hiding sets equal according to
     * the {@code equals}-method, but not necessarily different
     * for hiding sets different according to the {@code equals}-method
     * If hashing of hiding sets is heavily used,
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
}
