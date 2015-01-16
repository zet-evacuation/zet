/**
 * GraphConverter.java
 * Created: 29.06.2010 16:48:10
 */
package de.tu_berlin.math.coga.graph.io.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import de.tu_berlin.coga.graph.Edge;
import org.zetool.container.mapping.IdentifiableDoubleMapping;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.graph.DefaultDirectedGraph;
import de.tu_berlin.coga.graph.DirectedGraph;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphConverter implements Converter {

	private HierarchicalStreamReader reader;
	private MarshallingContext context;
	private UnmarshallingContext uncontext;
	private XMLData xmlData;
	private HierarchicalStreamWriter writer;

	public GraphConverter( XMLData xmlData ) {
		this.xmlData = xmlData;
	}

	/**
	 * {@inheritDoc }
	 * @param type the type of the class that is to be converted
	 * @return {@code true} if the instance is of the type {@link AbstractNetwork}
	 */
	@Override
	public boolean canConvert( Class type ) {
		return DirectedGraph.class.isAssignableFrom( type ); // type.equals( AbstractNetwork.class );
	}

	@Override
	public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
		DirectedGraph graph = (DirectedGraph) source;
		this.writer = writer;
		// automatically done before. either by xstream or by GraphViewConverter
		//writer.startNode("graph");
		writer.addAttribute( "n", Integer.toString( graph.nodeCount() ) );
		writer.addAttribute( "m", Integer.toString( graph.edgeCount() ) );
		
		for( Node node : graph.nodes() )
			convertNode( node );
		for( Edge edge : graph.edges() )
			convertEdge( edge );
		// same as above
		//writer.endNode();

	}

	protected void convertNode( Node node ) {
		//NodeAttributes attributes = graphView.getNodeAttributes( node );
		writer.startNode( "node" );
		writer.addAttribute( "id", Integer.toString( node.id() ) );
		if( xmlData.containsSupplies() )
			if( xmlData.suppliesIntegral.get( node ) != 0 )
				writer.addAttribute( "balance", Integer.toString( xmlData.suppliesIntegral.get( node ) ) );
		writer.endNode();
	}

	protected void convertEdge( Edge edge ) {
		writer.startNode( "edge" );
		writer.addAttribute( "id", Integer.toString( edge.id() ) );
		writer.addAttribute( "start", Integer.toString( edge.start().id() ) );
		writer.addAttribute( "end", Integer.toString( edge.end().id() ) );
		if( xmlData.containsEdgeCapacities() )
			if( xmlData.edgeCapacitiesIntegral.get( edge ) != 1 )
				writer.addAttribute( "capacity", Integer.toString( xmlData.edgeCapacitiesIntegral.get( edge ) ) );
		if( xmlData.containsTransitTimes() )
			if( xmlData.transitTimesIntegral.get( edge ) != 1 )
				writer.addAttribute( "transitTime", Integer.toString( xmlData.transitTimesIntegral.get( edge ) ) );
		writer.endNode();
	}

	@Override
	public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
		DefaultDirectedGraph graph = new DefaultDirectedGraph( 0, 0 );
		int nid = 0;
		int eid = 0;
		this.reader = reader;
		this.uncontext = context;


		int nodeCount = -1;
		int edgeCount = -1;
		// check, if node has number of vertices and edges
		final Iterator i = reader.getAttributeNames();
		while( i.hasNext() ) {
			Object name = i.next();
			if( name.equals( "n" ) )
				nodeCount = Integer.parseInt( reader.getAttribute( "n" ) );
			else if( name.equals( "m" ) )
				edgeCount = Integer.parseInt( reader.getAttribute( "m" ) );
		}

		xmlData.edgeCapacities = new IdentifiableDoubleMapping<>( edgeCount > 0 ? edgeCount : 10 );
		xmlData.nodeCapacities = new IdentifiableDoubleMapping<>( nodeCount > 0 ? nodeCount : 10 );
		xmlData.transitTimes = new IdentifiableDoubleMapping<>( edgeCount > 0 ? edgeCount : 10 );
		xmlData.supplies = new IdentifiableDoubleMapping<>( nodeCount > 0 ? nodeCount : 10 );
