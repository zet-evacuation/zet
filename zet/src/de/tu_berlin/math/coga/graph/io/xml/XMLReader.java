/*
 * XMLReader.java
 *
 */
package de.tu_berlin.math.coga.graph.io.xml;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * This class allows to readFlowVisualization a graph and especially a network flow from an
 * input file into the graph data structure.
 * @author Martin Groß, Jan-Philipp Kappmeier
 */
public class XMLReader {

	/** The file that is readFlowVisualization. */
	private File file;
	/** The XStream object that reads the XML graph file*/
	private XStream xstream;
	/** The data that was found in the XML file */
	private XMLData xmlData = new XMLData();

	/**
	 * Creates a new instance of the reader that reads a file with the specified
	 * filename.
	 * @param filename the filename (can include a (relative) path)
	 * @throws FileNotFoundException if the specified file does not exist
	 * @throws NullPointerException if the path is null
	 */
	public XMLReader( String filename ) throws NullPointerException, FileNotFoundException {
		this( new File( filename ) );
	}

	/**
	 * Creates a new instance of the reader that reads a given file.
	 * @param file the file that contains the graph
	 * @throws FileNotFoundException if the file does not exist
	 */
	public XMLReader( File file ) throws FileNotFoundException {
		if( !file.exists() )
			throw new FileNotFoundException( file.getName() );
		this.file = file;
		xstream = new XStream( new DomDriver() );
	}

	/**
	 * Returns the graph.
	 * @return the graph that is readFlowVisualization from the file
	 * @throws IOException
	 */
	public FlowVisualization readFlowVisualization() throws IOException {
		BufferedReader reader = null;
		FlowVisualization result = null;
		try {
			reader = new BufferedReader( new FileReader( file ) );
			xstream.alias( "flowVisualization", FlowVisualization.class );
			xstream.setMode( XStream.NO_REFERENCES );
			xstream.registerConverter( new ColorConverter() );
			xstream.registerConverter( new FontConverter() );
			xstream.registerConverter( new GraphViewConverter( xmlData ) );
			xstream.registerConverter( new FlowVisualisationConverter( xmlData ) );
			//xstream.registerConverter(new NodeShapeConverter());
			result = (FlowVisualization)xstream.fromXML( reader );
		} finally {
			if( reader != null )
				reader.close();
		}
		return result;
	}

	public Network readGraph() throws FileNotFoundException, IOException {
		BufferedReader reader = null;
		Network result = null;
		try {
			reader = new BufferedReader( new FileReader( file ) );
			xstream.alias( "graph", Network.class );
			xstream.setMode( XStream.NO_REFERENCES );
			xstream.registerConverter( new ColorConverter() );
			xstream.registerConverter( new FontConverter() );
			xstream.registerConverter( new GraphViewConverter( xmlData ) );
			xstream.registerConverter( new GraphConverter( xmlData ) );
			//xstream.registerConverter( new FlowVisualisationConverter() );
			//xstream.registerConverter(new NodeShapeConverter());
			result = (Network)xstream.fromXML( reader );
		} finally {
			if( reader != null )
				reader.close();
		}
		return result;
	}

	public EarliestArrivalFlowProblem readFlowInstance() throws FileNotFoundException, IOException {
		BufferedReader reader = null;
		EarliestArrivalFlowProblem eafp = null;
		try {
			reader = new BufferedReader( new FileReader( file ) );
			xstream.alias( "graph", Network.class );
			xstream.alias( "graphLayout", GraphView.class );
			xstream.setMode( XStream.NO_REFERENCES );
			xstream.registerConverter( new ColorConverter() );
			xstream.registerConverter( new FontConverter() );
			xstream.registerConverter( new GraphViewConverter( xmlData ) );
			xstream.registerConverter( new GraphConverter( xmlData ) );
			//xstream.registerConverter( new EarliestArrivalFlowProblemConverter( xmlData ) );
			//xstream.registerConverter( new FlowVisualisationConverter() );
			//xstream.registerConverter(new NodeShapeConverter());
			xstream.fromXML( reader );
		} finally {
			if( reader != null )
				reader.close();
		}

		// check, if the XML file contained valid flow instance
		if( xmlData.network == null )
			throw new InvalidFileFormatException( "File contains no network!" );

		Node sink = xmlData.getSinks().get( 0 );
		List<Node> sources = xmlData.getSources();
		IdentifiableIntegerMapping<Edge> edgeCapacities = xmlData.getEdgeCapacities();
		IdentifiableIntegerMapping<Node> nodeCapacities = xmlData.getNodeCapacities();
		IdentifiableIntegerMapping<Edge> transitTimes = xmlData.getTransitTimes();
		IdentifiableIntegerMapping<Node> supplies = xmlData.getSupplies();

		eafp = new EarliestArrivalFlowProblem( edgeCapacities, xmlData.network, nodeCapacities, sink, sources, -1, transitTimes, supplies );
		return eafp;
	}

	public XMLData getXmlData() {
		return xmlData;
	}


}
