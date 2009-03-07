package util;

import java.util.LinkedList;

import util.DebugFlags;
import algo.graph.traverse.DFS;
import algo.graph.traverse.DFS.State;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.ListSequence;
import ds.graph.Network;
import ds.graph.Node;

/**
 * This class provides a method to check a supplies mapping for a given network.
 * The method tests whether all sources in the network are connected to sinks.
 * If a source is found that is not connected to any sink, the method will
 * delete the source if there is only one sink. If there are more sinks,
 * it cannot be decided where to subtract the corresponding need.
 * In this case an exception is thrown.
 */
public class GraphInstanceChecker {
	
    private ListSequence<Node> sources, sinks;
    private LinkedList<Node> newSources;
    private LinkedList<Node> deletedSources;
    private Network network;
    private IdentifiableIntegerMapping<Node> supplies;
    private IdentifiableIntegerMapping<Node> newSupplies;
    boolean hasRun = false;
    
    /**
     * Creates a new instance of the checker.
     * @param network the network to be checked.
     * @param supplies the supplies of the network.
     */
    public GraphInstanceChecker(Network network,
			IdentifiableIntegerMapping<Node> supplies){
    	this.network = network;
    	this.supplies = supplies;
    }
	    
    /**
     * Returns whether the network has already been checked.
     * @return whether the network has already been checked.
     */
    public boolean hasRun(){
    	return hasRun;
    }
    
    /**
     * If the algorithm has already been called, this method gives the resulting supply mapping.
     * Else an exception is thrown.
     * @return If the algorithm has already been called, this method gives the resulting supply mapping.
     * Else an exception is thrown.
     */
    public IdentifiableIntegerMapping<Node> getNewSupplies(){
    	if (hasRun){
    		return newSupplies;
    	}
    	else{ 
    		throw new AssertionError("supplyChecker has to be called first.");
    	} 
    }
    
    /**
     * If the algorithm has already been called, this method gives the resulting list of sources.
     * Else an exception is thrown.
     * @return If the algorithm has already been called, this method gives the resulting list of sources.
     * Else an exception is thrown.
     */
    public LinkedList<Node> getNewSources(){
    	if (hasRun){
    		return newSources;
    	} else {
    		throw new AssertionError("supplyChecker has to be called first.");
    	}
    }
    
    /**
     * If the algorithm has already been called, this method gives the list of deleted sources.
     * Else an exception is thrown.
     * @return If the algorithm has already been called, this method gives the list of deleted sources.
     * Else an exception is thrown.
     */
    public LinkedList<Node> getDeletedSources(){
    	if (hasRun){
    		return deletedSources;
    	} else {
    		throw new AssertionError("supplyChecker has to be called first.");
    	}
    }
    
   /**
    * This method tests whether all sources in the network are connected to sinks.
    * If a source is found that is not connected to any sink, the method will
    * delete the source if there is only one sink. If there are more sinks,
    * it cannot be decided where to subtract the corresponding need.
    * In this case an exception is thrown.
    * @param network the underlying graph.
    * @param supplies given supplies.
    * @return corrected supplies that do not contain sources that are not
    * connected to any sink.
    */
    public void supplyChecker(){
		
    	/* Find all sources and sinks. */
		sources = new ListSequence<Node>();
		sinks = new ListSequence<Node>();		
		for (Node node : network.nodes()) {
			if (!supplies.isDefinedFor(node))
				throw new AssertionError(
						"There is a node in the network object that has no defined supply according to the supplies-Mapping.");
			else {
				if (supplies.get(node)>0)
					sources.add(node);
				if (supplies.get(node)<0)
					sinks.add(node);
			}
		}
		
		if (DebugFlags.MEL){
			System.out.println("Sources: "+sources);
			System.out.println("Sinks: "+sinks);
		}

		/* A mapping to save which sources can reach a sink. 
		 * We will search inverse (from sinks to sources),
		 * in this scenario each source has to be reachable.
		 */
		IdentifiableObjectMapping<Node,Boolean> reachable = new IdentifiableObjectMapping<Node,Boolean>(sources.size(), Boolean.class);
		int reachableSources = 0;		
		
		/* Go through all sinks. */
		for (Node sink : sinks){
			/* Call depth first search. */
			DFS dfs = new DFS(network);
			dfs.run(sink, true);
			for (Node source: sources){
				/* Check whether the source is reachable.*/
				if (dfs.state(source) == DFS.State.DONE){
					reachableSources++;
					reachable.set(source, true);
				} else reachable.set(source, false);
			}
		}
		
		newSources = sources;
		deletedSources = new LinkedList<Node>();
		
		/* Check whether there are error sources. */
		if (sources.size() > reachableSources){
			if (sinks.size()!=1)
				/* This case can't be repaired automatically if you want to subtract supply and need symmetrically. */
				throw new AssertionError("There are sources that cannot reach any sink, and there are "+sinks.size()+" sinks." +
						"This method can only automatically repairthe network for exactly one sink.");
			/* Now there is only one sink. Set the supply of each non reachable source to zero and
			 * subtract the corresponding value from the need of the sink. */
			newSources = new LinkedList<Node>(); 
			Node sink = sinks.first();
			for (Node source : sources){
				if (! reachable.get(source)){
					supplies.set(sink, supplies.get(sink)+supplies.get(source));
					supplies.set(source, 0);
					deletedSources.add(source);
				} else {
					newSources.add(source);
				}
			}
		}
		
		/* sets the corrected mapping as the new supply mapping. */
		newSupplies = supplies;
		hasRun=true;
	}
    
    /**
     * Checks whether there are no supplies in the network, i. e.
     * true is returned if there are no supplies and demands. 
     * If there are supplies and demands, but supplies=-demands
     * does not hold, an exception is thrown. If supplies=-demands holds
     * and is not zero, false is returned.
     * @param network the network to be checked.
     * @param supplies the supply mapping to be checked.
     * @return
     */
    public static boolean emptySupplies(Network network, IdentifiableIntegerMapping<Node> supplies){
    	int sup = 0, dem = 0;
    	for (Node node : network.nodes()){
    		if (supplies.get(node)>0){
    			sup+=supplies.get(node);
    		}
    		if (supplies.get(node)<0){
    			dem+=supplies.get(node);
    		}
    	}
    	if (sup == -dem && sup > 0){
    		return false;
    	}
    	if (sup == -dem && sup == 0){
    		return true;
    	}
    	if (sup != -dem){
    		throw new AssertionError("Number of supplies and demands is different, supplies: "+sup+", demands: "+dem+".");
    	}
    	throw new RuntimeException("Unknown error in method 'emptySupplies'.");
    }

}
