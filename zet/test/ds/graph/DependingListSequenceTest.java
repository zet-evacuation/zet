/*
 * DependingListSequenceTest.java
 * JUnit based test
 *
 * Created on 28. November 2007, 10:33
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
import java.util.LinkedList;

/**
 *
 * @author mouk
 */
public class DependingListSequenceTest 
{
    DependingListSequence<Identifiable> instance;
    IdentifiableStub [] testObjects;
    Random rand = new Random();
    IdentifiableCollection<Identifiable> collection;
    
    @Before
    public void setUp() 
    {
        LinkedList<Identifiable>  list = new LinkedList<Identifiable>();
        int length = rand.nextInt(20)+10;
        testObjects = new IdentifiableStub[length];
        for(int i = 0 ; i < length; i++)
        {
            testObjects[i] = new IdentifiableStub(i);
            list.add(testObjects[i]);
        }
        
        
        collection = new ListSequence<Identifiable>(list);        
        instance = new DependingListSequence<Identifiable>(collection);
        for(Identifiable iden : testObjects)
            instance.add(iden);
    }
    
    @Test
    public void canIterate()
    {
        int i = 0;
        for(Identifiable elem :  instance)
            assertEquals(testObjects[i++],elem);
        
        assertEquals(i, testObjects.length);
    }
    
    @Test
    public void cannotRetrieveAnElementNotExistingInTheBaseCollection()
    {
        int randomIdex = rand.nextInt(testObjects.length);
        Identifiable randomElement = testObjects[randomIdex];
        Identifiable newElement = new IdentifiableStub(testObjects.length);
        instance.add(newElement);
        collection.remove(randomElement);
        
        for(Identifiable elem : instance)
        {
            assertNotSame(randomElement,elem);
            assertNotSame(newElement,elem);
        }
    }        
    
    @Test
    public void canGetRightSize()
    {
        
        int length = testObjects.length;    
        assertEquals(length, instance.size());
        for(Identifiable elem : testObjects)
        {
            collection.remove(elem);
            assertEquals(--length,instance.size());
        }
        
        assertEquals(0, length);
            
    }
    
    
    @Test
    public void canRemoveElement()
    {
        int index = 9 ;
        Identifiable elem = testObjects[index];
        assertTrue(instance.contains(elem));
        instance.remove(testObjects[index-1]);
        assertFalse(instance.contains(testObjects[index-1]));
        
        collection.remove(testObjects[index-2]);
        assertFalse(instance.contains(testObjects[index-2]));
    }
    
    // Eine Debug-Funktion
    private void printBothToScreen(){
    	System.out.print("[");
        for (int i = 0; i < testObjects.length-1; i++){
        	System.out.print(testObjects[i].id()+" ");
        }
        System.out.print(testObjects[testObjects.length-1].id());
        System.out.println("]");
        System.out.println(instance.toString());
    }
    
    @Test
    public void canGetCorrectPredecessor()
    {
        int index = 8;
                
        assertEquals(testObjects[index-1],instance.predecessor(testObjects[index]));

        instance.remove(testObjects[index-1]);
        
        assertEquals(testObjects[index-2],instance.predecessor(testObjects[index]));
        collection.remove(testObjects[index-2]);
        
        assertEquals(testObjects[index-3],instance.predecessor(testObjects[index]));
        
    }
    
    
   
    public static junit.framework.Test suite(){
		return new JUnit4TestAdapter(DependingListSequenceTest.class);
     }
    
}
