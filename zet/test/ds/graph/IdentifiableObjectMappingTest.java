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
package ds.graph;



import java.util.Random;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;


/*
 * IdentifiableObjectMappingTest.java
 * JUnit based test
 *
 * Created on 29. November 2007, 20:40
 */

/**
 *
 * @author mouk
 */
public class IdentifiableObjectMappingTest
{
    Random rand = new Random();
    IdentifiableObjectMapping<Identifiable, Integer> instance;
    IdentifiableStub[] testObjects;
    Integer[] testIntegers;
    

    
    @Before
    public void setUp()
    {
        testObjects = new IdentifiableStub[25];
        testIntegers = new Integer[testObjects.length];
        for(int i = 0 ; i < testObjects.length; i++)
        {
            testObjects[i] = new IdentifiableStub(i);
            testIntegers[i] = new Integer(i);
        }
        instance = new IdentifiableObjectMapping<Identifiable, 
                Integer>(testIntegers, Integer.class);
        assertNotNull("couldn't construct object", instance);
    }
    
    @Test
    public void canInitiateUsingConstructorWithDomainCapacity()
    {
        IdentifiableObjectMapping<Identifiable, Integer> map= null;
        try
        {
            map =new  IdentifiableObjectMapping<Identifiable, Integer>(10, Integer.class);
        }
        catch(Exception e)
        {
            fail("Could not initiate Object. Constructor threw following exception: "
                    + e.toString());
        }
         assertNotNull(map);
    }
    @Test(expected=NullPointerException.class)
    public void cannotConstructWithBadData()
    {
         IdentifiableObjectMapping<Identifiable, Integer> map;
         //map =new  IdentifiableObjectMapping<Identifiable, Integer>(null, Integer.class);
         //fail("Mapping could be constructed with bad data. ");
    }
    
    @Test
    public void canGetMapping()
    {        
        for(Identifiable stub : testObjects)
        {
            assertNotNull("Get returned null!", instance.get(stub));
            assertEquals("Get returned wrong value.",
                stub.id(),instance.get(stub).intValue() );
        }
    }

    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void cannotGetMappingForObjectWithTooBigId()
    {        
        assertNotNull(instance.get(new IdentifiableStub(testObjects.length)));
    }
    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void cannotGetMappingForObjectWithNegativeId()
    {        
        assertNotNull(instance.get(new IdentifiableStub(-1)));
    }
    @Test
    public void canReplaceElementUsingSet()
    {        
        int randomIndex = rand.nextInt(testObjects.length - 8) + 3;
        assertEquals(testIntegers[randomIndex], instance.get(testObjects[randomIndex]));
        instance.set(testObjects[randomIndex],new Integer(randomIndex +1));
        assertEquals("Element was not replaced correctly. ",
                randomIndex+1, instance.get(testObjects[randomIndex]).intValue() );
    }
    @Test
    public void canReplaceLastElementUsingSet()
    {        
        int capacity = instance.getDomainSize();
        instance.set(testObjects[testObjects.length - 1],new Integer(testObjects.length));
        assertEquals("Element was not replaced correctly. ",
                testObjects.length, instance.get(testObjects[testObjects.length - 1]).intValue() );
        
        assertEquals("Domain size has been changed. ",
                capacity, instance.getDomainSize());
    }
    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void cannotSetMappingForElementWithNegativeId()
    {        
        Identifiable stub = new IdentifiableStub(-1);
        instance.set(stub, new Integer(30));
    }
    @Test
    public void canFigureOutWetherAnObjectBelongsToDomain()
    {        
        Identifiable stub = new IdentifiableStub(-1);
        assertFalse("An element with negative id doesn't belong to the domain. ", 
                instance.isDefinedFor(stub));
                
        stub = new IdentifiableStub(testObjects.length);
        assertFalse("An element with id bigger than size doesn't belong to the domain. ", 
                instance.isDefinedFor(stub)); 
        
        stub = new IdentifiableStub(testObjects.length-1);
        assertTrue("The last element belongs to domain. ", 
                instance.isDefinedFor(stub)); 
        stub = new IdentifiableStub(0);
        assertTrue("The first element belongs to domain. ", 
                instance.isDefinedFor(stub));
        
        stub = new IdentifiableStub(rand.nextInt(testObjects.length-5 ) + 2);
        assertTrue("isDefined returned wrong value ", 
                instance.isDefinedFor(stub)); 
        
        instance.set(stub, null);
        assertFalse("This element has been removed, so isDefined must return false ", 
                instance.isDefinedFor(stub)); 
    }
    
    @Test
    public void canCloneAnEqualObject()
    {
        assertTrue("A cloned instance should equals its parent",
                instance.equals(instance.clone()));
    }
    
    @Test
    public void cannotTrickOutEqualsWithSlightlyModifiedClones()
    {
        IdentifiableObjectMapping<Identifiable, Integer> clonedInstance =
                instance.clone();
        clonedInstance.set(new IdentifiableStub(5),new Integer(6));
        assertFalse("The cloned instance has been modified and should not match the original. ",
                instance.equals(clonedInstance));
        clonedInstance = instance.clone();
        clonedInstance.setDomainSize(clonedInstance.getDomainSize()+1);
        assertFalse("The cloned instance has been modified and should not match the original. ",
                instance.equals(clonedInstance));
    }
    
    public static junit.framework.Test suite() 
    {
	return new JUnit4TestAdapter(IdentifiableObjectMappingTest.class);
    }
}
