/*
 * GraphViewConverter.java
 *
 */
package zet.xml;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 * @author Martin Groß
 */
public class GraphViewConverter implements Converter {

	private MarshallingContext context;
	private UnmarshallingContext uncontext;
	private GraphView graphView;
	private HierarchicalStreamReader reader;
	private HierarchicalStreamWriter writer;
	protected LinkedHashMap<String, Node> nodes = new LinkedHashMap<String, Node>();
	protected LinkedHashMap<String, Edge> edges = new LinkedHashMap<String, Edge>();
	protected IdentifiableObjectMapping<Node, Vector3> nodePositionMapping = new IdentifiableObjectMapping<Node, Vector3>( 0, Vector3.class );
	IdentifiableIntegerMapping<Edge> edgeCapacities;
	IdentifiableIntegerMapping<Node> nodeCapacities;
	IdentifiableIntegerMapping<Edge> transitTimes;
	IdentifiableIntegerMapping<Node> supplies;
	ArrayList<Node> sources;
	ArrayList<Node> sinks;

	public GraphViewConverter() {
	}

	public boolean canConvert( Class type ) {
		return type.equals( GraphView.class );
	}

	public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
//        this.context = context;
//        this.graphView = (GraphView) source;
//        this.writer = writer;
//        DynamicGraph graph = graphView.getGraph();
//        writer.startNode("graph");
//        for (Node node : graph.nodes()) {
//            convertNode(node);
//        }
//        for (Edge edge : graph.edges()) {
//            convertEdge(edge);
//        }
//        writer.endNode();
//        writer.startNode("layouts");
//        for (int v=0; v<graph.numberOfNodes(); v++) {
//            convertNodeLayout(graphView.getNodeAttributes(graph.getNode(v)),v);
//        }
//        for (int e=0; e<graph.numberOfEdges(); e++) {
//            convertEdgeLayout(graphView.getEdgeAttributes(graph.getEdge(e)));
//        }
//        for (int p=0; p<graphView.getNumberOfNodeLayoutProfiles(); p++) {
//            writer.startNode("nodeprofile");
//            writer.addAttribute("id","nodelayout"+p);
//            convertLayoutProfile(graphView.getNodeLayoutProfile(p));
//            writer.endNode();
//        }
//        for (int p=0; p<graphView.getNumberOfEdgeLayoutProfiles(); p++) {
//            writer.startNode("edgeprofile");
//            writer.addAttribute("id","edgelayout"+p);
//            convertLayoutProfile(graphView.getEdgeLayoutProfile(p));
//            writer.endNode();
//        }
//        writer.startNode("nodeprofile");
//        writer.addAttribute("id","defaultNodeLayout");
//        convertLayoutProfile(graphView.getAttributes().getNodeDefaults());
//        writer.endNode();
//        writer.startNode("edgeprofile");
//        writer.addAttribute("id","defaultEdgeLayout");
//        convertLayoutProfile(graphView.getAttributes().getEdgeDefaults());
//        writer.endNode();
//        writer.endNode();
	}

	public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
		Network graph = new Network( 0, 0 );
		int nid = 0;
		int eid = 0;
		this.reader = reader;
		this.uncontext = context;

		// read <graph>
		// reader starts in <graphLayout>, so set down to get the graph
		reader.moveDown();
		if( !reader.getNodeName().equals( "graph" ) )
			throw new ConversionException( "Graph layout must start with a graph." );

		int nodeCount = -1;
		int edgeCount = -1;
		// check, if node has number of vertices and edges
		final Iterator i = reader.getAttributeNames();
		while(i.hasNext()) {
			Object name = i.next();
			if( name.equals( "n" ) )
				nodeCount = Integer.parseInt( reader.getAttribute( "n" ) );
			else if( name.equals( "m" ) )
				edgeCount = Integer.parseInt( reader.getAttribute( "m" ) );
		}

		edgeCapacities = new IdentifiableIntegerMapping<Edge>( edgeCount > 0 ? edgeCount : 10 );
		nodeCapacities = new IdentifiableIntegerMapping<Node>( nodeCount > 0 ? nodeCount : 10 );
		transitTimes = new IdentifiableIntegerMapping<Edge>( edgeCount > 0 ? edgeCount : 10 );
		supplies = new IdentifiableIntegerMapping<Node>( nodeCount > 0 ? nodeCount : 10 );

		sources = new ArrayList<Node>();
		sinks = new ArrayList<Node>();

		while(reader.hasMoreChildren()) {
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
		reader.moveUp();

		Collection<Node> n = nodes.values();
		Collection<Edge> e = edges.values();
		if( nodeCount < 0 )
			nodeCount = n.size();
		if( edgeCount < 0 )
			edgeCount = e.size();
		graph.setNodeCapacity( nodeCount );
		graph.setEdgeCapacity( edgeCount );
		if( nodeCount < n.size() )
			throw new ConversionException( "Number of nodes to large: " + n.size() );
		graph.setNodes( n );
		if( nodeCount < n.size() )
			throw new ConversionException( "Number of edges to large: " + e.size() );
		graph.setEdges( e );

		nodePositionMapping = new IdentifiableObjectMapping<Node, Vector3>( nodeCount, Vector3.class );
		// assign default position to all nodes
		for( Node node : nodes.values() )
			//graphAttributes.setNodeAttributes( nodes.get( key ), nodeAttributes.get( key ) );
			nodePositionMapping.set( node, new Vector3() );


		// read <layouts>. no other graphs are allowed
		if( reader.hasMoreChildren() ) {
			//while(reader.hasMoreChildren()) {
			reader.moveDown();
			if( reader.getNodeName().equals( "graph" ) )
				throw new ConversionException( "More than one graph is not allowed." );
			else if( reader.getNodeName().equals( "layouts" ) )
				// read the layouts
				readLayouts();
			reader.moveUp();
		}

//		GraphAttributes graphAttributes = new GraphAttributes();
//		graphAttributes.setNodeDefaults( nodeLayoutProfiles.get( "defaultNodeLayout" ) );
//		graphAttributes.setEdgeDefaults( edgeLayoutProfiles.get( "defaultEdgeLayout" ) );


//		for( String key : edges.keySet() )
//			graphAttributes.setEdgeAttributes( edges.get( key ), edgeAttributes.get( key ) );
//		for( NodeAttributes profile : nodeAttributes.values() ) {
//			String basedOn = profile.getType();
//			if( basedOn == null )
//				basedOn = "";
//			if( basedOn.equals( "" ) )
//				basedOn = "defaultNodeLayout";
//			profile.setDefaults( nodeLayoutProfiles.get( basedOn ) );
//		}
//		for( EdgeAttributes profile : edgeAttributes.values() ) {
//			String basedOn = profile.getType();
//			if( basedOn.equals( "" ) )
//				basedOn = "defaultEdgeLayout";
//			profile.setDefaults( edgeLayoutProfiles.get( basedOn ) );
//		}

		graphView = new GraphView( graph, nodePositionMapping, edgeCapacities, nodeCapacities, transitTimes, supplies, sources, sinks );

//		for( NodeAttributes profile : nodeLayoutProfiles.values() ) {
//			if( profile.getID().equals( "defaultNodeLayout" ) )
//				continue;
//			String basedOn = profile.getType();
//			if( basedOn.equals( "" ) )
//				basedOn = "defaultNodeLayout";
//			profile.setDefaults( nodeLayoutProfiles.get( basedOn ) );
//			graphView.addNodeLayoutProfile( profile );
//		}
//		for( EdgeAttributes profile : edgeLayoutProfiles.values() ) {
//			if( profile.getID().equals( "defaultEdgeLayout" ) )
//				continue;
//			String basedOn = profile.getType();
//			if( basedOn.equals( "" ) )
//				basedOn = "defaultEdgeLayout";
//			profile.setDefaults( edgeLayoutProfiles.get( basedOn ) );
//			graphView.addEdgeLayoutProfile( profile );
//		}
//		for( String key : edges.keySet() ) {
//			Edge edge = edges.get( key );
//			EdgeAttributes a = edgeAttributes.get( key );
//			Point2D[] points = a.getPoints();
//			if( points == null )
//				points = new Point2D[0];
//			Point2D[] points2 = new Point2D[points.length + 2];
//			System.arraycopy( points, 0, points2, 1, points.length );
//			points2[0] = new Point2D.Double(
//							graphAttributes.getNodeAttributes( edge.start() ).getBounds().getX(),
//							graphAttributes.getNodeAttributes( edge.start() ).getBounds().getY() );
//			points2[points2.length - 1] = new Point2D.Double(
//							graphAttributes.getNodeAttributes( edge.end() ).getBounds().getX(),
//							graphAttributes.getNodeAttributes( edge.end() ).getBounds().getY() );
//			Point2D[] points3 = new Point2D[points2.length + 2];
//			points3[0] = points2[0];
//			System.arraycopy( points2, 1, points3, 2, points.length );
//			points3[points3.length - 1] = points2[points2.length - 1];
//			points3[1] = NodeShape.circleNode(
//							points2[0],
//							graphAttributes.getNodeAttributes( edge.start() ).getBounds().getWidth() / 2,
//							points2[1] );
//			points3[points3.length - 2] = NodeShape.circleNode(
//							points2[points2.length - 1],
//							graphAttributes.getNodeAttributes( edge.end() ).getBounds().getWidth() / 2,
//							points2[points2.length - 2] );
//			a.setPoints( points3 );
//			if( !graphAttributes.getNodeAttributes( edge.start() ).isVisible() || !graphAttributes.getNodeAttributes( edge.end() ).isVisible() )
//				a.setVisible( false );
//		}

//		System.out.println( "Edge capacities: " + edgeCapacities.toString() );
//		System.out.println( "Node capacities: " + nodeCapacities.toString() );
//		System.out.println( "Transit times: " + transitTimes.toString() );
//		System.out.println( "Supplies: " + supplies.toString() );
//		System.out.println( "Sources: " + sources.toString() );
//		System.out.println( "Sinks: " + sinks.toString() );

		return graphView;
	}

	protected void readLayouts() {

		while(reader.hasMoreChildren()) {
			reader.moveDown();
			if( reader.getNodeName().equals( "nodelayout" ) )
				readNodeLayout();
//			else if( reader.getNodeName().equals( "edgelayout" ) )
//				readEdgeLayout();
//			else if( reader.getNodeName().equals( "nodeprofile" ) ) {
//				NodeAttributes a = new NodeAttributes();
//				String id = reader.getAttribute( "id" );
//				String basedOn = reader.getAttribute( "basedOn" );
//				if( basedOn == null )
//					basedOn = "";
//				a.setID( id );
//				a.setType( basedOn );
//				readLayoutProfile( a );
//				nodeLayoutProfiles.put( id, a );
//				//if (!profile.isVisible()) System.out.println(profile.getID());
//			} else if( reader.getNodeName().equals( "edgeprofile" ) ) {
//				EdgeAttributes a;
//				String id = reader.getAttribute( "id" );
//				if( id.equals( "defaultEdgeLayout" ) )
//					a = new EdgeAttributes( true );
//				else
//					a = new EdgeAttributes();
//				String basedOn = reader.getAttribute( "basedOn" );
//				if( basedOn == null )
//					basedOn = "";
//				a.setID( id );
//				a.setType( basedOn );
//				readLayoutProfile( a );
//				//System.out.println(id + " " + a.isArrowVisible());
//				edgeLayoutProfiles.put( id, a );
//			}
			reader.moveUp();
		}
		//			reader.moveDown();
//			if( reader.getNodeName().equals( "nodelayout" ) )
//				readNodeLayout();
//			reader.moveUp();
//		}
	}

	protected void convertNode( Node node ) {
//		NodeAttributes attributes = graphView.getNodeAttributes( node );
//		writer.startNode( "node" );
//		writer.addAttribute( "id", attributes.getID() );
//		if( attributes.getBalance() != 0 )
//			writer.addAttribute( "balance", String.valueOf( attributes.getBalance() ) );
//		writer.endNode();
	}

	protected Node readNode( Network graph, int nid ) {
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
				balance = reader.getAttribute( "capacity" );
		}
		node = new Node( nid );
		nodes.put( id, node );
		int balanceVal = (int)Double.parseDouble( balance );
		supplies.add( node, balanceVal );
		nodeCapacities.add( node, (int)Double.parseDouble( capacity ) );

		if( balanceVal > 0 )
			sources.add( node );
		else if( balanceVal < 0 )
			sinks.add( node );
		return node;
	}

	protected void readSink( Network graph, int nid ) {
		Node node = readNode( graph, nid );
		if( supplies.get( node ) == 0 )
			sinks.add( node );
	}

	protected void readSource( Network graph, int nid ) {
		Node node = readNode( graph, nid );
		if( supplies.get( node ) == 0 )
			sources.add( node );
	}

	protected void convertEdge( Edge edge ) {
//		writer.startNode( "edge" );
//		writer.addAttribute( "id", graphView.getEdgeAttributes( edge ).getID() );
//		writer.addAttribute( "start", graphView.getNodeAttributes( edge.start() ).getID() );
//		writer.addAttribute( "end", graphView.getNodeAttributes( edge.end() ).getID() );
//		if( graphView.getEdgeAttributes( edge ).getCapacity() != 1 )
//			writer.addAttribute( "capacity", String.valueOf( graphView.getEdgeAttributes( edge ).getCapacity() ) );
//		if( graphView.getEdgeAttributes( edge ).getTransitTime() != 1 )
//			writer.addAttribute( "transitTime", String.valueOf( graphView.getEdgeAttributes( edge ).getTransitTime() ) );
//		writer.endNode();
	}

	protected void readEdge( Network graph, int eid ) {
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
		Edge edge = new Edge( eid, nodes.get( source ), nodes.get( target ) );
		// TODO only integral transit times here!
		transitTimes.add( edge, (int)Double.parseDouble( transitTime ) );
		edgeCapacities.add( edge, (int)Double.parseDouble( capacity ) );
		edges.put( id, edge );
	}

