
package de.tu_berlin.math.coga.zet;

import org.zetool.graph.DirectedGraph;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.netflow.classic.mincost.SuccessiveShortestPath;
import de.tu_berlin.math.coga.graph.io.xml.XMLReader;
import de.tu_berlin.math.coga.graph.io.xml.XMLWriter;
import de.tu_berlin.math.coga.graph.io.xml.visualization.GraphVisualization;
import de.tu_berlin.math.coga.zet.converter.graph.GraphAssignmentConverter;
import ds.PropertyContainer;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.netflow.ds.network.TimeExpandedNetwork;
import org.zetool.netflow.classic.problems.MinimumCostFlowProblem;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Project;
import de.zet_evakuierung.model.ZControl;
import de.tu_berlin.math.coga.zet.converter.AssignmentConcrete;
import gui.AlgorithmControl;
import gui.GraphConverterAlgorithms;
import gui.ZETLoader;
import gui.editor.properties.PropertyLoadException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class XmlFileReaderWriter {

	public static void main( String[] argumengs ) throws IOException {
		//XMLReader reader = new XMLReader( "./testinstanz/test.xml" );
		//FlowVisualization fv = (FlowVisualization)reader.readFlowVisualization();

		//XMLReader reader = new XMLReader( "./testinstanz/test_graph.xml" );
		System.out.println( "Loading a network." );
		//XMLReader reader = new XMLReader( "./testinstanz/small_graph.xml" );
		XMLReader reader = new XMLReader( "./testinstanz/2 rooms demo.xml" );
		DirectedGraph n = reader.readGraph();		
		System.out.println( n.toString() );
		
		
		System.out.println( "Kantenkapazitäten: " + reader.getXmlData().getEdgeCapacities().toString() );
		System.out.println( "Kantentransitzeiten: " + reader.getXmlData().getTransitTimes().toString() );
		System.out.println( "Knotenbalances: " + reader.getXmlData().getSupplies().toString() );
		
		System.out.println( "Integral:" );
		System.out.println( "Kantenkapazitäten: " + reader.getXmlData().getEdgeCapacitiesIntegral().toString() );
		System.out.println( "Kantentransitzeiten: " + reader.getXmlData().getTransitTimesIntegral().toString() );
		System.out.println( "Knotenbalances: " + reader.getXmlData().getSuppliesIntegral().toString() );

		
		MinimumCostFlowProblem mcf = new MinimumCostFlowProblem( n, reader.getXmlData().getEdgeCapacitiesIntegral(), reader.getXmlData().getTransitTimesIntegral(), reader.getXmlData().getSuppliesIntegral() );
    SuccessiveShortestPath algo = new SuccessiveShortestPath(n, mcf.getBalances(), mcf.getCapacities(), mcf.getCosts());
    algo.run();
		if( algo.getFlow() != null ) {
			int costs = 0;
			for( Edge e : n.edges() ) {
				costs += algo.getFlow().get( e ) * mcf.getCosts().get( e );
			}
			System.out.println( "Flow exists with value " + costs );
		}
    System.out.println(algo.getFlow());

		System.out.println( "Time Expansion" );

		TimeExpandedNetwork teg = new TimeExpandedNetwork(n, mcf.getCapacities(), mcf.getCosts(), 27, mcf.getBalances(), true, false );
    SuccessiveShortestPath algoTEG = new SuccessiveShortestPath(teg, teg.supplies(), teg.capacities(), teg.costs());
    algoTEG.run();
		if( algoTEG.getFlow() != null ) {
			int costs = 0;
			for( Edge e : teg.edges() ) {
				costs += algoTEG.getFlow().get( e ) * teg.costs().get( e );
			}
			System.out.println( "Flow exists with value " + costs );
		}
    //System.out.println(algoTEG.getFlow());

		//System.out.println( mcf.toString() );
		//System.out.println( "Creating dynamic network." );
		//DynamicNetwork dn = new DynamicNetwork( n );
		//System.out.println( dn.toString() );

		if (true) return;
		
		// now try to write it out
		XMLWriter writer = new XMLWriter( "./testinstanz/small_graph_out.xml" );
		writer.writeGraph( n, mcf.getCapacities(), mcf.getCosts(), mcf.getBalances() );
		
		
		
		if( true )
			return;
		
		
		// now, try to load a project and write it

		// loading properties
		File propertyFile = new File( "./properties/properties.xml" );
		try {
			PropertyContainer.getInstance().applyParameters( propertyFile );
		} catch( PropertyLoadException ex ) {
			ZETLoader.exit( ex.getMessage() );
		}
		
		
		Project p;
		ZControl z = new ZControl();
		z.loadProject( "/homes/combi/kappmeie/Dateien/Programme/zet/examples/easy/" + "2 rooms demo.zet" );
		p = z.getProject();
		
		AlgorithmControl a = new AlgorithmControl( p );
		a.convertBuildingPlan();
		a.convertGraph( null, GraphConverterAlgorithms.NonGridGraph );
		System.out.println( "Graph converted" );
		
		System.out.println( a.getNetworkFlowModel().toString() );
		
		NetworkFlowModel nfm = a.getNetworkFlowModel();
		

		ConcreteAssignment concreteAssignment = AssignmentConcrete.createConcreteAssignment( p.getCurrentAssignment(), 400 );
		
		GraphAssignmentConverter cav = new GraphAssignmentConverter( nfm );
		
		cav.setProblem( concreteAssignment );
		cav.run();
		nfm = cav.getSolution();		
		
		
		writer = new XMLWriter( "./testinstanz/2 rooms demo.xml" );
		//writer.writeGraph( nfm.graph().getAsStaticNetwork(), nfm.edgeCapacities, nfm.transitTimes, nfm.currentAssignment );
		
		if( true )
			return;

		reader = new XMLReader( "./testinstanz/test.xml" );
		EarliestArrivalFlowProblem eafp = reader.readFlowInstance();

		System.out.println( "Schreibe nun die Ausgabe..." );
		// Write
		writer = new XMLWriter( "./testinstanz/output.xml" );
		final ArrayList<Node> sinks = new ArrayList<Node>(1);
		sinks.add( eafp.getSink() );

		GraphVisualization graphView = reader.getXmlData().generateGraphView();
		writer.writeLayoutedNetwork( graphView );// .writeNetwork( eafp );
	}
}
