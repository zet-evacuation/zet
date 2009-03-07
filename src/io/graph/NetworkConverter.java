/*
 * NetworkConverter.java
 *
 */

package io.graph;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import ds.graph.Edge;
import ds.graph.HidingSet;
import ds.graph.Network;
import ds.graph.Node;

/**
 *
 */
public class NetworkConverter implements Converter {
    
    protected HidingNodeSetConverter nodesConverter;
    protected HidingEdgeSetConverter edgesConverter;

    public NetworkConverter() {
        nodesConverter = new HidingNodeSetConverter();
        edgesConverter = new HidingEdgeSetConverter();
    }
    
    public boolean canConvert(Class type) {
        return type.equals(Network.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Network network = (Network) source;
        writer.startNode("nodes");
        context.convertAnother(network.nodes(), nodesConverter);
        writer.endNode();
        writer.startNode("edges");
        context.convertAnother(network.edges(), edgesConverter);
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Network result = new Network(0, 0);
        reader.moveDown();
        HidingSet<Node> nodes = (HidingSet<Node>) context.convertAnother(result, HidingSet.class, nodesConverter);
        result.setNodeCapacity(nodes.getCapacity());
        result.setNodes(nodes);
        reader.moveUp();
        reader.moveDown();
        edgesConverter.setNodes(nodes);
        HidingSet<Edge> edges = (HidingSet<Edge>) context.convertAnother(result, HidingSet.class, edgesConverter);
        result.setEdgeCapacity(edges.getCapacity());
        result.setEdges(edges);
        reader.moveUp();
        return result;
    }

}
