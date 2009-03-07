/*
 * NetworkFlowModelConverter.java
 *
 */

package io.graph;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import converter.ZToGraphMapping;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.NetworkFlowModel;
import ds.graph.Node;
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
    
    public boolean canConvert(Class type) {
        return type.equals(NetworkFlowModel.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        NetworkFlowModel problem = (NetworkFlowModel) source;
				
		writer.startNode("network");
        context.convertAnother(problem.getNetwork(), networkConverter);
        writer.endNode();
        writer.startNode("edgecapacities");
        mappingConverter.setDefaultValue(1);
        mappingConverter.setDomain(problem.getNetwork().edges());
        context.convertAnother(problem.getEdgeCapacities(), mappingConverter);
        writer.endNode();
        writer.startNode("nodecapacities");
        mappingConverter.setDefaultValue(Integer.MAX_VALUE);
        mappingConverter.setDomain(problem.getNetwork().nodes());
        context.convertAnother(problem.getNodeCapacities(), mappingConverter);
        writer.endNode();
        writer.startNode("transitTimes");
        mappingConverter.setDefaultValue(1);
        mappingConverter.setDomain(problem.getNetwork().edges());
        context.convertAnother(problem.getTransitTimes(), mappingConverter);
        writer.endNode();
        writer.startNode("assignment");
        mappingConverter.setDefaultValue(0);
        mappingConverter.setDomain(problem.getNetwork().nodes());
        context.convertAnother(problem.getCurrentAssignment(), mappingConverter);
        writer.endNode();
		
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
		NetworkFlowModel nf = new NetworkFlowModel();
		
        // Read the network
        reader.moveDown();
        Network network = (Network) context.convertAnother(null, Network.class, networkConverter);
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
		
		nf.setNetwork( network );
		nf.setEdgeCapacities(edgeCapacities);
		nf.setNodeCapacities(nodeCapacities);
		nf.setTransitTimes(transitTimes);
		nf.setCurrentAssignment(assignment);
		nf.setSources(sources);
		nf.setZToGraphMapping(zmapping);
		nf.setSupersink(supersink);
		return nf;	
	}
}
