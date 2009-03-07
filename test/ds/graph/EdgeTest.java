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
 * EdgeTest.java
 * JUnit based test
 *
 * Created on 29. November 2007, 09:47
 */

/**
 *
 * @author mouk
 */
//@RunWith(JMock.class)
public class EdgeTest 
{
    Edge instance;
    Node start;
    Node end;

    @Before
    public void setUp()
    {
        start = (Node) createMock(Node.class);
        end = (Node) createMock(Node.class);
        
        //expect(start.id()).andReturn(6).anyTimes();
        //replay(start);
        //end = (Node) mockControl.getMock(Node.class);
       
        instance = new Edge(10,start, end);
        
    }

    @Test(expected=Exception.class)
    public void cannotInitiateWithNullObjecs()
    {
        Edge edge= null;
        try
        {
            edge = new Edge(10,start,null);
        }
        finally{}
        
        assertNull("should not be able to constructe an edge with null object", edge);
        edge = null;
        try
        {
            edge = new Edge(10,null,end);
        }
        finally{}
        
        assertNull("should not be able to constructe an edge with null object", edge);
        
    }

    @Test
    public void canGetOpposite()
    {
        assertEquals(end,instance.opposite(start));
        assertEquals(start,instance.opposite(end));
    }
    
    @Test
    public void canDetectEquality()
    {
        Edge oppositeEdge = new Edge(2,end, start);
        assertFalse(instance.equals(oppositeEdge));
        assertTrue(instance.equals(instance));
    }
    
     public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(EdgeTest.class);
     }
}
