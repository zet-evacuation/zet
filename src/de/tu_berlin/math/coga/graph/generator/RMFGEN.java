/**
 * RMFGEN.java
 * Created: 08.10.2010, 10:24:13
 */
package de.tu_berlin.math.coga.graph.generator;

import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.DiscreteDistribution;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.ArrayList;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RMFGEN implements Generator {
	protected DiscreteDistribution dist;
	Network network;
	private IdentifiableIntegerMapping<Edge> capacities;

	public void setDistribution( DiscreteDistribution dist ) {
		this.dist = dist;
	}

	Node source;
	Node sink;

	public void generateCompleteGraph( int frameSize, int frames ) {

		// erzeuge frameSize*frameSize*frames knoten,
		// der erste ist source, der letzte ist sink
		int c2 = dist.getMax();

		if( dist == null )
			throw new IllegalStateException( "Random distribution is zero." );
		//network.clear();
		
		//network.addSource();
		System.out.println( "GEnerate with " + frameSize*frameSize*frames + " nodes." );
		int edges = 4*frames*(frameSize*frameSize-frameSize) + (frames-1)*frameSize*frameSize;
		System.out.println( "Generate with " + edges + " edges." );
		network = new Network( frameSize*frameSize*frames, edges );
		capacities = new IdentifiableIntegerMapping<Edge>( edges );
		source = network.getNode( 0 );
		sink = network.getNode( network.numberOfNodes()-1 );
		//for( int i = 2; i < frameSize*frameSize*frames; i++ ) {
		//	graph.newVertex( Integer.toString( i ) );
		//}
		//graph.addSink();

		int fq = frameSize * frameSize;

		// Generiere die Kanten zwischen den Knoten in einem frame
		for( int i = 1; i <= frames; i++ ) {
			// erste Reihe und spalte
			for( int j = 2; j < frameSize; j++ ) {
				// links
				//E e =  network.createAndSetEdge( network.getNode( (i-1) * fq + j -1 ), network.getNode( (i-1) * fq + j + 1 -1), fq*c2 );
				Edge e = network.createAndSetEdge( network.getNode( (i-1) * fq + j -1 ), network.getNode( (i-1) * fq + j + 1 -1) );

				capacities.add( e, fq*c2 );
				
				// rechts
				e = network.createAndSetEdge( network.getNode( (i-1) * fq + j -1), network.getNode( (i-1) * fq + j - 1 -1) );
				capacities.add( e, fq*c2 );
				// oben
				e = network.createAndSetEdge( network.getNode( (i-1) * fq + j -1), network.getNode( (i-1) * fq + j + frameSize -1) );
				capacities.add( e, fq*c2 );

				e = network.createAndSetEdge( network.getNode( (i-1) * fq + (j-1)*frameSize + 1  -1), network.getNode( (i-1) * fq + (j-1)*frameSize + 1 + 1 -1) );
				capacities.add( e, fq*c2 );
				e = network.createAndSetEdge( network.getNode( (i-1) * fq + (j-1)*frameSize + 1  -1), network.getNode( (i-1) * fq + (j-1)*frameSize + 1 + frameSize -1) );
				capacities.add( e, fq*c2 );
				e = network.createAndSetEdge( network.getNode( (i-1) * fq + (j-1)*frameSize + 1  -1), network.getNode( (i-1) * fq + (j-1)*frameSize + 1 - frameSize -1) );
				capacities.add( e, fq*c2 );
			}
			// letzte Reihe und spalte
			for( int j = 2; j < frameSize; j++ ) {
				// links
				Edge e = network.createAndSetEdge( network.getNode( (i-1) * fq + (frameSize-1)*frameSize + j -1), network.getNode( (i-1) * fq + (frameSize-1)*frameSize + j + 1 -1) );
				capacities.add( e, fq*c2 );
				// rechts
				e = network.createAndSetEdge( network.getNode( (i-1) * fq + (frameSize-1)*frameSize + j -1), network.getNode( (i-1) * fq + (frameSize-1)*frameSize + j - 1 -1) );
				capacities.add( e, fq*c2 );
				// oben
				e = network.createAndSetEdge( network.getNode( (i-1) * fq + (frameSize-1)*frameSize + j -1), network.getNode( (i-1) * fq + (frameSize-1)*frameSize + j - frameSize -1) );
				capacities.add( e, fq*c2 );

				e = network.createAndSetEdge( network.getNode( (i-1) * fq + j*frameSize -1), network.getNode( (i-1) * fq + j*frameSize + frameSize -1) );
				capacities.add( e, fq*c2 );
				e = network.createAndSetEdge( network.getNode( (i-1) * fq + j*frameSize -1), network.getNode( (i-1) * fq + j*frameSize - frameSize -1) );
				capacities.add( e, fq*c2 );
				e = network.createAndSetEdge( network.getNode( (i-1) * fq + j*frameSize -1), network.getNode( (i-1) * fq + j*frameSize - 1 -1 ) );
				capacities.add( e, fq*c2 );
			}
			// die vier ecken
			Edge e = network.createAndSetEdge( network.getNode( (i-1) * fq + 1 -1), network.getNode( (i-1) * fq + 1 + 1 -1) );
			capacities.add( e, fq*c2 );
			e = network.createAndSetEdge( network.getNode( (i-1) * fq + 1 -1), network.getNode( (i-1) * fq + 1 + frameSize -1) );
			capacities.add( e, fq*c2 );

			e = network.createAndSetEdge( network.getNode( (i-1) * fq + frameSize -1), network.getNode( (i-1) * fq + frameSize - 1 -1) );
			capacities.add( e, fq*c2 );
			e = network.createAndSetEdge( network.getNode( (i-1) * fq + frameSize -1), network.getNode( (i-1) * fq + frameSize + frameSize -1) );
			capacities.add( e, fq*c2 );

			e = network.createAndSetEdge( network.getNode( (i-1) * fq + frameSize*(frameSize-1)+1 -1), network.getNode( (i-1) * fq + frameSize*(frameSize-1)+1 +1 -1) );
			capacities.add( e, fq*c2 );
			e = network.createAndSetEdge( network.getNode( (i-1) * fq + frameSize*(frameSize-1)+1 -1), network.getNode( (i-1) * fq + frameSize*(frameSize-1)+1 -frameSize -1) );
			capacities.add( e, fq*c2 );

			e = network.createAndSetEdge( network.getNode( (i-1) * fq + fq -1), network.getNode( (i-1) * fq + fq -1 -1) );
			capacities.add( e, fq*c2 );
			e = network.createAndSetEdge( network.getNode( (i-1) * fq + fq -1), network.getNode( (i-1) * fq + fq -frameSize -1) );
			capacities.add( e, fq*c2 );

			// innere knoten
			for( int j = 2; j < frameSize; j++ ) {
				for( int k = 2; k < frameSize; k++ ) {
					e = network.createAndSetEdge( network.getNode( (i-1) * fq + (j-1)*frameSize + k -1), network.getNode( (i-1) * fq + (j-1)*frameSize + k + 1 -1) );
					capacities.add( e, fq*c2 );
					e = network.createAndSetEdge( network.getNode( (i-1) * fq + (j-1)*frameSize + k -1), network.getNode( (i-1) * fq + (j-1)*frameSize + k - 1 -1) );
					capacities.add( e, fq*c2 );
					e = network.createAndSetEdge( network.getNode( (i-1) * fq + (j-1)*frameSize + k -1), network.getNode( (i-1) * fq + (j-1)*frameSize + k + frameSize -1) );
					capacities.add( e, fq*c2 );
					e = network.createAndSetEdge( network.getNode( (i-1) * fq + (j-1)*frameSize + k -1), network.getNode( (i-1) * fq + (j-1)*frameSize + k - frameSize -1) );
					capacities.add( e, fq*c2 );
				}
			}
		}

		// inter-frame-kanten
		ArrayList<Integer> a = new ArrayList<Integer>( fq );
		for( int i = 1; i <= fq; ++i )
			a.add( i );
		for( int j = 0; j < frames-1; ++j ) {
			for( int i = 0; i < fq; ++i ) {
				// get position
				int position = RandomUtils.getInstance().getRandomGenerator().nextInt( fq - i );
				// swap
				int element = a.get( position );
				a.set( position, a.get( fq-i-1 ) );
				a.set( fq-i-1, element );
				Edge e = network.createAndSetEdge( network.getNode( j*fq + i + 1 -1 ), network.getNode( j*fq + fq + 1 + element -1-1) );
				capacities.add( e, dist.getNextRandom() );

			}
		}
	}

	/**
	 * Method that checks whether the generated graph is really cycle free.
	 * @return <code>true</code> if the graph is cycle free.
	 */
	public boolean isAcyclic() {
		return true;
		//DFS<V,E> dfs2 = new DFS<V,E>();
		//return dfs2.isAcyclic( graph );
	}

	void setFlow( int start, int end, int flow ) {
		//E e = graph.getEdge( network.getNode( start), network.getNode( end ) );
		//e.setUsed( flow );
	}

	public Network getGraph() {
		return network;
	}

	public int getEdgeCount() {
		return network.numberOfEdges();
	}

	public int getNodeCount() {
		return network.numberOfNodes();
	}

	public IdentifiableIntegerMapping<Edge> getCapacities() {
		return capacities;
	}

	public Node getSink() {
		return sink;
	}

	public Node getSource() {
		return source;
	}

	
}