//		xmlData.edgeCapacitiesIntegral = new IdentifiableIntegerMapping<Edge>( edgeCount > 0 ? edgeCount : 10 );
//		xmlData.nodeCapacitiesIntegral = new IdentifiableIntegerMapping<Node>( nodeCount > 0 ? nodeCount : 10 );
//		xmlData.transitTimesIntegral = new IdentifiableIntegerMapping<Edge>( edgeCount > 0 ? edgeCount : 10 );
//		xmlData.suppliesIntegral = new IdentifiableIntegerMapping<Node>( nodeCount > 0 ? nodeCount : 10 );

		xmlData.sources = new ArrayList<>();
		xmlData.sinks = new ArrayList<>();

		while( reader.hasMoreChildren() ) {
			reader.moveDown();
			if( reader.getNodeName().equals( "node" ) )
				readNode( graph, nid++ );
			else if( reader.getNodeName().equals( "sink" ) )
				readSink( graph, nid++ );
			else if( reader.getNodeName().equals( "source" ) )
				readSource( graph, nid++ );
			else if( reader.getNodeName().equals( "edge" ) )
				readEdge( graph, eid++ );
			reader.moveUp();
		}

		Collection<Node> n = xmlData.nodes.values();
		Collection<Edge> e = xmlData.edges.values();
		if( nodeCount < 0 )
			nodeCount = n.size();
		if( edgeCount < 0 )
			edgeCount = e.size();
		graph.setNodeCapacity( nodeCount );
		graph.setEdgeCapacity( edgeCount );
		if( nodeCount < n.size() )
			throw new InvalidFileFormatException( "Number of nodes to large: " + n.size() );
		graph.setNodes( n );
		if( nodeCount < n.size() )
			throw new InvalidFileFormatException( "Number of edges to large: " + e.size() );
		graph.setEdges( e );

		xmlData.network = graph;

		return graph;
	}

	protected Node readNode( DefaultDirectedGraph graph, int nid ) {
		Node node;
		String id = null;
		String balance = "0";
		String capacity = "0";
		for( Iterator i = reader.getAttributeNames(); i.hasNext(); ) {
			Object name = i.next();
			if( name.equals( "id" ) )
				id = reader.getAttribute( "id" );
			else if( name.equals( "balance" ) )
				balance = reader.getAttribute( "balance" );
			else if( name.equals( "capacity" ) )
				capacity = reader.getAttribute( "capacity" );
		}
		node = new Node( nid );
		xmlData.nodes.put( id, node );
		double balanceVal = Double.parseDouble( balance );
		xmlData.supplies.add( node, balanceVal );
		double minCapacity = Math.abs( balanceVal ); // make sure, the capacity is at least the supply or demand
		xmlData.nodeCapacities.add( node, Math.max( minCapacity, Double.parseDouble( capacity ) ) );

		if( balanceVal > 0 )
			xmlData.sources.add( node );
		else if( balanceVal < 0 )
			xmlData.sinks.add( node );
		return node;
	}

	protected void readSink( DefaultDirectedGraph graph, int nid ) {
		Node node = readNode( graph, nid );
		//if( xmlData.suppliesIntegral.get( node ) > 0 )
		if( xmlData.supplies.get( node ) > 0 )
			throw new InvalidFileFormatException( "Positive supply for a sink node." );
		// handle special case: a node is marked as sink in the xml-file but no suppliesIntegral are set.
		//if( xmlData.suppliesIntegral.get( node ) == 0 )
		if( xmlData.supplies.get( node ) == 0 )
			xmlData.sinks.add( node );
	}

	protected void readSource( DefaultDirectedGraph graph, int nid ) {
		Node node = readNode( graph, nid );
		//if( xmlData.suppliesIntegral.get( node ) < 0 )
		if( xmlData.supplies.get( node ) < 0 )
			throw new InvalidFileFormatException( "Negative supply for a source node." );
		// handle special case: a nodeis marked as source in the xml-file but no suppliesIntegral are set.
		//if( xmlData.suppliesIntegral.get( node ) == 0 )
		if( xmlData.supplies.get( node ) == 0 )
			xmlData.sources.add( node );
	}

	protected void readEdge( DefaultDirectedGraph graph, int eid ) {
		String id = null;
		String source = null;
		String target = null;
		String capacity = "1.0";
		String transitTime = "1.0";
		for( Iterator i = reader.getAttributeNames(); i.hasNext(); ) {
			Object name = i.next();
			if( name.equals( "id" ) )
				id = reader.getAttribute( "id" );
			else if( name.equals( "start" ) )
				source = reader.getAttribute( "start" );
			else if( name.equals( "end" ) )
				target = reader.getAttribute( "end" );
			else if( name.equals( "capacity" ) )
				capacity = reader.getAttribute( "capacity" );
			else if( name.equals( "transitTime" ) )
				transitTime = reader.getAttribute( "transitTime" );
		}
		Edge edge = new Edge( eid, xmlData.nodes.get( source ), xmlData.nodes.get( target ) );
		// TODO only integral transit times here!
		//xmlData.transitTimesIntegral.add( edge, (int) Double.parseDouble( transitTime ) );
		xmlData.transitTimes.add( edge, Double.parseDouble( transitTime ) );
		//xmlData.edgeCapacitiesIntegral.add( edge, (int) Double.parseDouble( capacity ) );
		xmlData.edgeCapacities.add( edge, Double.parseDouble( capacity ) );
		xmlData.edges.put( id, edge );
	}
}