//	protected void convertNodeLayout( NodeAttributes a, int v ) {
//		/*
//		if (0 <= v && v <= 132) { a.setType("sitzI"); a.setX(a.getX()+3.7); a.setY(a.getY()+3.7); }
//		else if (133 <= v && v <= 134) { a.setType("blockI"); a.setX(a.getX()-32); a.setY(a.getY()-13.5); }
//		else if (135 <= v && v <= 135) { a.setType("blockII"); a.setX(a.getX()-17); a.setY(a.getY()-41.3); }
//		else if (136 <= v && v <= 166) { a.setType("sitzI"); a.setX(a.getX()+3.7); a.setY(a.getY()+3.7); }
//		else if (167 <= v && v <= 167) { a.setType("blockIII"); a.setX(a.getX()+10.7); a.setY(a.getY()-6.2); }
//		else if (168 <= v && v <= 168) { a.setType("blockIV"); a.setX(a.getX()+10.7); a.setY(a.getY()-21.7); }
//		else if (169 <= v && v <= 169) { a.setType("blockIII"); a.setX(a.getX()+10.7); a.setY(a.getY()-6.2); }
//		else if (170 <= v && v <= 205) { a.setType("sitzII"); a.setX(a.getX()-2.6); a.setY(a.getY()+2.0); }
//		else if (206 <= v && v <= 207) { a.setType("blockV"); a.setX(a.getX()-8.2); a.setY(a.getY()-19.5); }
//		else if (208 <= v && v <= 208) { a.setType("blockVI"); a.setX(a.getX()-5.2); a.setY(a.getY()-25.6); }
//		else if (209 <= v && v <= 209) { a.setType("blockVII"); a.setX(a.getX()-5.2); a.setY(a.getY()-19.5); }
//		else if (210 <= v && v <= 221) { a.setType("sitzIII"); a.setX(a.getX()-2.0); a.setY(a.getY()+0.7); }
//		else if (222 <= v && v <= 229) { a.setType("sitzIV"); a.setX(a.getX()-16.7); a.setY(a.getY()-0.4); }
//		else if (230 <= v && v <= 231) { a.setType("blockVIII"); a.setX(a.getX()-6.9); a.setY(a.getY()-18.3); }
//		else if (232 <= v && v <= 232) { a.setType("blockIX"); a.setX(a.getX()+8.6); a.setY(a.getY()-9.3); }
//		else if (233 <= v && v <= 233) { a.setType("blockX"); a.setX(a.getX()+6.2); a.setY(a.getY()-16.7); }
//		else if (234 <= v && v <= 234) { a.setType("blockXI"); a.setX(a.getX()+9.4); a.setY(a.getY()-8.5); }
//		else if (235 <= v && v <= 235) { a.setType("blockXII"); a.setX(a.getX()+6.2); a.setY(a.getY()-17.5); }
//		else if (236 <= v && v <= 236) { a.setType("blockXIII"); a.setX(a.getX()-3.6); a.setY(a.getY()-99.2); }
//		else if (237 <= v && v <= 237) { a.setType("blockXIV"); a.setX(a.getX()-0.6); a.setY(a.getY()-77.9); }
//		else if (238 <= v && v <= 287) { a.setType("zwischenknoten"); a.setX(a.getX()+7.5); a.setY(a.getY()+7.5); }
//		else if (288 <= v && v <= 289) { a.setType("sitzI"); a.setX(a.getX()+3.7); a.setY(a.getY()+3.7); }
//		else if (290 <= v && v <= 317) { a.setType("zwischenknoten"); a.setX(a.getX()+7.5); a.setY(a.getY()+7.5); }
//		else if (318 <= v && v <= 318) { a.setType("supersink"); a.setX(a.getX()+4.4); a.setY(a.getY()+4.4); }
//		else if (319 <= v && v <= 339) { a.setType("zwischenknoten"); a.setX(a.getX()+7.5); a.setY(a.getY()+7.5); }
//		 */
//		writer.startNode( "nodelayout" );
//		writer.addAttribute( "node", a.getID() );
//		if( !a.getType().equals( "" ) )
//			writer.addAttribute( "basedOn", a.getType() );
//		writer.addAttribute( "x", String.valueOf( Math.round( a.getX() * 10 ) / 10.0 ) );
//		writer.addAttribute( "y", String.valueOf( Math.round( a.getY() * 10 ) / 10.0 ) );
//		writer.endNode();
//	}
	protected void readNodeLayout() {
//		NodeAttributes a = null;
//		String basedOn = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Node node = null;
		for( Iterator i = reader.getAttributeNames(); i.hasNext(); ) {
			Object name = i.next();
			if( name.equals( "node" ) )
				node = nodes.get( reader.getAttribute( "node" ) );
	//		else if( name.equals( "basedOn" ) ); // ignore based on layout right now basedOn = reader.getAttribute( "basedOn" );
			else if( name.equals( "x" ) )
				x = Double.parseDouble( reader.getAttribute( "x" ) );
			else if( name.equals( "y" ) )
				y = Double.parseDouble( reader.getAttribute( "y" ) );
			else if( name.equals( "z" ) )
				z = Double.parseDouble( reader.getAttribute( "z" ) );
		}
		if( node == null )
			throw new ConversionException( "Node id needed for node layout." );

		nodePositionMapping.get( node ).set( x, y, z );
//		a.setType( basedOn );
	}
