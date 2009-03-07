/*
 * PathBasedFlowOverTime.java
 * 
 */

package ds.graph.flow;

import java.util.Iterator;
import java.util.Vector;

/**
 * The <code>PathBasedFlowOverTime</code> class represents a dynamic flow in a path based representation.
 * The dynamic flow is stored as a <code>Vector</code> of {@link FlowOverTimePath} objects.
 */
public class PathBasedFlowOverTime implements Iterable<FlowOverTimePath>{
    
	/**
	 * The path flows belonging to this <code>PathBasedFlowOverTime</code>.
	 */
    Vector<FlowOverTimePath> pathFlows;
    
    /**
     * Creates a new <code>PathBasedFlowOverTime</code> object without any path flows.
     */
    public PathBasedFlowOverTime(){
        pathFlows = new Vector<FlowOverTimePath>();
    }
    
    /**
     * Adds a path flow to this dynamic flow.
     * @param pathFlow the path flow to be add.
     */
    public void addPathFlow(FlowOverTimePath pathFlow){
        if (pathFlow != null)
            pathFlows.add(pathFlow);
    }

    public void remove(FlowOverTimePath pathFlow){
            pathFlows.remove(pathFlow);
    }    
    
    /**
     * Returns an iterator to iterate over the <code>DynamicPathFlows</code> 
     * contained in this <code>PathBasedFlowOverTime</Code>.
     * @return an iterator to iterate over the <code>DynamicPathFlows</code> 
     * contained in this <code>PathBasedFlowOverTime</Code>.
     */
    @Override
    public Iterator<FlowOverTimePath> iterator(){
    	return pathFlows.iterator();
    }
    
    /**
     * Returns a String containing a description of all 
     * contained <code>DynamicPathFlows</code>.
     * @return a String containing a description of all 
     * contained <code>DynamicPathFlows</code>.
     */
    @Override
    public String toString(){
    	String result = "[\n";
    	for (FlowOverTimePath pathFlow : pathFlows){
    		result += " " + pathFlow.toString() + "\n";
    	}
    	result += "]";
    	return result;
    }
    
}
