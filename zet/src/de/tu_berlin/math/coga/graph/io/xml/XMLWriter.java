/*
 * XMLWriter.java
 *
 */
package de.tu_berlin.math.coga.graph.io.xml;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import com.thoughtworks.xstream.XStream;
//import fv.gui.view.FlowVisualisation;
import ds.graph.Network;
import ds.graph.Node;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Martin Groß, Jan-Philipp Kappmeier
 */
public class XMLWriter {

	/** The file that is readFlowVisualization. */
	private File file;
	/** The XStream object that reads the XML graph file*/
	private XStream xstream;
	/** The data that was found in the XML file */
	private XMLData xmlData = new XMLData();

	public XMLWriter( String filename ) throws IOException {
		this( new File( filename ) );
	}

	public XMLWriter( File file ) throws IOException {
		this.file = file;
		xstream = new XStream();
	}

	/**
	 * Writes a graph without additional information. Thus only the nodes, their
	 * ids and the edges are stored in the file.
	 * @param graph the graph
	 * @throws IOException if some error reading the file occurs
	 */
	public void writeGraph( Network graph ) throws IOException {
		BufferedWriter writer = null;
		xmlData.network = graph;
		try {
			writer = new BufferedWriter( new FileWriter( file ) );
			xstream.alias( "graph", Network.class );
			xstream.setMode( XStream.NO_REFERENCES );
			//xstream.alias("flowVisualisation",FlowVisualisation.class);
			xstream.registerConverter( new ColorConverter() );
			xstream.registerConverter( new FontConverter() );
			//xstream.registerConverter( new GraphViewConverter( xmlData ) );
			xstream.registerConverter( new GraphConverter( xmlData ) );
			//xstream.registerConverter(new NodeShapeConverter());
			xstream.toXML( graph, writer );
		} finally {
			if( writer != null ) {
				writer.flush();
				writer.close();
			}
		}
	}

	public void writeNetwork( EarliestArrivalFlowProblem eafp ) throws IOException {
		BufferedWriter writer = null;
		xmlData.network = eafp.getNetwork();

		final ArrayList<Node> sinks = new ArrayList<Node>(1);
		sinks.add( eafp.getSink() );
		xmlData.sinks = sinks;	// not needed during conversation in normal mode
		xmlData.sources = eafp.getSources(); // not needed during conversation in normal mode
		xmlData.edgeCapacities = eafp.getEdgeCapacities();
		xmlData.nodeCapacities = eafp.getNodeCapacities();
		xmlData.transitTimes = eafp.getTransitTimes();
		xmlData.supplies = eafp.getSupplies();

		try {
			writer = new BufferedWriter( new FileWriter( file ) );
			xstream.alias( "graph", Network.class );
			xstream.setMode( XStream.NO_REFERENCES );
			//xstream.alias("flowVisualisation",FlowVisualisation.class);
			xstream.registerConverter( new ColorConverter() );
			xstream.registerConverter( new FontConverter() );
			//xstream.registerConverter( new GraphViewConverter( xmlData ) );
			xstream.registerConverter( new GraphConverter( xmlData ) );
			//xstream.registerConverter(new NodeShapeConverter());
			xstream.toXML( eafp.getNetwork(), writer );
		} finally {
			if( writer != null ) {
				writer.flush();
				writer.close();
			}
		}
	}

	public void writeLayoutedNetwork( GraphView graphView ) throws IOException {
		BufferedWriter writer = null;
		xmlData.network = graphView.getNetwork();

		xmlData.sinks = graphView.sinks;	// not needed during conversation in normal mode
		xmlData.sources = graphView.getSources(); // not needed during conversation in normal mode
		xmlData.edgeCapacities = graphView.getEdgeCapacities();
		xmlData.nodeCapacities = graphView.getNodeCapacities();
		xmlData.transitTimes = graphView.getTransitTimes();
		xmlData.supplies = graphView.getSupplies();
		xmlData.scaleVal = graphView.getScale();
		xmlData.containsSuperSink = graphView.isContainsSuperSink();
		xmlData.nodePositionMapping = graphView.getNodePositionMapping();

		try {
			writer = new BufferedWriter( new FileWriter( file ) );
			xstream.alias( "graph", Network.class );
			xstream.alias( "graphLayout", GraphView.class );
			xstream.setMode( XStream.NO_REFERENCES );
			//xstream.alias("flowVisualisation",FlowVisualisation.class);
			xstream.registerConverter( new ColorConverter() );
			xstream.registerConverter( new FontConverter() );
			xstream.registerConverter( new GraphViewConverter( xmlData ) );
			xstream.registerConverter( new GraphConverter( xmlData ) );
			//xstream.registerConverter(new NodeShapeConverter());
			//xstream.toXML( eafp.getNetwork(), writer );
			xstream.toXML( graphView, writer );
		} finally {
			if( writer != null ) {
				writer.flush();
				writer.close();
			}
		}
	}

	public XMLData getXmlData() {
		return xmlData;
	}

}