//	protected void convertEdgeLayout( EdgeAttributes a ) {
//		writer.startNode( "edgelayout" );
//		writer.addAttribute( "edge", a.getID() );
//		if( !a.getType().equals( "" ) )
//			writer.addAttribute( "basedOn", a.getType() );
//		Point2D[] points = a.getPoints();
//		StringBuffer str = new StringBuffer();
//		for( int p = 0; p < points.length; p++ ) {
//			str.append( PointConverter.toString( points[p] ) );
//			if( p < points.length - 1 )
//				str.append( "," );
//		}
//		writer.addAttribute( "points", str.toString() );
//		writer.endNode();
//	}
//	protected void readEdgeLayout() {
//		EdgeAttributes a = null;
//		String basedOn = "";
//		String points = null;
//		Color color = null;
//		for( Iterator i = reader.getAttributeNames(); i.hasNext(); ) {
//			Object name = i.next();
//			if( name.equals( "edge" ) )
//				a = edgeAttributes.get( reader.getAttribute( "edge" ) );
//			else if( name.equals( "basedOn" ) )
//				basedOn = reader.getAttribute( "basedOn" );
//			else if( name.equals( "points" ) )
//				points = reader.getAttribute( "points" );
//			else if( name.equals( "color" ) )
//				color = ColorConverter.convert( reader.getAttribute( "color" ) );
//		}
//		if( color != null )
//			a.setColor( color );
//		a.setType( basedOn );
//		if( points != null ) {
//			points = points.substring( 1, points.length() - 1 );
//			String[] s = points.split( "\\)\\s*,\\s*\\(" );
//			Point2D[] p = new Point2D[s.length];
//			//p[0] = edges.get(a.getID()).start();
//			for( int i = 0; i < s.length; i++ ) {
//				String[] t = s[i].split( "\\s*,\\s*" );
//				p[i] = new Point2D.Double( Double.parseDouble( t[0] ), Double.parseDouble( t[1] ) );
//			}
//			a.setPoints( p );
//		}
//	}
//	protected void convertLayoutProfile( Attributes a ) {
//		Set keys = a.keySet();
//		for( Object key : keys ) {
//			String s = key.toString();
//			if( s.equals( "id" ) || s.equals( "points" ) || s.equals( "type" ) || s.equals( "x" ) || s.equals( "y" ) )
//				continue;
//			writer.startNode( key.toString() );
//			context.convertAnother( a.get( key ) );
//			writer.endNode();
//		}
//	}
	static HashMap<String, Class> requiredTypes = new HashMap<String, Class>();
