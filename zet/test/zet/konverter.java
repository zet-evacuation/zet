/**
 * konverter.java
 * input:
 * output:
 *
 * method:
 *
 * Created: Mar 19, 2010,5:11:40 PM
 */
package zet;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.graph.Node;
import java.io.FileNotFoundException;
import java.io.IOException;
import de.tu_berlin.math.coga.zet.DatFileReaderWriter;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class konverter {

//
//	public static void main( String[] argumengs ) throws IOException {
//		XMLReader reader = new XMLReader( "./testinstanz/test.xml" );
//
//		FlowVisualization fv = (FlowVisualization)reader.read();
//
//	}

	public static void main( String[] args ) throws FileNotFoundException, IOException {
		IdentifiableIntegerMapping<Node> x = new IdentifiableIntegerMapping<Node>(1);
		IdentifiableIntegerMapping<Node> y = new IdentifiableIntegerMapping<Node>(1);
		EarliestArrivalFlowProblem eafp = DatFileReaderWriter.read( "./testinstanz/siouxfalls_500_10s.dat", x, y );

		for( int i = 1; i < 500; ++i ) {
			for( Node node : eafp.getNetwork().nodes() )
				eafp.getSupplies().set( node, node.equals( eafp.getSink() ) ? -25 * i : i );

			DatFileReaderWriter.writeFile( "original", "./testinstanz/siouxfalls/siouxfalls_" + i + ".dat" , eafp.getTimeHorizon(), eafp.getNetwork().nodes(), eafp.getSources(), eafp.getSink(), eafp.getNetwork().edges(), eafp.getEdgeCapacities(), eafp.getTransitTimes(), eafp.getSupplies(), x, y);
		}

		// konvertieren

	}
}
