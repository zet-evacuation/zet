/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph.network;

import ds.graph.Edge;
import ds.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ResidualNetworkExtended extends ResidualNetwork {
	IdentifiableIntegerMapping<Edge> upper;
	IdentifiableIntegerMapping<Edge> lower;
				
	/**
	 * A constructor for clone and overriding classes.
	 * @param initialNodeCapacity
	 * @param initialEdgeCapacity  
	 */
	protected ResidualNetworkExtended( int initialNodeCapacity, int initialEdgeCapacity ) {
		super( initialNodeCapacity, initialEdgeCapacity );
	}

	/**
	 * Creates a new residual network, based on the specified network, the 
	 * zero flow and the specidied capacities.
	 * @param network the base network for the residual network.
	 * @param capacities the base capacities for the residual network.
	 */
	public ResidualNetworkExtended( AbstractNetwork network, IdentifiableIntegerMapping<Edge> capacities ) {
		super( network, capacities );
		this.upper = capacities;
	}

	public void setLower( IdentifiableIntegerMapping<Edge> lower ) {
		this.lower = lower;
	}

	/**
	 * Updates hidden edges in the residual network. Call this, if the network
	 * has changed and another iteration of a flow algorithm should be called
	 */
	@Override
	public void update() {
		// Update residual capacities
		if( !(network instanceof Network) )
			return; // No hidden edges and no update!
		Network n = (Network)network;
		for( Edge edge : n.allEdges() ) {
			Edge rev = allGetEdge( edge.end(), edge.start() );
			if( rev == null )
				throw new IllegalStateException( "Reverse edge is null! (Hidden?)" );
			if( n.isHidden( edge ) ) { // if an edge in original network is hidden, hide both edges.
				setHidden( edge, true );
				setHidden( rev, true);
				//System.out.println( "Hidden edge found in update: " + edge );
			} else { // otherwise, set up residual capacities
				// Edge is normal edge!
					int cap = upper.get( edge )-flow.get( edge );
					if( cap < 0 )
						throw new IllegalStateException( "Upper capacities to small!" );
					if( upper.get( edge ) == Integer.MAX_VALUE )
						cap = Integer.MAX_VALUE;
					residualCapacities.set( edge, cap );
					if( cap == 0 ) {
						setHidden( edge, true );
				//		System.out.println( "UPDATE: Hiding edge " + edge );
					}
					else
						setHidden( edge, false );
//				if( isReverseEdge( edge ) ) {

					// reverse edge
					cap = flow.get( edge ) - (lower == null ? 0 : lower.get( edge ) );
					if( cap < 0 )
						throw new IllegalStateException( "Lower capacities to high!" );
					//flow.set( edge, cap );
					residualCapacities.set( rev, cap );
					if( cap == 0 ) {
						setHidden( rev, true );
						//System.out.println( "UPDATE: Hiding edge " + edge );
					} else
						setHidden( rev, false );
					
//				} else {
//				}
			}
		}
		
//		for( Edge edge : n.allEdges() ) {
//			Edge rev = allGetEdge( edge.end(), edge.start() );
//			if( rev == null )
//				throw new IllegalStateException( "Reverse edge is null! (Hidden?)" );
//			if( n.isHidden( edge ) ) {
//				setHidden( edge, true );
//				setHidden( rev, true);
//			} else {
//				if( residualCapacities.get( edge ) > 0 )
//					setHidden( edge, false );
//				else
//					setHidden( edge, true );
//				if( residualCapacities.get( rev ) > 0 )
//					setHidden( rev, false );
//				else
//					setHidden( rev, true );
//			}
//		}
			
	}

}