//	static {
//		requiredTypes.put( "arrowStyle", ArrowStyle.class );
//		requiredTypes.put( "arrowHeight", Double.class );
//		requiredTypes.put( "arrowVisible", Boolean.class );
//		requiredTypes.put( "arrowWidth", Double.class );
//
//		requiredTypes.put( "backgroundColor", Color.class );
//		requiredTypes.put( "bounds", Rectangle2D.Double.class );
//		requiredTypes.put( "color", Color.class );
//		requiredTypes.put( "font", Font.class );
//		requiredTypes.put( "foregroundColor", Color.class );
//		requiredTypes.put( "height", Double.class );
//		requiredTypes.put( "text", String.class );
//		requiredTypes.put( "visible", Boolean.class );
//		requiredTypes.put( "width", Double.class );
//
//		requiredTypes.put( "linecolor", Color.class );
//		requiredTypes.put( "linewidth", Double.class );
//
//		requiredTypes.put( "bordercolor", Color.class );
//		requiredTypes.put( "borderwidth", Double.class );
//		requiredTypes.put( "shape", NodeShape.class );
//	}
//	protected void readLayoutProfile( Attributes a ) {
//		//System.out.println(reader.getNodeName() + " " + reader.getValue());
//		while(reader.hasMoreChildren()) {
//			reader.moveDown();
//			Object v = uncontext.convertAnother( uncontext.currentObject(), requiredTypes.get( reader.getNodeName() ) );
//			a.put( reader.getNodeName(), v );
//			//System.out.println(reader.getNodeName() + " " + v);
//			reader.moveUp();
//		}
//	}
}
