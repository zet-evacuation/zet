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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ds.graph;

import java.util.Random;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mouk
 */
public class HidingSetTest 
{
    HidingSet instance;
    IdentifiableStub[] testObjects;
    Random rand = new Random();

    @Before
    public void setUp() 
    {
        int length = rand.nextInt(20)+6;
        testObjects = new IdentifiableStub[length];
        for(int i = 0; i<length; i++)
            testObjects[i] = new IdentifiableStub(i);
        instance = new HidingSet(testObjects);
        
        
    }
    
    @Test
    public void canIdentifyContainedness()
    {
        Identifiable element = testObjects[rand.nextInt(testObjects.length)];
        assertTrue(instance.contains(element));
        instance.setHidden(element, true);
        assertFalse(instance.contains(element));
        
        element = new IdentifiableStub(100);
        
        assertFalse(instance.contains(element));
        
    }
    
    @Test
    public void canRetrieveElements()
    {
        for(int i = 0 ; i < testObjects.length; i++)
            assertEquals(testObjects[i], instance.get(i));
        Identifiable element = testObjects[4];
        instance.setHidden(element, true);
        assertNull(instance.get(element.id()));
        
    }
    
    @Test
    public void canHideElement()
    {
        Identifiable element = testObjects[rand.nextInt(testObjects.length)];
        int all = instance.numberOfAllElements();
        int allHidden = instance.numberOfHiddenElements();
        assertEquals(element, instance.get(element.id()));
        
        instance.setHidden(element, true);
        
        assertEquals(all, instance.numberOfAllElements());
        assertEquals(allHidden+1, instance.numberOfHiddenElements());
        assertNull(instance.get(element.id()));

    }
    
    @Test
    public void canGetFirstElement()
    {
        for(Identifiable elem: testObjects)
        {
            assertEquals(elem, instance.first());
            instance.setHidden(elem, true);
        }
        assertNull(instance.first());
    }
    
    @Test
    public void canGetLastElement()
    {
        for(int i = testObjects.length -1; i >=0 ; i--)
        {
            Identifiable elem =  testObjects[i];
            assertEquals(elem, instance.last());
            instance.setHidden(elem, true);
        }
        assertNull(instance.last());
    }

}