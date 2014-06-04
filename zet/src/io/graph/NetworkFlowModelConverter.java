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

package io.graph;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphMapping;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.graph.DirectedGraph;
import java.util.LinkedList;

/**
 *
 */
public class NetworkFlowModelConverter implements Converter {
    
    //protected FlowProblemInstanceConverter flowProblemInstanceConverter;
    protected NetworkConverter networkConverter;
    protected IdentifiableIntegerMappingConverter mappingConverter;

    public NetworkFlowModelConverter() {
        networkConverter = new NetworkConverter();
        mappingConverter = new IdentifiableIntegerMappingConverter();
        //flowProblemInstanceConverter = new FlowProblemInstanceConverter();
    }
    
	@Override
    public boolean canConvert(Class type) {
        return type.equals(NetworkFlowModel.class);
    }

	@Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        NetworkFlowModel problem = (NetworkFlowModel) source;
				
//		writer.startNode("network");
//        context.convertAnother(problem.getNetwork(), networkConverter);
//        writer.endNode();
//        writer.startNode("edgecapacities");
//        mappingConverter.setDefaultValue(1);
//        mappingConverter.setDomain(problem.getNetwork().edges());
//        context.convertAnother(problem.getEdgeCapacities(), mappingConverter);
//        writer.endNode();
//        writer.startNode("nodecapacities");
//        mappingConverter.setDefaultValue(Integer.MAX_VALUE);
//        mappingConverter.setDomain(problem.getNetwork().nodes());
//        context.convertAnother(problem.getNodeCapacities(), mappingConverter);
//        writer.endNode();
//        writer.startNode("transitTimes");
//        mappingConverter.setDefaultValue(1);
//        mappingConverter.setDomain(problem.getNetwork().edges());
//        context.convertAnother(problem.getTransitTimes(), mappingConverter);
//        writer.endNode();
//        writer.startNode("assignment");
//        mappingConverter.setDefaultValue(0);
//        mappingConverter.setDomain(problem.getNetwork().nodes());
//        context.convertAnother(problem.getCurrentAssignment(), mappingConverter);
//        writer.endNode();
		
		writer.startNode("sources");
		for (Node n : problem.getSources ()) {
			writer.startNode("source");
	        context.convertAnother (n);
			writer.endNode ();
		}
        writer.endNode();
		writer.startNode("mapping");
        context.convertAnother(problem.getZToGraphMapping ());
        writer.endNode();
		writer.startNode("supersink");
        context.convertAnother(problem.getSupersink ());
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		NetworkFlowModel nf = new NetworkFlowModel( (ZToGraphRasterContainer)null );
		
        // Read the network
        reader.moveDown();
        DirectedGraph network = (DirectedGraph) context.convertAnother(null, DirectedGraph.class, networkConverter);
        reader.moveUp();
        // Read the edge capacities
        reader.moveDown();
        if (reader.getAttribute("default") != null) {
            mappingConverter.setDefaultValue(Integer.parseInt(reader.getAttribute("default")));
        } else {
            mappingConverter.setDefaultValue(1);
        }
        mappingConverter.setDomain(network.edges());
        IdentifiableIntegerMapping<Edge> edgeCapacities = (IdentifiableIntegerMapping<Edge>) context.convertAnother(null, IdentifiableIntegerMapping.class, mappingConverter);
        reader.moveUp();
        // Read the node capacities
        reader.moveDown();
        if (reader.getAttribute("default") != null) {
            mappingConverter.setDefaultValue(Integer.parseInt(reader.getAttribute("default")));
        } else {
            mappingConverter.setDefaultValue(Integer.MAX_VALUE);
        }
        mappingConverter.setDomain(network.nodes());
        IdentifiableIntegerMapping<Node> nodeCapacities = (IdentifiableIntegerMapping<Node>) context.convertAnother(null, IdentifiableIntegerMapping.class, mappingConverter);
        reader.moveUp();
        // Read the transit times
        reader.moveDown();
        if (reader.getAttribute("default") != null) {
            mappingConverter.setDefaultValue(Integer.parseInt(reader.getAttribute("default")));
        } else {
            mappingConverter.setDefaultValue(1);
        }
        mappingConverter.setDomain(network.edges());
        IdentifiableIntegerMapping<Edge> transitTimes = (IdentifiableIntegerMapping<Edge>) context.convertAnother(null, IdentifiableIntegerMapping.class, mappingConverter);
        reader.moveUp();
        // Read the assignment
        reader.moveDown();        
        if (reader.getAttribute("default") != null) {
            mappingConverter.setDefaultValue(Integer.parseInt(reader.getAttribute("default")));
        } else {
            mappingConverter.setDefaultValue(0);
        }
        mappingConverter.setDomain(network.nodes());
        IdentifiableIntegerMapping<Node> assignment = (IdentifiableIntegerMapping<Node>) context.convertAnother(null, IdentifiableIntegerMapping.class, mappingConverter);
        reader.moveUp();
		
		// Read the sources
        reader.moveDown();
		// Direct conversion of the LinkedList did not work correctly
        LinkedList<Node> sources = new LinkedList<Node> ();
		while (reader.hasMoreChildren ()) {
			reader.moveDown ();
			sources.add ((Node)context.convertAnother (null, Node.class));
			reader.moveUp ();
		}
        reader.moveUp();
		// Read the ZToGraphMapping
        reader.moveDown();        
        ZToGraphMapping zmapping = (ZToGraphMapping) context.convertAnother(null, ZToGraphMapping.class);
        reader.moveUp();
		// Read the supersink
        reader.moveDown();        
        Node supersink = (Node) context.convertAnother(null, Node.class);
        reader.moveUp();
		
//		nf.setNetwork( network );
//		nf.setEdgeCapacities(edgeCapacities);
//		nf.setNodeCapacities(nodeCapacities);
//		nf.setTransitTimes(transitTimes);
//		nf.setCurrentAssignment(assignment);
//		nf.setSources(sources);
//		nf.setZToGraphMapping(zmapping);
//		nf.setSupersink(supersink);
		return nf;	
	}
}
