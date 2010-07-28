/**
 * XmlFileReaderWriter.java
 * Created: 18.03.2010, 10:53:48
 */
package de.tu_berlin.math.coga.zet;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import de.tu_berlin.math.coga.graph.io.xml.GraphView;
import java.io.IOException;
import de.tu_berlin.math.coga.graph.io.xml.XMLReader;
import de.tu_berlin.math.coga.graph.io.xml.XMLWriter;
import ds.graph.DynamicNetwork;
import ds.graph.Network;
import ds.graph.Node;
import java.util.ArrayList;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class XmlFileReaderWriter {

	public static void main( String[] argumengs ) throws IOException {
		//XMLReader reader = new XMLReader( "./testinstanz/test.xml" );
		//FlowVisualization fv = (FlowVisualization)reader.readFlowVisualization();

		XMLReader reader = new XMLReader( "./testinstanz/test_graph.xml" );
		Network n = reader.readGraph();
		System.out.println( n.toString() );


		DynamicNetwork dn = new DynamicNetwork( n );
		System.out.println( dn.toString() );

		if( true )
			return;


		reader = new XMLReader( "./testinstanz/test.xml" );
		EarliestArrivalFlowProblem eafp = reader.readFlowInstance();

		System.out.println( "Schreibe nun die Ausgabe..." );
		// Write
		XMLWriter writer = new XMLWriter( "./testinstanz/output.xml" );
		final ArrayList<Node> sinks = new ArrayList<Node>(1);
		sinks.add( eafp.getSink() );

		GraphView graphView = reader.getXmlData().generateGraphView();
		writer.writeLayoutedNetwork( graphView );// .writeNetwork( eafp );
	}
}
