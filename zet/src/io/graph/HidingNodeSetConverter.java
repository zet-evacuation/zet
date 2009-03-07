/*
 * HidingSetConverter.java
 *
 */
package io.graph;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import ds.graph.HidingSet;
import ds.graph.Node;

/**
 *
 */
public class HidingNodeSetConverter implements Converter {

    public boolean canConvert(Class type) {
        return type.equals(HidingSet.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        HidingSet<Node> set = (HidingSet<Node>) source;        
        writer.addAttribute("number", String.valueOf(set.getCapacity()));
        for (Node node : set) {
            if (node == null) {
                throw new AssertionError("I didn't expect null in a NodeSet.");
            }
            if (set.isHidden(node)) {
                writer.startNode("node");
                writer.addAttribute("id", String.valueOf(node.id()));
                writer.addAttribute("hidden", String.valueOf(set.isHidden(node)));
                writer.endNode();
            }
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        int size = Integer.parseInt(reader.getAttribute("number"));
        Node[] nodes = new Node[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = new Node(i);
        }
        HidingSet<Node> result = new HidingSet<Node>(nodes);
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            int id = Integer.parseInt(reader.getAttribute("id"));
            boolean hidden = false;
            if (reader.getAttribute("hidden") != null) {
                hidden = Boolean.parseBoolean(reader.getAttribute("hidden"));
            }
            result.setHidden(nodes[id], hidden);
            reader.moveUp();
        }
        return result;
    }
}
