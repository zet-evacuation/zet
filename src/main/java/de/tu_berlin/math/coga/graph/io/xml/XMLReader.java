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

import de.tu_berlin.math.coga.graph.io.xml.visualization.GraphVisualization;
import de.tu_berlin.math.coga.graph.io.xml.visualization.FlowVisualization;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.zetool.graph.Edge;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DefaultDirectedGraph;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Node;
import io.FileTypeException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * This class allows to readFlowVisualization a graph and especially a network flow from an input file into the graph
 * data structure.
 *
 * @author Martin Groß
 * @author Jan-Philipp Kappmeier
 */
public class XMLReader {

	public static enum XMLFileData {
		Graph,
		GraphView,
		FlowVisualization,
		NashFlow,
		Invalid
	}

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

	public DirectedGraph readGraph() throws FileNotFoundException, IOException {
		BufferedReader reader = null;
		DefaultDirectedGraph result = null;
		try {
			reader = new BufferedReader( new FileReader( file ) );
			xstream.alias( "graph", DirectedGraph.class );
			xstream.setMode( XStream.NO_REFERENCES );
			xstream.registerConverter( new ColorConverter() );
			xstream.registerConverter( new FontConverter() );
			xstream.registerConverter( new GraphViewConverter( xmlData ) );
			xstream.registerConverter( new GraphConverter( xmlData ) );
			//xstream.registerConverter( new FlowVisualisationConverter() );
			//xstream.registerConverter(new NodeShapeConverter());
			result = (DefaultDirectedGraph)xstream.fromXML( reader );
		} finally {
			if( reader != null )
				reader.close();
		}
		return result;
	}

	public GraphVisualization readGraphView() throws FileNotFoundException, IOException {
		BufferedReader reader = null;
		GraphVisualization result = null;
		try {
			reader = new BufferedReader( new FileReader( file ) );
			xstream.alias( "graphLayout", GraphVisualization.class );
			xstream.setMode( XStream.NO_REFERENCES );
			xstream.registerConverter( new ColorConverter() );
			xstream.registerConverter( new FontConverter() );
			xstream.registerConverter( new GraphViewConverter( xmlData ) );
			//xstream.registerConverter( new GraphConverter( xmlData ) );
			//xstream.registerConverter( new FlowVisualisationConverter() );
			//xstream.registerConverter(new NodeShapeConverter());
			result = (GraphVisualization)xstream.fromXML( reader );
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
			xstream.alias( "graph", DirectedGraph.class );
			xstream.alias( "graphLayout", GraphVisualization.class );
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
		IdentifiableIntegerMapping<Edge> edgeCapacities = xmlData.getEdgeCapacitiesIntegral();
		IdentifiableIntegerMapping<Node> nodeCapacities = xmlData.getNodeCapacitiesIntegral();
		IdentifiableIntegerMapping<Edge> transitTimes = xmlData.getTransitTimesIntegral();
		IdentifiableIntegerMapping<Node> supplies = xmlData.getSuppliesIntegral();

		eafp = new EarliestArrivalFlowProblem( edgeCapacities, xmlData.network, nodeCapacities, sink, sources, -1, transitTimes, supplies );
		return eafp;
	}

	public XMLData getXmlData() {
		return xmlData;
	}

	public static XMLFileData getFileData( File f ) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader( new FileReader( f ) );
		String line = reader.readLine().trim();
		if( line.charAt( 0 ) == '<') {
			final int i1 = line.indexOf( " " );
			final int i2 = line.indexOf( ">" );
			final int end = i1 * i2 > 0 ? Math.min( i1, i2 ) : Math.max( i1, i2 );
			String s = line.substring( 1, end );
			if( s.equals( "graphLayout") ) {
				return XMLFileData.GraphView;
			} else if( s.equals( "graph") ) {
				return XMLFileData.Graph;
			} else if( s.equals( "fv") ) {
				return XMLFileData.FlowVisualization;
			} else if( s.equals( "nf") ) {
				return XMLFileData.NashFlow;
			} else
				throw new FileTypeException( "Not supported XML-Format" );
		} else
			throw new FileTypeException( "Not a valid XML-File" );
	}
}
