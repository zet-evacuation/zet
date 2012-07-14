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
 * ListSet.java
 * 
 */

package ds.collection;

import ds.graph.IdentifiableCollection;
import ds.mapping.Identifiable;
import java.util.Iterator;

/**
 * The {@code DependingHidingList} class represents a list of {@link Identifiable}
 * objects that depends on another {@link IdentifiableCollection}. This means
 * that the list behaves as if elements that are stored in the
 * {@code DependingHidingList} but not in the underlying {@link IdentifiableCollection}
 * will be considered as not present whenever methods are called (e.g. {@code size()</code,
 * {@code first()} and so on).
 */
public class DependingListSequence<E extends Identifiable> extends ListSequence<E> {
    
    public IdentifiableCollection<E> baseSet;

    /**
     * Creates a new {@code DependingHidingList} that depends on the
     * {@code IdentifiableCollection} {@code baseSet}.
     * @param baseSet the {@code IdentifiableCollection} this 
     *                {@code DependingHidingList} depends on.
     */
    public DependingListSequence(IdentifiableCollection<E> baseSet) {
        this.baseSet = baseSet;
    }
		
	  /**
     * Returns whether the element is contained in this {@code DependingHidingList}.
     * An element is considered as contained in the {@code DependingHidingList}
     * if it is stored in the list itsself and in the underlying 
     * {@link IdentifiableCollection}. If the underlying 
     * {@link IdentifiableCollection}> is a {@link HidingSet} 
     * the element is considered as contained if it is stored in the list itsself,
     * stored in the {@code HidingSet} and not hidden in the {@code HidingSet}
     * (because containedness in {@code HidingSets} implies that the element
     * is not hidden).
     * @param element the element that shall be checked for containedness.
     * @return whether the element {@code element} contained in this 
     *         {@code ListSequence}.
     */
    @Override
    public boolean contains(E element) {
        return (baseSet.contains(element) && super.contains(element));
    }
    
    /**
     * Returns whether this {@code DependingHidingList} is empty.
     * The list is considered to be empty if there are no elements that are
     * stored in the list and contained in the underlying 
     * {@link IdentifiableCollection}.
     * Runtime O(n*CTest), where n is the number of stored (!= contained) elements
     * and CTest ist the running time for testing whether an element is contained
     * in the underlying {@code IntenfiableCollection}.
     * @return whether this {@code ListSequence} is empty
     */
    @Override
    public boolean empty(){
        return (size()==0);
    }

    /**
     * Returns the predecessor of the element {@code element}.
     * Returns null if the  {@code element} is the first element contained
     * in the {@code DependingHidingList} or if it is not contained in the 
     * {@code DependingHidingList}.
     * To be contained means being stored and beeing contained in the
     * underlying {@code IntenfiableCollection}.
     * The order of the elements does not regard the IDs but the 
     * order of their addings, as it is in {@link ListSequence}.
     * @param element the element which predecessor is wanted
     * @return the predecessor of {@code element<\code> or null if the element 
     * is the first element contained in the {@code DependingHidingList} 
     * or is not contained in the {@code DependingHidingList}.
     */
    @Override
    public E predecessor(E element) {
        E pred = null;
        Iterator<E> it = super.iterator();
        int i = (super.indexOf(element));
        for (int j = 0; j < i; j++){
            E e = it.next();
            if (baseSet.contains(e))
                pred = e;
        }
        return pred;
    }
    
        
    /**
     * Returns the successor of the element {@code element}.
     * Returns null if the  {@code element} is the last element contained
     * in the  {@code DependingHidingList} or if it is not contained in the 
     * {@code DependingHidingList}.
     * To be contained means being stored and beeing contained in the
     * underlying {@code IntenfiableCollection}.
     * The order of the elements does not regard the IDs but the 
     * order of their addings, as it is in {@link ListSequence}.
     * @param element the element which successor is wanted
     * @return the successor of {@code element<\code> or null if the element 
     * is the last element contained in the {@code DependingHidingList} or is not 
     * contained in the {@code DependingHidingList}.
     */
    @Override
    public E successor(E element) {
        Iterator<E> it = super.iterator();
        int i = (super.indexOf(element));
        for (int j = 0; j <= i; j++)
            it.next();
        boolean notFound = true;
        E e = null;
        while (it.hasNext() && notFound){
            e = it.next();
            if (baseSet.contains(e))
                notFound=false;
        }
        if (notFound)
            return null;
        else 
            return e;
    }     
    
    /**
     * Returns the first element contained in the {@code DependingHidingList}
     * and null if the {@code DependingHidingList} is empty.
     * To be contained means being stored and beeing contained in the
     * underlying {@code IntenfiableCollection}.
     * The order of the elements does not regard the IDs but the 
     * order of their addings.
     * @return the first element contained in the {@code DependingHidingList}
     * and null if the {@code DependingHidingList} is empty.
     */
    @Override
    public E first() {
        if (this.empty()) {
            return null;
        } else {
            Iterator<E> it = super.iterator();
            boolean notFound = true;
            E e = null;
            while (it.hasNext() && notFound){
                e = it.next();
                if (baseSet.contains(e))
                    notFound = false;
            }            
            if (notFound)
                return null;
            else
                return e;
        }
    }

