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
