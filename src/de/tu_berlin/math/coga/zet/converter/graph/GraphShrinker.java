/**
 * GraphShrinker.java Created: 12.10.2012, 17:58:48
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import org.zetool.common.algorithm.Algorithm;
import org.zetool.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.graph.Node;
import ds.graph.NodeRectangle;
import java.util.LinkedList;
import java.util.logging.Level;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class GraphShrinker extends Algorithm<NetworkFlowModel, NetworkFlowModel> {
  protected NetworkFlowModel newModel;
	protected ZToGraphMapping newMapping;
	private IdentifiableCollection<Edge> shrinkedEdges;
	private boolean createReverseEdgesManually;

	public GraphShrinker( boolean createReverseEdgesManually ) {
		this.createReverseEdgesManually = createReverseEdgesManually;
	}
	
	@Override
	protected NetworkFlowModel runAlgorithm( NetworkFlowModel problem ) {
		log.log( Level.INFO, "Shrinking a Graph with {0} nodes and {1} edges.", new Object[]{problem.numberOfNodes(), problem.numberOfEdges()} );

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

		log.log( Level.INFO, "Edges used in shrinked graph: {0}", newModel.numberOfEdges());
		
		checkPlausibility();

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
		NetworkFlowModel oldModel = getProblem();
		
		LinkedList<Edge> reverseEdges = new LinkedList<>();
		for( Edge newEdge : edges ) {
			Edge oldEdge = getProblem().getEdge( newEdge.start(), newEdge.end() );
			newModel.addEdge( newEdge, getProblem().getEdgeCapacity( oldEdge ), getProblem().getTransitTime( oldEdge ), getProblem().getExactTransitTime( oldEdge ) );
			newMapping.setEdgeLevel( newEdge, getProblem().getZToGraphMapping().getEdgeLevel( oldEdge ) );
		
			// add reverse edges
			if( !newEdge.isIncidentTo( newModel.getSupersink() ) )
				reverseEdges.add( oldModel.getEdge( newEdge.end(), newEdge.start() ) );
		}
		int edgeNumber = newModel.numberOfEdges();
		for( Edge oldReverse : reverseEdges ) {
				Edge newReverse = new Edge( edgeNumber++, oldReverse.start(), oldReverse.end() );
				newModel.addEdge( newReverse, getProblem().getEdgeCapacity( oldReverse ), getProblem().getTransitTime( oldReverse ), getProblem().getExactTransitTime( oldReverse ) );
				newMapping.setEdgeLevel( newReverse, getProblem().getZToGraphMapping().getEdgeLevel( oldReverse ) );			
		}
	}

	/**
	 * Copies the information in the old mapping to the new mapping.
	 */
	private void copyMappingInformation() {
		//values from mapping of original network 
		newMapping.raster = getProblem().getZToGraphMapping().getRaster();
		newMapping.nodeRectangles = getProblem().getZToGraphMapping().getNodeRectangles();
		newMapping.nodeFloorMapping = getProblem().getZToGraphMapping().getNodeFloorMapping();
		newMapping.isDeletedSourceNode = getProblem().getZToGraphMapping().isDeletedSourceNode;
		newMapping.exitName = getProblem().getZToGraphMapping().exitName;
		//if( createReverseEdgesManually )
		//	BaseZToGraphConverter.createReverseEdges( newModel );
		newModel.resetAssignment();
	}

	private void checkPlausibility() {
		log.info( "Check plausibility" );
		
		NetworkFlowModel oldModel = getProblem();
		
		for( Edge newEdge :  newModel.edges() ) {
			// get the same edges in the old model
			Edge oldEdge = oldModel.getEdge( newEdge.start(), newEdge.end() );
			
			int newTransit = newModel.getTransitTime( newEdge );
			int oldTransit = oldModel.getTransitTime( oldEdge );
			
			if( newTransit != oldTransit )
				log.log( Level.WARNING, "newTransit = {0} = {1} = {2}", new Object[]{ newTransit, oldTransit, oldTransit });
			
			assert newTransit == oldTransit;
		}
		
	}
}
