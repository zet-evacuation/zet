/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * ArraySetTest.java
 * JUnit based test
 *
 * Created on 27. November 2007, 18:45
 */

package ds.graph;

import java.util.Random;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.util.Iterator;
import ds.graph.IdentifiableStub;
/**
 *
 * @author mouk
 */
public class ArraySetTest {
    
   Identifiable[] testObjects;
   ArraySet<Identifiable> instance;
   ArraySet<Identifiable> emptyInstance;
   Random rand = new Random();

   @Before
   public void setUp() {
       int length = rand.nextInt(20)+5;
       testObjects = new Identifiable[length];
       IdentifiableStub placeHolder;
       for(int i = 0 ; i < length; i++)
       {
           placeHolder = new IdentifiableStub();
           placeHolder.setId(i);
           testObjects[i] = placeHolder;
       }
       //System.out.print(testObjects.length);
       instance = new ArraySet<Identifiable>(testObjects);
       emptyInstance = new ArraySet<Identifiable>(Identifiable.class);
   }
    
   
     @Test
    public void canGetTheRightElementUsingItsIdWithoutChngingSize()
    {
        int length = testObjects.length;
        int oldSize = instance.size();
        for(int i=0; i < length; i++)
        {
            assertEquals(testObjects[i],instance.get(i));
        }
        assertEquals(oldSize, instance.size());
    }
    @Test
     public void canGetTheCorrectSize()
    {
        assertEquals("size() must return the number of elements it received in the constructure",
                testObjects.length,instance.size());
        assertEquals(emptyInstance.size(),0);
    }
    
    @Test
     public void canGetEmpty()
    {
        assertFalse("Non-empty instances shouldn't return true for empty", instance.empty());
        assertTrue("An empty instance should return true for empty",emptyInstance.empty());
    }
    
    @Test
     public void canReplaceElementUsingAdd()
    {
        int randomIndex = rand.nextInt(testObjects.length);
        Identifiable randomElement = testObjects[randomIndex];
        IdentifiableStub replacement = new IdentifiableStub();
        replacement.setId(randomIndex);
        
        assertEquals(randomElement,instance.get(randomIndex));
        assertTrue("Couldn't replace the Element", instance.add(replacement));
        assertEquals("Element was not replaced correctly",
                replacement,instance.get(randomIndex));
    }
    
    @Test
     public void canRemoveElement()
    {
        int randomIndex = rand.nextInt(testObjects.length);
        Identifiable randomElement = testObjects[randomIndex];
        
        
        assertEquals(randomElement,instance.get(randomIndex));
        instance.remove(randomElement);
        assertNull("Element was not removed correctly",
                instance.get(randomIndex));
    }
    @Test
     public void canGetFirst()
    {
        assertEquals("Return wrong elemnt as the first one",
                instance.first(),testObjects[0]);
    }
    @Test
     public void canGetLast()
    {
        assertEquals("couldn't get last",
                instance.last(),testObjects[testObjects.length-1]);
    }
    @Test
     public void canRemoveLast()
    {
        int idLastElement = testObjects.length-1;
        assertEquals("The last element in the array has not been added correctly",
                testObjects[idLastElement], instance.get(idLastElement));
        
        assertEquals("Couldn't remove last element",
                testObjects[idLastElement],  instance.removeLast());
        
        assertNull("Last element was not removed correctly",
                instance.get(idLastElement));
    }
    
    @Test
     public void cannotRemoveLastFromEmptyArray()
    {
        assertNull("Should not be able to remove anything from an empty array",
                emptyInstance.removeLast());
    }
    @Test
     public void canFindOutIfAnElmentBelongsTo()
    {
        int randomIndex = rand.nextInt(testObjects.length);
        Identifiable randomElement = testObjects[randomIndex];
        
        assertTrue("Couldn't find a element has been added using constructure",
                instance.contains(randomElement));
        IdentifiableStub fakeElement = new IdentifiableStub();
        fakeElement.setId(-1);
        assertFalse("Element with negative id should not be found in the array",
                instance.contains(fakeElement));
        fakeElement.setId(testObjects.length);
        assertFalse("Element with bigger id than the biggest one in the list should not be found",
                instance.contains(fakeElement));
    }

    @Test
    public void canGetPredecessor()
    {
        int randomIndex = rand.nextInt(testObjects.length-1)+1;
        Identifiable randomElement = testObjects[randomIndex];
        assertEquals("couldn't get precessor",
                testObjects[randomIndex-1],instance.predecessor(randomElement ));
        
    }
    @Test
    public void cannotGetPredecessorForTheFirstOne()
    {
        assertNull("Should return no predecessor for the first element",
                instance.predecessor(testObjects[0] ));   
    }
    @Test
    @Ignore("Fails because successor returns currentlx the same object")
    public void canGetSuccessor()
    {
        int randomIndex = rand.nextInt(testObjects.length-1);
        Identifiable randomElement = testObjects[randomIndex];
        assertEquals("couldn't get successor",
                testObjects[randomIndex+1],instance.successor(randomElement));
        
    }
    @Test
    @Ignore("Fails because successor returns currently the same object")
    public void cannotGetSuccessorForTheFirstOne()
    {
        assertNull("Should return no predecessor for the first element",
                instance.predecessor(testObjects[testObjects.length-1] ));   
    }
    
    @Test
    public void canSetCapacityAndPreserveElements()
    {
        int currentCapacity = testObjects.length;
        assertEquals(currentCapacity,instance.getCapacity());
        
        int newCapacity = currentCapacity + 5;
        instance.setCapacity(newCapacity);
        boolean elementsPreserved = true;
        for(int i = 0 ; i < Math.min(currentCapacity, newCapacity); i++)
        {
            if(testObjects[i] != instance.get(i))
            {
                elementsPreserved = false;
                break;
            }
        }
        assertTrue(elementsPreserved);
            
        newCapacity = currentCapacity - 4 ;
        instance.setCapacity(newCapacity);
        elementsPreserved = true;
        
        for(int i = 0 ; i < Math.min(currentCapacity, newCapacity); i++)
            if(testObjects[i] != instance.get(i))
            {
                elementsPreserved = false;
                break;
            }
                
        
    }
     
     public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ArraySetTest.class);
     }
}
