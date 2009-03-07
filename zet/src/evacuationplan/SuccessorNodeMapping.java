/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package evacuationplan;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.Node;
import ds.graph.Edge;
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
        for (Edge e: dynamicPathFlow){
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
