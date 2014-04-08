/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph.network;

import ds.graph.DirectedGraph;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface  NetworkInterface extends DirectedGraph {

	@Override
	public default boolean isDirected() {
		return true;
	}
	
}
