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
package ds.graph;


import ds.graph.flow.FlowOverTimePath;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;

/*
 * PathFlowTest.java
 * JUnit based test
 *
 * Created on 29. November 2007, 12:35
 */

/**
 *
 * @author mouk
 */
public class PathFlowTest
{
    FlowOverTimePath instance;
    DynamicPath dynamicPath;
    
    
    @Before
    public void setUp()
    {
        dynamicPath = (DynamicPath) createMock(DynamicPath.class);
        instance = new FlowOverTimePath(dynamicPath,1,0);
    }
    
    @Test(expected=Exception.class)
    public void cannotSetDynamicPath()
    {
        instance.setPath(null);
        //fail("PathFlow.setPath(null) should throw an exception. ");
    }
    
    @Test(expected=Exception.class)
    @Ignore("Is this necessary?")
    public void cannotSetNegativeAmount()
    {
        instance.setAmount(-1);
    }
    @Test(expected=Exception.class)
    @Ignore("Is this necessary?")
    public void cannotSetNegativeRate()
    {
        instance.setRate(-1);
    }
   
    @Test
    public void canGetDelayFromDynamicPath()
    {
        Edge edge = createMock(Edge.class);
        expect(dynamicPath.getDelay(edge)).andReturn(25).once();
        replay(dynamicPath);
        //assertEquals(25,instance.delay(edge));
        verify(dynamicPath);
    }
    
    /*
    @Test
    public void canRetrieveEdges()
    {
        expect(dynamicPath.edges).andReturn(null).once();
        instance.edges();
        verify(dynamicPath);
    }
    
    
    @Test
    public void canRetrieveFirstEdge()
    {
        expect(dynamicPath.first()).andReturn(null).once();
        instance.firstEdge();
        verify(dynamicPath);
    }
    
    @Test
    public void canRetrieveLastEdge()
    {
        expect(dynamicPath.last()).andReturn(null).once();
        instance.lastEdge();
        verify(dynamicPath);
    }
    */
    @Test
    public void canDetectEquality()
    {
        FlowOverTimePath clonePath = new FlowOverTimePath(dynamicPath,1,0);
        assertTrue(instance.equals(clonePath));
        clonePath.setAmount(10);
        assertFalse(instance.equals(clonePath));
        clonePath.setAmount(0);
        clonePath.setRate(2);
        assertFalse(instance.equals(clonePath));
        clonePath.setRate(1);
        clonePath.setPath(new DynamicPath());
        assertFalse(instance.equals(clonePath));
        
                
    }
    
    @Test
    public void canCloneAnEqualObject()
    {
        assertTrue(instance.equals(instance.clone()));
    }
    
    public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(PathFlowTest.class);
    }
    
}