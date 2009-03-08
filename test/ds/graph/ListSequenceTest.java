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
 * ListSequenceTest.java
 * JUnit based test
 *
 * Created on 28. November 2007, 10:45
 */

package ds.graph;


import java.util.LinkedList;
import java.util.Random;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

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
public class ListSequenceTest
{
    ListSequence<Identifiable> instance;
    ListSequence<Identifiable> emptyInstance;
    Collection<Identifiable> testObjects;
    Identifiable[] testArray;
    Random rand = new Random();
    int length;

    @Before
    public void setUp()
    {
        
        emptyInstance = new ListSequence<Identifiable>();
        
        length = rand.nextInt(10)+10;
        testObjects = new  LinkedList();
        testArray = new Identifiable[length];
                
        for(int i = 0 ; i < length; i++ )
        {
            IdentifiableStub item = new IdentifiableStub();
            item.setId(i);
            testObjects.add(item);
            testArray[i] = item;
        }
        
        instance = new ListSequence<Identifiable>(testObjects);
    }

    @Test
    public void canDo() 
    {
        
    }
    
    @Test
    public void canRemoveElementFromMiddle() 
    {
        int randomId = rand.nextInt(testArray.length -5) + 2;
        Identifiable randomElement = testArray[randomId];
        instance.remove(randomElement);
        
        boolean elementFound = false;
        for(Identifiable current : instance)
            if(current == randomElement)
                elementFound = true ;
        assertFalse("Removed element has been found", elementFound);
    }
    
    @Test
    public void canGetPredecessor() 
    {
        int randomId = rand.nextInt(testObjects.size()-3) + 2;
        Identifiable randomElement = testArray[randomId];;
        
        assertEquals("Wrong predecessor was returnd",
                testArray[randomId-1],instance.predecessor(randomElement));
    }
    @Test
    public void cannotGetPredecessorOfTheFirstElement() 
    {
        assertNull("First element should have no predessor",
                instance.predecessor(testArray[0] ));
    }
    
    @Test
    public void canGetSuccessor() 
    {
        int randomId = rand.nextInt(testObjects.size()-3);
        Identifiable randomElement = testArray[randomId];;
        
        assertEquals("Wrong successor was returnd",
                testArray[randomId+1],instance.successor(randomElement));
    }
    @Test
    public void cannotGetSuccessorOfTheFirstElement() 
    {
        assertNull("Last element should have no successor.",
                instance.successor(testArray[testArray.length-1] ));
    }
    
    @Test
    public void canGetFirst() 
    {
        assertEquals(testArray[0], instance.first());
    }
    
    @Test
    public void cannotGetFirstFromEmptyList() 
    {
        assertNull(emptyInstance.first());
    }

   
    
    @Test
    public void canDetectEquality() 
    {
        assertTrue(instance.equals(instance));
        assertFalse(instance.equals(emptyInstance));
        
        ListSequence nInstance = new ListSequence(testObjects);
        nInstance.remove(testArray[testArray.length -1]);
        
        assertFalse(instance.equals(nInstance));
    }
    
    public static junit.framework.Test suite(){
		return new JUnit4TestAdapter(ListSequenceTest.class);
     }
    
}
