/*
 * PathBasedFlow.java
 * 
 */

package ds.graph.flow;

import ds.graph.flow.StaticPathFlow;
import java.util.Iterator;
import java.util.Vector;

/**
 * The <code>PathBasedFlow</code> class represents a static flow in a path based representation.
 * The static flow is stored as a <code>Vector</code> of {@link StaticPathFlow} objects.
 */
public class PathBasedFlow implements Iterable<StaticPathFlow>{
    
	/**
	 * The static path flows belonging to this <code>PathBasedFlow</code>.
	 */
    Vector<StaticPathFlow> staticPathFlows;
    
    /**
     * Creates a new <code>DynamicFlow</code> object without any path flows.
     */
    public PathBasedFlow(){
    	staticPathFlows = new Vector<StaticPathFlow>();
    }
    
    /**
     * Adds a path flow to this dynamic flow.
     * @param pathFlow the path flow to be add.
     */
    public void addPathFlow(StaticPathFlow staticPathFlow){
        if (staticPathFlow != null)
            staticPathFlows.add(staticPathFlow);
    }
    
    /**
     * Returns an iterator to iterate over the <code>StaticPathFlows</code> 
     * contained in this <code>PathBasedFlow</Code>.
     * @return an iterator to iterate over the <code>StaticPathFlows</code> 
     * contained in this <code>PathBasedFlow</Code>.

     */
    @Override
    public Iterator<StaticPathFlow> iterator(){
    	return staticPathFlows.iterator();
    }
    
    /**
     * Returns a String containing a description of all 
     * contained <code>StaticPathFlows</code>.
     * @return a String containing a description of all 
     * contained <code>StaticPathFlows</code>.
     */
    @Override
    public String toString(){
    	String result = "[\n";
    	for (StaticPathFlow pathFlow : staticPathFlows){
    		result += " " + pathFlow.toString() + "\n";
    	}
    	result += "]";
    	return result;
    }
    
}