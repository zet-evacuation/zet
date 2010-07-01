/*
 * GraphViewConverter.java
 *
 */
package de.tu_berlin.math.coga.graph.io.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Martin Groß, Jan-Philipp Kappmeier
 */
public class GraphViewConverter implements Converter {

	private HierarchicalStreamReader reader;
	private XMLData xmlData;
	private GraphView graphView;
	private GraphConverter graphConverter;
	private HierarchicalStreamWriter writer;

	public GraphViewConverter( XMLData xmlData ) {
		this.xmlData = xmlData;
	}

	public boolean canConvert( Class type ) {
		return type.equals( GraphView.class );
	}

	public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
		this.writer = writer;
		graphView = (GraphView) source;

		// write parameter for the graph view element
		if( xmlData.scaleVal != 1 )
			writer.addAttribute( "scale", Double.toString( xmlData.scaleVal ) );
		if( xmlData.containsSuperSink == true )
			writer.addAttribute( "containsSuperSink", "1" );
		if( xmlData.doubleEdges == true )
			writer.addAttribute( "doubleEdges", "1" );

		// write the graph
		writer.startNode( "graph" );
		context.convertAnother( graphView.getNetwork() );
		writer.endNode();

		// write layouts
		writer.startNode( "layouts" );
		this.writeLayouts();
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
		writer.endNode();
	}

	protected void writeLayouts() {
		writeNodeLayout();
	}

	protected void writeNodeLayout() {
		for( Node node : xmlData.network.nodes() ) {
			final double x = xmlData.nodePositionMapping.get( node ).x;
			final double y = xmlData.nodePositionMapping.get( node ).y;
			final double z = xmlData.nodePositionMapping.get( node ).z;
			writer.startNode( "nodelayout" );
			writer.addAttribute( "node", Integer.toString( node.id() ) );
			writer.addAttribute( "x", Double.toString( x ) );
			writer.addAttribute( "y", Double.toString( y ) );
			if( z != 0 )
				writer.addAttribute( "z", Double.toString( z ) );
//		if( !a.getType().equals( "" ) )
//			writer.addAttribute( "basedOn", a.getType() );
			writer.endNode();
		}
	}

	public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
		this.reader = reader;

		// read the scale factor and the information about supersinks
		double scaleVal = 1;
		boolean doubleEdges = false;
		boolean containsSuperSink = false;
		Iterator iter = reader.getAttributeNames();
		while( iter.hasNext() ) {
			Object name = iter.next();
			if( name.equals( "scale" ) )
				scaleVal = Double.parseDouble( reader.getAttribute( "scale" ) );
			else if( name.equals( "doubleEdges" ) ) {
				final String res = reader.getAttribute( "doubleEdges" );
				if( res.equals( "1" ) )
					doubleEdges = true;
				else if( res.equals( "0" ) )
					doubleEdges = false;
				else
					throw new InvalidFileFormatException( "doubleEdges has to be either 0 or 1" );
			} else if( name.equals( "containsSuperSink" ) ) {
				final String res = reader.getAttribute( "containsSuperSink" );
				if( res.equals( "1" ) )
					containsSuperSink = true;
				else if( res.equals( "0" ) )
					containsSuperSink = false;
				else
					throw new InvalidFileFormatException( "containsSuperSink has to be either 0 or 1" );
			}
		}
		xmlData.doubleEdges = doubleEdges;
		xmlData.scaleVal = scaleVal;
		xmlData.containsSuperSink = containsSuperSink;

		// convert the graph
		reader.moveDown(); // reader starts in <graphLayout>, so set down to get the graph
		if( !reader.getNodeName().equals( "graph" ) )
			throw new InvalidFileFormatException( "Graph layout must start with a graph." );
		graphConverter = new GraphConverter( xmlData );
		xmlData.network = (Network) graphConverter.unmarshal( reader, context );
		reader.moveUp();

		// assign default position to all nodes
		xmlData.nodePositionMapping = new IdentifiableObjectMapping<Node, Vector3>( xmlData.network.numberOfNodes(), Vector3.class );
		for( Node node : xmlData.nodes.values() )
			xmlData.nodePositionMapping.set( node, new Vector3() );

		// read <layouts>. no other graphs are allowed
		if( reader.hasMoreChildren() ) {
			//while(reader.hasMoreChildren()) {
			reader.moveDown();
			if( reader.getNodeName().equals( "graph" ) )
				throw new InvalidFileFormatException( "More than one graph is not allowed." );
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

		graphView = xmlData.generateGraphView();
		xmlData.graphView = graphView;
//		graphView = new GraphView( xmlData.network, xmlData.nodePositionMapping, xmlData.getEdgeCapacities(), xmlData.getNodeCapacities(), xmlData.getTransitTimes(), xmlData.getSupplies(), xmlData.getSources(), xmlData.getSinks() );
//		graphView.setScale( scaleVal );
//		graphView.setContainsSuperSink( containsSuperSink );

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
		return graphView;
	}

	protected void readLayouts() {
		while( reader.hasMoreChildren() ) {
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

	protected void readNodeLayout() {
//		String basedOn = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Node node = null;
		for( Iterator i = reader.getAttributeNames(); i.hasNext(); ) {
			Object name = i.next();
			if( name.equals( "node" ) )
				node = xmlData.nodes.get( reader.getAttribute( "node" ) );
			//		else if( name.equals( "basedOn" ) ); // ignore based on layout right now basedOn = reader.getAttribute( "basedOn" );
			else if( name.equals( "x" ) )
				x = Double.parseDouble( reader.getAttribute( "x" ) );
			else if( name.equals( "y" ) )
				y = Double.parseDouble( reader.getAttribute( "y" ) );
			else if( name.equals( "z" ) )
				z = Double.parseDouble( reader.getAttribute( "z" ) );
		}
		if( node == null )
			throw new InvalidFileFormatException( "Node id needed for node layout." );
		xmlData.nodePositionMapping.get( node ).set( x, y, z );
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