    /**
     * Returns the last element contained in the {@code DependingHidingList}
     * and null if the {@code DependingHidingList} is empty.
     * To be contained means being stored and beeing contained in the
     * underlying {@code IntenfiableCollection}.
     * The order of the elements does not regard the IDs but the 
     * order of their addings.
     * @return the last element contained in the {@code DependingHidingList}
     * and null if the {@code DependingHidingList} is empty.
     */
    @Override
    public E last() {
        if (this.empty()) {
            return null;
        } else {
            Iterator<E> it = super.iterator();
            E last = null;
            while (it.hasNext()) {
                E e = it.next();
                if (baseSet.contains(e)) {
                    last = e;
                }
            }
            return last;
        }
    }   
    
    /**
     * Returns the size of this {@code DependingHidingList}, i.e.
     * the number of elements that are stored in the {@code DependingHidingList}
     * and that are contained in the underlying {@code IntenfiableCollection}.
     * Runtime O(n*CTest), where n is the number of stored (!= contained) elements
     * and CTest ist the running time for testing whether an element is contained
     * in the underlying {@code IntenfiableCollection}.
     * @return the number of elements that are stored in the {@code DependingHidingList}
     * and that are contained in the underlying {@code IntenfiableCollection}.
     */
    @Override
    public int size(){
        Iterator<E> it = super.iterator();
        int s = 0;
        while (it.hasNext()){
            if (baseSet.contains(it.next()))
                s++;
        }
        return s;
    }
    
    /**
     * Returns an iterator for the elements of this {@code DependingHidingList}.
     * With the iterator one can iterate comfortable through all elements.
     * Elements that are stored but not contained in the underlying 
     * {@code IntenfiableCollection} are skipped.
     * @return an iterator for the elements of this {@code ArraySet}.
     */   
    @Override
    public Iterator<E> iterator() {
       return new DependingListIterator();
   }
		
		public Iterator<E> iteratorAll() {
			if( baseSet instanceof HidingSet )
				return new DependingHidingListIterator( (HidingSet)baseSet );
			else
				return iterator();
		} 
		
public class DependingListIterator implements Iterator<E> {
        
        private Iterator<E> collectionIterator;
        
        /**
         * The element that will be returned next by the iterator,
         * {@code null} at the start and after the last element
         * was returned.
         */
        private E next;
        
        /**
         * Creates a new {@code DependingHidingListIterator}.
         */
        public DependingListIterator() {
            collectionIterator = DependingListSequence.super.iterator();
        }
				
        /**
         * Checks whether there still elements not returned but contained in this
         * {@code DependingHidingList}.
         * @return {@code true} if there are elements not yet returned but contained,
         *         {@code false} else.
         */
        public boolean hasNext() {
            if (next != null)
                return true;
            while (collectionIterator.hasNext()) {
                E e = collectionIterator.next();
									if (baseSet.contains(e)) {
		                  next = e;
			                return true;
				          }
            }
            return false;
        }

        /**
         * Returns the next element not returned but contained in this
         * {@code DependingHidingList}. If there are no more elements,
         * {@code null} is returned.
         * @return the next element if there are more, {@code null} else.
         */
        public E next() {
            if (next == null) {
                hasNext();
            }
            E current = next;
            next = null;
            return current;
        }

        /**
         * Removes the element from this {@code DependingHidingList} that was
         * last returned. 
         */
        public void remove() {
            collectionIterator.remove();
        }
    }		
		
    /**
     * An iterator to comfortably iterate through the elements of a
     * {@code DependingHidingList}. The elements in an {@code DependingHidingList}
     * are ordered by their IDs.      
     * Elements that are stored but not contained in the underlying 
     * {@code IntenfiableCollection} are skipped.
     */
    public class DependingHidingListIterator implements Iterator<E> {
        
        private Iterator<E> collectionIterator;
				private HidingSet<E> baseSet;
				private boolean ignoreHidden = true;
        
        /**
         * The element that will be returned next by the iterator,
         * {@code null} at the start and after the last element
         * was returned.
         */
        private E next;
        
        /**
         * Creates a new {@code DependingHidingListIterator}.
         */
        public DependingHidingListIterator( HidingSet baseSet ) {
            collectionIterator = DependingListSequence.super.iterator();
						this.baseSet = baseSet;
        }
				
        public DependingHidingListIterator( boolean ignoreHidden ) {
            collectionIterator = DependingListSequence.super.iterator();
						this.ignoreHidden = ignoreHidden;
        }
				
        
        /**
         * Checks whether there still elements not returned but contained in this
         * {@code DependingHidingList}.
         * @return {@code true} if there are elements not yet returned but contained,
         *         {@code false} else.
         */
        public boolean hasNext() {
            if (next != null)
                return true;
            while (collectionIterator.hasNext()) {
                E e = collectionIterator.next();
//                if( ignoreHidden )
									if (baseSet.containsEvenIfHidden( e ) ) {
		                  next = e;
			                return true;
				          }
//								else
//									if (baseSet.containsEven( e)) {
//		                  next = e;
//			                return true;
//				          }
									
            }
            return false;
        }

        /**
         * Returns the next element not returned but contained in this
         * {@code DependingHidingList}. If there are no more elements,
         * {@code null} is returned.
         * @return the next element if there are more, {@code null} else.
         */
        public E next() {
            if (next == null) {
                hasNext();
            }
            E current = next;
            next = null;
            return current;
        }

        /**
         * Removes the element from this {@code DependingHidingList} that was
         * last returned. 
         */
        public void remove() {
            collectionIterator.remove();
        }
    }
}
