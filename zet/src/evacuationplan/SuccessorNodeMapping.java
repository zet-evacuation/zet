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

package evacuationplan;
import ds.graph.flow.FlowOverTimePath;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.graph.Edge;
import java.util.HashMap;


/**
 * Contains a mapping from nodes to their successornodes in a path
 */
public class SuccessorNodeMapping {
    private HashMap<Node,Node> successorMapping;
    
    /**
     * 
     * @param dynamicPathFlow
     * reads out the dynamicPathFlow and stores the information about the successornodes 
     */
    
    public SuccessorNodeMapping (FlowOverTimePath dynamicPathFlow) {
        successorMapping = new HashMap<Node,Node>();
        for (Edge e: dynamicPathFlow.edges()){
            successorMapping.put(e.start(), e.end());
        }
        successorMapping.put(dynamicPathFlow.lastEdge().end(), null);
    }
    
    /**
     * 
     * @param node a node
     * @return the successornode of the given node
     * @return null if the given node is the last node in the path and so has no successor
     * @throws java.lang.IllegalArgumentException if the path doesn`t contain the given node
     */
    public Node getSuccessor(Node node) throws IllegalArgumentException {
        if (successorMapping.containsKey(node)) {
            return successorMapping.get(node);
        }
        else throw new IllegalArgumentException("This path doesn`t contain the node "+node+" or something else went wrong.");
    }
    
    public boolean isDefinedFor(Node node){
    	return successorMapping.containsKey(node);
    }
   

}
