/**
 * GraphShrinker.java Created: 12.10.2012, 17:58:48
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.Node;
import ds.graph.NodeRectangle;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class GraphShrinker extends Algorithm<NetworkFlowModel, NetworkFlowModel> {
  NetworkFlowModel newModel;
	private ZToGraphMapping newMapping;
	private IdentifiableCollection<Edge> shrinkedEdges;
	private boolean createReverseEdgesManually;

	public GraphShrinker( boolean createReverseEdgesManually ) {
		this.createReverseEdgesManually = createReverseEdgesManually;
	}
	
	@Override
	protected NetworkFlowModel runAlgorithm( NetworkFlowModel problem ) {
		log.info( "Number of edges of original graph: " + problem.numberOfEdges() );

		// create a new model
		newModel = new NetworkFlowModel( problem );
		
		// create a new mapping and set up super sink
		Node Super = newModel.getSupersink();
		newMapping = newModel.getZToGraphMapping();
		newMapping.setNodeSpeedFactor( Super, 1 );
		newMapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newMapping.setFloorForNode( Super, -1 );

		// copy node capacities, as nodes are not infected
		copyNodes();
			
		// perform actual shrinking (in inherited classes)
		shrinkedEdges = runEdge();

		// add edges to the new model
		addEdges( shrinkedEdges );

		// copy the remaining information into the new mapping
		copyMappingInformation();

		return newModel;
	}

	/**
	 * The actual algorithm that shrinks the {@code Graph} in the
	 * {@code NetworkFlowModel} in the problem instance. This method has to be
	 * implemented by each overriden shrinker. The result is a set of edges that
	 * is contained in the shrinked {@code Graph}. Mostly this must be a subset
	 * of the original edges.
	 * @return a list of edges remaining in the graph
	 */
	abstract IdentifiableCollection<Edge> runEdge();

	protected void copyNodes() {
		// copy node capacities, as nodes are not infected
		for( Node node : getProblem() )
			if( node.id() != 0 ) {
				newModel.setNodeCapacity( node, getProblem().getNodeCapacity( node ) );
				newMapping.setNodeSpeedFactor( node, getProblem().getZToGraphMapping().getNodeSpeedFactor( node ) );
				newMapping.setNodeUpSpeedFactor( node, getProblem().getZToGraphMapping().getUpNodeSpeedFactor( node ) );
				newMapping.setNodeDownSpeedFactor( node, getProblem().getZToGraphMapping().getDownNodeSpeedFactor( node ) );
			}
	}
	
	/**
	 * This method is called by {@link #runEdge() }. The parameter takes all the
	 * edges remaining in the shrinked graph. These edges are then added to the
	 * {@link NetworkFlowModel}. The edge will have the same transit times and
	 * capacities as the original edge in the graph had. Also, the level information
	 * in the corresponding {@link ZToGraphMapping} is set to the same value for
	 * the edge.
	 * @param edges the edges remaining in the shrinked level. subset of the original edges
	 */
	protected void addEdges( IdentifiableCollection<Edge> edges ) {
		for( Edge edge : edges )
			newModel.addEdge( edge, getProblem().getEdgeCapacity( edge ), getProblem().getTransitTime( edge ), getProblem().getExactTransitTime( edge ) );
	}

	/**
	 * Copies the information in the old mapping to the new mapping.
	 */
	private void copyMappingInformation() {
		for( Edge edge : shrinkedEdges )
			newMapping.setEdgeLevel( edge, getProblem().getZToGraphMapping().getEdgeLevel( edge ) );

		//values from mapping of original network 
		newMapping.raster = getProblem().getZToGraphMapping().getRaster();
		newMapping.nodeRectangles = getProblem().getZToGraphMapping().getNodeRectangles();
		newMapping.nodeFloorMapping = getProblem().getZToGraphMapping().getNodeFloorMapping();
		newMapping.isDeletedSourceNode = getProblem().getZToGraphMapping().isDeletedSourceNode;
		newMapping.exitName = getProblem().getZToGraphMapping().exitName;
		if( createReverseEdgesManually )
			BaseZToGraphConverter.createReverseEdges( newModel );
		newModel.resetAssignment();
	}
}
