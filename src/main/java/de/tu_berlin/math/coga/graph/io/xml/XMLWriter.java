/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.graph.io.xml;

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import com.thoughtworks.xstream.XStream;
import de.tu_berlin.math.coga.graph.io.xml.visualization.GraphVisualization;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.graph.DefaultDirectedGraph;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DirectedGraph;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Martin Groß
 * @author Jan-Philipp Kappmeier
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
	public void writeGraph( DirectedGraph graph ) throws IOException {
		BufferedWriter writer = null;
		xmlData.network = graph;
		try {
			writer = new BufferedWriter( new FileWriter( file ) );
			xstream.alias( "graph", DirectedGraph.class );
			xstream.alias( "network", DefaultDirectedGraph.class );
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

		public void writeGraph( DirectedGraph graph, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Edge> edgeCosts, IdentifiableIntegerMapping<Node> nodeSupplies ) throws IOException {
		BufferedWriter writer = null;
		xmlData.network = graph;
		xmlData.edgeCapacitiesIntegral = edgeCapacities;
		xmlData.transitTimesIntegral = edgeCosts;
		xmlData.suppliesIntegral = nodeSupplies;
		try {
			writer = new BufferedWriter( new FileWriter( file ) );
			xstream.alias( "graph", DirectedGraph.class );
			xstream.alias( "graph", DefaultDirectedGraph.class );
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

		final ArrayList<Node> sinks = new ArrayList<>( 1 );
		sinks.add( eafp.getSink() );
		xmlData.sinks = sinks;	// not needed during conversation in normal mode
		xmlData.sources = eafp.getSources(); // not needed during conversation in normal mode
		xmlData.edgeCapacitiesIntegral = eafp.getEdgeCapacities();
		xmlData.nodeCapacitiesIntegral = eafp.getNodeCapacities();
		xmlData.transitTimesIntegral = eafp.getTransitTimes();
		xmlData.suppliesIntegral = eafp.getSupplies();

		try {
			writer = new BufferedWriter( new FileWriter( file ) );
			xstream.alias( "graph", DirectedGraph.class );
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

	public void writeLayoutedNetwork( GraphVisualization graphView ) throws IOException {
		BufferedWriter writer = null;
		xmlData.network = graphView.getNetwork();

		xmlData.sinks = graphView.getSinks();	// not needed during conversation in normal mode
		xmlData.sources = graphView.getSources(); // not needed during conversation in normal mode
		xmlData.edgeCapacitiesIntegral = graphView.getEdgeCapacities();
		xmlData.nodeCapacitiesIntegral = graphView.getNodeCapacities();
		xmlData.transitTimesIntegral = graphView.getTransitTimes();
		xmlData.suppliesIntegral = graphView.getSupplies();
		xmlData.scaleVal = graphView.getScale();
		xmlData.containsSuperSink = graphView.isContainsSuperSink();
		xmlData.nodePositionMapping = graphView.getNodePositionMapping();

		try {
			writer = new BufferedWriter( new FileWriter( file ) );
			xstream.alias( "graph", DirectedGraph.class );
			xstream.alias( "graphLayout", GraphVisualization.class );
			xstream.setMode( XStream.NO_REFERENCES );
			//xstream.alias("flowVisualisation",FlowVisualisation.class);
			xstream.registerConverter( new ColorConverter() );
			xstream.registerConverter( new FontConverter() );
			xstream.registerConverter( new GraphViewConverter( xmlData ) );
			xstream.registerConverter( new GraphConverter( xmlData ) );
			//xstream.registerConverter(new NodeShapeConverter());
			//xstream.toXML( eafp.getGraph(), writer );
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
