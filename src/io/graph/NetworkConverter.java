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

package io.graph;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.zetool.graph.Edge;
import org.zetool.container.collection.HidingSet;
import org.zetool.graph.Node;
import org.zetool.graph.DefaultDirectedGraph;
import org.zetool.graph.DirectedGraph;

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
        return type.equals(DirectedGraph.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        DirectedGraph network = (DirectedGraph) source;
        writer.startNode("nodes");
        context.convertAnother(network.nodes(), nodesConverter);
        writer.endNode();
        writer.startNode("edges");
        context.convertAnother(network.edges(), edgesConverter);
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        DefaultDirectedGraph result = new DefaultDirectedGraph(0, 0);
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
