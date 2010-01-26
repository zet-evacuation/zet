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
 * ArrayIteratorTest.java
 * JUnit based test
 *
 * Created on 27. November 2007, 17:30
 */

package ds.graph;

import java.util.Random;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.util.Iterator;

/**
 *
 * @author mouk
 */
public class ArrayIteratorTest{
    
    public ArrayIteratorTest() {
        
    }

   
    Integer[] testObjects;
    ArrayIterator<Integer> instance;
    
   @Before
   public void setUp() {
       Random rand = new Random();
       int length = rand.nextInt(20)+5;
       testObjects = new Integer[length];
       for(int i = 0 ; i < length; i++)
           testObjects[i] = new Integer(rand.nextInt());
       /*
       testObjects = new Integer[]{new Integer(3),
                                    new Integer(5),
                                    new Integer(-3),
                                    new Integer(1)};
        */
       instance = new ArrayIterator<Integer>(testObjects);
       
   }
    
    /**
     * Test of hasNext method, of class graph.ArrayIterator.
     */
   @Test
    public void canIterateOverTheDeliveredObjects() {
       for(int i = 0 ; i < testObjects.length; i++){
           
           assertTrue(instance.hasNext());
           Integer got = (Integer) instance.next();
           assertEquals(got,testObjects[i]);
       }
       assertFalse(instance.hasNext());
           
    }
        
    
    public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ArrayIteratorTest.class);
	}
    
}