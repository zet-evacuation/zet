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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.graph;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import ds.graph.Edge;
//import ds.graph.EvacuationProblem;
//import ds.graph.FlowProblemInstance;
import ds.graph.HidingSet;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Martin
 */
public class FlowProblemInstanceConverterTest {

    protected XStream xstream;
    //protected EvacuationProblem problem;
    //protected FlowProblemInstance instance;
    
    public FlowProblemInstanceConverterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        xstream = new XStream();
        //Annotations.configureAliases(xstream, EvacuationProblem.class);
        /*
        Annotations.configureAliases(xstream, FlowProblemInstance.class);
        Network network = new Network(4, 5);
        IdentifiableIntegerMapping<Edge> cap = new IdentifiableIntegerMapping<Edge>(network.getEdgeCapacity());        
        IdentifiableIntegerMapping<Edge> tt = new IdentifiableIntegerMapping<Edge>(network.getEdgeCapacity());        
        IdentifiableIntegerMapping<Node> ass = new IdentifiableIntegerMapping<Node>(network.getNodeCapacity());
        ass.set(network.getNode(0), 5);
        ass.set(network.getNode(1), 0);
        ass.set(network.getNode(2), 0);
        ass.set(network.getNode(3), -5);        
        //problem = new EvacuationProblem(network, cap, tt, null, null);
        instance = new FlowProblemInstance(network, cap, tt, ass);
        network.createAndSetEdge(network.getNode(0), network.getNode(1));
        network.createAndSetEdge(network.getNode(0), network.getNode(2));
        network.createAndSetEdge(network.getNode(1), network.getNode(2));
        network.createAndSetEdge(network.getNode(1), network.getNode(3));
        network.createAndSetEdge(network.getNode(2), network.getNode(3));
        cap.set(network.getEdge(0), 1);
        cap.set(network.getEdge(1), 1);
        cap.set(network.getEdge(2), 1);
        cap.set(network.getEdge(3), 1);
        cap.set(network.getEdge(4), 1);
        tt.set(network.getEdge(0), 1);
        tt.set(network.getEdge(1), 4);
        tt.set(network.getEdge(2), 1);
        tt.set(network.getEdge(3), 4);
        tt.set(network.getEdge(4), 1);*/
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of marshal method, of class EvacuationProblemConverter.
     */
    @Test
    public void writeToSystemOut() {
        //String s = xstream.toXML(problem);
        /*
        String s2 = xstream.toXML(instance);
        //System.out.println(s);
        System.out.println(s2);
        //EvacuationProblem p = (EvacuationProblem) xstream.fromXML(s);
        FlowProblemInstance p2 = (FlowProblemInstance) xstream.fromXML(s2);
        System.out.println(p2.getNetwork());
        System.out.println(p2.getEdgeCapacities());
        System.out.println(p2.getNodeCapacities());
        System.out.println(p2.getTransitTimes());
        System.out.println(p2.getAssignment());*/
    }

}