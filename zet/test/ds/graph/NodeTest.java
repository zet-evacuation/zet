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


import ds.graph.*;
import java.util.LinkedList;
import java.util.Random;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.util.Iterator;
import junit.framework.*;
/*
 * NodeTest.java
 * JUnit based test
 *
 * Created on 29. November 2007, 09:04
 */

/**
 *
 * @author mouk
 */
public class NodeTest 
{
    Node instance;

    @Before
    public void setUp()
    {
        instance = new Node(3);
    }
    
    @Test
    public void canDetectEquality()
    {
        assertTrue("Two identical nodes were not detect as such",
                instance.equals(new Node(instance.id() )));
        assertFalse("Different nodes returned true in equality test",
                instance.equals(new Node(instance.id() + 1)));
    }
    
    @Test
    public void canClone()
    {
        assertTrue("Couldn't clone correctly",
                instance.equals(instance.clone() ));        
    }

    
    
   
    public static junit.framework.Test suite(){
		return new JUnit4TestAdapter(NodeTest.class);
     }
    
    
}
