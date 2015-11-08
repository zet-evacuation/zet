/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
/*
 * Test.java
 *
 */

package io.graph;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.zetool.graph.Edge;
import org.zetool.container.collection.HidingSet;
import org.zetool.graph.Node;

/**
 *
 */
public class HidingEdgeSetConverter implements Converter {
    
    protected HidingSet<Node> nodes;

    public boolean canConvert(Class type) {
        return type.equals(HidingSet.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        HidingSet<Edge> set = (HidingSet<Edge>) source;
        writer.addAttribute("number", String.valueOf(set.getCapacity()));
        if (set.size() == set.getCapacity()) {
            for (Edge edge : set) {
                if (edge == null) throw new AssertionError("Hey! HidingSet told me it didn't contain any null edges!");
                writer.startNode("edge");
                writer.addAttribute("start", String.valueOf(edge.start()));
                writer.addAttribute("end", String.valueOf(edge.end()));
                if (set.isHidden(edge)) {
                    writer.addAttribute("hidden", String.valueOf(set.isHidden(edge)));
                }
                writer.endNode();
            }
        } else {
            for (Edge edge : set) {
                if (edge == null) continue;
                writer.startNode("edge");
                writer.addAttribute("id", String.valueOf(edge.id()));                
                writer.addAttribute("start", String.valueOf(edge.start()));
                writer.addAttribute("end", String.valueOf(edge.end()));
                if (set.isHidden(edge)) {
                    writer.addAttribute("hidden", String.valueOf(set.isHidden(edge)));
                }
                writer.endNode();
            }            
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        int size = Integer.parseInt(reader.getAttribute("number"));
        HidingSet<Edge> result = new HidingSet<Edge>(Edge.class, size);
        int id = 0;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getAttribute("id") != null) {
                id = Integer.parseInt(reader.getAttribute("id"));
            }
            Node start = nodes.get(Integer.parseInt(reader.getAttribute("start")));
            Node end = nodes.get(Integer.parseInt(reader.getAttribute("end")));
            Edge edge = new Edge(id, start, end);
            boolean hidden = false;
            if (reader.getAttribute("hidden") != null) {
                hidden = Boolean.parseBoolean(reader.getAttribute("hidden"));
            }
            result.add(edge);
            result.setHidden(edge, hidden);
            reader.moveUp();
            id++;
        }
        return result;
    }

    public HidingSet<Node> getNodes() {
        return nodes;
    }

    public void setNodes(HidingSet<Node> nodes) {
        this.nodes = nodes;
    }
    
    
    
}
